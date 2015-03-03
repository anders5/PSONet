import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

 //TODO: need to update gbestaction even if gbest hasn't changed: the action has to be based on gbest particles new position, not its old one

public class TraderParticleSwarm extends ParticleSwarm {
	private List<TraderParticle> swarm;
	private int time;
	private int windowsize = 3;
	private double profit = 0;
	private double transactionfee = 0.01, fitnessweight = 0.9;
	public double gbestfitness;
	protected double gbestaction;
	protected TraderParticle gbestparticle;
	private double gbestoutput = 0;
	private List<Double> window = new ArrayList<Double>();
	private NeuralNetwork net;
	private List<Double> series;
	
	public TraderParticleSwarm(int numparticles, int dimensions, NeuralNetwork net, List<Double> series) throws IOException {
		 super(numparticles, dimensions);
		 this.net = net;
		 this.series = series;
		 this.swarm = new ArrayList<TraderParticle>();
		 this.time = windowsize + 2;
		 
	}
	
	public List<Double> getWindow() {
		return window;
	}
	
	public void initialiseWindow() {
		for(int i = 0; i < windowsize + 1; i++) {
			double pricediff = series.get(i+1) - series.get(i);
			window.add(pricediff);
		}
	}
	
	public void moveWindow() {
		time++;
		window.remove(0);
		double pricediff = series.get(time) - series.get(time-1);
		window.add(pricediff);
	}
	
	public double getgbestaction() {
		return gbestaction;
	}
	
	public TraderParticle getgbestparticle() {
		return gbestparticle;
	}
	
	public double getgbestfitness() {
		return gbestfitness;
	}
	
	public class TraderParticle extends ParticleSwarm.Particle {
		double fitness, pbfitness, netoutput; 
		double tradingaction;
		
		TraderParticle(List<Double> position, List<Double> velocity) {
			super();
			this.position = position;
			this.velocity = velocity;
			this.fitness = 0;
			this.pbfitness = 0;
			this.netoutput = 0;
			this.tradingaction = 0;
		}
		
		
	}
	
	public void createSwarm() {
		for(int i = 0; i < numparticles; i++) {
			List<Double> position = new ArrayList<Double>();
			List<Double> velocity = new ArrayList<Double>();
			for(int j = 0; j < dimensions; j++) {
				Double p = (rand.nextDouble() * 20.0) - 10.0;
				position.add(p);
				double v = (rand.nextDouble() * 8) - 4;
				velocity.add(v);
			}
	
		TraderParticle p = new TraderParticle(position, velocity);
		
		swarm.add(p);
		
		gbest = swarm.get(0).position;
		gbestparticle = swarm.get(0);
		gbestfitness = swarm.get(0).fitness;
		gbestaction = swarm.get(0).tradingaction;
		
		
		calculateFitness(p);
		p.pbest = position;
		p.pbfitness = p.fitness;
		//System.out.println(position);
		
		}

		updateGlobalBest();

}
	
	public TraderParticle moveTraderSwarm(NeuralNetwork net) {	
		for(TraderParticle p : swarm) {
			double rp = rand.nextDouble();
			double rg = rand.nextDouble();
			List<Double> newvelocity = new ArrayList<Double>();
			for(int i = 0; i < p.position.size(); i++) {
				double newv = vw * (1.0 * p.velocity.get(i)) + (vp * rp * (p.pbest.get(i) - p.position.get(i)) ) + (vg * rg * (gbest.get(i) - p.position.get(i))); //particle velocity update rule

				//double newv = ((rand.nextDouble() * 2.0 - 1.0) * 0.5) +  (vw * p.velocity.get(i)) + (vp * rp * (p.pbest.get(i) - p.position.get(i)) ) + (vg * rg * (gbest.get(i) - p.position.get(i))); //particle velocity update rule
				//double newv = 0.73 *  (vw * p.velocity.get(i)) + (vp * rp * (p.pbest.get(i) - p.position.get(i)) ) + (vg * rg * (gbest.get(i) - p.position.get(i))); //particle velocity update rule
				//if(newv < -vmax) {newv = -(vmax);};
				//if(newv > vmax) {newv = vmax;};
				newvelocity.add(newv);
			}
			p.updateVelocity(newvelocity);
			
			p.updatePosition();
			checkNewSolution(p);
			//System.out.println(p.position);
			
		}
		updateGlobalBest();
		
		return gbestparticle;
	}
	
	protected void checkNewSolution(TraderParticle p) {
		calculateFitness(p);
		if(p.fitness > p.pbfitness) {p.pbfitness = p.fitness; p.pbest = p.position;}
		
	}

	
	protected void updateGlobalBest() {
		
		for(TraderParticle p : swarm) {
			if(p.fitness > gbestfitness) {
				gbest = p.position;
				gbestparticle = p;
				gbestfitness = p.fitness;
				gbestaction = p.tradingaction;
				gbestoutput = p.netoutput;
			}
		}
	}
	
	protected void calculateFitness(TraderParticle p) {
		net.putWeights(p.position);
		//System.out.println(p.position);
		List<Double> inputs = new ArrayList<Double>(window);
		for(int i = 0; i < inputs.size(); i++) {
			inputs.set(i, (inputs.get(i) * 500));
		}
		inputs.add(gbestparticle.netoutput);
		//System.out.println(inputs);
		double output = net.input(inputs).get(0);
		
		double action = Math.signum(output);
		//System.out.println(output);
		//System.out.println(action);
		double tradereturn = calculateReturn(output, p.netoutput);
		p.fitness = (fitnessweight * p.fitness) + tradereturn; //calculate fitness using nets output rather than trading action?
		p.tradingaction = action;
		p.netoutput = output;
		//System.out.println(p.fitness);
		
		
			
	}
	
	public double calculateReturn(double currentaction, double previousaction) {
		double pricechange = window.get(window.size()-1);
		//System.out.println(window);
		double tradereturn = (previousaction * pricechange) - ((transactionfee / 2) * Math.abs(currentaction - previousaction));
		return tradereturn;
		
	}
	
	
}
