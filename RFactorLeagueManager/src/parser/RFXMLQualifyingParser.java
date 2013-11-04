package parser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringTokenizer;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import pointStandings.Lap;
import pointStandings.RFDriverQ;
import pointStandings.RFDriverR;
import pointStandings.RFQualifyingResults;
import utilities.LogFileManager;


/**
  * Parses an XML file for a qualifying session.
  * 
  * @author Richard Matthews
  */
public class RFXMLQualifyingParser extends Parser
{
	private String header = "RFactor XML Qualifying Parser";
	private final static boolean fullOutput = false;
	
	/**
	  * Constructor for the parser.  
	  * 
	  * @param file - Path to the file in need of parsing
	  */
	public RFXMLQualifyingParser(String file)
	{
		setFilePath(file);
		LogFileManager.logPrint(header, "Constructor");
	}
	
	/**
	  * Parses the qualifying file into the
	  * qualifying results, which are returned.
	  * to the user.
	  * 
	  * @param filepath - Path to a file
	  * @return The results of the parsed file, or
	  *    null if the file path is not valid
	  */
	public RFQualifyingResults parseFile()
	{
		//Handle null file path
		if (getFilePath() == null)
		{
			LogFileManager.replayPrint("No file specified."+
					System.getProperty("line.separator"));
			LogFileManager.logPrint(header, "No file specified");
			
			return null;
		}
		
		LogFileManager.logPrint(header, "Parsing file");
		RFQualifyingResults results = null;
		
		try
		{
			//Set up reading an XML file
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			InputStream in = new FileInputStream(getFilePath());
			XMLEventReader xmlReader = inputFactory.createXMLEventReader(in);

			//Create a new instance of qualifying results after file has
			//		been successfully found
			results = new RFQualifyingResults();
			
			//Read XML File
			while (xmlReader.hasNext())
			{
		        XMLEvent event = xmlReader.nextEvent();
		        
		        //Check start element
		        if (event.isStartElement())
		        {
					StartElement startElement = event.asStartElement();

					// Handle a "DateTime" tag
					if (startElement.getName().getLocalPart() == "DateTime")
					{
						event = xmlReader.nextEvent();
						long id = Long.parseLong(event.asCharacters().getData());
						results.setEventID(id);
//						LogFileManager.replayPrint("Event ID: "+results.getEventID()+
//								System.getProperty("line.separator"));
						xmlReader.nextEvent();//Burn the end element
					}
					
					// Handle a "Mod" tag
					if (startElement.getName().getLocalPart() == "Mod")
					{
						event = xmlReader.nextEvent();
						String mod = event.asCharacters().getData();
							//Parse out the .rfm at the end
						StringTokenizer tokens = new StringTokenizer(mod, ".");
						results.setModName(tokens.nextToken());
						LogFileManager.replayPrint(results.getModName()+
								System.getProperty("line.separator"));
						xmlReader.nextEvent();//Burn the end element
					}
					
					// Handle a "TrackVenue" tag
					if (startElement.getName().getLocalPart() == "TrackVenue")
					{
						event = xmlReader.nextEvent();
						results.setTrackName(event.asCharacters().getData());
						LogFileManager.replayPrint(results.getTrackName()+
								System.getProperty("line.separator"));
						xmlReader.nextEvent();//Burn the end element
					}
					
					// Handle a "TrackEvent" tag
					if (startElement.getName().getLocalPart() == "TrackEvent")
					{
						event = xmlReader.nextEvent();
						results.setEventName(event.asCharacters().getData());
						LogFileManager.replayPrint(results.getEventName()+
								System.getProperty("line.separator"));
						xmlReader.nextEvent();//Burn the end element
					}
					
					// Handle a "TrackLength" tag
					if (startElement.getName().getLocalPart() == "TrackLength")
					{
						event = xmlReader.nextEvent();
						results.setTrackLength(Float.parseFloat(event.asCharacters().getData())/1000);
						LogFileManager.replayPrint(results.getTrackLength()+" km"+
								System.getProperty("line.separator"));
						xmlReader.nextEvent();//Burn the end element
					}
					
					// Handle a "Laps" tag
					if (startElement.getName().getLocalPart() == "Laps")
					{
						event = xmlReader.nextEvent();
						results.setMaxLaps(Integer.parseInt(event.asCharacters().getData()));
						LogFileManager.replayPrint(results.getMaxLaps() + 
								" lap maximum"+System.getProperty("line.separator"));
						xmlReader.nextEvent();//Burn the end element
					}
					
					// Handle a "Minutes" tag
					if (startElement.getName().getLocalPart() == "Minutes")
					{
						event = xmlReader.nextEvent();
						results.setMaxTime(Integer.parseInt(event.asCharacters().getData()));
						LogFileManager.replayPrint(results.getMaxTime() + 
								" minute maximum"+System.getProperty("line.separator"));
						xmlReader.nextEvent();//Burn the end element
					}
					
					// Handle a "Driver" tag
					if (startElement.getName().getLocalPart() == "Driver")
					{
						RFDriverQ driver = new RFDriverQ();
							//Temp vars for this driver
						boolean isPlayer = false;
						LinkedList<Lap> tempLaps = new LinkedList<Lap>();
						int pos = 0, classPos = 0;
						
						//Get next item
				        event = xmlReader.nextEvent();
				        
				        //Check for end driver tag (Which it shouldn't be)
				        boolean endElement = event.isEndElement();
				        String title = "";
				        if (endElement)
				        	title = event.asEndElement().getName().getLocalPart();
				        
				        //Check tag
						while(title != "Driver")
						{
							if (event.isStartElement())
							{
								StartElement element = event.asStartElement();
								
								// Handle "Name" tag
								if (element.getName().getLocalPart() == "Name")
								{
									event = xmlReader.nextEvent();
									driver.setName(event.asCharacters().getData());
									xmlReader.nextEvent();//Burn the end element
								}
								
								// Handle "CarClass" tag
								if (element.getName().getLocalPart() == "CarClass")
								{
									event = xmlReader.nextEvent();
									driver.setCarClass(event.asCharacters().getData());
									xmlReader.nextEvent();//Burn the end element
								}
								
								// Handle "CarNumber" tag
								if (element.getName().getLocalPart() == "CarNumber")
								{
									event = xmlReader.nextEvent();
									driver.setCarNumber(event.asCharacters().getData());
									xmlReader.nextEvent();//Burn the end element
								}
								
								// Handle "TeamName" tag
								if (element.getName().getLocalPart() == "TeamName")
								{
									event = xmlReader.nextEvent();
									driver.setTeamName(event.asCharacters().getData());
									xmlReader.nextEvent();//Burn the end element
								}
								
								// Handle "Position" tag
								if (element.getName().getLocalPart() == "Position")
								{
									event = xmlReader.nextEvent();
									pos = Integer.parseInt(event.asCharacters().getData());
									xmlReader.nextEvent();//Burn the end element
								}
								
								// Handle "ClassPosition" tag
								if (element.getName().getLocalPart() == "ClassPosition")
								{
									event = xmlReader.nextEvent();
									classPos = Integer.parseInt(event.asCharacters().getData());
									xmlReader.nextEvent();//Burn the end element
								}
								
								// Handle "isPlayer" tag
								if (element.getName().getLocalPart() == "isPlayer")
								{
									event = xmlReader.nextEvent();
									if (Integer.parseInt(event.asCharacters().getData()) == 0)
										isPlayer = false;
									else
										isPlayer = true;
									driver.setPlayer(isPlayer);
									xmlReader.nextEvent();//Burn the end element
								}
								
								// Handle "Lap" tag
								if (element.getName().getLocalPart() == "Lap")
								{
									Lap lap = new Lap();
									lap.setCompleted(false);
									
									@SuppressWarnings("unchecked")
									Iterator<Attribute> attributes = element.getAttributes();
									while (attributes.hasNext())
									{
										Attribute attribute = attributes.next();
										
										//If sector 1...
										if (attribute.getName().toString().equals("s1"))
											lap.setSector1(Float.parseFloat(attribute.getValue()));
										
										//If sector 2...
										if (attribute.getName().toString().equals("s2"))
											lap.setSector2(Float.parseFloat(attribute.getValue()));
										
										//If sector 3...
										if (attribute.getName().toString().equals("s3"))
											lap.setSector3(Float.parseFloat(attribute.getValue()));

										//If lap number...
										if (attribute.getName().toString().equals("num"))
											lap.setLapNumber(Integer.parseInt(attribute.getValue()));
										
										//If position...
										if (attribute.getName().toString().equals("p"))
											lap.setPosition(Integer.parseInt(attribute.getValue()));

										//If lap number...
										if (attribute.getName().toString().equals("et"))
										{
											String time = attribute.getValue();
											if (time.charAt(1) != '-')
												lap.setStartTime(Float.parseFloat(time));
											else
												lap.setStartTime(0.0f);
										}

										//If fuel...
										if (attribute.getName().toString().equals("fuel"))
											lap.setFuel(Float.parseFloat(attribute.getValue()));
										
										//If pit...
										if (attribute.getName().toString().equals("pit"))
										{
											if (Integer.parseInt(attribute.getValue()) == 1)
												lap.setPit(true);
										}
									}
									
									//Get time for the lap
									event = xmlReader.nextEvent();
									
									//Record lapTime
									String time = event.asCharacters().getData();
									//If no time
									if (time.charAt(1) == '-')
										lap.setCompleted(false);
									else
										lap.setLapTime(Float.parseFloat(time));
									tempLaps.add(lap);
									xmlReader.nextEvent();//Burn the end element
								}
								
								// Handle "FinishStatus" tag
								if (element.getName().getLocalPart() == "FinishStatus")
								{
									event = xmlReader.nextEvent();
									Lap[] laps = new Lap[tempLaps.size()];
									laps = tempLaps.toArray(laps);
									driver.submitTimes(laps);
									String status = event.asCharacters().getData();
									if (driver.getTimedLapCount() == 0)
										driver.submitTimes(status);
									xmlReader.nextEvent();//Burn the end element
								}
							}
							
							//Get next tag
					        event = xmlReader.nextEvent();
					        endElement = event.isEndElement();
					        if (endElement)
					        	title = event.asEndElement().getName().getLocalPart();
						}//End driver tag reader
						
						//Finish and add driver
						driver.setClassPosition(classPos);
						driver.setPosition(pos);
						results.addDriver(driver);
					}//End driver
		        }//End read start element
			}//End XML Reading
			
			//Driver Header
			if (!fullOutput)
				LogFileManager.replayPrint("Pos  Cla  Laps   Time     "+
					"Dream     Name                    Team                   "+
					"    Class        Behind"+System.getProperty("line.separator"));
			
			//Driver output
			Iterator<RFDriverQ> iterator = results.getDriverIterator();
			while(iterator.hasNext())
			{
				RFDriverQ driver = iterator.next();
				
				if (fullOutput)
				{
					LogFileManager.replayPrint("          Laps:    Fastest:  "+
						driver.getFastestTime()+" sec    Dream:  "+driver.getDreamLap()+
						" sec    Average:  "+driver.getAverageLap()+
						" sec"+System.getProperty("line.separator"));
				}
				else
				{
					//Built attribute by attribute
					String driverLine = " "+driver.getOverallPosition();
					driverLine = addWhiteSpace(driverLine, 6-driverLine.length());
					driverLine = driverLine.concat(driver.getClassPosition()+"");
					driverLine = addWhiteSpace(driverLine, 11-driverLine.length());
					driverLine = driverLine.concat(driver.getTimedLapCount()+"");
					driverLine = addWhiteSpace(driverLine, 16-driverLine.length());
					driverLine = driverLine.concat(RFDriverR.shortenTime(driver.getFastestTime())+"");
					driverLine = addWhiteSpace(driverLine, 25-driverLine.length());
					driverLine = driverLine.concat(RFDriverR.shortenTime(driver.getDreamLap())+"");
					driverLine = addWhiteSpace(driverLine, 36-driverLine.length());
					driverLine = driverLine.concat(driver.getName()+"");
					driverLine = addWhiteSpace(driverLine, 60-driverLine.length());
					driverLine = driverLine.concat(driver.getTeamName()+"");
					driverLine = addWhiteSpace(driverLine, 87-driverLine.length());
					driverLine = driverLine.concat(driver.getCarClass()+"");
					driverLine = addWhiteSpace(driverLine, 98-driverLine.length());
					driverLine = driverLine.concat("  "+results.findDifference(driver));
					LogFileManager.replayPrint(driverLine+System.getProperty("line.separator"));
				}
			}
			
			//TODO Proper post-session analysis
			LogFileManager.replayPrint("Session results:"+System.getProperty("line.separator"));
				//Sector times
			for (int i = 1; i < 4; i++)
			{
				RFDriverQ driver = results.getSectorDriver(i);
				LogFileManager.replayPrint("Fastest Sector "+i+":  "+driver.getSectorTime(i)+" sec by "+
						driver.getName()+System.getProperty("line.separator"));
			}
			LogFileManager.replayPrint("Overall Dream Lap:  "+
					results.getDreamLap()+" sec"+System.getProperty("line.separator"));
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			results = null;
	    }
		catch (XMLStreamException e)
	    {
			e.printStackTrace();
			results = null;
	    }
		
		return results;
	}
	
	/**
	  * Adds (numToAdd) spaces to string
	  * @param out - String to add spaces to
	  * @param numToAdd - Number of spaces to add
	  * @return String after adding spaces
	  */
	private String addWhiteSpace(String out, int numToAdd)
	{
		for (int i = 0; i < numToAdd; i++)
			out = out.concat(" ");
		
		return out;
	}
}