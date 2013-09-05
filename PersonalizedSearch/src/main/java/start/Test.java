package start;

import java.awt.List;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.helpers.Pair;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.kernel.EmbeddedReadOnlyGraphDatabase;

import edu.uci.ics.jung.algorithms.scoring.PageRank;
import edu.uci.ics.jung.graph.DirectedSparseGraph;

import UserProfile.UserProfile;

import search.BFS_all;
import search.BFS_noPR;
import search.Eval_Averages;
import search.Evaluation;
import search.Search;
import search.Search_TestData;
import search.SimpleSearch;
import setup.Config;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		EmbeddedGraphDatabase db = new EmbeddedGraphDatabase(Config.get().DB_PATH);
		
			System.out.println("Test");
		
		
			db.shutdown();

	}
	


}
