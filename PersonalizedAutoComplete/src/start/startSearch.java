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
		
		EmbeddedReadOnlyGraphDatabase db = new EmbeddedReadOnlyGraphDatabase(Config.get().DB_PATH);
		
		UserProfile profile = new UserProfile();
		
		
		String[] queries = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
		
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(Config.get().WIKI_USERS));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		String line = "";
		
		// countUsers: to define how many users are going to be used for a search process
		int countUsers = 1;
		//defines how many users are used for search
		int users = Config.get().USER_CNT;


			/* for each line of the file: one user
			 * extract userdata -> get edits to use for personalized search 
			 * writer stores recall and precision values in a file
			 */
			try {
				while(((line=reader.readLine())!=null)){
					System.out.println("######### Starts calculation for user number " + countUsers + " ##########");
					profile.createUserProfile(line, db);
					String name = profile.getName();
					ArrayList<Node> learning = profile.getEdits_learning();
					System.out.println("\t Learning data size: " + learning.size());
					ArrayList<Node> testing = profile.getEdits_testing();
					System.out.println("\t Testing data size: " + testing.size());
					HashMap<Long ,Double> results = new HashMap<>();
					
					if(learning!=null){
						
						BFS_all bfs = new BFS_all();
						ArrayList<Long> bfsResults = new ArrayList<Long>();
						Search search = null;
						Evaluation eval = new Evaluation();
						Long time = System.currentTimeMillis();
						System.out.println("\n \t calculating BFS");
						bfs.getResults(learning);
						bfsResults = bfs.endNodes;
						System.out.println("\t Done with BFS after "+ (System.currentTimeMillis()-time)/1000 +"sec [" + bfsResults.size() + " nodes visited] \n");
						for(String query : queries){
							
							System.out.println("\n \t #### QUERY: " + query + " ####");
							
							//get relevant articles:
							Search_TestData tsearch = new Search_TestData();
							HashMap<Long ,Double> relevant = tsearch.getResults(query, testing, db);
							
							//all BFS algorithms:
							search = new BFS_noPR();
							results = new HashMap<Long,Double>();
							results = search.getResults(query, bfsResults, db);
							System.out.println("\n \t [1] Number of results: " + results.size());
							eval.calculate(results, relevant, query, 1, name);
							System.out.println("\t Done with BFS 1");
							
							search = new BFS_PR();
							results = new HashMap<Long,Double>();
							results = search.getResults(query, bfsResults, db);
							System.out.println("\n \t [2] Number of results: " + results.size());
							eval.calculate(results, relevant, query, 2, name);
							System.out.println(" \tDone with BFS 2");
							
							search = new BFS_adaptPR();
							results = new HashMap<Long,Double>();
							results = search.getResults(query, bfsResults, db);
							System.out.println("\n \t [3] Number of results: " + results.size());
							eval.calculate(results, relevant, query, 3, name);
							System.out.println("\t Done with BFS 3");
							
							search = new BFS_newPR();
							results = new HashMap<Long,Double>();
							results = search.getResults(query, bfsResults, db);
							System.out.println("\n \t [4] Number of results: " + results.size());
							eval.calculate(results, relevant, query, 4, name);
							System.out.println("\t Done with BFS 4");
												
					}
						
					}System.out.println("\n >>>>> Done with calculation for user number " + countUsers++ + "\n");
				}

			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e){
				e.printStackTrace();
				System.out.println("Failed!");
			}
			db.shutdown();
			
	}
	

	public static void main(String[] args) {
		
		execute();
		
	}
		

		

}
