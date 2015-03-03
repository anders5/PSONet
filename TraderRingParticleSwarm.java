import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class TraderRingParticleSwarm extends TraderParticleSwarm {
	
	List<TraderRingParticle> swarm;
	TraderRingParticle gbestringparticle;
	
	public TraderRingParticleSwarm(int numparticles, int dimensions, NeuralNetwork net, List<Double> series) throws IOException {
		super(numparticles, dimensions, net, series);
		this.swarm = new ArrayList<TraderRingParticle>();
		
	}
	
	public void createSwarm() {
		for(int i = 0; i < numparticles; i++) {
			List<Double> position = new ArrayList<Double>();
			List<Double> velocity = new ArrayList<Double>();
			for(int j = 0; j < dimensions; j++) {
				Double p = (rand.nextDouble() * 20.0) - 10.0;
				position.add(p);
				double v = (rand.nextDouble() * 8.0) - 4.0;
				velocity.add(v);
			}
	
		TraderRingParticle p = new TraderRingParticle(position, velocity);
		swarm.add(p);
		calculateFitness(p);
		p.pbest = position;
		p.pbfitness = p.fitness;
		if(i == 0) {
			gbest = position;
			gbestfitness = p.fitness;
			gbestringparticle = p;
		}
		else {
			if(p.fitness > gbestfitness) {
				gbest = position;
				gbesterror = p.fitness;
			}
		//System.out.println(position);
		
		
		}
		}
		for(TraderRingParticle p : swarm) {
			updateLocalBest(p);
		}
	}
	
	public TraderRingParticle moveTraderSwarm(NeuralNetwork net) {	
		for(TraderRingParticle p : swarm) {
			double rp = rand.nextDouble();
			double rg = rand.nextDouble();
			List<Double> newvelocity = new ArrayList<Double>();
			for(int i = 0; i < p.position.size(); i++) {
				double newv = (vw * p.velocity.get(i)) + (vp * rp * (p.pbest.get(i) - p.position.get(i)) ) + (vg * rg * (p.localbest.get(i) - p.position.get(i))); //particle velocity update rule

				//double newv = ((rand.nextDouble() * 2.0 - 1.0) * 0.5) +  (vw * p.velocity.get(i)) + (vp * rp * (p.pbest.get(i) - p.position.get(i)) ) + (vg * rg * (gbest.get(i) - p.position.get(i))); //particle velocity update rule
				//double newv = 0.73 *  (vw * p.velocity.get(i)) + (vp * rp * (p.pbest.get(i) - p.position.get(i)) ) + (vg * rg * (gbest.get(i) - p.position.get(i))); //particle velocity update rule
				if(newv < -vmax) {newv = -(vmax);};
				if(newv > vmax) {newv = vmax;};
				newvelocity.add(newv);
			}
			p.updateVelocity(newvelocity);
			
			p.updatePosition();
			checkNewSolution(p);
			updateLocalBest(p);
		
			//System.out.println(p.position);
			
		}
		updateGlobalBest();
		
		return gbestringparticle;
	}
	
	private void updateLocalBest(TraderRingParticle p) {
		TraderRingParticle localbest = checkNeighbours(p);
		p.localbest = localbest.localbest;
		p.localbestfitness = localbest.localbestfitness;
	}
		
	
	
	private class TraderRingParticle extends TraderParticleSwarm.TraderParticle {
		List<Double> localbest;
		double localbestfitness;
		
		TraderRingParticle(List<Double> position, List<Double> velocity) {
			super(position,velocity);
			this.localbest = position;
			this.localbestfitness = 0;
		}
	}

	private TraderRingParticle checkNeighbours(TraderRingParticle p) {
		int index = swarm.indexOf(p);
		Double leftbestfitness, rightbestfitness;
		if(index == 0) {leftbestfitness = swarm.get(swarm.size()-1).pbfitness; rightbestfitness = swarm.get(1).pbfitness;}
		else {
		if(index == swarm.size()-1) { leftbestfitness = swarm.get(swarm.size()-2).pbfitness; rightbestfitness = swarm.get(0).pbfitness;}
		else { leftbestfitness = swarm.get(index-1).pbfitness; rightbestfitness = swarm.get(index+1).pbfitness; }
		}
		if(leftbestfitness > p.pbfitness) {
			if(leftbestfitness > rightbestfitness) {
				if(index == 0) { return swarm.get(swarm.size()-1);}
				else {return swarm.get(index-1);}
			}
			else { if(index == swarm.size()-1) { return swarm.get(0);}
			else {return swarm.get(index+1); }}
		}
		else if(p.pbfitness > rightbestfitness) {
			return p;
		}
		else { if(index == swarm.size()-1) { return swarm.get(0);} 
		else {return swarm.get(index+1); }}
		
	}
	
	

}
