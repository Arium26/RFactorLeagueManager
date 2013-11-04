package parser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import utilities.LogFileManager;

/**
  * A parser superclass that defines the
  * basics of a parser class and provides 
  * a few useful functions
  * 
  * @author Richard Matthews
  * @version 1 (5-28-2013)
  */
public abstract class Parser
{
	private String filepath;
	private static String header = "Super Parser";
	
	/**
	  * An enumeration meant to track what 
	  * type of XML file is being read
	  * 
	  * @author Richard Matthews
	  */
	public static enum FileType
	{
		None, Qualifying, Race, Practice, Test, Warmup
	}
	
	/**
	  * Gets the type of a rFactor XML file.  See enum FileType.
	  * @param filePath - Path to the file to check
	  * @return The associated file type (None if not an rFactor XML file)
	  */
	public static FileType getFileType(String filePath)
	{
		FileType type = FileType.None;
		
		if (filePath == null)
		{
			LogFileManager.replayPrint("No file specified."+
					System.getProperty("line.separator"));
			LogFileManager.logPrint(header, "No file specified");
			
			return type;
		}
		
		try
		{
			//Set up reading an XML file
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			InputStream in = new FileInputStream(filePath);
			XMLEventReader xmlReader = inputFactory.createXMLEventReader(in);
			
			//Keep track of tags
			boolean found = false;
			XMLEvent event = xmlReader.nextEvent();
			
			while (xmlReader.hasNext() && !found)
			{
				if(event.isStartElement())
				{
					StartElement startElement = event.asStartElement();
					
					if (startElement.getName().getLocalPart() == "Race")
					{
						found = true;
						type = FileType.Race;
					}
					
					if (startElement.getName().getLocalPart() == "Qualify")
					{
						found = true;
						type = FileType.Qualifying;
					}
					
					if (startElement.getName().getLocalPart() == "Warmup")
					{
						found = true;
						type = FileType.Warmup;
					}
					
					//Not 100% sure how deep this goes, so BSTS
					if (startElement.getName().getLocalPart() == "Practice1" ||
						startElement.getName().getLocalPart() == "Practice2" ||
						startElement.getName().getLocalPart() == "Practice3" ||
						startElement.getName().getLocalPart() == "Practice4")
					{
						found = true;
						type = FileType.Practice;
					}
					
					if (startElement.getName().getLocalPart() == "TestDay")
					{
						found = true;
						type = FileType.Test;
					}
				}
				
				event = xmlReader.nextEvent();
			}
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (XMLStreamException e)
		{
			e.printStackTrace();
		}
		
		return type;
	}
	
	public String getFilePath()	{	return filepath;	}
	public void setFilePath(String path)	{	filepath = path;	}
}