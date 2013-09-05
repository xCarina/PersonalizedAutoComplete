package search;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
		
		ArrayList<Long> topResults = getTopKResults(results, k);
		
		int cnt = 1;
		
		for(Long id : topResults){
			if(relevantArticles.containsKey(id)){
				relevantRetrieved++;
				precisionK += (relevantRetrieved/cnt);
			}
			cnt++;
		}if(relevantRetrieved != 0){
			precisionK = precisionK/relevantRetrieved;
		}
		
/*----------------------------------------------------------------------------------
 * SAVE RECALL AND PRECISION VALUES IN FILE
 ----------------------------------------------------------------------------------*/
		try {
			long time = System.currentTimeMillis();
			Timestamp ts = new Timestamp(time);
			BufferedWriter writer = new BufferedWriter(new FileWriter(Config.get().EVAL_DIR + "/" + algo + ".txt", true));
			writer.newLine();
			writer.write(name + "\t" + query + "\t" + recall + "\t" + precision + "\t" + precisionK + "\t" + ts.toString());
			writer.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private static ArrayList<Long> getTopKResults(HashMap<Long, Double> unsortMap, int k)
    {
		ArrayList<Long> result = new ArrayList<Long>();
		ArrayList<Entry<Long, Double>> list = new ArrayList<Entry<Long, Double>>(unsortMap.entrySet());
		Collections.sort(list, new Comparator<Entry<Long, Double>>(){

			@Override
			public int compare(Entry<Long, Double> e1,
					Entry<Long, Double> e2) {
				return e2.getValue().compareTo(e1.getValue());
			}});
		int cnt = 0;
		for(Entry<Long, Double> e : list){
			result.add(e.getKey());
			if (++cnt >= k) break;
		}
		
		return result;
		
    }


}
