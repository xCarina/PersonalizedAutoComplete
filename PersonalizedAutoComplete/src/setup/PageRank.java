package setup;

import java.util.HashMap;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;

public class PageRank {

	private static final String DB_PATH = "/home/carina/Workspaces/myDatabase";
	
	public static void calculatePageRank(){
		
		EmbeddedGraphDatabase graphDB = new EmbeddedGraphDatabase( DB_PATH );
		HashMap<Long, Double> pageRank = new HashMap<Long, Double>();
		for (Node n : graphDB.getAllNodes()) {
			pageRank.put(n.getId(), 1.0);
		}
		HashMap<Long, Double> newPageRank = new HashMap<Long, Double>(pageRank);		
	}
	
	public static void main(String[] args) {
		
		calculatePageRank();

	}

}
