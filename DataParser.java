import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class DataParser {
	
	
	
	
	public static List<Double> parseMoodySeries(String filename) throws IOException {
		List<Double> data = new ArrayList<Double>();
		
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line;
		while((line = br.readLine()) != null) {
			int dotindex = line.indexOf(".");
			int x = dotindex - 1;
			while(Character.isDigit(line.charAt(x))) { x--; }
			data.add(Double.parseDouble(line.substring(x+1,line.length())));	
		}
		return data;
		
		
	}

}
