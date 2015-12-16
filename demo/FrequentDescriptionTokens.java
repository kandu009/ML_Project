package demo;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class FrequentDescriptionTokens {
	
	public static Integer THRESHOLD_FACTOR = 7;
	public static String inputFile = new String();
	public static String outputFile = new String();
	
	public static void initFrequentTokensFromFile(String inputFile, String outputFile) {
		
		try {
			String line = new String();
			HashMap<String, Integer> freqCount = new HashMap<String, Integer>();
			int maxFrequency = 0;
			BufferedReader bufferedReader = new BufferedReader(new FileReader(inputFile));
			while((line = bufferedReader.readLine()) != null) {
            	String[] tokens = line.split("[\\s]+");
            	if(tokens.length < 2) {
            		continue;
            	}
            	String[] items = tokens[1].trim().split("[|]+");
            	for(String item:items) {
            		int newCount = 1;
            		if(freqCount.containsKey(item)) {
            			newCount = freqCount.get(item)+1;
            		}
        			freqCount.put(item, newCount);
        			maxFrequency = maxFrequency < newCount ? newCount : maxFrequency;
            	}
            }   
			bufferedReader.close();      

			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputFile));
			int threshold = (THRESHOLD_FACTOR*maxFrequency)/100;
            for(String fkey: freqCount.keySet()) {
            	if(freqCount.get(fkey) >= threshold) {
            		bufferedWriter.write(fkey+"|");
            	}
            }
            bufferedWriter.close();
            
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
		
	public static void main(String[] args) {

		if(args.length != 1) {
			System.out.println("Usage: java FrequentDescriptionTokens");
			return;
		}
		
		int type = Integer.parseInt(args[0]);
		switch (type) {
			case 1: {
				inputFile = "/home/ravalikandur/Desktop/ml/project/data/demo/outputs/descriptionid_tokensid.txt";
				outputFile = "/home/ravalikandur/Desktop/ml/project/data/demo/frequencies/descriptionid_tokensid.txt";
				initFrequentTokensFromFile(inputFile, outputFile);
				break;
			} case 2: {
				inputFile = "/home/ravalikandur/Desktop/ml/project/data/demo/outputs/queryid_tokensid.txt";
				outputFile = "/home/ravalikandur/Desktop/ml/project/data/demo/frequencies/queryid_tokensid.txt";
				initFrequentTokensFromFile(inputFile, outputFile);
				break;
			} case 3: {
				inputFile = "/home/ravalikandur/Desktop/ml/project/data/demo/outputs/titleid_tokensid.txt";
				outputFile = "/home/ravalikandur/Desktop/ml/project/data/demo/frequencies/titleid_tokensid.txt";
				initFrequentTokensFromFile(inputFile, outputFile);
				break;
			} case 4: {
				inputFile = "/home/ravalikandur/Desktop/ml/project/data/demo/outputs/purchasedkeywordid_tokensid.txt";
				outputFile = "/home/ravalikandur/Desktop/ml/project/data/demo/frequencies/purchasedkeywordid_tokensid.txt";
				initFrequentTokensFromFile(inputFile, outputFile);
				break;
			} default: {
				break;
			}
		}
				
	}

}
