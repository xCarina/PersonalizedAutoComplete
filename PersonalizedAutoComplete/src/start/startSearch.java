package start;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import search.Eval_Averages;
import search.Evaluation;
import search.Search;
import setup.Config;

import UserProfile.UserProfile;

public class startSearch {
	
	public static void execute(){
		
		UserProfile profile = new UserProfile();
		
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(Config.get().WIKI_USERS));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		String line = "";
		
		// countUsers: to define how many users are going to be used for a search process
		int countUsers = 0;
		//defines how many users are used for search
		int users = Config.get().USER_CNT;
		// search query
		String query =Config.get().QUERY;
		// ID of algorithm being used
		int algo = Config.get().ALGO_ID;
		//output directory for evaluation values
		String outputPath = Config.get().EVAL_DIR;

			/* for each line of the file: one user
			 * extract userdata -> get edits to use for personalized search 
			 * writer stores recall and precision values in a file called query_QUERY.txt, one line for earch user
			 */
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath + "/" + algo + "_query_" + query + ".txt"));
				while(((line=reader.readLine())!=null)&&(countUsers < users)){
					profile.createUserProfile(line);
					String[] learning = profile.getEdits_learning();
					String[] testing = profile.getEdits_testing();
					if(learning!=null){
						Search search = new Search(algo, query, learning);
						search.execute();
						HashMap<Long ,Double> results = search.getResults();
						//TODO: get testData for query from 'testing' 
						HashMap<Long ,Double> testData = null;
						//calculate precision and recall
						Evaluation eval = new Evaluation(results, testData, query, algo, profile.getName());
						eval.calculate();
						
					}countUsers++;
				}
				writer.close();
				// calculates the average precision and recall for one query over all users
				Eval_Averages eval_a = new Eval_Averages(query,algo);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
	}
	
	public static void main(String[] args) {
		
		execute();
		
	}
		

		

}
