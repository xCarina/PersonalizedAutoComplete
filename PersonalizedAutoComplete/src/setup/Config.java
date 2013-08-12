package setup;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Properties;

public class Config extends Properties{

	//pathes to files
	public String DB_PATH; 
	public String WIKI_LINKS;
	public String WIKI_TITLES;
	public String WIKI_USERS;
	//for building the index
	public String INDEX_NAME;
	//for page rank calculation
	public int NUMBER_OF_ITERATIONS;
	//for precision calculation | output directory for values
	public int PRECISION_K;
	public String EVAL_DIR;
	// number of users used for search | search query | id for algorithm being used
	public int USER_CNT;
	public String QUERY;
	public int ALGO_ID;
	
	static Config instance = null;
	
	public Config(){
		// 
		String configFile = "config.txt";
		try {
			BufferedInputStream stream = new BufferedInputStream(
					new FileInputStream(configFile));
			this.load(stream);
			stream.close();
			
		} catch (IOException e) {
			System.out.println("file not found");
		}
		try {
			this.initialize();
		} catch (IllegalArgumentException e) {
			System.out.println("wrong argument in the config file");
			System.exit(1);
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
;	}
	private void initialize() throws IllegalArgumentException, IllegalAccessException {
		Field[] fields = this.getClass().getFields();
		for (Field f : fields) {
			if (this.getProperty(f.getName()) == null) {
				System.err.print("Property '" + f.getName()	+ "' missing in config file");
			}
			if (f.getType().equals(String.class)) {
				f.set(this, this.getProperty(f.getName()));
			}else if (f.getType().equals(int.class)) {
				f.setInt(this, Integer.valueOf(this.getProperty(f.getName())));
			}
			
		}
		
	}
	public static Config get() {
		if (instance == null) {
			instance = new Config();
		}
		return instance;
	}
}
