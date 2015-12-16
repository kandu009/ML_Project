package demo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;

public class GetRandomZeroSamples {

	public static void fetchRandomDataFromFile(String inputFile, String outputFile, String randomLineNumsFile) {
		
        try {
    		// get all the required line numbers.
        	BufferedReader randomLineNumsReader = new BufferedReader(new FileReader(randomLineNumsFile));
    		String line = new String();
    		HashSet<Integer> randomLineNums = new HashSet<Integer>();
    		int maxLineNum = 0;
            while((line = randomLineNumsReader.readLine()) != null) {
            	int temp = Integer.parseInt(line.trim());
            	randomLineNums.add(temp);
            	maxLineNum = maxLineNum < temp ? temp : maxLineNum;
            }   
			randomLineNumsReader.close();
			
			// write the specific lines from the required number.
			BufferedReader bufferedReader = new BufferedReader(new FileReader(inputFile));
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputFile));
			int currentLineNum = 1;
			
			while( (currentLineNum <= maxLineNum) && ((line = bufferedReader.readLine()) != null) ) {
				if(randomLineNums.contains(currentLineNum)) {
					bufferedWriter.write(line);
            		bufferedWriter.write("\n");
				}
				++currentLineNum;
            }   
			
            bufferedReader.close();
            bufferedWriter.close();
            			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		
		String zeroUniqLinesFile = new String("/home/ravalikandur/Desktop/ml/project/data/demo/sort.txt");
//		String nonzeroUniqLinesFile = new String("/home/ravalikandur/Desktop/ml/project/data/demo/uniqids/title.txt");
		
		String zeroInputInfoFile = new String("/home/ravalikandur/Desktop/ml/project/data/demo/zero_training.txt");
//		String nonzeroInputInfoFile = new String("/home/ravalikandur/Desktop/ml/project/data/demo/inputs/titleid_tokensid.txt");
		
		String zeroOutputInfoFile = new String("/home/ravalikandur/Desktop/ml/project/data/demo/1lakh_zero_train_java.txt");
//		String nonzeroOutputInfoFile = new String("/home/ravalikandur/Desktop/ml/project/data/demo/outputs/titleid_tokensid.txt");
		
		fetchRandomDataFromFile(zeroInputInfoFile, zeroOutputInfoFile, zeroUniqLinesFile);
//		fetchRandomDataFromFile(nonzeroInputInfoFile, nonzeroOutputInfoFile, nonzeroUniqLinesFile);

	}

}
