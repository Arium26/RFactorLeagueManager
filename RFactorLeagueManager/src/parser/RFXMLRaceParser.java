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
import pointStandings.RFDriverR;
import pointStandings.RFRaceResults;
import utilities.LogFileManager;

/**
  * Parses an XML file for a race session.
  * 
  * @author Richard Matthews
  */
public class RFXMLRaceParser extends Parser
{
	private String header = "RFactor XML Race Parser";
	private final static boolean fullOutput = false;
	
	/**
	  * Constructor being fed a file path to read from.
	  * 
	  * @param file - The path to the file to read
	  */
	public RFXMLRaceParser(String file)
	{
		setFilePath(file);
		LogFileManager.logPrint(header, "Constructor");
	}
	
	/**
	  * Parses the file into an instance of
	  * the RFRaceResults class.
	  * 
	  * @return The instance of RFRaceResults, or
	  * 	null if the process failed.
	  */
	public RFRaceResults parseFile()
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
		RFRaceResults results = null;
		
		try
		{
			//Set up reading an XML file
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			InputStream in = new FileInputStream(getFilePath());
			XMLEventReader xmlReader = inputFactory.createXMLEventReader(in);
			
			//Create a new instance of race results after file has
			//		been successfully found
			results = new RFRaceResults();
			
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
					if (startElement.getName().getLocalPart() == "RaceLaps")
					{
						event = xmlReader.nextEvent();
						results.setMaxLaps(Integer.parseInt(event.asCharacters().getData()));
						LogFileManager.replayPrint(results.getMaxLaps() + 
								" laps"+System.getProperty("line.separator"));
						xmlReader.nextEvent();//Burn the end element
					}
					
					// Handle a "Minutes" tag
					if (startElement.getName().getLocalPart() == "RaceMinutes")
					{
						event = xmlReader.nextEvent();
						results.setMaxTime(Integer.parseInt(event.asCharacters().getData()));
						LogFileManager.replayPrint("Time limit: "+results.getMaxTime() + 
								" minutes"+System.getProperty("line.separator"));
						xmlReader.nextEvent();//Burn the end element
					}
					
					//TODO Check stream for contact and cautions
					//		<Incident et="929.2">
					//		Mick Robinson(16) reported contact (0.32) with 
					//		another vehicle Mike Bednowicz(21)</Incident>
					//Yellow Flag Situation (Lap 20-24)
					//		<Score et="1160.0">Yellow flag state 2-&gt;4</Score>
					//	[Need to track leader lap]
					//Lights out
					//		<Score et="1376.0">Yellow flag state 4-&gt;5</Score>
					//Green Flag
					//		<Score et="1467.1">Yellow flag state 5-&gt;6</Score>
					//	<Score et="1470.9">Budster87(0) lap=24 point=0 t=94.426 et=1470.875</Score>
					//		<Score et="1471.0">Yellow flag state 6-&gt;0</Score>
					
					// Handle a "Driver" tag
					if (startElement.getName().getLocalPart() == "Driver")
					{
						RFDriverR driver = new RFDriverR();
							//Temp vars for this driver
						boolean isPlayer = false;
						LinkedList<Lap> tempLaps = new LinkedList<Lap>();
						int pos = 0, classPos = 0;//Finish position
						int ovStart = 0, clStart = 0;//Grid position
						
						//Get next tag
				        event = xmlReader.nextEvent();
				        
				        //Check for end driver tag
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
								
								// Handle "Position" tag (Overall finish)
								if (element.getName().getLocalPart() == "Position")
								{
									event = xmlReader.nextEvent();
									pos = Integer.parseInt(event.asCharacters().getData());
									xmlReader.nextEvent();//Burn the end element
								}
								
								// Handle "ClassPosition" tag (Class finish)
								if (element.getName().getLocalPart() == "ClassPosition")
								{
									event = xmlReader.nextEvent();
									classPos = Integer.parseInt(event.asCharacters().getData());
									xmlReader.nextEvent();//Burn the end element
								}
								
								// Handle "GridPos" tag (Overall start)
								if (element.getName().getLocalPart() == "GridPos")
								{
									event = xmlReader.nextEvent();
									ovStart = Integer.parseInt(event.asCharacters().getData());
									xmlReader.nextEvent();//Burn the end element
								}
								
								// Handle "ClassGridPos" tag (Class start)
								if (element.getName().getLocalPart() == "ClassGridPos")
								{
									event = xmlReader.nextEvent();
									clStart = Integer.parseInt(event.asCharacters().getData());
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
											lap.setStartTime(Float.parseFloat(attribute.getValue()));

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
									lap.setLapTime(Float.parseFloat(event.asCharacters().getData()));
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
									String reason = event.asCharacters().getData();
									if (!reason.matches("Finished Normally"))
										driver.setDNF(reason);
									xmlReader.nextEvent();//Burn the end element
								}
								
								// Handle "FinishTime" tag
								if (element.getName().getLocalPart() == "FinishTime")
								{
									event = xmlReader.nextEvent();
									double time = Double.parseDouble(event.asCharacters().getData());
									driver.setFinishTime(time);
									xmlReader.nextEvent();//Burn the end element
								}
							}//End startElement
							
							//Get next tag
					        event = xmlReader.nextEvent();
					        endElement = event.isEndElement();
					        if (endElement)
					        	title = event.asEndElement().getName().getLocalPart();
						}//End driver tag reader
						
