package demo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class ClubZeroNonZeroDataRandomly {

	
	public static void shuffleAndCreateOneFile(String zeroFile, String nonzeroFile, String outputFile) {
		
		try {
			
			BufferedReader zeroReader = new BufferedReader(new FileReader(zeroFile));
			BufferedReader nonzeroReader = new BufferedReader(new FileReader(nonzeroFile));
			BufferedWriter shuffledWriter = new BufferedWriter(new FileWriter(outputFile));
			String line = new String();
			ArrayList<String> contentList = new ArrayList<String>();
			
			while( (line = zeroReader.readLine()) != null ) {
				contentList.add(line);
			}  
			while( (line = nonzeroReader.readLine()) != null ) {
				contentList.add(line);
			}  
			
			Collections.shuffle(contentList);
			for(String s : contentList) {
				shuffledWriter.write(s);
				shuffledWriter.write("\n");
			}
			
			zeroReader.close();
			nonzeroReader.close();
			shuffledWriter.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {

		String zeroFile = new String("/home/ravalikandur/Desktop/ml/project/data/demo/train_data/10lakh_zero_training_data.txt");
		String nonzeroFile = new String("/home/ravalikandur/Desktop/ml/project/data/demo/train_data/10lakh_nonzero_training_data.txt");
		String outputFile = new String("/home/ravalikandur/Desktop/ml/project/data/demo/train_data/10lakh_training_data.txt");
		
		shuffleAndCreateOneFile(zeroFile, nonzeroFile, outputFile);
		
	}

}
