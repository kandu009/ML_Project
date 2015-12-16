package demo;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class VarunPreprocessTrainingData {
	
	public static Integer CLICKS_INDEX = 0;
	public static Integer IMPRESSIONS_INDEX = 1;
	public static Integer AD_ID = 3;
	public static Integer ADVERTISER_ID = 4;
	public static Integer DEPTH_ID = 5;
	public static Integer POSITION_ID = 6;
	public static Integer QUERY_INFO_INDEX = 7;
	public static Integer KEYWORD_INFO_INDEX = 8;
	public static Integer TITLE_INFO_INDEX = 9;
	public static Integer DESC_INFO_INDEX = 10;
	public static Integer USER_INFO_INDEX = 11;
	
	public static Integer ACTUAL_FEATURES_SIZE = 12;
	
	public static HashMap<Integer, Integer> userQueryMap = new HashMap<Integer, Integer>();
	public static HashMap<Integer, Integer> userKeywordMap = new HashMap<Integer, Integer>();
	public static HashMap<Integer, Integer> queryAdvertiserMap = new HashMap<Integer, Integer>();
	public static HashMap<Integer, Integer> adIdMap = new HashMap<Integer, Integer>();
	public static HashMap<Integer, Integer> advIdMap = new HashMap<Integer, Integer>();
	
	public static HashMap<Integer, Float> ctrSumAdMap = new HashMap<Integer, Float>();
	public static HashMap<Integer, Float> ctrSumAdvMap = new HashMap<Integer, Float>();
	public static HashMap<Integer, Float> ctrSumDepthMap = new HashMap<Integer, Float>();
	public static HashMap<Integer, Float> ctrSumPositionMap = new HashMap<Integer, Float>();
	public static HashMap<Float, Float> ctrSumRelativePositionMap = new HashMap<Float, Float>();
	
	public static HashMap<Integer, Integer> ctrCountAdMap = new HashMap<Integer, Integer>();
	public static HashMap<Integer, Integer> ctrCountAdvMap = new HashMap<Integer, Integer>();
	public static HashMap<Integer, Integer> ctrCountDepthMap = new HashMap<Integer, Integer>();
	public static HashMap<Integer, Integer> ctrCountPositionMap = new HashMap<Integer, Integer>();
	public static HashMap<Float, Integer> ctrCountRelDepthMap = new HashMap<Float, Integer>();
	
	public static String inputTrainFile = new String();
	public static String outTrainFile = new String();
	
	public static void preprocessTrainData() {
		try {
			
			String line = new String();
            BufferedReader bufferedReader = new BufferedReader(new FileReader(inputTrainFile));
            int key = 0;
            int count = 0;
            float sum = 0;
            float ctr = 0;
            float relativeDepth = 0;
            while((line = bufferedReader.readLine()) != null) {
            	
            	String[] tokens = line.split("[,]+");
            	if(tokens.length < ACTUAL_FEATURES_SIZE) {
            		continue;
            	}
            	ctr = Float.parseFloat(tokens[CLICKS_INDEX])/Float.parseFloat(tokens[IMPRESSIONS_INDEX]);
//            	tokens[CLICKS_INDEX] = new StringBuilder().append(ctr).append(",").append(tokens[CLICKS_INDEX]).toString();
            	
            	// userid_queryid frequency collection
            	key = (tokens[USER_INFO_INDEX]+"_"+tokens[QUERY_INFO_INDEX]).hashCode();
            	count = 1;
            	if(userQueryMap.containsKey(key)) { count = userQueryMap.get(key)+1; } 
            	userQueryMap.put(key, count);
            	
            	// userid_keywordid frequency collection
            	key = (tokens[USER_INFO_INDEX]+"_"+tokens[KEYWORD_INFO_INDEX]).hashCode();
            	count = 1;
            	if(userKeywordMap.containsKey(key)) { count = userKeywordMap.get(key)+1; } 
            	userKeywordMap.put(key, count);
            	
            	// queryid_advid frequency collection
            	key = (tokens[QUERY_INFO_INDEX]+"_"+tokens[ADVERTISER_ID]).hashCode();
            	count = 1;
            	if(queryAdvertiserMap.containsKey(key)) { count = queryAdvertiserMap.get(key)+1; } 
            	queryAdvertiserMap.put(key, count);
            	
            	// adid frequency collection
            	key = tokens[AD_ID].hashCode();
            	count = 1;
            	if(adIdMap.containsKey(key)) { count = adIdMap.get(key)+1; } 
            	adIdMap.put(key, count);
            	// ctr by Adid aggregation
            	count = 1;
            	sum = ctr;
            	if(ctrCountAdMap.containsKey(key)) {
            		count = ctrCountAdMap.get(key)+1;
            		sum = ctrSumAdMap.get(key)+ctr;
            	}
            	ctrCountAdMap.put(key, count);
            	ctrSumAdMap.put(key, sum);
            	
            	// advid frequency collection
            	key = tokens[ADVERTISER_ID].hashCode();
            	count = 1;
            	if(advIdMap.containsKey(key)) { count = advIdMap.get(key)+1; } 
            	advIdMap.put(key, count);
            	// ctr by Advid aggregation
            	count = 1;
            	sum = ctr;
            	if(ctrCountAdvMap.containsKey(key)) {
            		count = ctrCountAdvMap.get(key)+1;
            		sum = ctrSumAdvMap.get(key)+ctr;
            	}
            	ctrCountAdvMap.put(key, count);
            	ctrSumAdvMap.put(key, sum);
            	
            	// ctr by Depth aggregation
            	key = tokens[DEPTH_ID].hashCode();
            	count = 1;
            	sum = ctr;
            	if(ctrCountDepthMap.containsKey(key)) {
            		count = ctrCountDepthMap.get(key)+1;
            		sum = ctrSumDepthMap.get(key)+ctr;
            	}
            	ctrCountDepthMap.put(key, count);
            	ctrSumDepthMap.put(key, sum);
            	
            	// ctr by Position aggregation
            	key = tokens[POSITION_ID].hashCode();
            	count = 1;
            	sum = ctr;
            	if(ctrCountPositionMap.containsKey(key)) {
            		count = ctrCountPositionMap.get(key)+1;
            		sum = ctrSumPositionMap.get(key)+ctr;
            	}
            	ctrCountPositionMap.put(key, count);
            	ctrSumPositionMap.put(key, sum);
            	
            	// ctr by Relative Position aggregation
            	relativeDepth = (Float.parseFloat(tokens[DEPTH_ID])-Float.parseFloat(tokens[POSITION_ID]))/Float.parseFloat(tokens[DEPTH_ID]); 
            	count = 1;
            	sum = ctr;
            	if(ctrCountRelDepthMap.containsKey(relativeDepth)) {
            		count = ctrCountRelDepthMap.get(relativeDepth)+1;
            		sum = ctrSumRelativePositionMap.get(relativeDepth)+ctr;
            	}
            	ctrCountRelDepthMap.put(relativeDepth, count);
            	ctrSumRelativePositionMap.put(relativeDepth, sum);
            	
            }   
            bufferedReader.close();
            
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void preprocessTrainData2() {
		try {
			
			String line = new String();
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outTrainFile));
            BufferedReader bufferedReader = new BufferedReader(new FileReader(inputTrainFile));
            float ctr = 0;
            float relativeDepth = 0;
        	int key = 0;
        	
            while((line = bufferedReader.readLine()) != null) {
            	
            	String[] tokens = line.split("[,]+");
            	if(tokens.length < ACTUAL_FEATURES_SIZE) {
            		continue;
            	}
            	ctr = Float.parseFloat(tokens[CLICKS_INDEX])/Float.parseFloat(tokens[IMPRESSIONS_INDEX]);
            	bufferedWriter.write(ctr+",");
//            	tokens[CLICKS_INDEX] = new StringBuilder().append(ctr).append(",").append(tokens[CLICKS_INDEX]).toString();
            	
            	StringBuilder newFeatures = new StringBuilder(); 
            	relativeDepth = (Float.parseFloat(tokens[DEPTH_ID])-Float.parseFloat(tokens[POSITION_ID]))/Float.parseFloat(tokens[DEPTH_ID]);
            	newFeatures.append(relativeDepth).append(",");
            	
            	// userid_queryid frequency collection
            	key = (tokens[USER_INFO_INDEX]+"_"+tokens[QUERY_INFO_INDEX]).hashCode();
            	newFeatures.append(userQueryMap.get(key)).append(",");
            	
            	// userid_keywordid frequency collection
            	key = (tokens[USER_INFO_INDEX]+"_"+tokens[KEYWORD_INFO_INDEX]).hashCode();
            	newFeatures.append(userKeywordMap.get(key)).append(",");
            	
            	// queryid_advid frequency collection
            	key = (tokens[QUERY_INFO_INDEX]+"_"+tokens[ADVERTISER_ID]).hashCode();
            	newFeatures.append(queryAdvertiserMap.get(key)).append(",");
            	
            	// adid frequency collection
            	key = tokens[AD_ID].hashCode();
            	newFeatures.append(adIdMap.get(key)).append(",");

            	// advid frequency collection
            	key = tokens[ADVERTISER_ID].hashCode();
            	newFeatures.append(advIdMap.get(key)).append(",");
            	
            	// ctr by Adid aggregation
            	key = tokens[AD_ID].hashCode();
            	newFeatures.append((1.0*ctrSumAdMap.get(key))/(1.0*ctrCountAdMap.get(key))).append(",");
            	
            	// ctr by Advid aggregation
            	key = tokens[ADVERTISER_ID].hashCode();
            	newFeatures.append((1.0*ctrSumAdvMap.get(key))/(1.0*ctrCountAdvMap.get(key))).append(",");
            	
            	// ctr by Depth aggregation
            	key = tokens[DEPTH_ID].hashCode();
            	newFeatures.append((1.0*ctrSumDepthMap.get(key))/(1.0*ctrCountDepthMap.get(key))).append(",");
            	
            	// ctr by Position aggregation
            	key = tokens[POSITION_ID].hashCode();
            	newFeatures.append((1.0*ctrSumPositionMap.get(key))/(1.0*ctrCountPositionMap.get(key))).append(",");
            	
            	// ctr by Relative Position aggregation
            	newFeatures.append((1.0*ctrSumRelativePositionMap.get(relativeDepth))/(1.0*ctrCountRelDepthMap.get(relativeDepth)));

            	bufferedWriter.write(line+",");
            	bufferedWriter.write(newFeatures.toString()+"\n");
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
		
		inputTrainFile = "/home/ravalikandur/Desktop/ml/project/data/demo/train_data/2lakh_preprocessed_training_data.txt";
		outTrainFile = "/home/ravalikandur/Desktop/ml/project/data/demo/train_data/varun_2lakh_preprocessed_training_data.txt";
		
		preprocessTrainData();
		preprocessTrainData2();
		
	}

}
