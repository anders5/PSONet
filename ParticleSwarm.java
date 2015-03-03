import java.util.ArrayList;
import java.util.List;
import java.util.Random;




public class ParticleSwarm {
	
	protected int dimensions;
	protected double vmax = 4.0;
	protected NetTrainer trainer;
	protected int numparticles;
	protected List<Particle> swarm;
	protected List<Double> gbest;
	protected double gbesterror;
	protected Random rand = new Random();
	protected double vw; //these three parameters should be set before initialising the swarm: vw = weight of previous velocity, vp = weight of pbest, vg = weight of gbest
	protected double vp;
	protected double vg;
	
	public ParticleSwarm(int numparticles, int dimensions, NetTrainer trainer) {
		this.numparticles = numparticles;
		this.dimensions = dimensions;
		swarm = new ArrayList<Particle>();
		this.trainer = trainer;
	}
	
	public ParticleSwarm(int numparticles, int dimensions) {
		this.numparticles = numparticles;
		this.dimensions = dimensions;
		swarm = new ArrayList<Particle>();
		
	}
	
	public void setParameters(double vw, double vp, double vg) {
		this.vw = vw;
		this.vp = vp;
		this.vg = vg;
	}
	
	public List<Double> getgbest() {
		return gbest;
	}
	
	public Double getgbesterror() {
		return gbesterror;
	}
	
	public void createSwarm() {
		for(int i = 0; i < numparticles; i++) {
			List<Double> position = new ArrayList<Double>();
			List<Double> velocity = new ArrayList<Double>();
			for(int j = 0; j < dimensions; j++) {
//				if(rand.nextDouble() > 0.5) {
//					Double p = (rand.nextDouble() * 5.0) + 5.0;
//					position.add(p);
//				}
//				else {
//					Double p = (rand.nextDouble() * -5.0) -5.0;
//					position.add(p);
//				}
				
				Double p = (rand.nextDouble() * 20.0) - 10.0;
				position.add(p);
				double v = (rand.nextDouble() * 8.0) - 4.0;
				velocity.add(v);
			}
		
			Particle p = new Particle(position, velocity);
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
	}}
	
	
	protected class Particle {
		List<Double> pbest,velocity,position;
		Double pbesterror;
	
		Particle(List<Double> position, List<Double> velocity) {
			this.position = position;
			this.velocity = velocity;
			this.pbest = position;
			pbesterror = trainer.testWeights(position);
		}
		
		Particle() {
			
		}
		
		public void updateVelocity(List<Double> newvelocity) {
			velocity = newvelocity;
		}
		
		void updatePosition() {
			for(int i = 0; i < position.size(); i++) {
				Double newposition = position.get(i) + velocity.get(i);
				//position.remove(i);
				position.set(i,newposition);
			}
		}
		
		
	}
	
	public List<Double> moveSwarm(NeuralNetwork net) {	
		for(Particle p : swarm) {
			double rp = rand.nextDouble();
			double rg = rand.nextDouble();
			List<Double> newvelocity = new ArrayList<Double>();
			for(int i = 0; i < p.position.size(); i++) {
				//double newv = (vw * p.velocity.get(i)) + (vp * rp * (p.pbest.get(i) - p.position.get(i)) ) + (vg * rg * (gbest.get(i) - p.position.get(i))); //particle velocity update rule

				//double newv = ((rand.nextDouble() * 2.0 - 1.0) * 0.5) +  (vw * p.velocity.get(i)) + (vp * rp * (p.pbest.get(i) - p.position.get(i)) ) + (vg * rg * (gbest.get(i) - p.position.get(i))); //particle velocity update rule
				double newv = 0.7289 *  (p.velocity.get(i) + (vp * rp * (p.pbest.get(i) - p.position.get(i)) ) + (vg * rg * (gbest.get(i) - p.position.get(i)))); //particle velocity update rule
				//if(newv < -vmax) {newv = -(vmax);};
				//if(newv > vmax) {newv = vmax;};
				newvelocity.add(newv);
			}
			p.updateVelocity(newvelocity);
			
			p.updatePosition();
			checkNewSolution(net,p);
			
		}
		
		return gbest;
	}

	protected void checkNewSolution(NeuralNetwork net, Particle p) {
			Double solutionerror = trainer.testWeights(p.position);
			if(solutionerror < p.pbesterror) {
				p.pbest = p.position;
				p.pbesterror = solutionerror;
			}
			if(solutionerror < gbesterror) {
				gbest = p.position;
				gbesterror = solutionerror;
			}
		}
		}

