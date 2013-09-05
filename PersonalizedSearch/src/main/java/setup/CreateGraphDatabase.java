package setup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.kernel.impl.batchinsert.BatchInserter;
import org.neo4j.kernel.impl.batchinsert.BatchInserterImpl;
import org.neo4j.kernel.impl.util.FileUtils;

@SuppressWarnings("deprecation")
public class CreateGraphDatabase {
	
	//create relationship type
	private static enum RelTypes implements RelationshipType{  LINKS_TO }
	
	
	public static void createDatabase(){
		
/* -----------------------------------------------------------------------------------------------------
 *  CONFIGURATIONS FOR BATCHINSERTER
 *  ----------------------------------------------------------------------------------------------------*/	
		
		String DB_PATH = Config.get().DB_PATH;
		
		Map<String, String> configuration = new HashMap<String, String>();
		configuration.put( "neostore.nodestore.db.mapped_memory", "90M" );
		configuration.put( "neostore.relationshipstore.db.mapped_memory", "2G" );
		configuration.put( "neostore.propertystore.db.mapped_memory", "50M" );
		configuration.put( "neostore.propertystore.db.strings.mapped_memory", "500M" );
		
/* -----------------------------------------------------------------------------------------------------
 *  CLEAR DATABASE
 *  ----------------------------------------------------------------------------------------------------*/		
		
		 try
	        {
	            FileUtils.deleteRecursively( new File( DB_PATH ) );
	        }
	        catch ( IOException e )
	        {
	            throw new RuntimeException( e );
	        }
		 
		
/* -----------------------------------------------------------------------------------------------------
 *  INSERT NODES
 *  ----------------------------------------------------------------------------------------------------*/		
		
			
		Map<String, Object> properties = new HashMap<String, Object>(); 
		 
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(Config.get().WIKI_TITLES));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		String line = "";
		int count = 0;
		long time1 = System.currentTimeMillis();
		
		BatchInserter inserter = new BatchInserterImpl(DB_PATH, configuration);
		try {
			System.out.println("Starts adding nodes...");
			while((line=reader.readLine())!=null){
				String[] values = line.split("\t");
				/* value 1 = Article ID = NOde ID
				 * value 2 = Article title*/
				if(values.length>=2){
					long id = Integer.parseInt(values[0]);
					properties.put("title", values[1]);
					inserter.createNode(id, properties);
				}
				 count++;//if(count > 100)break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		inserter.shutdown();
		long time2 = System.currentTimeMillis();
		long time = (time2 - time1)/1000;
		System.out.println("Added " + count + " nodes to the database. [Time needed: " + time + "sec]");
				
/* -----------------------------------------------------------------------------------------------------
 *  INSERT RELATIONSHIPS
 *  ----------------------------------------------------------------------------------------------------*/		
				

		try {
			reader = new BufferedReader(new FileReader(Config.get().WIKI_LINKS));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}			
		
		
		
//		inserter = new BatchInserterImpl(DB_PATH, configuration);
//		count = 0;
//		time1 = System.currentTimeMillis();
//		try {
//			System.out.println("Starts adding relationships...");
//			while((line=reader.readLine())!=null){
//				String[] values = line.split("\t");
//				/* value 1 = Article ID Node 1
//				 * value 2 = Article ID Node 2 */
//				if(values.length>=2){
//					long id1 = Integer.parseInt(values[0]);
//					long id2 = Integer.parseInt(values[1]);
//					inserter.createRelationship(id1, id2, RelTypes.LINKS_TO, null);
//				}
//				 count++;
//				 if((count%1000000)==0) {
//					 System.out.println(count + " Relationships [" + (System.currentTimeMillis()-time1)/1000 + "sec]");
//					 }
//				//if(count > 1000000)break;
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		inserter.shutdown();
//		time2 = System.currentTimeMillis();
//		time = (time2 - time1)/1000;
//		long min = time / 60;
//		System.out.println("Added " + count + " relationships to the database[Time needed: " + time + "sec= "+ min+ "min]");
		
		EmbeddedGraphDatabase graphDB = new EmbeddedGraphDatabase( DB_PATH );
		Node node1, node2 = null;
		Transaction transaction = graphDB.beginTx();
		try {
			System.out.println("Starts adding relationships...");
			while((line=reader.readLine())!=null){
				String[] values = line.split("\t");
				/* value 1 = Article ID Node 1
				 * value 2 = Article ID Node 2 */
				if(values.length>=2){
					node1 = graphDB.getNodeById(Integer.parseInt(values[0]));
					node2 = graphDB.getNodeById(Integer.parseInt(values[1]));
					node1.createRelationshipTo(node2, RelTypes.LINKS_TO);
				}
				 
				 if((count++%100000)==99999) {
					System.out.println(count + " Relationships [" + (System.currentTimeMillis()-time1)/1000 + "sec]");
					transaction.success();
					transaction.finish();
					transaction = graphDB.beginTx();
				 }
				//if(count > 1000000)break;
			}transaction.success();
		} catch (IOException e) {
			e.printStackTrace();
			transaction.failure();
		}finally {
			transaction.finish();
			System.out.println("Finished adding " + count + " relationships.");
		}
		registerShutdownHook( graphDB );
		
		
		
/* -----------------------------------------------------------------------------------------------------
 *  TEST
 *  ----------------------------------------------------------------------------------------------------*/						
		
//		int counter = 0;
//		EmbeddedGraphDatabase graphDB = new EmbeddedGraphDatabase( DB_PATH );
//		
//		Iterable<Node> nodes = graphDB.getAllNodes();	
//		for(Node n : nodes){
//			if(n.hasProperty("title"))
//			System.out.println(n.getId() + " -> " + n.getProperty("title"));
//			for (Relationship rs:n.getRelationships(Direction.BOTH)){
//				Node temp = rs.getOtherNode(n);
//				if(temp.hasProperty("title"))
//					System.out.println(n.getId() + " Verlinkt zu:  " + temp.getProperty("title"));
//				
//			}
//				
//			counter++;if(counter > 5) break;
//		}
//		registerShutdownHook( graphDB );
		
	}
	
	
	
	private static void registerShutdownHook( final GraphDatabaseService graphDb ){

	    Runtime.getRuntime().addShutdownHook( new Thread()
	    {
	        @Override
	        public void run()
	        {
	            graphDb.shutdown();
	        }
	    } );
	}
	
	public static void main(String[] args) {
		
		createDatabase();

	}

	
	
	
}
