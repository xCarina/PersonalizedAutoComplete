package search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.kernel.EmbeddedReadOnlyGraphDatabase;

import setup.CalcPR;
import setup.Config;

/*
 * calculates new pagerank values of the subgraph obtained by the BFS
 */
public class BFS_newPR{


	public HashMap<Long, Double> getResults(String query,
			HashMap<Long, Double> bfsResults, 
			EmbeddedReadOnlyGraphDatabase db) {
		
		HashMap<Long, Double> results = new HashMap<>();
		
		for(Entry<Long,Double> e : bfsResults.entrySet()){
			long id = e.getKey();
			Node node = db.getNodeById(id);
			if(node.hasProperty("title")){
				String title = (String) node.getProperty("title");
				if(title.startsWith(query)){
					results.put(id, e.getValue());
				}
			}
		}
		
		return results;
		
		
	}
	

}
