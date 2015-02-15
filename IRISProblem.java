import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;


public class IRISProblem {
	
	public static void main(String[] args) throws IOException {
		List<Integer> netlayers = new ArrayList<Integer>() {{ add(4); add(3);}};
		NeuralNetwork net = new NeuralNetwork(4,2,netlayers);
		net.createNetwork();
		
		List<List<List<Double>>> trainingdata = parseData("C:\\workspace\\PSONet\\irisdata\\irisdata.train.txt");
		List<List<List<Double>>> validationdata = parseData("C:\\workspace\\PSONet\\irisdata\\irisdata.val.txt");
		List<List<List<Double>>> testdata = parseData("C:\\workspace\\PSONet\\irisdata\\irisdata.test.txt");		
		
		NetTrainer trainingset = new NetTrainer(trainingdata.get(0), trainingdata.get(1), net);
		NetTrainer validationset = new NetTrainer(validationdata.get(0), validationdata.get(1), net);
		NetTrainer testset = new NetTrainer(testdata.get(0), testdata.get(1), net);
		
		/*ParticleSwarm swarm = new ParticleSwarm(100, net.getNumberOfWeights(), trainingset);
		swarm.createSwarm();
		swarm.setParameters(0.7,2.05,2.05); */
		
		RingParticleSwarm swarm = new RingParticleSwarm(100, net.getNumberOfWeights(), trainingset);
		swarm.createSwarm();
		swarm.setParameters(0.7,2.05,2.05);
		
		double gbestonvalerror = validationset.testWeights(swarm.getgbest());
		List<Double> gbestonval = swarm.getgbest();
		int iterations = 200;
		int lastupdatetogval = 0;
		for(int i = 0; i < iterations; i++) {
			
			swarm.moveSwarm(net);
			if(validationset.testWeights(swarm.getgbest()) < gbestonvalerror) {
				gbestonval = swarm.getgbest();
				gbestonvalerror = validationset.testWeights(swarm.getgbest());
				lastupdatetogval = i;
			}
			
		}
		System.out.println(gbestonvalerror);
		System.out.println(gbestonval);
		System.out.println(lastupdatetogval);
		
		List<List<Double>> testsetoutputs = testset.testInputs(testdata.get(0));
		for(int i = 0; i < testsetoutputs.size(); i++) {
			System.out.println("Input pattern: " + testdata.get(0).get(i) + " Expected output: " + testdata.get(1).get(i) + " Nets output: " + testsetoutputs.get(i));
		}
		
	}
	
	
	

	private static List<List<List<Double>>> parseData(String location) throws IOException {
		List<List<List<Double>>> parseddata = new ArrayList<List<List<Double>>>();
		List<List<Double>> patterns = new ArrayList<List<Double>>(); 
		List<List<Double>> outputs = new ArrayList<List<Double>>(); 
		
		BufferedReader br = new BufferedReader(new FileReader(location));
	    StringBuilder sb = new StringBuilder();
	    String line = br.readLine();
	    	while (line != null) {
	            sb.append(line);
	            sb.append("\n");
	            line = br.readLine();
	        }
	       
	        br.close();
	        
	   String[] st = sb.toString().split("\\s");
	  
	   int x = 0;
	   for (int i = 0; i < st.length; i= i + 7) {
		   List<Double> pattern = new ArrayList<Double>();
		   List<Double> output = new ArrayList<Double>();
		   for(int j = 0; j < 4; j++) {pattern.add(Double.parseDouble(st[i+j]));}
		   for(int k = 0; k < 3; k++) {output.add(Double.parseDouble(st[i+4+k]));}
		
		   patterns.add(pattern);
		   outputs.add(output);
	   }
	   
	   parseddata.add(patterns);
	   parseddata.add(outputs);
	       
	        
	        
	        return parseddata;
	}
	
		
	}
	



