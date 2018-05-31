//============================================================================
// Name        : Crawler.java
// Author      : Srilakshmi Sruthi Pasumarthy
// Description : This class implements crawler functionality. 
//============================================================================

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.SocketTimeoutException;

public class Crawler {

	private static int maxDepth;
	
	private static List<CrawledURL> crawledLinks = new ArrayList<CrawledURL>();
	

	public List<CrawledURL> getCrawledLinks(String seedURL, int crawlDepth)
	{
	 /*
	  * This method retrieves Crawled URLs recursively based on the given seed URL.
	  * @Params: 'seedURL' - input of type String - specifies the seed URL
	  * 'crawlDepth' - input of type Integer - specifies the crawl depth
	  * @Return type: returns an ArrayList of type 'CrawledURL' objects which contains the crawled URL and it's depth
	  */
		maxDepth = crawlDepth;
		if(crawlDepth  >= 0)
		{
			try {
				fetchURLs(seedURL,0);
			} catch (SocketTimeoutException ignore) {
				//ignoring the timeout exception that might arise when there is large amount of data to read
			}	
		}
		
		return crawledLinks;
	}
	
	
	public static void fetchURLs(String url, int depth) throws SocketTimeoutException
	{
		 /*
		  * This method retrieves Crawled URLs recursively based on the seed URL and crawl depth.
		  * @Params: 'url' - input of type String - specifies the URL on which crawler functionality has to be implemented
		  * 'depth' - input of type Integer - specifies the current crawl depth
		  */
		try {
			
			if(depth == maxDepth)
			{
				return;
			}
			
			CrawledURL cURL = new CrawledURL(depth, url);
			crawledLinks.add(cURL);
	        Document doc = null;
	        doc = Jsoup.connect(url).get(); 
	
			if(doc != null)
			{
		        Elements links = doc.select("a[href]");
		        for (Element link : links) {
		        	String nextURL = link.attr("abs:href");
		    		CrawledURL newURL = new CrawledURL(depth+1, nextURL);
		    		crawledLinks.add(newURL);
	
		        	fetchURLs(nextURL, depth+1);
		        	
		        }
			}
		}
		catch(Exception ignore)
		{
			//ignoring the exceptions that might arise while creating a document for parsing
		}
	}
}
