package com.ir.programming.task2;

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
	/*
	 * Author: Srilakshmi Sruthi Pasumarthy
	 * This class is used to apply Porter Stemmer algorithm over a document collection. 
	 * The purpose is to stem all the words using this algorithm.
	 */
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
			if (file.isFile() && (file.getName().endsWith(".txt") || file.getName().endsWith(".html"))) { //Retrieve all the files of type .txt and .html
				Path filePath = file.toPath();
				byte[] encoded = null;
				try {
					encoded = Files.readAllBytes(filePath); //Reading each files into byte to convert all the bytes into a String
				} catch (IOException e) {
					e.printStackTrace();
				}
				String wordsSet = new String(encoded);
				Analyzer analyzer = new StandardAnalyzer(); //Used to build tokens using one of it's member functions
				TokenStream stream = analyzer.tokenStream(null, new StringReader(wordsSet));
				try {
					stream.reset();
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					while(stream.incrementToken()) //Iterate over the tokenStream to add each token into a collection (ArrayList<String>(), in this case)
					{
						tokens.add(stream.getAttribute(CharTermAttribute.class).toString());
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				analyzer.close();
				}
			}
		for(String t: tokens) //Iterate over the token collection to perform stemming on each token
		{
			String stemmedTerm = stemming(t);  
			result.add(stemmedTerm); //Stemmed terms are maintained in another collection(again ArrayList<String>, in this case)
		}
		
		System.out.println("Porter Stemmer algorithm has been applied successfully");
		System.out.println("Size of the stemmed resultset: "+result.size());
	}
	
}
