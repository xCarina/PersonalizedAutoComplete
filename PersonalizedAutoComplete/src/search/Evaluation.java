package search;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import setup.Config;

public class Evaluation {
	
	
	public void calculate(HashMap<Long ,Double> results, HashMap<Long ,Double> relevantArticles, String query, int algo, String name){
		
/*----------------------------------------------------------------------------------
 * 	CALCULATE RECALL AND PRECISION:
 ----------------------------------------------------------------------------------*/
		
		Double recall = 0.0;
		Double precision = 0.0;
		
		double relevant = relevantArticles.size();
		double retrieved = results.size();
		
		double relevantRetrieved = 0;
		for(Entry<Long, Double> result : results.entrySet()){
			Long id = result.getKey();
			if(relevantArticles.containsKey(id)){
				relevantRetrieved++;
			}
		}
		if(relevant != 0){
			recall = relevantRetrieved/relevant;
		}
		if(retrieved != 0){
			precision = relevantRetrieved/retrieved;
		}


/*----------------------------------------------------------------------------------
 * 	CALCULATE PRECISION AT K
 ----------------------------------------------------------------------------------*/
		
		//TODO: not working yet, need to order results via PR
		
		double precisionK = 0.0;
		relevantRetrieved = 0.0;
		int k = Config.get().PRECISION_K;
		int cnt = 1;
		
		for(Entry<Long, Double> result : results.entrySet()){
			Long id = result.getKey();
			if(relevantArticles.containsKey(id)){
				relevantRetrieved++;
				precisionK += (relevantRetrieved/cnt);
			}
			if(cnt++ > k) break;
		}
		precisionK = precisionK/relevantRetrieved;
		System.out.println("precision at 10 is " + precisionK);
		
/*----------------------------------------------------------------------------------
 * SAVE RECALL AND PRECISION VALUES IN FILE
 ----------------------------------------------------------------------------------*/
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(Config.get().EVAL_DIR + "/" + algo + ".txt", true));
			writer.newLine();
			writer.write(name + "\t" + query + "\t" + recall + "\t" + precision + "\t" + precisionK);
			writer.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}




}
