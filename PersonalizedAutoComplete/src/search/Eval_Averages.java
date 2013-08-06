package search;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import setup.Config;

public class Eval_Averages {
	
	private static String query;
	private static int algo;
	public Eval_Averages(String query, int algo){
		this.query = query;
		this.algo = algo;
	}
	
	public void calculate(){
		
		Double recallVal = 0.0;
		Double recallSum = 0.0;
		Double precisionVal = 0.0;
		Double precisionSum = 0.0;
		int lines = 0;
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(Config.get().EVAL_DIR + "/" + algo + "_query_" + query + ".txt"));
			String line = "";
			while(((line=reader.readLine())!=null)){
				String[] values = line.split("\t");
				if(values.length>=3){
					recallVal = Double.parseDouble(values[1]);
					precisionVal = Double.parseDouble(values[2]);
					recallSum += recallVal;
					precisionSum += precisionVal;
					lines++;
				}

			}
			Double recall = recallSum / lines;
			Double precision = precisionSum/ lines;
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(Config.get().EVAL_DIR + "/" + algo + "_average.txt",true));
			writer.newLine();
			writer.write(query + "\t" + recall + "\t" + precision);
			writer.close();
			
			
		} catch (FileNotFoundException e) {
			System.out.println("File not found!");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	

}
