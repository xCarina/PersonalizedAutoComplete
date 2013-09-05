package search;

import java.util.ArrayList;
import java.util.HashMap;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.kernel.EmbeddedReadOnlyGraphDatabase;

/*
 * a BFS that adapts the pagerank value to the number of visits a node gets while doing the search
 */
public class BFS_adaptPR implements Search{

	@Override
	public HashMap<Long, Double> getResults(String query,
			ArrayList<Long> bfsResults,
			EmbeddedReadOnlyGraphDatabase db) {
	
		HashMap<Long, Double> results = new HashMap<>();
		
		for(Long id : bfsResults){
			Node node = db.getNodeById(id);
			if(node.hasProperty("title")){
				String title = (String) node.getProperty("title");
				if(title.startsWith(query)){
					if(node.hasProperty("pageRankValue")){
						Double pr = (Double) node.getProperty("pageRankValue");
						if(results.containsValue(id)){
							Double oldPR = results.get(id);
							results.put(id, oldPR + pr);
						}else{
							results.put(id, pr);
						}
					}
				}
			}
		}
		
		return results;
	}


}
