package setup;

import java.io.Reader;
import java.util.HashSet;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;

public class CustomTokenAnalyzer extends Analyzer{
	
	  private final Analyzer actual;
	  private final HashSet<String> STOP_WORDS_SET = new HashSet<String>( );
	    
	    public CustomTokenAnalyzer(){
	        actual = new StandardAnalyzer( Version.LUCENE_31,STOP_WORDS_SET );
	    }

	public TokenStream tokenStream(String field, Reader reader) {
		return actual.tokenStream( field, reader );
	}
	
	

}
