package search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.kernel.EmbeddedReadOnlyGraphDatabase;

import setup.Config;

/*
 * calculates new pagerank values of the subgraph obtained by the BFS
 */
public class BFS_newPR implements Search{

	@Override
	public HashMap<Long, Double> getResults(String query,
			ArrayList<Node> startNodes, 
			EmbeddedReadOnlyGraphDatabase db) {
		
		HashMap<Long, Double> pr = new HashMap<>();
		HashMap<Long, Double> results = new HashMap<>();
		
		for(Node node : startNodes){
			for(Relationship rs : node.getRelationships(Direction.OUTGOING)){
				Node rsNode = rs.getEndNode();
				pr.put(rsNode.getId(), 1.0);
				for(Relationship rs2 : rsNode.getRelationships(Direction.OUTGOING)){
					Node rsNode2 = rs2.getEndNode();
					pr.put(rsNode2.getId(), 1.0);
				}
			}
		}
		HashMap<Long, Double> newPageRank = new HashMap<Long, Double>(pr);
		int numberOfIterations = Config.get().NUMBER_OF_ITERATIONS;
		Double alpha = 0.85;
		//PageRank Iteration:
		for(int i= 0; i < numberOfIterations; i++){
			System.out.println("PageRank iteration " + i);
			for(Entry<Long, Double> entry : pr.entrySet()){
				Node node = db.getNodeById(entry.getKey());
				//count number of outgoing links
				int outgoingLinks = 0;
				for (Relationship rel: node.getRelationships(Direction.OUTGOING)){
					Long nodeID = rel.getEndNode().getId();
					if(pr.containsKey(nodeID)){
						outgoingLinks++;
					}

				}
				//get current page rank value and split it over all outgoing links
				Double currentPageRank = entry.getValue();
				if(outgoingLinks != 0){
					Double addition = alpha * currentPageRank / outgoingLinks;
					//add additional page rank value delta in newPageRank
					for(Relationship rel: node.getRelationships(Direction.OUTGOING)){
						Long nodeID = rel.getEndNode().getId();
						if(nodeID != null && pr.containsKey(nodeID)){
							Double current = newPageRank.get(nodeID);
							newPageRank.put(nodeID, current + addition);
						}
					}
				}
				
			}
			pr = newPageRank;
			
			for(Entry<Long, Double> entry : pr.entrySet()){
				Node node = db.getNodeById(entry.getKey());
				if(node.hasProperty("title") && ((String)node.getProperty("title")).startsWith(query)){
					results.put(entry.getKey(), entry.getValue());
				}
			}
			
		}
		return results;
	}

}
