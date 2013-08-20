package search;

import java.util.ArrayList;
import java.util.HashMap;
import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedReadOnlyGraphDatabase;

public interface Search {
	
	HashMap<Long ,Double> getResults(String query, ArrayList<Node> startNodes, EmbeddedReadOnlyGraphDatabase db);
	

}
