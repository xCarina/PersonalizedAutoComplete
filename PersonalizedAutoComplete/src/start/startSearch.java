package start;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedReadOnlyGraphDatabase;

import search.*;
import setup.Config;

import UserProfile.UserProfile;

public class startSearch {
	
	public static void execute(){
		
		EmbeddedReadOnlyGraphDatabase graphDB = new EmbeddedReadOnlyGraphDatabase(Config.get().DB_PATH);
		
		UserProfile profile = new UserProfile();
		
		String[] queries = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "R", "S", "T", "U", "V", "W", "Z"};
		
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


			/* for each line of the file: one user
			 * extract userdata -> get edits to use for personalized search 
			 * writer stores recall and precision values in a file
			 */
			try {
				while(((line=reader.readLine())!=null)){
					profile.createUserProfile(line, graphDB);
					String name = profile.getName();
					ArrayList<Node> learning = profile.getEdits_learning();
					ArrayList<Node> testing = profile.getEdits_testing();
					HashMap<Long ,Double> results = new HashMap<>();
					
					// loop over all queries:
					if(learning!=null){
						for(String query : queries){
							//simple search without BFS:
							SimpleSearch ssearch = new SimpleSearch();
							results = ssearch.getResults(query, learning, graphDB);
							//all BFS algorithms:
							BFS_all search = new BFS_all();
							search.getResults(query, learning, graphDB);
							Search_TestData tsearch = new Search_TestData();
							HashMap<Long ,Double> relevant = tsearch.getResults(query, testing, graphDB);
							Evaluation eval = new Evaluation();
							eval.calculate(results, relevant, query, 0, name);
							eval.calculate(search.results_noPR, relevant, query, 1, name);
							eval.calculate(search.results_PR, relevant, query, 2, name);
							eval.calculate(search.results_adaptPR, relevant, query, 3, name);
							eval.calculate(search.results_newPR, relevant, query, 4, name);						
					}
						
					}//if(countUsers++ > users) break;
				}

			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e){
				System.out.println("Failed!");
			}
			graphDB.shutdown();
			
	}
	

	public static void main(String[] args) {
		
		execute();
		
	}
		

		

}
