package search;

import java.util.ArrayList;
import java.util.HashMap;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.kernel.EmbeddedReadOnlyGraphDatabase;

public class BFS_PR implements Search{

	@Override
	public HashMap<Long, Double> getResults(String query,
			ArrayList<Node> startNodes,
			EmbeddedReadOnlyGraphDatabase db) {
		
		
		HashMap<Long, Double> results = new HashMap<>();
		
		for(Node node : startNodes){
			for(Relationship rs : node.getRelationships(Direction.OUTGOING)){
				Node rsNode = rs.getEndNode();
				if(rsNode.hasProperty("title")){
					String title = (String) rsNode.getProperty("title");
					if(title.startsWith(query)){
						Double pr = (Double) rsNode.getProperty("pageRankValue");
						results.put(rsNode.getId(), pr);
					}
				}
				for(Relationship rs2 : rsNode.getRelationships(Direction.OUTGOING)){
					Node rsNode2 = rs2.getEndNode();
					if(rsNode2.hasProperty("title")){
						String title = (String) rsNode2.getProperty("title");
						if(title.startsWith(query)){
							Double pr = (Double) rsNode2.getProperty("pageRankValue");
							results.put(rsNode2.getId(), pr);
						}
					}
				}
			}
		}
		return results;
	}


}
