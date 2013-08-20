package search;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import setup.Config;

public class Eval_Averages {
	
	private static String[] queries;
	private static int algo;
	
	public Eval_Averages(String[] queries, int algo){
		this.queries = queries;
		this.algo = algo;
	}
	
	public void calculate(){
		
		
		//calculates averages for one query over all users
		for(String query : queries){
			Double recallVal = 0.0;
			Double recallSum = 0.0;
			Double precisionVal = 0.0;
			Double precisionKVal = 0.0;
			Double precisionSum = 0.0;
			Double precisionKSum = 0.0;
			int lines = 0;
			try {
				BufferedReader reader = new BufferedReader(new FileReader(Config.get().EVAL_DIR + "/" + algo + ".txt"));
				String line = "";
				while(((line=reader.readLine())!=null)){
					String[] values = line.split("\t");
					if(values.length>=5){
						String searchQuery = (String) values[1];
						if(searchQuery.equals(query)){
							recallSum += Double.parseDouble(values[2]);
							System.out.println("recal: " + recallSum);
							precisionSum += Double.parseDouble(values[3]);
							precisionKSum +=  Double.parseDouble(values[4]);
							lines++;
							System.out.println(lines);
						}					
					}

				}
				Double recall = recallSum / lines;
				Double precision = precisionSum/ lines;
				Double precisionK = precisionSum/lines;
				
				BufferedWriter writer = new BufferedWriter(new FileWriter(Config.get().EVAL_DIR + "/" + algo + "_average.txt",true));
				writer.newLine();
				writer.write(query + "\t" + recall + "\t" + precision + "\t" + precisionK);
				writer.close();
				
				
			} catch (FileNotFoundException e) {
				System.out.println("File not found!");
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}

		
	}
	
	

}
