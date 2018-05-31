//============================================================================
// Name        : RankModule.java
// Author      : Srilakshmi Sruthi Pasumarthy
// Description : This class illustrates the application of Vector Space Ranking Model.
//============================================================================

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.FSDirectory;

public class RankModule {

	private static int maxHits = 5000;
	
	public void ranking(String indexPath, String query)
	{
	       /*
		* This method is used to apply Vector Space model for ranking the retrieved documents with relevance score.
	    	* documents(the documents that are retrieved based on the search query).
	    	* @Params: 'indexPath' - input of type String - to access the indexed documents
	    	* 'query' - input of type String - search query given by the user
		*/
		String field = "contents";
		int counter = 0;
	
	    final Path docDir = Paths.get(indexPath);
	    if (!Files.isReadable(docDir))
	    {
	        System.out.println("Sorry!This path" +docDir.toAbsolutePath()+ " doesn't exist or might be invalid.Please provide me the path where index file is stored");
	        System.exit(1);
	    }
	    
	    IndexReader reader = null;
		try {
			reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
		
		} catch (IOException e1) 
		{
			e1.printStackTrace();
		}      
	    IndexSearcher searcher = new IndexSearcher(reader);
	    Analyzer analyzer = new StandardAnalyzer();
	   
	    while(true)
	    {
	    	try
	    	{
	    		QueryParser parser = new QueryParser(field, analyzer);
	    		Query userQuery = parser.parse(query);
    			searcher.setSimilarity(new ClassicSimilarity());	

	    		TopDocs hits = searcher.search(userQuery, maxHits);
	    		int totHits = (int)hits.totalHits;
	    		System.out.println("Number of hits: " + totHits);
	    		if(totHits==0)
	    		{
	    			System.out.println("Check whether its is indexed or not as the hits are Zero. If its is indexed then there are no documnets for your query");
	    		}
	    		ScoreDoc[] scoreDocs = hits.scoreDocs;
	    		int numOfDocs = 0;
	    		if(scoreDocs.length > 10)
	    		{
	    			numOfDocs = 10;
	    		}
	    		else
	    		{
	    			numOfDocs = scoreDocs.length;
	    		}
	    		for (int n = 0; n < numOfDocs; n++) {
	    			counter=counter+1;	
	    			float score = scoreDocs[n].score;
	    			Document rankedDoc = searcher.doc(scoreDocs[n].doc);
	    	    	
	    	    	//To display the most relevant documents with Rank, Title, URL, and Relevance Score
	    			System.out.printf("\nRank:%d\t",counter);
	    			System.out.printf("\nTitle: %s\t",rankedDoc.get("title"));
	    			System.out.printf("\nURL: %s\t",rankedDoc.get("url"));
	    			System.out.printf("\nRelevance Score: %4.3f\t",score);
	    		}
	    		break;
	    	}
	    	catch(Exception e){
	    		e.printStackTrace();
	    		break;
	    	}
	    }
	}
}
