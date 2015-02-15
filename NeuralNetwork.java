import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;




public class NeuralNetwork {
	
/*	public static void main(String[] args) {
		List<Integer> netlayers = new ArrayList<Integer>() {{ add(2); add(4); add(1);}};
		List<Double> inputs = new ArrayList<Double>() {{ add((double) 1); add((double) 1); }};
		NeuralNetwork testnet = new NeuralNetwork(2,3,netlayers);
		testnet.createNetwork();
		testnet.input(inputs);
	}*/
	
	private int numinputs,numlayers;
	private List<Layer> netlayers;
	private List<Integer> numneurons;
	
	
	public NeuralNetwork(int numinputs,int numlayers,List<Integer> numneurons) {
		this.numinputs = numinputs;
		this.numlayers = numlayers;
		this.numneurons = new ArrayList<Integer>(numneurons);
		netlayers = new ArrayList<Layer>();
		

	}
	
	public static void main(String[] args) throws IOException{
		
		List<Double> series = DataParser.parseMoodySeries("C:\\workspace\\PSONet\\moodydata\\n1.txt");
		List<Integer> netlayers = new ArrayList<Integer>() {{ add(1);}};
		NeuralNetwork net = new NeuralNetwork(5,1,netlayers);
		net.createNetwork();
		List<Double> input = new ArrayList<Double>() {{add(1.0);add(2.0);add(3.0);add(4.0);add(5.0);}};
		List<Double> output = net.input(input);
		
	}
	
	public void createNetwork(){
		Layer inputlayer = new Layer(numneurons.get(0), numinputs);
		netlayers.add(inputlayer);
		for(int i = 1; i < numlayers; i++) {
			Layer layer = new Layer(numneurons.get(i),numneurons.get(i-1));
			netlayers.add(layer);
		
		
			
		}
		
	}
	
	
	
	public List<Double> getWeights(){
		List<Double> weights = new ArrayList<Double>();
		for(int i = 0; i < netlayers.size(); i++) {
			for(int j = 0; j < netlayers.get(i).numneurons; j++) {
				for(int k = 0; k < netlayers.get(i).layerneurons.get(j).weights.size(); k++) {
					weights.add(netlayers.get(i).layerneurons.get(j).weights.get(k));
				}
			}
		}
		return weights;
	};
	
	
	public int getNumberOfWeights(){
		int weighttotal = 0;
		for(int i = 0; i < netlayers.size(); i++) {
			for(int j = 0; j < netlayers.get(i).numneurons; j++) {
				weighttotal += netlayers.get(i).layerneurons.get(j).weights.size();
			}
		}
		return weighttotal;
	};
	
	
	public void putWeights(List<Double> newweights){
		if(newweights.size() != getWeights().size()) {System.out.println("Incorrect number of weights applied to network!");}
		int weightcounter = 0;
		for(int i = 0; i < netlayers.size(); i++) { //iterate through each layer in the net
			for(int j = 0; j < netlayers.get(i).numneurons; j++) { //iterate through each neuron in the current layer
				for(int k = 0; k < netlayers.get(i).layerneurons.get(j).weights.size(); k++) { //iterate through and change each weight for the current neuron
					netlayers.get(i).layerneurons.get(j).weights.remove(k);
					netlayers.get(i).layerneurons.get(j).weights.add(k,newweights.get(weightcounter));
					weightcounter++;
				}
			}
		}
	};
	
	
	
	public List<Double> input(List<Double> inputs){
		List<Double> outputs = new ArrayList<Double>();
		
		if(inputs.size() != numinputs) { //check for correct number of inputs
			System.out.println("Incorrect number of inputs!");
			return outputs;
			
		}
		
		for(int i = 0; i < numlayers; i++) { //for every layer
			int weightnumber = 0;
			if( i > 0) { //every layer except input layer receives inputs from the previous layers outputs
				inputs = new ArrayList<Double>(outputs);
				outputs.clear();
				
			}
			for(int j=0; j < netlayers.get(i).numneurons; j++) { //for every neuron j in layer i
				double weightedsuminput = 0;
				for(int k=0; k < netlayers.get(i).layerneurons.get(j).numinputs+1; k++) { //for every input k to neuron j in layer i
					if(k == 0) { //add bias to weighted sum
						weightedsuminput += -1 * netlayers.get(i).layerneurons.get(j).weights.get(k);
						
					}
					else {
					weightedsuminput += netlayers.get(i).layerneurons.get(j).weights.get(k) * inputs.get(weightnumber);
					weightnumber++;
				}}
				
				outputs.add(activateNeuron(weightedsuminput));
				weightnumber = 0;
			}
			
		}
		
		return outputs;
	};	
	 
	
	private Double activateNeuron(double weightedsuminput) {
		//return (1/(1 + Math.pow(Math.E,(-1*weightedsuminput)))); //sigmoid activation function
		//System.out.println(weightedsuminput);
		return Math.tanh(weightedsuminput);
	}


	private class Neuron {
		int numinputs;
		List<Double> weights;
		
		
		Neuron(int numinputs) {
			this.numinputs = numinputs;
			this.weights = new ArrayList<Double>();
			
			for(int i=0; i < numinputs+1; i++) { //initialise neuron weights to small random values, with an extra input added for the bias
				double random = new Random().nextDouble();
				if(i==0){ //set the first weight to be the bias
					weights.add((double) -1);
				}
				else {
				weights.add(0.1 + random); //weights between 0.1-1.1
			}}
		
		}
	}
	
	private class Layer {
		int numneurons;
		List<Neuron> layerneurons;
		
		Layer(int numneurons, int numinputs) {
			this.numneurons = numneurons;
			layerneurons = new ArrayList<Neuron>();
			 createNeurons(numinputs);
		}
		
		private void createNeurons(int numinputs) {
			for(int i = 0; i < numneurons; i++) {
				Neuron neuron = new Neuron(numinputs);
				layerneurons.add(neuron);
			}
		}
	}

}
