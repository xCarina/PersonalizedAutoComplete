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
public class BFS_newPR implements Search{

	@Override
	public HashMap<Long, Double> getResults(String query,
			ArrayList<Long> bfsResults, 
			EmbeddedReadOnlyGraphDatabase db) {
		
		HashMap<Long, Double> results = new HashMap<>();
		
		CalcPR pr = new CalcPR();
		try {
			results = pr.recalcPR(bfsResults, db);
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}
		
		return results;
		
		
	}

}
