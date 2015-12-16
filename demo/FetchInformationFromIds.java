package demo;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;


public class FetchInformationFromIds {

	public static int MAX_COUNT = 100000;
	
	public static void fetchDataScalable2(String uniqIdsFile, String inputFile, String outputFile, boolean isUserData) {
		
		try {

			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputFile));
			BufferedReader bufferedReader = new BufferedReader(new FileReader(inputFile));
			HashMap<String, String> fileContents = new HashMap<String, String>();

			String line = new String();
			int counter = MAX_COUNT;
			
			while((line = bufferedReader.readLine()) != null) {

				--counter;
				String[] tokens = line.split("[\\s]+");
            	if(isUserData) {
            		fileContents.put(tokens[0].trim(), tokens[1].trim()+" "+tokens[2].trim());
            	} else {
            		fileContents.put(tokens[0].trim(), tokens[1].trim());
            	}

            	if(counter == 0) {
            		counter = MAX_COUNT;
        			Set<String> keys = fileContents.keySet();
        			BufferedReader uniqBufferedReader = new BufferedReader(new FileReader(uniqIdsFile));
        			String uline = new String();
                    while((uline = uniqBufferedReader.readLine()) != null) {
                    	if(keys.contains(uline.trim())) {
                    		bufferedWriter.write(uline+" ");
                    		bufferedWriter.write(fileContents.get(uline.trim()));
                    		bufferedWriter.write("\n");
                    		bufferedWriter.flush();
                    	}
                    }
                    uniqBufferedReader.close();
            		fileContents.clear();
            	}
            	
            }
			
			if(!fileContents.isEmpty()) {
				Set<String> keys = fileContents.keySet();
    			BufferedReader uniqBufferedReader = new BufferedReader(new FileReader(uniqIdsFile));
    			String uline = new String();
                while((uline = uniqBufferedReader.readLine()) != null) {
                	if(keys.contains(uline.trim())) {
                		bufferedWriter.write(uline+" ");
                		bufferedWriter.write(fileContents.get(uline.trim()));
                		bufferedWriter.write("\n");
                		bufferedWriter.flush();
                	}
                }
                uniqBufferedReader.close();
        		fileContents.clear();
			}

			bufferedWriter.close();
			bufferedReader.close();
            
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		// Usage: java FetchInformationFromIds
		String queryUniqIdsFile = new String("/home/ravalikandur/Desktop/ml/project/data/demo/uniqids/query.txt");
		String titleUniqIdsFile = new String("/home/ravalikandur/Desktop/ml/project/data/demo/uniqids/title.txt");
		String userUniqIdsFile = new String("/home/ravalikandur/Desktop/ml/project/data/demo/uniqids/user.txt");
		String keywordUniqIdsFile = new String("/home/ravalikandur/Desktop/ml/project/data/demo/uniqids/keyword.txt");
		String descriptionUniqIdsFile = new String("/home/ravalikandur/Desktop/ml/project/data/demo/uniqids/description.txt");
		
		String queryInputInfoFile = new String("/home/ravalikandur/Desktop/ml/project/data/demo/inputs/queryid_tokensid.txt");
		String titleInputInfoFile = new String("/home/ravalikandur/Desktop/ml/project/data/demo/inputs/titleid_tokensid.txt");
		String userInputInfoFile = new String("/home/ravalikandur/Desktop/ml/project/data/demo/inputs/userid_profile.txt");
		String keywordInputInfoFile = new String("/home/ravalikandur/Desktop/ml/project/data/demo/inputs/purchasedkeywordid_tokensid.txt");
		String descriptionInputInfoFile = new String("/home/ravalikandur/Desktop/ml/project/data/demo/inputs/descriptionid_tokensid.txt");
		
		String queryOutputInfoFile = new String("/home/ravalikandur/Desktop/ml/project/data/demo/outputs/queryid_tokensid.txt");
		String titleOutputInfoFile = new String("/home/ravalikandur/Desktop/ml/project/data/demo/outputs/titleid_tokensid.txt");
		String userOutputInfoFile = new String("/home/ravalikandur/Desktop/ml/project/data/demo/outputs/userid_profile.txt");
		String keywordOutputInfoFile = new String("/home/ravalikandur/Desktop/ml/project/data/demo/outputs/purchasedkeywordid_tokensid.txt");
		String descriptionOutputInfoFile = new String("/home/ravalikandur/Desktop/ml/project/data/demo/outputs/descriptionid_tokensid.txt");
		
		fetchDataScalable2(queryUniqIdsFile, queryInputInfoFile, queryOutputInfoFile, false);
		fetchDataScalable2(titleUniqIdsFile, titleInputInfoFile, titleOutputInfoFile, false);
		fetchDataScalable2(userUniqIdsFile, userInputInfoFile, userOutputInfoFile, true);
		fetchDataScalable2(keywordUniqIdsFile, keywordInputInfoFile, keywordOutputInfoFile, false);
		fetchDataScalable2(descriptionUniqIdsFile, descriptionInputInfoFile, descriptionOutputInfoFile, false);
		
	}

}
