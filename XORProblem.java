import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class XORProblem {
	
	
	public static void main(String[] args) {
		List<Integer> netlayers = new ArrayList<Integer>() {{ add(3); add(1);}};
		NeuralNetwork net = new NeuralNetwork(2,2,netlayers);
		net.createNetwork();
		
		
		List<List<Double>> inputpatterns = new ArrayList<List<Double>>();
		List<Double> p1 = new ArrayList<Double>() {{add((double) 0); add((double) 0);}};
		List<Double> p2 = new ArrayList<Double>() {{add((double) 0); add((double) 1);}};
		List<Double> p3 = new ArrayList<Double>() {{add((double) 1); add((double) 0);}};
		List<Double> p4 = new ArrayList<Double>() {{add((double) 1); add((double) 1);}};
		inputpatterns.add(p1);
		inputpatterns.add(p2);
		inputpatterns.add(p3);
		inputpatterns.add(p4);
		
		List<List<Double>> expectedoutputs = new ArrayList<List<Double>>();
		List<Double> o1 = new ArrayList<Double>() {{add((double) 0);}};
		List<Double> o2 = new ArrayList<Double>() {{add((double) 1);}};
		List<Double> o3 = new ArrayList<Double>() {{add((double) 1);}};
		List<Double> o4 = new ArrayList<Double>() {{add((double) 0);}};
		expectedoutputs.add(o1);
		expectedoutputs.add(o2);
		expectedoutputs.add(o3);
		expectedoutputs.add(o4);
		
		NetTrainer trainer = new NetTrainer(inputpatterns, expectedoutputs, net);
	
		
		ParticleSwarm swarm = new ParticleSwarm(100, net.getNumberOfWeights(), trainer);
//		RingParticleSwarm swarm = new RingParticleSwarm(100, net.getNumberOfWeights(), trainer);
		swarm.setParameters(0.7,2.05,2.05);
		swarm.createSwarm();
		Double error = trainer.testWeights(swarm.getgbest());
		System.out.println(error);
		System.out.println(swarm.getgbest());
		
		int x = 100;
		while(x > 0) { 
			
			swarm.moveSwarm(net);
			System.out.println(swarm.getgbest());
			System.out.println(swarm.getgbesterror());
			
			x--;
		}
		System.out.println(swarm.getgbesterror());		
	}

}
