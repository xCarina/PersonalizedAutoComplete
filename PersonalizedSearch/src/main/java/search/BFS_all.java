package search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.kernel.EmbeddedReadOnlyGraphDatabase;

import setup.CalcPR;

/*
 *  This class calculates the BFS over all startnodes = 20% of a user's interests
 *  Stores all visited nodes with their IDs in an ArrayList
 */

public class BFS_all{
	
	public ArrayList<Long> endNodes;

	public BFS_all(){
		ArrayList<Long> endNodes = new ArrayList<Long>();
	}

	public void getResults(	ArrayList<Node> startNodes) throws NumberFormatException, IOException {
		
		endNodes = new ArrayList<Long>();

		int cnt = 0;
		int cnt2 = 0;
		for(Node node : startNodes){
			for(Relationship rs : node.getRelationships(Direction.OUTGOING)){
				Node rsNode = rs.getEndNode();
				Long id = rsNode.getId();
				endNodes.add(id);
				for(Relationship rs2 : rsNode.getRelationships(Direction.OUTGOING)){
					Node rsNode2 = rs2.getEndNode();
					Long id2 = rsNode2.getId();
					endNodes.add(id2);
				}
			}
		}
		

	}

}
