import java.util.ArrayList;
import java.util.List;


public class NetTrainer {
	private static List<List<Double>> inputdata;
	private static List<List<Double>> expectedoutputs;
	private NeuralNetwork net;
	
	public NetTrainer(List<List<Double>> inputdata, List<List<Double>> expectedoutputs, NeuralNetwork net) {
		this.inputdata = inputdata;
		this.expectedoutputs = expectedoutputs;
		this.net = net;
	}
	
	public  Double testWeights(List<Double> weights) {
		List<List<Double>> observedoutputs = new ArrayList<List<Double>>();
		net.putWeights(weights);
		for(int i = 0; i < inputdata.size(); i++) { //loop through every pattern and show it to the net, recording the outputs in an array
			observedoutputs.add(net.input(inputdata.get(i)));		
		}
		
		return calculateError(observedoutputs,expectedoutputs);
			
		
	}
	
	public List<List<Double>> testInputs(List<List<Double>> inputs) {
		List<List<Double>> observedoutputs = new ArrayList<List<Double>>();
		for(int i = 0; i < inputs.size(); i++) {
			observedoutputs.add(net.input(inputs.get(i)));
		}
		return observedoutputs;
	}
	
	public NeuralNetwork getNetwork() {
		return net;
	}
	
	private static Double calculateError(List<List<Double>> observedoutputs, List<List<Double>> expectedoutputs) {
		Double runningtotal = (double) 0;
		Double numpatterns = (double) observedoutputs.size();
		Double numoutputnodes = (double) observedoutputs.get(0).size();
		for(int i = 0; i < observedoutputs.size(); i++) {
			for(int j = 0; j < observedoutputs.get(0).size(); j++) {
				runningtotal += Math.pow((expectedoutputs.get(i).get(j) - observedoutputs.get(i).get(j)),2);
			}
		}
		return ((1/(numpatterns*numoutputnodes)) * runningtotal);
	}

}
