package start;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import search.breadthFirstSearch;

import UserProfile.UserProfile;

public class startSearch {
	
	public static void execute(){
		
		UserProfile profile = new UserProfile();
		
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader("/home/carina/Workspaces/Data/wiki-weightedInterestsOfPowerUsers.tsv"));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		String line = "";
		
		/* countUsers: to define how many users are going to be used for a search process*/
		int countUsers = 0;

			/* for each line of the file: one user
			 * extract userdata -> get edits to use for personalized search */
			try {
				while(((line=reader.readLine())!=null)&&(countUsers < 3)){
					profile.createUserProfile(line);
					String[] edits = profile.getUserEdits();
					/*
					 * TODO: searchQuery einfügen
					 * String searchQuery = "a";
					 */
					if(edits!=null){
						for(String s : edits){
							/* 
							 * insert BFS over each edit
							 * breadthFirstSearch search = new breadthFirstSearch();
							 * HashMap<String, Integer> results = search.doSearch(1, "searchQuery", s);
							*/ 
							System.out.println("Titel von Edit: " + s);
					}
					}
					countUsers++;
				}	
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			/*
			 * TODO: calculate recall and precision
			 * 		add results to file for each algorithm: line = query /tab username /tab recall /tab precision
			 */
	}
	
	public static void main(String[] args) {
		
		execute();
		
	}
		

				
			
		
	
		

}
