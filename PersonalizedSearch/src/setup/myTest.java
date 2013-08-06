package setup;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.kernel.impl.batchinsert.BatchInserter;
import org.neo4j.kernel.impl.batchinsert.BatchInserterImpl;
import org.neo4j.kernel.impl.util.FileUtils;
import org.neo4j.unsafe.batchinsert.BatchInserters;

import UserProfile.UserProfile;

public class myTest {
	
	private static final String DB_PATH = "/home/carina/Workspaces/myTestDatabase";

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */

	public static void main(String[] args) throws FileNotFoundException {
		
		
		
		EmbeddedGraphDatabase graphDB = new EmbeddedGraphDatabase( DB_PATH );
		Iterable<Node> nodes = graphDB.getAllNodes();
		IndexManager index = graphDB.index();
		Index<Node> i = index.forNodes("title");

		Transaction transaction = graphDB.beginTx();
		
		for(Node n : nodes){
			if(n.hasProperty("title"))
			i.add(n, "key", n.getProperty("title"));
		}
		IndexHits<Node> hits = i.query("test");
		System.out.println(hits.getSingle().getId());

		registerShutdownHook( graphDB );
		

		
		
//		try{
//			Node n1 = graphDB.createNode();
//			n1.setProperty("title", "Test1");
//			n1 = graphDB.createNode();
//			n1.setProperty("title", "Test2");
//			n1 = graphDB.createNode();
//			n1.setProperty("title", "Test3");
//			
//			Iterable<Node> nodes = graphDB.getAllNodes();	
//			int cnt = 0;
//			for(Node n : nodes){
//				if(n.hasProperty("title"))System.out.println("Knoten: " + n.getProperty("title"));
//			}
//			transaction.success();
//		}catch (Exception e) {
//			transaction.failure();
//		}finally {
//			transaction.finish();
//		}
		
//		Iterable<Node> nodes = graphDB.getAllNodes();	
//		int cnt = 0;
//		for(Node n : nodes){
//			if(n.hasProperty("title"))
//			System.out.println(n.getId() + " -> " + n.getProperty("title"));
//			for (Relationship rs:n.getRelationships(Direction.BOTH)){
//				Node temp = rs.getOtherNode(n);
//				if(temp.hasProperty("title"))
//					System.out.println(n.getId() + " Verlinkt zu:  " + temp.getProperty("title"));
//				
//			}if(cnt++ >3)break;
//		}



		
		
		/*-------------------------*/

		
	}
	
	private static void registerShutdownHook( final GraphDatabaseService graphDb )
	{
	    // Registers a shutdown hook for the Neo4j instance so that it
	    // shuts down nicely when the VM exits (even if you "Ctrl-C" the
	    // running application).
	    Runtime.getRuntime().addShutdownHook( new Thread()
	    {
	        @Override
	        public void run()
	        {
	            graphDb.shutdown();
	        }
	    } );
	}

}
