package com.ir.programming.task2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class IndexModule {

	private static String filesPath; 

	/*
	 * Author: Srilakshmi Sruthi Pasumarthy
	 * This class is illustrates the application of Indexing methodology over all the documents in a document collection. 
	 */
   	private static void createFilesFromURLs(String indexPath, List<CrawledURL> urlList) throws FileNotFoundException, IOException, SocketTimeoutException
	{
   	 /*
   	  * This method creates file objects(of the list of Crawled URLs) which can be used for creating indices.
   	  * @Params: 'indexPath' - input of type String - specifies the path where index is stored
   	  * 'urlList' - collection input(ArrayList of type 'CrawledURL') - specifies the list of crawled URLs along with their depths
   	  */
		filesPath = indexPath + "\\Files";
		File dir = new File(filesPath);
		dir.mkdir();
		System.out.println("Parsing the documents...");
		int counter = 0;
		for(CrawledURL c : urlList)
		{
		   counter++;
		   StringBuilder content = new StringBuilder();

		    //Wrapped all the exceptions in one try-catch block
		    try
		    {
			      URL url = new URL(c.url);
			      URLConnection urlConnection = url.openConnection();
			      
			      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			      
			      String line;
			      if(bufferedReader != null)
			      {
			    	  content.append("<url value='"+c.url+"'></url>");
				      while ((line = bufferedReader.readLine()) != null)
				      {
				        content.append(line + "\n");
				      }
				      bufferedReader.close();
	
				      	File f = new File(filesPath+"\\"+Integer.toString(counter)+".txt");
				    	FileWriter fw = null;
				    	fw = new FileWriter(f);
						if(fw != null)
						{
							fw.write(content.toString());
							fw.flush();
							fw.close();
						}
			      }
		    }
			catch(Exception ignore)
			{
			     // Ignoring multiple exceptions that may occur while reading URL's content into file
			}    
		}		
	}

	private static void deleteFiles(File folder)
	{
	 /*
	  * This method retrieves Crawled URLs recursively based on the given seed URL.
	  * @Params: 'folder' - instance of 'File' object - specifies the folder where files of Crawled urls are stored
	  */
	    File[] contents = folder.listFiles();
	    if (contents != null) {
	        for (File f : contents) {
	            f.delete();
	        }
	    }
	    try {
			Files.delete(Paths.get(folder.getPath()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void indexDocsWriter(final IndexWriter indexwriter, Path pathDocDir) throws IOException 
	{
		  /*
		   * This method is used to parse through each document of the collection to index the document
		   * @Params- 'indexwriter' - an instance of 'IndexWriter' 
		   * 'pathDocDir' - an instance of 'Path' object - specifies the folder where files of Crawled urls are stored
		   */
		    if (Files.isDirectory(pathDocDir)) 
		    {
			      Files.walkFileTree(pathDocDir, new SimpleFileVisitor<Path>() 
			      { 
			        
				        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException 
				        {
					          try 
					          {
					            indexDoc(indexwriter, file, attrs.lastModifiedTime().toMillis());
					          } catch (IOException ignore) {
					            // Ignoring the files that can't be read
					          }
					          return FileVisitResult.CONTINUE;
				        }
			      });
		    } 
		    else 
		    {
		    	indexDoc(indexwriter, pathDocDir, Files.getLastModifiedTime(pathDocDir).toMillis());
		    }
	  }

	private static void createIndex(String docsPath, String indexPath) 
	{ 
		  /*
		   * This method is used to create index and store the indexed document in the 'indexPath'
		   * @Params - 'docsPath' - input of type String - specifies the folder where files of Crawled URLs are stored 
		   * 'indexPath' - input of type String - specifies the folder where index files are stored
		   */
		    boolean create = true;
		    
		    final Path docDir = Paths.get(docsPath);
		    if (!Files.isReadable(docDir)) 
		    {
			    System.out.println("This path " +docDir.toAbsolutePath()+ " doesn't exist.");
			    System.exit(1);
		    }
		    
		    Date start = new Date();
		    try 
		    {
		    System.out.println("Indexing is starting " + indexPath );
	
		    Directory direc = FSDirectory.open(Paths.get(indexPath));
		    Analyzer analyzer = new StandardAnalyzer();
		    IndexWriterConfig indexconfig = new IndexWriterConfig(analyzer);
	
		    if (create) 
		    { //To create new index
		        indexconfig.setOpenMode(OpenMode.CREATE);
		    } 
		    else 
		    { //To update existing index
		        indexconfig.setOpenMode(OpenMode.CREATE_OR_APPEND);
		    }
	
		    IndexWriter indexwriter = new IndexWriter(direc, indexconfig);
		    indexDocsWriter(indexwriter, docDir);
	
		    indexwriter.close();
	
		    Date end = new Date();
		    System.out.println("Indexing process is completed in "+(end.getTime() - start.getTime()) + " milliseconds");
	
		    } 
		    catch (IOException e) 
		    {
		       e.printStackTrace();
		    }
	
	}

	private static void indexDoc(IndexWriter indexwriter, Path file, long lastModifiedTime) throws IOException 
	{
	       /*
	    	* This method creates new empty document with fields.
	    	* @Params - 'indexwriter' an instance of 'IndexWriter'
	    	* 'file' -  an instance of 'Path' - path of the file to be indexed
	    	* 'lastModifiedTime' - an input of type Long - specifies latest modified time of the file
	    	*/
		    try (InputStream stream = Files.newInputStream(file)) 
		    {
			      Document doc = new Document();
			      File currentFile = new File(file.toString());
		    	  org.jsoup.nodes.Document parseDoc = Jsoup.parse(currentFile,"UTF-8","");
		    	  String docTitle = parseDoc.title();	
		    	  String docURL = null;
		    	  Elements links = parseDoc.select("url");
		    	  for(Element e: links)
		    	  {
		    		  docURL = e.attr("abs:value");
		    	  }
		    	   
		    	  String fileContent = new String(Files.readAllBytes(file));
			      //adds field: 'path' to the document
			      Field pathField = new StringField("path", file.toString(), Field.Store.YES);
			      doc.add(pathField);	
			      
			      //adds field: 'title' to the document
			      Field titleField = new StringField("title", docTitle, Field.Store.YES);
			      doc.add(titleField);
			      
			      //adds field: 'url' to the document
			      doc.add(new StringField("url", docURL, Field.Store.YES));
			      
			      //adds field: 'modified' to the document
			      doc.add(new LongPoint("modified", lastModifiedTime));
			      
			      //adds field: 'contents' to the document
			      doc.add(new TextField("contents", fileContent, Store.YES));
			      
			      if (indexwriter.getConfig().getOpenMode() == OpenMode.CREATE) 
			      {
			        indexwriter.addDocument(doc);
			      }
			      else 
			      {
			        System.out.println("updating " + file);
			        indexwriter.updateDocument(new Term("path", file.toString()), doc);
			      }
		    }
	}

    public void performIndexingAndStemming(String indexPath, List<CrawledURL> urlList)
    {
       /*
    	* This method implements creation of index and stemming functionality.
    	* @Params: 'indexPath' - input of type String - specifies the path where index is stored
   	    * 'urlList' - collection input(ArrayList of type 'CrawledURL') - specifies the list of crawled URLs 
   	    * along with their depths
    	*/
    	try {
    		createFilesFromURLs(indexPath, urlList);
			createIndex(filesPath, indexPath);
    		Stemmer stemmer = new Stemmer();
    		stemmer.stemTokens(filesPath);
			deleteFiles(new File(filesPath));
		} 
    	catch (Exception ignore) {
			//ignoring the exceptions that might arise while creating/deleting File objects
		}
    }
}
