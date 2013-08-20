package search;

import java.util.ArrayList;
import java.util.HashMap;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.kernel.EmbeddedReadOnlyGraphDatabase;

public class BFS_all{
	
	public HashMap<Long, Double> results_noPR;
	public HashMap<Long, Double> results_PR;
	public HashMap<Long, Double> results_newPR;
	public HashMap<Long, Double> results_adaptPR;
	public BFS_all(){
		results_noPR = new HashMap<>();
		results_PR = new HashMap<>();
		results_newPR = new HashMap<>();
		results_adaptPR = new HashMap<>();
	}

	public void getResults(String query,
			ArrayList<Node> startNodes, EmbeddedReadOnlyGraphDatabase db) {

		int cnt = 0;
		for(Node node : startNodes){
			for(Relationship rs : node.getRelationships(Direction.OUTGOING)){
				System.out.println("getting outgoing node number " + cnt++);
				Node rsNode = rs.getEndNode();
				if(rsNode.hasProperty("title")){
					String title = (String) rsNode.getProperty("title");
					Long id = rsNode.getId();
					results_newPR.put(id, 1.0);
					if(title.startsWith(query)){
						results_noPR.put(id, 0.0);
						if(rsNode.hasProperty("pageRankValue")){
							Double pr = ((Double) rsNode.getProperty("pageRankValue"));
							results_PR.put(id, pr);
							Double oldPR = 0.0;
							if(results_adaptPR.containsValue(id)){
								oldPR = results_adaptPR.get(id);
								}
							results_adaptPR.put(id, pr + oldPR);
						}
					}
				} 
				for(Relationship rs2 : rsNode.getRelationships(Direction.OUTGOING)){
					Node rsNode2 = rs2.getEndNode();
					if(rsNode2.hasProperty("title")){
						String title = (String) rsNode2.getProperty("title");
						Long id = rsNode2.getId();
						results_newPR.put(id, 1.0);
						if(title.startsWith(query)){
							results_noPR.put(id, 0.0);
							if(rsNode2.hasProperty("pageRankValue")){
								Double pr = ((Double) rsNode2.getProperty("pageRankValue"));
								results_PR.put(id, pr);
								Double oldPR = 0.0;
								if(results_adaptPR.containsValue(id)){
									oldPR = results_adaptPR.get(id);
									}
								results_adaptPR.put(id, pr + oldPR);
							}
						}
					}
				}
			}
		}
		
		//TODO: calculate new pagerank values with results_newPR
	}

}
