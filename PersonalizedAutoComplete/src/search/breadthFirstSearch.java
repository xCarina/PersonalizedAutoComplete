package search;

import java.util.HashMap;
import java.util.Iterator;

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

import setup.config;

public class breadthFirstSearch {
	
	public HashMap<String, Integer> results;
	
	public void doSearch(int searchID, String queryString, String startNode){
		
		results = new HashMap<String, Integer>();
		
		EmbeddedGraphDatabase graphDB = new EmbeddedGraphDatabase(config.get().DB_PATH);
		
		Sort s = new Sort();
		s.setSort(new SortField("score", SortField.DOUBLE, true));
		
		Index<Node> index = null;
		IndexHits<Node> hits = index.query(new QueryContext(startNode).defaultOperator(Operator.AND).sort(s).top(2));		
			
		if ((index == null) || (hits == null)){
			System.out.println("No results available");
		}
		else{
			switch(searchID){
			case 1: BFS_PageRank(hits, queryString);
					break;
			case 2: BFS_adaptPageRank(hits, queryString);
					break;
			case 3: BFS_recPageRank(hits, queryString);
					break;
			default: System.out.println("Wrong search ID");
					break;
			}
			
		}
		
	}
	
/*---------------------------------------------------------------------------------
 *  BFS with PR
 ---------------------------------------------------------------------------------*/

	public void  BFS_PageRank(IndexHits<Node> hits, String query){

		for(Node n : hits){
			for (Relationship rs:n.getRelationships(Direction.BOTH)){
				Node temp = rs.getOtherNode(n);
				//TODO: Breitensuche Tiefe 1
				if(temp.hasProperty("name")){
					String name = (String) temp.getProperty("name");
					if(name.startsWith(query)){
						Integer pr = (int)(10000.*(Double)temp.getProperty("PR"));
						results.put(name, pr);
					}
				}
				for (Relationship rs2:temp.getRelationships(Direction.BOTH)){
					Node temp2 = rs.getOtherNode(n);
					//TODO: Breitensuche Tiefe 2
					if(temp2.hasProperty("name")){
						String name = (String) temp2.getProperty("name");
						if(name.startsWith(query)){
							Integer pr = (int)(10000.*(Double)temp2.getProperty("PR"));
							results.put(name, pr);
						}
					}
				}
			}
		}
		
	}
/*----------------------------------------------------------------------------------------------
 *  changing the PR while doing the BFS (adaptation to the number of visits, a node gets) 
*----------------------------------------------------------------------------------------------*/
	
	public  void BFS_adaptPageRank(IndexHits<Node> hits, String query){

			for(Node n : hits){
				for (Relationship rs:n.getRelationships(Direction.BOTH)){
					Node temp = rs.getOtherNode(n);
					//TODO: Breitensuche Tiefe 1
					if(temp.hasProperty("name")){
						String name = (String) temp.getProperty("name");
						if(name.startsWith(query)){
							Integer pr1 = (int)(10000.*(Double)temp.getProperty("PR"));
							updateMap((String)temp.getProperty("name"), pr1, results);
						}
					}
					for (Relationship rs2:temp.getRelationships(Direction.BOTH)){
						Node temp2 = rs.getOtherNode(n);
						//TODO: Breitensuche Tiefe 2
						if(temp2.hasProperty("name")){
							String name = (String) temp2.getProperty("name");
							if(name.startsWith(query)){
								Integer pr1 = (int)(10000.*(Double)temp2.getProperty("PR"));
								updateMap((String)temp.getProperty("name"), pr1, results);
							}
						}
					}
				}
			}
			
		}
	
	private void updateMap(String key, Integer value, HashMap<String, Integer> map){
		if (map.containsKey(key)){
			Integer tmp = map.get(key);
			map.put(key, tmp + value);
		}
		else 
			map.put(key, value);
	}
	
/*---------------------------------------------------------------------------------
	 recalculates PR on subgraph existing of results of the BFS
 ---------------------------------------------------------------------------------*/
	
	public void BFS_recPageRank(IndexHits<Node> hits, String query){
	
		BFS_PageRank(hits, query);
		HashMap<String, Integer> newresults = recalculatePR(results);
		
	}
	
	public HashMap<String, Integer> recalculatePR(HashMap<String, Integer> results){
		//TODO: recalculate PR on subgraph
		return results;
	}


}
