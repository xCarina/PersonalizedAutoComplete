package setup;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.EmbeddedGraphDatabase;

public class PageRank {

	private static EmbeddedGraphDatabase graphDB = new EmbeddedGraphDatabase( Config.get().DB_PATH );
	
	public static void calculatePageRank(){
		
		int numberOfIterations = Config.get().NUMBER_OF_ITERATIONS;
		double alpha = 0.85;
		HashMap<Long, Double> pageRank = new HashMap<Long, Double>();
		for (Node n : graphDB.getAllNodes()) {
			pageRank.put(n.getId(), 1.0);
		}
		HashMap<Long, Double> newPageRank = new HashMap<Long, Double>(pageRank);
		
		/*-----------------------------------------------------------------------------------------
		 * PAGE RANK ITERATION:
		 -----------------------------------------------------------------------------------------*/
		
		System.out.println(newPageRank.size() + " nodes to compute");
		long start = System.currentTimeMillis();
		
		for(int i= 0; i < numberOfIterations; i++){
			System.out.println("calculating Page rank iteration numer: " + (i+1));
			int cnt = 0;
			for(Entry<Long, Double> entry : pageRank.entrySet()){
				Node node = graphDB.getNodeById(entry.getKey());
				cnt++;
				if (cnt%50000==0){
					long time = System.currentTimeMillis() - start;
					System.out.println("computed: " + cnt + " nodes\t" + (time / 1000) + " s");	
				}
				//count number of outgoing links
				int outgoingLinks = 0;
				for (Relationship rel: node.getRelationships(Direction.OUTGOING)){
					outgoingLinks++;
				}
				//get current page rank value and split it over all outgoing links
				Double currentPageRank = entry.getValue();
				if(outgoingLinks != 0){
					Double delta = alpha * currentPageRank / outgoingLinks;
					//add additional page rank value delta in newPageRank
					for(Relationship rel: node.getRelationships(Direction.OUTGOING)){
						Long nodeID = rel.getEndNode().getId();
						if(nodeID != null){
							Double current = newPageRank.get(nodeID);
							newPageRank.put(nodeID, current + delta);
						}
					}
				}
				
			}
			pageRank = newPageRank;
		}
		updatePageRankValues(pageRank);
		
		
	}
	
	private static void updatePageRankValues(HashMap<Long, Double> pageRank) {
	
		int count = 0;
		Transaction tx = graphDB.beginTx();
		System.out.println("make PR values persistent ");
		int i = 1;
		try {
			for(Entry<Long, Double> entry : pageRank.entrySet()){
				Node node = graphDB.getNodeById(entry.getKey());
				node.setProperty("pageRankValue", entry.getValue());
				if(count++ > 50000){
					tx.success();
					tx.finish();
					count = 0;
					System.out.println((i * count) + " nodes are now persistent");
					tx = graphDB.beginTx();
				}
			}
		} catch (Exception e) {
			tx.failure();
		} finally{
			tx.finish();
		}
		
		graphDB.shutdown();
		
	}
	
	private static void addPageRankValues(){
		
	
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(Config.get().WIKI_PR));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		String line = "";
		Node node = null;
		Transaction transaction = graphDB.beginTx();
		int count = 0;
		try {
			long time1 = System.currentTimeMillis();
			System.out.println("Starts adding pr values...");
			while((line=reader.readLine())!=null){
				String[] values = line.split("\t");
				if(values.length >= 2){
					long id = Integer.parseInt(values[0]);
					Double pr = Double.parseDouble(values[1]);
					node = graphDB.getNodeById(id);
					node.setProperty("pageRankValue", pr);
				}
				if((count++%100000)==99999) {
					System.out.println(count + " PR Values added [" + (System.currentTimeMillis()-time1)/1000 + "sec]");
					transaction.success();
					transaction.finish();
					transaction = graphDB.beginTx();
				 }
				
			}transaction.success();
		}catch (IOException e) {
			e.printStackTrace();
			transaction.failure();
		}finally {
			transaction.finish();
		}
		graphDB.shutdown();
		
	}
	


	public static void main(String[] args) {
		
		addPageRankValues();

	}

}
