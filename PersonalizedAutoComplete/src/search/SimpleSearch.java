package search;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedReadOnlyGraphDatabase;

import setup.Config;

public class SimpleSearch implements Search{


	public HashMap<Long, Double> getResults(String query,
			ArrayList<Node> startNodes,
			EmbeddedReadOnlyGraphDatabase db) {
		
		HashMap<Long, Double> results = new HashMap<>();
		
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(Config.get().WIKI_TITLES));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		String line = "";
		try {
			while((line=reader.readLine())!=null){
				String[] values = line.split("\t");
				if(values.length>=2){
					long id = Integer.parseInt(values[0]);
					String t = values[1];
					if(t.startsWith(query)){
						Double pr = 0.0;
						if(db.getNodeById(id).hasProperty("pageRankValue")){
							pr = (Double) db.getNodeById(id).getProperty("pageRankValue");
						}
						results.put(id, pr);
							}
				}
				}

			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		return results;
		
	}

}
