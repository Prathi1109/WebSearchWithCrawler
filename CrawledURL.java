package com.ir.programming.task2;

public class CrawledURL implements Comparable<CrawledURL>{

   /*
	* Author: Srilakshmi Sruthi Pasumarthy
	* This class is used as a custom data structure to store the crawled url and it's depth. 
	*/
	public int depth;
	public String url;
	
	public CrawledURL(int depth, String url) {
		super();
		this.depth = depth;
		this.url = url;
		
	}
	
	public int getDepth()
	{
		return depth;
	}
	
	@Override
	public int compareTo(CrawledURL compareObj) {
		
		int compareDepth = ((CrawledURL) compareObj).getDepth();
		
		return this.depth - compareDepth;
	}
	
}
