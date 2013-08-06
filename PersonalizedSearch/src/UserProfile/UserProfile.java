package UserProfile;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class UserProfile {

	private static String userName;
	private static String[] edits_learning;
	private static String[] edits_testing;
	
	public String[] getEdits_learning(){
		return edits_learning;
	}
	public String[] getEdits_testing(){
		return edits_testing;
	}	
	
	public void createUserProfile(String line) {
		String[] values = line.split("\t");
		userName = values[0];
		int edits = values.length-1;
		int numberOfTestData = 0;
		int numberOfLearningData = 0;
		if(edits != 0){
			// 20:80 split to use as test and learning data
			numberOfTestData = 	(edits * 20) / 100;
			numberOfLearningData = edits -numberOfTestData;
		}
		/* first 20% of all edits are being used as interests for the personalized search 
		 * (edits are ordered by number of edits => first edits = most interested in 
		 *  Store edits in userEdits for use while searching */
		for(int i = 0; i< numberOfTestData; i++){
			String id = getEditTitel(values[i+1]);
			edits_learning[i] = id;
		}
		for(int i = numberOfTestData; i< numberOfLearningData; i++){
			String id = getEditTitel(values[i+1]);
			edits_testing[i] = id;
		}
	}
	
	/*
	 *  edits are given in the format: {article_title,count}
	 *  this function separates title and count and gives back only the title
	 */
	public String getEditTitel(String string){
		String[] data = string.split(",");
		return data[0].substring(1);
	}
	










}
