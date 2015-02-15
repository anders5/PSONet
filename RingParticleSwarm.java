import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class RingParticleSwarm extends ParticleSwarm {
	
	List<RingParticle> swarm;

	public RingParticleSwarm(int numparticles, int dimensions,
			
			NetTrainer trainer) {
		super(numparticles, dimensions, trainer);
		this.swarm = new ArrayList<RingParticle>();
		
	}
	
	public void createSwarm() {
		for(int i = 0; i < numparticles; i++) {
			List<Double> position = new ArrayList<Double>();
			List<Double> velocity = new ArrayList<Double>();
			for(int j = 0; j < dimensions; j++) {
				Double p = (rand.nextDouble() * 2.0) - 1.0;
				position.add(p);
				double v = (rand.nextDouble() * 8.0) - 4.0;
				velocity.add(v);
			}
		
			RingParticle p = new RingParticle(position, velocity);
			swarm.add(p);
			if(i == 0) {
				gbest = position;
				
				gbesterror = trainer.testWeights(position);
			}
			else {
				if(trainer.testWeights(position) < gbesterror) {
					gbest = position;
					gbesterror = trainer.testWeights(position);
				}
			}
	}
		for(RingParticle p : swarm) {
			updateLocalBest(p);
		}
		}
	
	public List<Double> moveSwarm(NeuralNetwork net) {
		for(RingParticle p : swarm) {
			double rp = rand.nextDouble();
			double rg = rand.nextDouble();
			List<Double> newvelocity = new ArrayList<Double>();
			for(int i = 0; i < p.position.size(); i++) {
				updateLocalBest(p);
				double newv = (vw * p.velocity.get(i)) + (vp * rp * (p.pbest.get(i) - p.position.get(i)) ) + (vg * rg * (p.localbest.get(i) - p.position.get(i))); //particle velocity update rule

				//double newv = ((rand.nextDouble() * 2.0 - 1.0) * 0.5) +  (vw * p.velocity.get(i)) + (vp * rp * (p.pbest.get(i) - p.position.get(i)) ) + (vg * rg * (gbest.get(i) - p.position.get(i))); //particle velocity update rule
				//double newv = 0.73 *  (vw * p.velocity.get(i)) + (vp * rp * (p.pbest.get(i) - p.position.get(i)) ) + (vg * rg * (gbest.get(i) - p.position.get(i))); //particle velocity update rule
				if(newv < -vmax) {newv = -(vmax);};
				if(newv > vmax) {newv = vmax;};
				newvelocity.add(newv);
			}
			p.updateVelocity(newvelocity);
			
			p.updatePosition();
			checkNewSolution(net,p);
			updateLocalBest(p);
			
		}
		
		return gbest;
	}
	
	
	private class RingParticle extends ParticleSwarm.Particle {
		
		List<Double> localbest;
		Double localbesterror;

		RingParticle(List<Double> position, List<Double> velocity) {
			super(position, velocity);
			this.localbest = position;
			this.localbesterror = trainer.testWeights(position);
			
		}
		
	}
	
	private void updateLocalBest(RingParticle p) {
		RingParticle localbest = checkNeighbours(p);
		p.localbest = localbest.localbest;
		p.localbesterror = localbest.localbesterror;
	}
	
	private RingParticle checkNeighbours(RingParticle p) {
		int index = swarm.indexOf(p);
		Double leftbesterror, rightbesterror;
		if(index == 0) {leftbesterror = swarm.get(swarm.size()-1).pbesterror; rightbesterror = swarm.get(1).pbesterror;}
		else {
		if(index == swarm.size()-1) { leftbesterror = swarm.get(swarm.size()-2).pbesterror; rightbesterror = swarm.get(0).pbesterror;}
		else { leftbesterror = swarm.get(index-1).pbesterror; rightbesterror = swarm.get(index+1).pbesterror; }
		}
		if(leftbesterror < p.pbesterror) {
			if(leftbesterror < rightbesterror) {
				if(index == 0) { return swarm.get(swarm.size()-1);}
				else {return swarm.get(index-1);}
			}
			else { if(index == swarm.size()-1) { return swarm.get(0);}
			else {return swarm.get(index+1); }}
		}
		else if(p.pbesterror < rightbesterror) {
			return p;
		}
		else { if(index == swarm.size()-1) { return swarm.get(0);} 
		else {return swarm.get(index+1); }}
		
	}
	
	
	
	

}
