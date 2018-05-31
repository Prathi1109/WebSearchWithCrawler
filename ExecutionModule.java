package com.ir.programming.task2;

import java.util.List;
import java.util.ArrayList;

public class ExecutionModule {
	
   /*
	* Author: Srilakshmi Sruthi Pasumarthy
	* This class includes the invocation of all the methods to implement Crawler, Normalizing URLs, Indexing crawled pages, 
	* Searching, Stemming and Ranking functionalities over a crawled collection from a Seed URL. The variables/attributes used in the class are-
	* 'seedURL': of type String- the URL which has to be crawled based on the crawl depth;
	* 'crawlDepth' : of type Integer - the maximum depth of pages to be accessed from the seed URL as the base point
	* 'indexPath' : of type String- the path where indexed documents are to be stored;
	* 'query' : of type String- search query;
	* 'crawledURLs' : of type 'CrawledURL' -an ArrayList of custom datatype to access the crawled URLs along with their depths
	*/

	private static String seedURL;
	private static int crawlDepth;
	private static String indexPath;
	private static String query;
	private static List<CrawledURL> crawledURLs = new ArrayList<CrawledURL>();
	
	public static void main(String[] args) {
		
		if(args.length < 4)
		{
			System.out.println("Please check the inputs given. Inputs are to be given in the following order--");
			System.out.println("1.Seed URL; 2.Crawl Depth; 3.Path where index files are to be stored; 4.Query");
		}
		else
		{
			seedURL = args[0];
			crawlDepth = Integer.parseInt(args[1]);
			indexPath = args[2];
			if(args.length == 4)
			{
				query = args[3];
			}
			else if(args.length > 4)
			{
				for(int i=3; i<args.length; i++)
				{
					query += args[i]+" ";
				}
			}

		
			Crawler crawler = new Crawler();
			crawledURLs = crawler.getCrawledLinks(seedURL, crawlDepth);
			System.out.println("Crawling functionality has been applied successfully!");
			NormalizationModule optimizer = new NormalizationModule();
			crawledURLs = optimizer.normalize(indexPath, crawledURLs);
			System.out.println("URL Normalization functionality has been applied successfully!");
			IndexModule indexer = new IndexModule();
			indexer.performIndexingAndStemming(indexPath, crawledURLs);
			SearchModule searchOperation = new SearchModule();
			searchOperation.search(indexPath, query);
			System.out.println("Search Implementation has been applied successfully!");
			RankModule rankOperation = new RankModule();
			rankOperation.ranking(indexPath, query);
		}

	}

}
