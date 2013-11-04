package pointStandings;

import java.util.Iterator;
import java.util.LinkedList;

import parser.Parser;
import parser.Parser.FileType;
import parser.RFXMLQualifyingParser;
import parser.RFXMLRaceParser;
import utilities.LogFileManager;

/**
  * Holds the results of all sessions
  * used.  
  * 
  * @author Richard Matthews
  * @version 1 (5-21-2013)
  */
public class RFResultsManager 
{
	private LinkedList<Results> results;
	private LinkedList<String> paths;
	private String header = "ResultsManager";
	private static RFResultsManager manager;
	
	/**
	  * Constructor
	  */
	private RFResultsManager()
	{
		LogFileManager.logPrint(header, "Constructor");
		results = new LinkedList<Results>();
		paths = new LinkedList<String>();
	}
	
	/**
	  * The key to singleton, either produces
	  * the single manager or creates it
	  * @return The only implementation of DriverManager allowed
	  */
	public static RFResultsManager getManager()
	{
		if (manager == null)
			manager = new RFResultsManager();
		
		return manager;
	}
	
	/**
	  * Create a results object from the
	  * file at the path specified.
	  * 
	  * @param path - Path pointing to results
	  * @return The results generated from file at path, or null if failed
	  */
	public Results readResults(String path)
	{
		LogFileManager.logPrint(header, "Reading Results");
		
		Results result = null;
		Parser.FileType type = Parser.getFileType(path);
		
		if (type == FileType.Practice || type == FileType.Test ||
				type == FileType.Warmup)
		{
			System.out.println(type+": Not implemented yet");
		}
		
		if (type == FileType.Qualifying)
		{
			result = new RFXMLQualifyingParser(path).parseFile();
		}
		
		if (type == FileType.Race)
		{
			result = new RFXMLRaceParser(path).parseFile();
		}
		
		if (type == FileType.None)
		{
			System.out.println("Not an XML file");
		}
		
		//Save variables
		if (result != null)
		{
			results.add(result);
			paths.add(path);
		}
		
		return result;
	}
	
	/**
	  * Gets the number of results in the manager
	  * @return The number of results
	  */
	public int getResultsSize()	{	return results.size();	}
	
	/**
	  * Gets an iterator for iterating
	  * through the results
	  * 
	  * @return The iterator in queastion
	  */
	public Iterator<Results> getResultsIterator()
	{
		LogFileManager.logPrint(header, "Path iterator results");
		return new ResultsIterator(results);
	}
	
	/**
	  * Gets an iterator for iterating
	  * through the list of file paths
	  */
	public Iterator<String> getPathIterator()
	{
		LogFileManager.logPrint(header, "Path iterator creation");
		return new PathIterator(paths);
	}
	
		//Iterator
	/**
	  * Gets a list that iterates through
	  * the results in the parent class.
	  * 
	  * @author Richard Matthews
	  */
	private class ResultsIterator implements Iterator<Results>
	{
		private int position;
		private LinkedList<Results> list;
		
		public ResultsIterator(LinkedList<Results> results)
		{
			list = results;
			position = 0;
		}
		
		public boolean hasNext()
		{
			if (position < list.size())
				return true;
			return false;
		}
		
		public Results next()
		{
			Results result = null;
			result = list.get(position++);
			return result;
		}
		
		/**	Empty method as removal is not an option	*/
		public void remove() {	}
	}
	
	/**
	  * Gets a list that iterates through
	  * the file paths in the parent class.
	  * 
	  * @author Richard Matthews
	  */
	private class PathIterator implements Iterator<String>
	{
		private int position;
		private LinkedList<String> list;
		
		public PathIterator(LinkedList<String> paths)
		{
			list = paths;
			position = 0;
		}
		
		public boolean hasNext()
		{
			if (position < list.size())
				return true;
			return false;
		}
		
		public String next()
		{
			String result = null;
			result = list.get(position++);
			return result;
		}
		
		/**	Empty method as removal is not an option	*/
		public void remove() {	}
	}
}