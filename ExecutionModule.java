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
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

public class ExecutionModule {

	private static String seedURL;
	private static int crawlDepth;
	private static String indexPath;
	private static String query;
	private static List<CrawledURL> crawledURLs = new ArrayList<CrawledURL>();
	
	public static void main(String[] args) {
		
		Scanner scanner = new Scanner(System.in);
		System.out.println("Please enter the seed URL: " );
		seedURL = scanner.next();
		System.out.println("Please enter the crawl depth: " );
		crawlDepth = scanner.nextInt();
		System.out.println("Please enter the path where index files are to be stored: " );
		indexPath = scanner.next();
		System.out.println("Please enter the search query: " );
		query = scanner.next();
		
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
