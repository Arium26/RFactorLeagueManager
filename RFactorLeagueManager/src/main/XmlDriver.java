package main;

import java.util.Iterator;
import java.util.LinkedList;

import pointStandings.RFResultsManager;
import pointStandings.StandingsGenerator;
import utilities.LogFileManager;

/**
  * Runs the XML parser
  * 
  * @author Richard Matthews
  */
public class XmlDriver
{
	public static void main(String[] args)
	{
		//Find file paths
		LinkedList<String> paths = new LinkedList<String>();
//		paths.add("E1-Chatham300(110)SQ.xml");
//		paths.add("E1-Chatham300(110)SR.xml");
		paths.add("Legends-HickorySQ.xml");
		paths.add("Legends-HickorySR.xml");
		paths.add("Legends-ThunderSQ.xml");
		paths.add("Legends-ThunderSR.xml");
		String newLine = "----------------------------------------"+
				System.getProperty("line.separator");
		RFResultsManager manager = RFResultsManager.getManager();
		StandingsGenerator standings = new StandingsGenerator();
		
		//Create results based on file paths
		Iterator<String> iterator = paths.iterator();
		while(iterator.hasNext())
		{
			LogFileManager.replayPrint(newLine);
			String path = iterator.next();
			manager.readResults(path);
		}
		
		//Compile results
		LogFileManager.replayPrint(newLine);
		LogFileManager.replayPrint(standings.produceOverallStandings());
//		LogFileManager.replayPrint(newLine);
//		LogFileManager.replayPrint(standings.produceClassStandings());
		//TODO Standings and results
		
		//Shutdown logs
		LogFileManager.shutdown();
	}
}