package search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.lucene.queryParser.QueryParser.Operator;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.index.lucene.QueryContext;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import setup.Config;

public class Search {
	
	private static int algoID;
	private static String query;
	private static String[] startNodes;
	
	private static HashMap<Long ,Double> results;
	private static EmbeddedGraphDatabase graphDB = new EmbeddedGraphDatabase( Config.get().DB_PATH );
		
	public Search(int algoID, String query, String[] startNodes){
		this.algoID = algoID;
		this.query = query;
		this.startNodes = startNodes;
		
	}
	public HashMap<Long ,Double> getResults(){
		return results;
	}
	
	public void execute(){
		
		EmbeddedGraphDatabase graphDB = new EmbeddedGraphDatabase(Config.get().DB_PATH);
		
		Sort s = new Sort();
		s.setSort(new SortField("score", SortField.DOUBLE, true));
		
		Index<Node> index = graphDB.index().forNodes(Config.get().INDEX_NAME);
		
		
		//TODO!
		IndexHits<Node> hits = index.query(new QueryContext(startNodes).defaultOperator(Operator.AND).sort(s).top(2));		
			
		if ((index == null) || (hits == null)){
			System.out.println("No results available");
		}
		else{
			switch(algoID){
			case 1: BFS_PageRank(hits);
					break;
			case 2: BFS_adaptPageRank(hits);
					break;
			case 3: BFS_recPageRank(hits);
					break;
			default: System.out.println("Wrong search ID");
					break;
			}
			
		}
		graphDB.shutdown();
		
	}
	
/*---------------------------------------------------------------------------------
 *  BFS with PR
 ---------------------------------------------------------------------------------*/

	private void  BFS_PageRank(IndexHits<Node> hits){
		for(Node n : hits){
			for (Relationship rs:n.getRelationships(Direction.BOTH)){
				Node temp = rs.getOtherNode(n);
				//Breitensuche Tiefe 1
				if(temp.hasProperty("title")){
					String title = (String) temp.getProperty("title");
					if(title.startsWith(query)){
						Double pr = (Double)temp.getProperty("PR");
						results.put(temp.getId(), pr);
					}
				}
				for (Relationship rs2:temp.getRelationships(Direction.BOTH)){
					Node temp2 = rs.getOtherNode(n);
					// Breitensuche Tiefe 2
					if(temp2.hasProperty("title")){
						String title = (String) temp2.getProperty("title");
						if(title.startsWith(query)){
							Double pr = (Double)temp2.getProperty("PR");
							results.put(temp2.getId(), pr);
						}
					}
				}
			}
		}
		
	}
/*----------------------------------------------------------------------------------------------
 *  changing the PR while doing the BFS (adaptation to the number of visits, a node gets) 
*----------------------------------------------------------------------------------------------*/
	
	private  void BFS_adaptPageRank(IndexHits<Node> hits){

		
		for(Node n : hits){
			for (Relationship rs:n.getRelationships(Direction.BOTH)){
				Node temp = rs.getOtherNode(n);
				//TODO: BFS 1
				if(temp.hasProperty("title")){
					String title = (String) temp.getProperty("title");
					if(title.startsWith(query)){
						Double pr1 = (Double)temp.getProperty("PR");
						updateMap(temp.getId(), pr1, results);
					}
				}
				for (Relationship rs2:temp.getRelationships(Direction.BOTH)){
					Node temp2 = rs.getOtherNode(n);
					//TODO: BFS 2
					if(temp2.hasProperty("title")){
						String title = (String) temp2.getProperty("title");
						if(title.startsWith(query)){
							Double pr1 = (Double)temp2.getProperty("PR");
							updateMap(temp2.getId(), pr1, results);
						}
					}
				}
			}
		}
			
	}
	
	private void updateMap(Long key, Double value, HashMap<Long, Double> map){
		if (map.containsKey(key)){
			Double tmp = map.get(key);
			map.put(key, tmp + value);
		}
		else 
			map.put(key, value);
	}
	
/*---------------------------------------------------------------------------------
	 recalculates PageRank on results of the BFS
 ---------------------------------------------------------------------------------*/
	
	private void BFS_recPageRank(IndexHits<Node> hits){
	
		/* 
		 * searches for titles beginning with the query string
		 * matching nodes are added to the page rank hashMap, with a value of 1
		 */
		HashMap<Long, Double> pageRank = new HashMap<Long, Double>();
		for(Node n : hits){
			for (Relationship rs:n.getRelationships(Direction.BOTH)){
				Node temp = rs.getOtherNode(n);
				// BFS 1
				if(temp.hasProperty("title")){
					String title = (String) temp.getProperty("title");
					if(title.startsWith(query)){
						pageRank.put(temp.getId(), 1.0);
					}
				}
				for (Relationship rs2:temp.getRelationships(Direction.BOTH)){
					Node temp2 = rs.getOtherNode(n);
					// BFS 2
					if(temp2.hasProperty("title")){
						String title = (String) temp2.getProperty("title");
						if(title.startsWith(query)){
							pageRank.put(temp2.getId(), 1.0);
						}
					}
				}
			}
		}
		HashMap<Long, Double> newPageRank = new HashMap<Long, Double>(pageRank);

		
		int numberOfIterations = Config.get().NUMBER_OF_ITERATIONS;
		Double alpha = 0.85;
		//PageRank Iteration:
		for(int i= 0; i < numberOfIterations; i++){
			
			for(Entry<Long, Double> entry : pageRank.entrySet()){
				Node node = graphDB.getNodeById(entry.getKey());
				//count number of outgoing links
				int outgoingLinks = 0;
				for (Relationship rel: node.getRelationships(Direction.OUTGOING)){
					Long nodeID = rel.getEndNode().getId();
					if(pageRank.containsKey(nodeID)){
						outgoingLinks++;
					}

				}
				//get current page rank value and split it over all outgoing links
				Double currentPageRank = entry.getValue();
				if(outgoingLinks != 0){
					Double delta = alpha * currentPageRank / outgoingLinks;
					//add additional page rank value delta in newPageRank
					for(Relationship rel: node.getRelationships(Direction.OUTGOING)){
						Long nodeID = rel.getEndNode().getId();
						if(nodeID != null && pageRank.containsKey(nodeID)){
							Double current = newPageRank.get(nodeID);
							newPageRank.put(nodeID, current + delta);
						}
					}
				}
				
			}
			pageRank = newPageRank;
		}
		

		
	}

/*---------------------------------------------------------------------------------
* takes the result hashMap and returns a new one with titles instead of IDs:
---------------------------------------------------------------------------------*/

	private HashMap<String, Double> getResultTitles(HashMap<Long, Double> pageRank) {
		
		HashMap<String, Double> resultTitles = new HashMap<String, Double>();
		for(Entry<Long, Double> entry : pageRank.entrySet()){
			Node node = graphDB.getNodeById(entry.getKey());
			String title = (String) node.getProperty("title");
			if(node.hasProperty("title")){
				resultTitles.put(title, entry.getValue());
			}
			
		}
		return resultTitles;
	}

}
