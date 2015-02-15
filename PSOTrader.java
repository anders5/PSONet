import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//New features:
//-create charts automatically using java library (alternatively create charts manually in excel)
//-change fitnessweight based on current volatility (place less emphasis on historical results in times of high volatility)
//-context switching (switch to different swarm based on current volatility) (possibly use another net to recognise what type of environment the time series is in currently?)
//-periodically re-introducing fresh particles into the swarm and removing worst performing ones
//-run on longer time series (could possibly get real data from reuters eikon?)
//-allow trading based on confidence (factor in the nets confidence in its decision somehow)
//-allow trading of multiple instruments (multiple time series) concurrently, with the net specifying a trading decision and an instrument at each time tick??

public class PSOTrader {
	
	protected static int trainingtime = 2000;
	double trainingprofit = 0;
	double testprofit = 0;
	List<Double> series;
	List<Integer> netlayers;
	NeuralNetwork net;
	TraderParticleSwarm swarm;
	List<Double> profitlist = new ArrayList<Double>();
	
	public static void main(String[] args) throws IOException{
		
	PSOTrader trader = new PSOTrader();	
	trader.setUp();
	double trainingprofit = trader.startTraining(trainingtime);
	
	while(trainingprofit < 0) { //keep restarting until a succesful swarm is found
		trader.setUp();
		trainingprofit = trader.startTraining(trainingtime);
		System.out.println(trainingprofit);
		
	}
	
	double testprofit = trader.startTrading(9995);
	System.out.println(testprofit);
	

	
	
	//TraderRingParticleSwarm swarm = new TraderRingParticleSwarm(20, 6, net, series);

	
	//System.out.println(swarm.getWindow());
	
	
	
	
	
	
	
	//for(int i = 0; i < 100; i++) {
//	while(t < trainingtime) {
//		
//	swarm.moveWindow();
//	double currentaction = swarm.moveTraderSwarm(net).tradingaction;
//	trainingprofit = trainingprofit + swarm.calculateReturn(currentaction, previousaction);
//	previousaction = currentaction;
//	t++;
	
	//System.out.println;
	//System.out.println(swarm.getWindow());
	//System.out.println(swarm.)
	//System.out.println(swarm.getgbest());
	//System.out.println(swarm.getgbestparticle().position);
	//System.out.println(previousaction);
	//
	}
	//profitlist.add(trainingprofit);
	//}
	
	
	//System.out.println(profitlist);
	
	
	
	
	
	
	
	private void setUp() throws IOException {
		trainingprofit = 0;
		series = DataParser.parseMoodySeries("C:\\workspace\\PSONet\\moodydata\\n3.txt");
		netlayers = new ArrayList<Integer>() {{ add(1);}};
		net = new NeuralNetwork(5,1,netlayers);
		net.createNetwork();
		this.swarm = new TraderParticleSwarm(100, 6, net, series);
		swarm.initialiseWindow();
		swarm.setParameters(0.7,2.05,2.05);
		swarm.createSwarm();
		
	}
	
	private double startTraining(int trainingtime) {
		int t = 1;
		double previousaction = swarm.getgbestaction();
		
		while(t < trainingtime) {
			
			swarm.moveWindow();
			double currentaction = swarm.moveTraderSwarm(net).tradingaction;
			trainingprofit = trainingprofit + swarm.calculateReturn(currentaction, previousaction);
			previousaction = currentaction;
			t++;

	}
		return trainingprofit;
	}
	
	private double startTrading(int tradingtime) {
		int t = 2000;
		double previousaction = swarm.getgbestaction();
		
		while(t < tradingtime) {
			swarm.moveWindow();
			double currentaction = swarm.moveTraderSwarm(net).tradingaction;
			testprofit = testprofit + swarm.calculateReturn(currentaction, previousaction);
			previousaction = currentaction;
			t++;
		}
		return testprofit;
	}
	
	
	
}
