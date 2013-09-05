package UserProfile;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.neo4j.graphdb.Node;
import org.neo4j.helpers.Pair;
import org.neo4j.kernel.EmbeddedReadOnlyGraphDatabase;

import setup.Config;

public class UserProfile {

	private static String userName;
	private static ArrayList<Node> allEdits;
	private static ArrayList<Node> learning;
	private static ArrayList<Node> testing;
	private static HashMap<Long, Double> mapEdits;
	
	public String getName(){
		return userName;
	}
	public ArrayList<Node> getEdits_learning(){
		return learning;
	}
	public ArrayList<Node> getEdits_testing(){
		return testing;
	}	
	public HashMap<Long,Double> getNumberOfEdits(){
		return mapEdits;
	}
	
	public void createUserProfile(String line, EmbeddedReadOnlyGraphDatabase db) {
		
		long time0, time1;
		time0 = System.currentTimeMillis();	
		System.out.println("Starts creating userprofile...");
		
		allEdits = new ArrayList<Node>();
		learning = new ArrayList<Node>();
		testing = new ArrayList<Node>();
		mapEdits = new HashMap<Long, Double>();

		String[] values = line.split("\t");
		userName = values[0];
		int edits = values.length-1;	
		System.out.println("\t Number of edits for this user: " + edits);
		int numberOfLearningData = 0;		
		if(edits != 0){
			//Store all edits as nodes in allEdits
			for(int i= 1; i<=edits; i++){
				String title = getEditTitel(values[i]);
				Node node = getNode(title,db);
				if(node!=null){
					allEdits.add(node);
					
				}

			}
			// 20:80 split to use as test and learning data
			numberOfLearningData = 	(edits * 20) / 100;
		}
		if(Config.get().RANDOM_SPLIT){
			Collections.shuffle(allEdits);
		}
		
		
		/* first 20% of all edits are being used as interests for the personalized search 
		 * (edits are ordered by number of edits => first edits = most interested in 
		 *  Store edits in userEdits for use while searching */
		for(int i = 0; i< numberOfLearningData; i++){
			 learning.add(allEdits.get(i));
			}	
		for(int i = numberOfLearningData; i< edits; i++){
			testing.add(allEdits.get(i));
		}
		time1 = System.currentTimeMillis();
		System.out.println("\t Userprofile complete! (after "+ (time1-time0)/1000 + "sec)");
	}
	
	
	private Node getNode(String title, EmbeddedReadOnlyGraphDatabase db) {
		
		Node n = null;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(Config.get().WIKI_TITLES));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		String line = "";
		boolean found = false;
		try {
			while(!found && (line=reader.readLine())!=null){
				String[] values = line.split("\t");
				if(values.length>=2){
					long id = Integer.parseInt(values[0]);
					String t = values[1];
					if(t.equals(title)){
						found = true;
						n = db.getNodeById(id);
							}
				}
				}

			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return n;
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
