package demo;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

public class OptimizedPreprocessTrainingData {
	
	public static String AGE_1 = "1,0,0,0,0,0";
	public static String AGE_2 = "0,1,0,0,0,0";
	public static String AGE_3 = "0,0,1,0,0,0";
	public static String AGE_4 = "0,0,0,1,0,0";
	public static String AGE_5 = "0,0,0,0,1,0";
	public static String AGE_6 = "0,0,0,0,0,1";
	
	public static String GENDER_1 = "1,0,0";
	public static String GENDER_2 = "0,1,0";
	public static String GENDER_3 = "0,0,1";
	
	public static Integer QUERY_INFO_INDEX = 7;
	public static Integer KEYWORD_INFO_INDEX = 8;
	public static Integer TITLE_INFO_INDEX = 9;
	public static Integer DESC_INFO_INDEX = 10;
	public static Integer USER_INFO_INDEX = 11;
	
	public static Integer THRESHOLD_FACTOR = 5;
	public static Integer ACTUAL_FEATURES_SIZE = 12;
	
	public static HashSet<String> descriptionTopTokens = new HashSet<String>();
	public static HashSet<String> queryTopTokens = new HashSet<String>();
	public static HashSet<String> titleTopTokens = new HashSet<String>();
	public static HashSet<String> keywordTopTokens = new HashSet<String>();
	
	public static HashMap<String, String[]> descriptionTokens = new HashMap<String, String[]>();
	public static HashMap<String, String[]> queryTokens = new HashMap<String, String[]>();
	public static HashMap<String, String[]> titleTokens = new HashMap<String, String[]>();
	public static HashMap<String, String[]> keywordTokens = new HashMap<String, String[]>();
	public static HashMap<String, Integer[]> userTokens = new HashMap<String, Integer[]>();
	
	public static String descriptionsFile = new String();
	public static String queryFile = new String();
	public static String titleFile = new String();
	public static String keywordFile = new String();
	public static String userFile = new String();
	
	public static String inputTrainFile = new String();
	public static String outTrainFile = new String();
	
