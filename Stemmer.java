//============================================================================
// Name        : Stemmer.java
// Author      : Srilakshmi Sruthi Pasumarthy
// Description : This class illustrates the implementation of Porter Stemmer algorithm.
//============================================================================

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.tartarus.snowball.ext.PorterStemmer;

public class Stemmer {

	private static String stemming(String token)
	{
		/*
		 * This method will return a string output after stemming the input using Porter Stemmer.
		 * The stem() of the org.tartarus.snowball.ext.PorterStemmer class applies the Porter Stemmer functionality
		 * @Params: 'token' an input of type String - term to be stemmed.
		 */
		PorterStemmer ps = new PorterStemmer();
		ps.setCurrent(token);
		ps.stem(); 
		return ps.getCurrent();
	}

	public void stemTokens(String docsPath)
	{
		/*
		 * This method performs tokenization on all the documents present in a collection and stemming is performed on these tokens. 
		 * @Params: 'docsPath' an input of type String - path where the files/documents source exists
		 */
		final Path docDir = Paths.get(docsPath);
	    if (!Files.isReadable(docDir)) { //To validate the path of the document collection
	      System.out.println("This path " +docDir.toAbsolutePath()+ " doesn't exist.");
	      System.exit(1);
	    }
		File folder = new File(docsPath);
		File[] listOfFiles = folder.listFiles();
		List<String> tokens = new ArrayList<String>();
		List<String> result = new ArrayList<String>();
		for (int i = 0; i < listOfFiles.length; i++) {
			File file = listOfFiles[i];
			
			//Retrieve all the files of type .txt and .html
			if (file.isFile() && (file.getName().endsWith(".txt") || file.getName().endsWith(".html"))) { 
				Path filePath = file.toPath();
				byte[] encoded = null;
				try 
				{
					//Reading each files into byte to convert all the bytes into a String
					encoded = Files.readAllBytes(filePath); 
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
				String wordsSet = new String(encoded);
				
				//StandardAnalyser -used to build tokens using one of it's member functions
				Analyzer analyzer = new StandardAnalyzer(); 
				TokenStream stream = analyzer.tokenStream(null, new StringReader(wordsSet));
				try 
				{
					stream.reset();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				try 
				{
					//Iterate over the tokenStream to add each token into a collection (ArrayList<String>(), in this case)
					while(stream.incrementToken()) 
					{
						tokens.add(stream.getAttribute(CharTermAttribute.class).toString());
					}
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
				analyzer.close();
				}
			}
		for(String t: tokens) //Iterate over the token collection to perform stemming on each token
		{
			String stemmedTerm = stemming(t);  
			//Stemmed terms are maintained in another collection(again ArrayList<String>, in this case)
			result.add(stemmedTerm); 
		}
		
		System.out.println("Porter Stemmer algorithm has been applied successfully");
		System.out.println("Size of the stemmed resultset: "+result.size());
	}
	
}
