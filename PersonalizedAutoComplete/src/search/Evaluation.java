package search;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import setup.Config;

public class Evaluation {
	
	private static String query;
	private static int algo;
	private static String name;
	private static HashMap<Long ,Double> results;
	private static HashMap<Long ,Double> relevantArticles;
	
	public Evaluation(HashMap<Long ,Double> results, HashMap<Long ,Double> relevantArticles, String query, int algo, String name){
		this.relevantArticles = relevantArticles;
		this.results = results;
		this.query = query;
		this.algo = algo;
		this.name = name;
	}
	
	public void calculate(){
		
/*----------------------------------------------------------------------------------
 * 	CALCULATE RECALL:
 ----------------------------------------------------------------------------------*/
		Double recall = 0.0;
		int relevant = relevantArticles.size();
		int relevantRetrieved = 0;
		for(Entry<Long, Double> result : results.entrySet()){
			Long id = result.getKey();
			if(relevantArticles.containsKey(id)){
				relevantRetrieved++;
			}
		}
		recall = (double) (relevantRetrieved/relevant);

/*----------------------------------------------------------------------------------
 * 	CALCULATE PRECISION AT K
 ----------------------------------------------------------------------------------*/
		
		Double precisionK = 0.0;
		relevantRetrieved = 0;
		int k = Config.get().PRECISION_K;
		int cnt = 1;
		
		for(Entry<Long, Double> result : results.entrySet()){
			Long id = result.getKey();
			if(relevantArticles.containsKey(id)){
				relevant++;
				precisionK += relevantRetrieved/cnt;
			}
			if(cnt++ > k) break;
		}
		precisionK = precisionK/relevantRetrieved;
		
/*----------------------------------------------------------------------------------
 * SAVE RECALL AND PRECISION VALUES IN FILE
 ----------------------------------------------------------------------------------*/
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(Config.get().EVAL_DIR + "/" + algo + "_query_" + query + ".txt", true));
			writer.newLine();
			writer.write(name + "\t" + recall + "\t" + precisionK);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}




}
