//============================================================================
// Name        : NormalizationModule.java
// Author      : Srilakshmi Sruthi Pasumarthy
// Description : This class illustrates the implementation of Indexing methodology over all the documents in a document collection.
//============================================================================

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class NormalizationModule {
	
	/*
	 * Author: Srilakshmi Sruthi Pasumarthy
	 * This class illustrates the implementation of URL Normalization
	 */

	private static List<CrawledURL> links;
	
	public static String normalizeURL(String url)
	{
	   /*
		* This method is used to normalize a URL
		* @Params - 'url' - input of type String - specifies the URL which has to be normalized
		*/
		String currentURL = url;
		String domain = "";
		try {
			URL urlObj = new URL(currentURL);
			domain = urlObj.getProtocol() + "://" + urlObj.getHost();
		} 
		catch (MalformedURLException e) {	
			//ignoring exceptions that might arise while trying to retrieve URLs
		}
		
		if(!domain.equals(""))
		{
			if(currentURL.startsWith(domain))
			{
				currentURL = currentURL.substring(domain.length());
				domain.toLowerCase(); //Converting only domain into lowercase
				currentURL = domain + currentURL;
			}
		}
		
		if(currentURL.contains("?"))
		{ //ignoring the characters after '?'
			int index = currentURL.indexOf("?");
			currentURL = currentURL.substring(0, index-1);
		}
		if(currentURL.contains("#"))
		{ //ignoring the characters after '#'
			int index = currentURL.indexOf("#");
			currentURL = currentURL.substring(0, index-1);
		}
		if(!currentURL.endsWith("/"))
		{ //appending '/' to the URL to avoid redundancies
			currentURL = currentURL.concat("/");
		}
		

		return currentURL;
	}
	
	public static boolean validateURL(String url)
	{
	   /*
		* This method is used to validate a URL
		* @Params - 'url' - input of type String - specifies the URL which has to be validated
		*/
        try {
            new URL(url).toURI();
            return true;
        }
        catch (URISyntaxException e) 
        {
            return false;
        }
 
        catch (MalformedURLException e) 
        {
            return false;
        }
        catch(Exception e) 
        {
        	return false;
        }
	}
	
	public static List<CrawledURL> removeDuplicates(List<CrawledURL> listWithDuplicates)
	{
	   /*
		* This method is used to remove duplicate/ redundant values in a ArrayList of objects of type 'CrawledURL'
		* @Params - 'listWithDuplicates' - input of type List<CrawledURL> - specifies the list of objects which has duplicates
		* @Return type - returns a List<CrawledURL> after eliminating redundancies
		*/
		Iterator<CrawledURL> it = listWithDuplicates.iterator();
		List<CrawledURL> optimizedList = new ArrayList<CrawledURL>();
		Map<String, CrawledURL> map = new HashMap<String,CrawledURL>();
		System.out.println("Number of URLs before optimization: "+listWithDuplicates.size());
		while(it.hasNext())
		{
			CrawledURL c = it.next();
			if(!map.containsKey(c.url))
			{
				map.put(c.url, c);
			}
		}
		for(String s : map.keySet())
		{
			if(validateURL(s))
			{
				optimizedList.add(map.get(s));
			}
		} 
		System.out.println("Number of URLs after optimization: "+optimizedList.size());
		
		return optimizedList;
	}

	public List<CrawledURL> normalize(String indexPath, List<CrawledURL> listWithDuplicates) 
	{
	   /*
		* This method is used to normalize the crawled URLs in a ArrayList of objects of type 'CrawledURL'
		* @Params - 'indexPath' - input of type String - specifies the path where index is stored
		* 'listWithDuplicates' - input of type List<CrawledURL> - specifies the list of objects which has duplicates
		* @Return type - returns a List<CrawledURL> after normalizing all the URLs
		*/

		for(CrawledURL c : listWithDuplicates)
		{
			c.url = normalizeURL(c.url);			
		}
		links = removeDuplicates(listWithDuplicates);
		Print(indexPath);
		
		return links;

	}
	
	public static void Print(String indexPath)
	{
	  /*
	   * This method is used to write the crawled URLs in a text file
	   * @Params - 'indexPath' - input type of String - the path where the text file(with crawled URLs) has to be stored
	   */
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(indexPath+"\\pages.txt","UTF-8");
		} catch (FileNotFoundException e) {
	
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
		}
		if(writer != null)
		{
			for(CrawledURL c : links)
			{
				writer.println(c.url+"      Depth: "+c.depth);
			}
			writer.close();
		}
		else
		{
			System.out.println("Error in print writer");
		}
	}

}
