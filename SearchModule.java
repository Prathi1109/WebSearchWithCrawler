package com.ir.programming.task2;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

public class SearchModule {

	/*
	 * Author: Srilakshmi Sruthi Pasumarthy
	 * This class is used to apply Searching(Index Search) methodology over the indexed documents. 
	 */
	
	public void search(String indexPath, String userQuery)
	{ /*
	   * This method is used to perform search operation on the 'userQuery' over the indexed documents.
	   * @Params: 'indexPath' - input of type String - path where index documents are to be stored
	   * 'userQuery' - input of type String - search query given by the user
	   */
	    String field = "contents";
	    String queries = null;
	    boolean factor = false;
	    
	    final Path docDir = Paths.get(indexPath);
	    if (!Files.isReadable(docDir)) 
	    {
	        System.out.println("Sorry!This path" +docDir.toAbsolutePath()+ " doesn't exist or might be invalid.Please provide me the path where index file is stored");
	        System.exit(1);
	    }
	    int hitsPerPage = 1000;
	    BufferedReader in = null;
	    //Start of Index Search process
	    IndexReader indexReader = null;
		try 
		{
			indexReader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
			
		} catch (IOException e1) 
		{
			e1.printStackTrace();
		}
	    IndexSearcher indexSearcher = new IndexSearcher(indexReader);
	    Analyzer analyzer = new StandardAnalyzer();
	    
		QueryParser parser = new QueryParser(field, analyzer);
		Query query = null;
		try 
		{
			query = parser.parse(userQuery);
			
		} catch (ParseException e1) 
		{
			e1.printStackTrace();
		}
		System.out.println("Started searching for " + query.toString(field));
		try 
		{	
			doPagingSearch(in, indexSearcher, query, hitsPerPage, factor, queries == null && userQuery == null);
		
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
		try 
		{
			indexReader.close();
			
		} catch (IOException e) 
		{
			e.printStackTrace();
		}

   }
	
   public static void doPagingSearch(BufferedReader in, IndexSearcher indexsearcher, Query query, 
	                                     int hitsPerPage, boolean factor, boolean interactive) throws IOException 
   {
	   /*
	    * This method is used to segregate the retrieved documents into pages for readability.
	    * @Params - 'in' - an instance of BufferedReader, 
	    * 'indexsearcher' - an instance of IndexSearcher, 
	    * 'query' - an instance of Query,
	    * 'hitsPerPage' - input of type Integer - specifies the number of hits per page
	    * 'factor', 'interactive' - inputs of type boolean
	    */
	 
	    TopDocs results = indexsearcher.search(query, 5 * hitsPerPage);
	    ScoreDoc[] hitscounts = results.scoreDocs;
	    
	    int numTotalHits = (int) results.totalHits;
	    System.out.println(numTotalHits + " total matching documents");
	
	    int startNum = 0;
	    int endNum = Math.min(numTotalHits, hitsPerPage);
	        
	    while (true) 
	    {
		      if (endNum > hitscounts.length) 
		      {
			        System.out.println("In this page we have results from 1 - " + hitscounts.length +" out of " + numTotalHits + " total matching documents.");
			        System.out.println("If you are interested for more give 'y' for yes and 'n' for no (y/n) ?");
			        String line = in.readLine();
			        if (line.length() == 0 || line.charAt(0) == 'n') 
			        {
			        	break;
			        }
			
			        hitscounts = indexsearcher.search(query, numTotalHits).scoreDocs;
		 }
	     
	     endNum = Math.min(hitscounts.length, startNum + hitsPerPage);
	      
	      for (int i = startNum; i < endNum; i++) 
	      {
		        if (factor) 
		        {                             
			          System.out.println("doc="+hitscounts[i].doc+" score="+hitscounts[i].score);
			          continue;
		        }       
	      }
	      if (!interactive || endNum == 0) 
	      {
	    	  	break;
	      }
	
	      if (numTotalHits >= endNum) 
	      {
		        boolean quit = false;
		        while (true)
		        {
			          System.out.print("Press ");
			          if (startNum - hitsPerPage >= 0) 
			          {
			        	  System.out.print("(p)revious page, ");  
			          }
			          if (startNum + hitsPerPage < numTotalHits) 
			          {
			        	  System.out.print("(n)ext page, ");
			          }
			          System.out.println("(q)uit or enter number to jump to a page.");
			          
			          String line = in.readLine();
			          if (line.length() == 0 || line.charAt(0)=='q') 
			          {
			        	  quit = true;
			        	  break;
			          }
			          if (line.charAt(0) == 'p') 
			          {
			        	  startNum = Math.max(0, startNum - hitsPerPage);
			        	  break;
			          }
			          else if (line.charAt(0) == 'n') 
			          {
			        	  if (startNum + hitsPerPage < numTotalHits) 
			        	  {
			        		  startNum+=hitsPerPage;
			        	  }
			        	  break;
			          } 
			          else 
			          {
			        	  int page = Integer.parseInt(line);
			        	  if ((page - 1) * hitsPerPage < numTotalHits) 
			        	  {
			        		  startNum = (page - 1) * hitsPerPage;
			        		  break;
			        	  }
			        	  else 
			        	  {
			        		  System.out.println("No such page");
			        	  }
			          	}
		        	}
		        	if (quit) break;
		        	endNum = Math.min(numTotalHits, startNum + hitsPerPage);
	      	}
	    }
	}
}