						//Finish driver
						driver.setClassFinish(classPos);
						driver.setOverallFinish(pos);
						driver.setOverallStart(ovStart);
						driver.setClassStart(clStart);
						
						//Affect results
						results.addDriver(driver);
						
					}//End driver
		        }//End read start element
			}//End XML Reading
			
			//Cycle through drivers for output here
			Iterator<RFDriverR> iterator = results.getFinishDriverIterator();
			if (!fullOutput)
				LogFileManager.replayPrint("Fin  Str   LL  Laps  Finish Time  "+
					"FastLap  Name                     Team                   "+
					"    Class        Behind"+System.getProperty("line.separator"));
			while (iterator.hasNext())
			{
				RFDriverR driver = iterator.next();
				if (fullOutput)
				{
						//Header output
					LogFileManager.replayPrint(driver.getName()+"  "+driver.getCarClass()+
							"  #"+driver.getCarNumber()+"  "+driver.getTeamName());
					if (driver.isPlayer())
						LogFileManager.replayPrint("  Player"+System.getProperty("line.separator"));
					else
						LogFileManager.replayPrint("  AI"+System.getProperty("line.separator"));
						//Results output
					LogFileManager.replayPrint("          Finish:    #"+driver.getOverallFinish()+
							" ["+driver.getOverallStart()+"]    Time: "+driver.getFinishTime()+
							" sec"+"    "+driver.getLapCount()+" laps     "+
							driver.getOverallLapsLed()+" laps led"+System.getProperty("line.separator")+
							"          Timing:    Fastest: "+driver.getFastestTime()+" sec [lap "+
							driver.getFastestLap()+"]   Dream: "+driver.getDreamLap()+
							" sec    Average: "+driver.getAverageLap()+" sec"+
							System.getProperty("line.separator")+"          Sector:    1 - "+
							driver.getSectorTime(1)+" sec [lap "+driver.getSectorLap(1)+"]    2 - "+
							driver.getSectorTime(2)+" sec [lap "+driver.getSectorLap(2)+"]    3 - "+
							driver.getSectorTime(3)+" sec [lap "+driver.getSectorLap(3)+"]"+
							System.getProperty("line.separator"));
						//Pit output
					if (driver.getPitStops() > 0)
						LogFileManager.replayPrint("          Pit:       Stops:  "+driver.getPitStops()+
							"    Time:  "+driver.getPitTime()+" sec"+System.getProperty("line.separator"));
						//Finished output
					if (!driver.isFinished())
						LogFileManager.replayPrint("          DNF"+System.getProperty("line.separator"));
				}
				else
				{
						//Built attribute by attribute
					String driverLine = " "+driver.getOverallFinish();
					driverLine = addWhiteSpace(driverLine, 6-driverLine.length());
					driverLine = driverLine.concat(driver.getOverallStart()+"");
					driverLine = addWhiteSpace(driverLine, 11-driverLine.length());
					driverLine = driverLine.concat(driver.getOverallLapsLed()+"");
					driverLine = addWhiteSpace(driverLine, 15-driverLine.length());
					driverLine = driverLine.concat(driver.getLapCount()+"");
					driverLine = addWhiteSpace(driverLine, 21-driverLine.length());
					if (driver.isFinished())
						driverLine = driverLine.concat(driver.getFinishTime()+"");
					else
						driverLine = driverLine.concat(driver.getReason());						
					driverLine = addWhiteSpace(driverLine, 34-driverLine.length());
					driverLine = driverLine.concat(RFDriverR.shortenTime(driver.getFastestTime())+"");
					driverLine = addWhiteSpace(driverLine, 43-driverLine.length());
					driverLine = driverLine.concat(driver.getName()+"");
					driverLine = addWhiteSpace(driverLine, 68-driverLine.length());
					driverLine = driverLine.concat(driver.getTeamName()+"");
					driverLine = addWhiteSpace(driverLine, 95-driverLine.length());
					driverLine = driverLine.concat(driver.getCarClass()+"");
					driverLine = addWhiteSpace(driverLine, 106-driverLine.length());
					driverLine = driverLine.concat("  "+results.findDifference(driver));
					LogFileManager.replayPrint(driverLine+System.getProperty("line.separator"));
				}
			}//End driver output
			
			LogFileManager.replayPrint("Session results:"+System.getProperty("line.separator"));
				//List winner
			LogFileManager.replayPrint("Winner: "+results.getWinner().getName()+" with "+
					results.getWinner().getLapCount()+" laps in "+
					results.getWinner().getFinishTime()+" sec leading "
					+results.getWinner().getOverallLapsLed()+" laps"+System.getProperty("line.separator"));
				//Most laps led
			LogFileManager.replayPrint("Most laps led:  "+results.getLapLeader().getName()+
					" with "+results.getLapLeader().getOverallLapsLed()+
					" laps led"+System.getProperty("line.separator"));
				//Fast lap stats
			LogFileManager.replayPrint("Fastest Sector 1:  "+results.getDriverSector1().getSectorTime(1)+
				" sec by "+results.getDriverSector1().getName()+" on lap "+
				results.getDriverSector1().getSectorLap(1)+System.getProperty("line.separator"));
			LogFileManager.replayPrint("Fastest Sector 2:  "+results.getDriverSector2().getSectorTime(2)+
				" sec by "+results.getDriverSector2().getName()+" on lap "+
				results.getDriverSector2().getSectorLap(2)+System.getProperty("line.separator"));
			LogFileManager.replayPrint("Fastest Sector 3:  "+results.getDriverSector3().getSectorTime(3)+
				" sec by "+results.getDriverSector3().getName()+" on lap "+
				results.getDriverSector3().getSectorLap(3)+System.getProperty("line.separator"));
					//Get fastest laps
			RFDriverR[] fastDrivers = results.getFastestDrivers();
			for (int i = 0; i < fastDrivers.length; i++)
			{
				LogFileManager.replayPrint("Fastest Lap:  "+fastDrivers[i].getFastestTime()+
						" sec by "+fastDrivers[i].getName()+" on lap "+
						fastDrivers[i].getFastestLap()+"   ["+
						fastDrivers[i].getCarClass()+"]"+
						System.getProperty("line.separator"));
			}
			float total = results.getDriverSector1().getSectorTime(1)+
				results.getDriverSector2().getSectorTime(2)+
				results.getDriverSector3().getSectorTime(3);
			LogFileManager.replayPrint("Overall Dream Lap:  "+
					total+" sec"+System.getProperty("line.separator"));
				//Get highest gainer
			RFDriverR gainer = results.getBiggestGainer();
			int gain = gainer.getOverallStart()-gainer.getOverallFinish();
					//Build output
			String gainerOutput = "Biggest gainer:  "+gainer.getName();
			gainerOutput = addWhiteSpace(gainerOutput, 42-gainerOutput.length());
			gainerOutput = gainerOutput.concat("["+gain+"]");
			gainerOutput = addWhiteSpace(gainerOutput, 48-gainerOutput.length());
			gainerOutput = gainerOutput.concat("ST: "+gainer.getOverallStart());
			gainerOutput = addWhiteSpace(gainerOutput, 56-gainerOutput.length());
			gainerOutput = gainerOutput.concat("FI: "+gainer.getOverallFinish());
			LogFileManager.replayPrint(gainerOutput+System.getProperty("line.separator"));
				//Get biggest loser
			RFDriverR loser = results.getBiggestLoser();
			int loss = loser.getOverallStart()-loser.getOverallFinish();
					//Build output
			String loserOutput = "Biggest loser:   "+loser.getName();
			loserOutput = addWhiteSpace(loserOutput, 42-loserOutput.length());
			loserOutput = loserOutput.concat("["+loss+"]");
			loserOutput = addWhiteSpace(loserOutput, 48-loserOutput.length());
			loserOutput = loserOutput.concat("ST: "+loser.getOverallStart());
			loserOutput = addWhiteSpace(loserOutput, 56-loserOutput.length());
			loserOutput = loserOutput.concat("FI: "+loser.getOverallFinish());
			LogFileManager.replayPrint(loserOutput+System.getProperty("line.separator"));
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