	public static void initFrequentTokensFromFile(HashSet<String> topTokens, HashMap<String, String[]> allTokens, String file) {
		
		try {
			String line = new String();
			int maxFrequency = 0;
			HashMap<String, Integer> freqCount = new HashMap<String, Integer>();
			BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
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
            	allTokens.put(tokens[0].trim(), items);
            	
            }   
            bufferedReader.close(); 
            
            int threshold = (THRESHOLD_FACTOR*maxFrequency)/100;
            for(String fkey: freqCount.keySet()) {
            	if(freqCount.get(fkey) >= threshold) {
            		topTokens.add(fkey);
            	}
            }
            
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void init() {
		initFrequentTokensFromFile(descriptionTopTokens, descriptionTokens, descriptionsFile);
		initFrequentTokensFromFile(queryTopTokens, queryTokens, queryFile);
		initFrequentTokensFromFile(titleTopTokens, titleTokens, titleFile);
		initFrequentTokensFromFile(keywordTopTokens, keywordTokens, keywordFile);
		initUserInfo();
	}
	
	public static void initUserInfo() {
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(userFile));
			String line = new String();
            while((line = bufferedReader.readLine()) != null) {
            	String[] tokens = line.split("[\\s]+");
            	if(tokens.length != 3) {
            		continue;
            	}
            	userTokens.put(tokens[0].trim(), new Integer[] {Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2])});
            }
            bufferedReader.close();      
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String getAgeFeatures(Integer age) {
		switch(age) {
			case 1: { return AGE_1; } 
			case 2: { return AGE_2; } 
			case 3: { return AGE_3; } 
			case 4: { return AGE_4; } 
			case 5: { return AGE_5; }
			default: { return AGE_6; }
		}
	}
	
	public static String getGenderFeatures(int gender) {
		switch(gender) {
			case 1: {return GENDER_1; }
			case 2: {return GENDER_2; }
			default: {return GENDER_3; }
		}
	}
	
	public static void preprocessTrainData() {
		try {
			
			boolean isFirstDataSet = true;
			String line = new String();
			StringBuilder header = new StringBuilder();
        	
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outTrainFile));
            BufferedReader bufferedReader = new BufferedReader(new FileReader(inputTrainFile));
        	
            while((line = bufferedReader.readLine()) != null) {
            	
            	String[] tokens = line.split("[,]+");
            	if(tokens.length < ACTUAL_FEATURES_SIZE) {
            		continue;
            	}
            	
            	Integer[] userInfo = userTokens.get(tokens[USER_INFO_INDEX].trim());
            	if(userInfo == null || userInfo.length != 2) {
            		userInfo = new Integer[2];
            		userInfo[0] = -1;
            		userInfo[1] = -1;
            	}
            	
            	tokens[USER_INFO_INDEX] = tokens[USER_INFO_INDEX]+","+getGenderFeatures(userInfo[0])+","+getAgeFeatures(userInfo[1]);
            	if(isFirstDataSet) header.append("click, impression, displayURL, AdId, AdvertiserId, depth, position, queryId, keywordId, titleId, descriptionId, userId, gender1, gender2, gender0, age1, age2, age3, age4, age5, age0, ");
            	for(int i = 0; i < ACTUAL_FEATURES_SIZE; ++i) {
            		bufferedWriter.write(tokens[i]+",");
            	}
            	
            	String[] currDescToks = descriptionTokens.get(tokens[DESC_INFO_INDEX]);
            	for(String d: descriptionTopTokens) {
            		if(isFirstDataSet) header.append(d).append(", ");
            		boolean found = false;
            		for(int i = 0; i < currDescToks.length; ++i) {
            			if(currDescToks[i].equals(d)) {
            				found=true;
            				break;
            			}
            		}
            		bufferedWriter.write(found ? "1," : "0,");
            	}
            	
            	String[] currQueryToks = queryTokens.get(tokens[QUERY_INFO_INDEX]);
            	for(String d: queryTopTokens) {
            		if(isFirstDataSet) header.append(d).append(", ");
            		boolean found = false;
            		for(int i = 0; i < currQueryToks.length; ++i) {
            			if(currQueryToks[i].equals(d)) {
            				found=true;
            				break;
            			}
            		}
            		bufferedWriter.write(found ? "1," : "0,");
            	}
            	
            	String[] currTitleToks = titleTokens.get(tokens[TITLE_INFO_INDEX]);
            	for(String d: titleTopTokens) {
            		if(isFirstDataSet) header.append(d).append(", ");
            		boolean found = false;
            		for(int i = 0; i < currTitleToks.length; ++i) {
            			if(currTitleToks[i].equals(d)) {
            				found=true;
            				break;
            			}
            		}
            		bufferedWriter.write(found ? "1," : "0,");
            	}
            	
            	String[] currKeywordToks = keywordTokens.get(tokens[KEYWORD_INFO_INDEX]);
            	for(String d: keywordTopTokens) {
            		if(isFirstDataSet) header.append(d).append(", ");
            		boolean found = false;
            		for(int i = 0; i < currKeywordToks.length; ++i) {
            			if(currKeywordToks[i].equals(d)) {
            				found=true;
            				break;
            			}
            		}
            		bufferedWriter.write(found ? "1," : "0,");
            	}
            	
            	if(isFirstDataSet) header.append("descTokensSize").append(",")
            		.append("queryTokensSize").append(",")
            		.append("titleTokensSize").append(",")
            		.append("keywordTokensSize").append(",");

				bufferedWriter.write(new StringBuilder()
						.append(currDescToks.length).append(",")
						.append(currQueryToks.length).append(",")
						.append(currTitleToks.length).append(",")
						.append(currKeywordToks.length).append(",").append("\n").toString());
            	
            	if(isFirstDataSet) {
            		System.out.println(header.toString()+"\n");
            		header.delete(0, header.length());
            		isFirstDataSet = false;
            	}
            	
                bufferedWriter.flush();
            	
            }   
            bufferedReader.close();
            bufferedWriter.close();
            
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		
		// usage: javac PreprocessTrainingData.java; java PreprocessTrainingData
		descriptionsFile = "/home/ravalikandur/Desktop/ml/project/data/demo/outputs/descriptionid_tokensid.txt";
		queryFile = "/home/ravalikandur/Desktop/ml/project/data/demo/outputs/queryid_tokensid.txt";
		titleFile = "/home/ravalikandur/Desktop/ml/project/data/demo/outputs/titleid_tokensid.txt";
		keywordFile = "/home/ravalikandur/Desktop/ml/project/data/demo/outputs/purchasedkeywordid_tokensid.txt";
		userFile = "/home/ravalikandur/Desktop/ml/project/data/demo/outputs/userid_profile.txt";
		
		inputTrainFile = "/home/ravalikandur/Desktop/ml/project/data/demo/train_data/2lakh_training_data.txt";
		outTrainFile = "/home/ravalikandur/Desktop/ml/project/data/demo/train_data/temp.txt";

		init();
		
		preprocessTrainData();
		
		System.out.println(descriptionTopTokens.size()+","+queryTopTokens.size()+","+titleTopTokens.size()+","+keywordTopTokens.size());
		
	}

}
