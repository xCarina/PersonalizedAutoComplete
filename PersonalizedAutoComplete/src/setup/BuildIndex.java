package setup;


import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.helpers.collection.MapUtil;
import org.neo4j.kernel.EmbeddedGraphDatabase;

public class BuildIndex {

	private static final String DB_PATH = "/home/carina/Workspaces/myTestDatabase";
	private static final String indexName = "wiki-nodes";
	private static Index<Node> searchIndex;
	
	public Index<Node> buildSearchIndex(){
		
		EmbeddedGraphDatabase graphDB = new EmbeddedGraphDatabase( DB_PATH );
		
/*-----------------------------------------------------------------------------------------------
 * DELETE SEARCH INDEX IF IT ALREADY EXISTS:
 -----------------------------------------------------------------------------------------------*/
		
		if (graphDB.index().existsForNodes(indexName)) {
			System.out.println("Deleting index...");
			Transaction tx = graphDB.beginTx();
			graphDB.index().forNodes(indexName).delete();
			tx.success();
			tx.finish();
		}
		 
 /*-----------------------------------------------------------------------------------------------
  * SET SEARCH INDEX
  -----------------------------------------------------------------------------------------------*/
		
		searchIndex = graphDB.index().forNodes(indexName,MapUtil.stringMap("analyzer", CustomTokenAnalyzer.class.getName())	);
		
 /*-----------------------------------------------------------------------------------------------
  * BUILD SEARCH INDEX
  -----------------------------------------------------------------------------------------------*/
		
		Transaction transaction = graphDB.beginTx();
		try {
			Iterable<Node> nodes = graphDB.getAllNodes();
			int cnt = 0;
			for(Node node: nodes){
				if(node.hasProperty("title") && node.hasProperty("pageRank")){
					searchIndex.add(node, "key", node.getProperty("title"));
					searchIndex.add(node, "score", node.getProperty("pageRank"));
				}
				if(cnt++%10000 == 9999){
					transaction.success();
					transaction.finish();
					transaction = graphDB.beginTx();
				}
					
			}
			transaction.success();
		} catch (Exception e) {
			System.out.println("Failed to build index");
			System.out.println(e.getMessage());
			transaction.failure();
		} finally {
			transaction.finish();
			System.out.println("Finished building index");
		}
		
		
		return searchIndex;
	}
	
	public static void main(String[] args) {
		
	}

}
