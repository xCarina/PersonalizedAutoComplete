package search;

import java.util.ArrayList;
import java.util.HashMap;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedReadOnlyGraphDatabase;

public class Search_TestData{


	public HashMap<Long, Double> getResults(String query,
			ArrayList<Node> startNodes, EmbeddedReadOnlyGraphDatabase db) {
		
		HashMap<Long, Double> results = new HashMap<>();
		
		for(Node node : startNodes){
			if(node.hasProperty("title")&& ((String)node.getProperty("title")).startsWith(query)){
				results.put(node.getId(), 1.0);
			}
		}
		
		return results;
		
	}

}
