package pointStandings;

import java.util.Iterator;

import utilities.LogFileManager;

/**
  * Responsible for managing results and
  * drivers and then generating class
  * and overall standings
  * 
  * @author Richard Matthews
  * @version 1 (6-16-2013)
  */
public final class StandingsGenerator
{
	private RFResultsManager results;//Race results
	private int resultsLength;//Length of results as of last compiling
	private boolean compiled;//Have results been compiled
	private static final String section = "StandingGen";
	private DriverManager drivers;
	
	/**
	  * Constructor
	  */
	public StandingsGenerator()
	{
		LogFileManager.logPrint(section, "Constructor");
		results = RFResultsManager.getManager();
		resultsLength = results.getResultsSize();
		compiled = false;
		drivers = DriverManager.getManager();
	}
	
	/**
	  * Compiles the race results into a readable results format
	  * 
	  * @return True if successfully compiled
	  */
	private boolean compileResults()
	{
		boolean check = false;
		PointsTemplate scorekeeper = new PointsTemplate();//Points tracker
		
		//Start iteratin'
		Iterator<Results> resultsIterator = results.getResultsIterator();
		while (resultsIterator.hasNext())
		{
			Results result = resultsIterator.next();
			
			//If race results
			if (result instanceof RFRaceResults)
			{
				Iterator<RFDriverR> driverIterator = 
						((RFRaceResults) result).getFinishDriverIterator();
				
				//Cycle drivers
				while (driverIterator.hasNext())
				{
					//Get StandingsDriver
					RFDriverR tempDriver = driverIterator.next();
					StandingsDriver driver = drivers.addDriver(tempDriver);
					
					//Find event
					DriverEvent event = driver.getEvent(result.getEventID());
					
					//Null check
					if (event.getFinishPositionOverall() == 0)
					{
						
						//Flesh out basic information
						event.setTeamName(tempDriver.getTeamName());
						event.setCarClass(tempDriver.getCarClass());
						event.setEventID(result.getEventID());
						event.setCarNumber(tempDriver.getCarNumber());
						//Flesh out event-specific stats
						event.setFastestRaceLap(tempDriver.getFastestTime());
						for (int i = 1; i < 4; i++)
							event.setFastSectorTime(i, tempDriver.getSectorTime(i));
						//Flesh out session-specific stats
						event.setLapsLed(tempDriver.getOverallLapsLed());
						event.setStartingPositionOverall(tempDriver.getOverallStart());
						event.setFinishPositionOverall(tempDriver.getOverallFinish());
						event.setStartingPositionClass(tempDriver.getClassStart());
						event.setFinishPositionClass(tempDriver.getClassFinish());
						if (((RFRaceResults) result).getFastestDriver().getName() 
								== tempDriver.getName())
							event.setFastestLap(true);
						if (((RFRaceResults) result).getMostLapsLed() 
								== tempDriver.getOverallLapsLed())
							event.setMostLapsLed(true);
						if (((RFRaceResults) result).getMostGainedDriver().getName() 
								== tempDriver.getName())
							event.setFastestLap(true);
						event.setFinished(tempDriver.isFinished());
							//Fast sectors
						int fastSectors = 0;
						if (((RFRaceResults) result).getDriverSector1().getName() 
								== tempDriver.getName())
							fastSectors++;
						if (((RFRaceResults) result).getDriverSector2().getName() 
								== tempDriver.getName())
							fastSectors++;
						if (((RFRaceResults) result).getDriverSector3().getName() 
								== tempDriver.getName())
							fastSectors++;
						event.setRaceFastSectorCount(fastSectors);
						
						//TODO Calculate points here
						//TODO Log each time points are added
						//Points for finishing position
						event.setFinishPositionOverall(tempDriver.getOverallFinish());
						event.setFinishPositionClass(tempDriver.getClassFinish());
						int fPoints = scorekeeper.getPointsFinishPosition(event.getFinishPositionOverall(), event.hasFinished());
						event.setRacePointsOverall(fPoints);
						if (scorekeeper.shouldOutput() && fPoints > 0)
						{
							LogFileManager.logPrint(section, "Awarding "+fPoints+" to "+
									tempDriver.getName()+" in "+result.getEventName()+
									" for finishing in position "+
									event.getFinishPositionOverall()+" overall");
						}
							//Class points
						if (scorekeeper.shouldClassPoints())
						{
							int fPointsC = scorekeeper.getPointsFinishPosition(event.getFinishPositionClass(), event.hasFinished());
							event.setRacePointsClass(fPointsC);
							if (scorekeeper.shouldOutput() && fPointsC > 0)
							{
								LogFileManager.logPrint(section, "Awarding "+fPointsC+" to "+
										tempDriver.getName()+" in "+result.getEventName()+
										" for finishing in position "+
										event.getFinishPositionClass()+" in class");
							}
						}
						
						//Points for owner points
						//TODO Create owner points system
						
						//Laps led points processing info
						event.setLapsLed(tempDriver.getOverallLapsLed());
						int ledPoints = 0;
						if (event.getLapsLed() > 0)
							ledPoints = scorekeeper.pointsLapsLed();
						event.addBonusPointsOverall(ledPoints);
						if (scorekeeper.shouldOutput() && ledPoints > 0)
						{
							LogFileManager.logPrint(section, "Awarding "+ledPoints+
									" to "+tempDriver.getName()+" in "
									+result.getEventName()+" for leading laps");
						}
						if (scorekeeper.shouldClassPoints())
							event.addBonusPointsClass(ledPoints);
						
						//Most laps led points
						if (((RFRaceResults) result).getMostLapsLed() == event.getLapsLed())
						{
							event.setMostLapsLed(true);
							int mostPoints = scorekeeper.pointsMostLapsLed();
							event.addBonusPointsOverall(mostPoints);
							if (scorekeeper.shouldOutput() && mostPoints > 0)
							{
								LogFileManager.logPrint(section, "Awarding "+mostPoints+
										" to "+tempDriver.getName()+" in "
										+result.getEventName()+" for leading the most laps");
							}
							if (scorekeeper.shouldClassPoints())
								event.addBonusPointsClass(mostPoints);
						}
						
						//Fast lap points
						if (((RFRaceResults) result).getFastestDriver().getName() == tempDriver.getName())
						{
							event.setFastestLap(true);
							int fastPoints = scorekeeper.pointsRaceFastLap();
							event.addBonusPointsOverall(fastPoints);
							if (scorekeeper.shouldOutput() && fastPoints > 0)
							{
								LogFileManager.logPrint(section, "Awarding "+fastPoints+
										" to "+tempDriver.getName()+" in "+
										result.getEventName()+" for turning the fastest lap");
							}
							if (scorekeeper.shouldClassPoints())
								event.addBonusPointsClass(fastPoints);
						}
						//TODO Check for class-only, fast lap
						
						//Fast sector points
						if (fastSectors > 0)
						{
							int sectPoints = scorekeeper.pointsRaceFastSector()*fastSectors;
							event.addBonusPointsOverall(sectPoints);
							if (scorekeeper.shouldOutput() && sectPoints > 0)
							{
								LogFileManager.logPrint(section, "Awarding "+sectPoints+
										" to "+tempDriver.getName()+" in "+
										result.getEventName()+" for setting "+
										fastSectors+" fast sectors in the race");
							}
							if (scorekeeper.shouldClassPoints())
								event.addBonusPointsClass(sectPoints);
						}
						
						//Points for most positions gained
						if (((RFRaceResults) result).getBiggestGainer().getName() == tempDriver.getName())
						{
							event.setMostGained(true);
							int mostPoints = scorekeeper.pointsMostGained();
							event.addBonusPointsOverall(mostPoints);
							if (scorekeeper.shouldOutput() && mostPoints > 0)
							{
								LogFileManager.logPrint(section, "Awarding "+mostPoints+
										" to "+tempDriver.getName()+" in "+
										result.getEventName()+" for gaining the most positions ["+
										event.getStartingPositionOverall()+
										"->"+event.getFinishPositionOverall()+"]");
							}
							if (scorekeeper.shouldClassPoints())
								event.addBonusPointsClass(mostPoints);
						}
						
						//Add event to driver
						driver.addEvent(event);
					}
				}
			}//End Race
			
			//If qualifying results
			if (result instanceof RFQualifyingResults)
			{
				Iterator<RFDriverQ> driverIterator = 
						((RFQualifyingResults) result).getDriverIterator();
				
				//Cycle drivers
				while (driverIterator.hasNext())
				{
					//Get StandingsDriver
					RFDriverQ tempDriver = driverIterator.next();
					StandingsDriver driver = drivers.addDriver(tempDriver);
					
					//Find event
					DriverEvent event = driver.getEvent(result.getEventID());
					
					
					//Flesh out basic information
					event.setTeamName(tempDriver.getTeamName());
					event.setCarClass(tempDriver.getCarClass());
					event.setEventID(result.getEventID());
					event.setCarNumber(tempDriver.getCarNumber());
					//Flesh out event-specific stats
					event.setQualifyingTime(tempDriver.getFastestTime());
					for (int i = 1; i < 4; i++)
						event.setFastSectorTime(i, tempDriver.getSectorTime(i));
					//Flesh out session-specific stats
					event.setStartingPositionOverall(tempDriver.getOverallPosition());
					event.setStartingPositionClass(tempDriver.getClassPosition());
						//Fast sectors
					int fastSectors = 0;
					for (int i = 1; i < 4; i++)
						if (((RFQualifyingResults) result).getSectorDriver(i).getName() 
								== tempDriver.getName())
							fastSectors++;
					
					//Points for qualifying position
					event.setStartingPositionOverall(tempDriver.getOverallPosition());
					event.setStartingPositionClass(tempDriver.getClassPosition());
					int qPoints = scorekeeper.getPointsQualifyingPosition(event.getStartingPositionOverall());
					event.setQualifyingPointsOverall(qPoints);
					if (scorekeeper.shouldOutput() && qPoints > 0)
					{
						LogFileManager.logPrint(section, "Awarding "+qPoints+" to "+
								tempDriver.getName()+" in "+result.getEventName()+
								" for qualifying in position "+
								event.getStartingPositionOverall()+" overall");
					}
					
					//Score class points
					if (scorekeeper.shouldClassPoints())
					{
						int qPointsC = scorekeeper.getPointsQualifyingPosition(event.getStartingPositionClass());
						event.setQualifyingPointsClass(qPointsC);
						if (scorekeeper.shouldOutput() && qPointsC > 0)
						{
							LogFileManager.logPrint(section, "Awarding "+qPointsC+" to "+
									tempDriver.getName()+" in "+result.getEventName()+
									" for qualifying in position "+
									event.getStartingPositionClass()+" in class");
						}
					}
					
					//Points for fast sectors in qualifying
						//Ensure no doubling of bonus points
					if (event.getQualifyingFastSectorCount() == 0)
					{
						event.setQualifyingFastSectorCount(fastSectors);
						int sectBonus = fastSectors*scorekeeper.pointsQualiFastSector();
						event.addBonusPointsOverall(sectBonus);
						if (scorekeeper.shouldOutput() && sectBonus > 0)
						{
							LogFileManager.logPrint(section, "Awarding "+sectBonus+" to "+
									tempDriver.getName()+" in "+result.getEventName()+
									" for setting "+fastSectors+" fast sectors in qualifying");
						}
						if (scorekeeper.shouldClassPoints())
							event.addBonusPointsClass(sectBonus);
					}
					
					//Add event to driver
					driver.addEvent(event);
				}//End qualifying
			}
		}
		
		return check;
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
	
	public String produceOverallStandings()
	{
		String output = "";
		
		int count = 0;
		while (!compiled && count++ < 2)
			compiled = compileResults();
		
//		if (!compiled)
//			return output;
		
		drivers.sortByOverallPoints();
		String newLine = ""+System.getProperty("line.separator");
		
		Iterator<StandingsDriver> iterator = drivers.getIterator();
		int newCount = 0;
		while (iterator.hasNext())
		{
			newCount++;
			iterator.next();
		}
		LogFileManager.logPrint(section, "Driver array count "+newCount);
		iterator = drivers.getIterator();
		while(iterator.hasNext())
		{
			StandingsDriver driver = iterator.next();
			String line = " "+driver.getOverallPosition();
			line = addWhiteSpace(line, 5-line.length());
			line = line.concat(""+driver.getName());
			line = addWhiteSpace(line, 35-line.length());
			line = line.concat(""+driver.getOverallPointTotal());
			line = addWhiteSpace(line, 40-line.length());
			line = line.concat(""+driver.getNumberOfRaces());
			output = output.concat(line+newLine);
		}
		LogFileManager.logPrint(section, output);
		
		//TODO Output
			//Get race names and build header
			//Sort drivers by overall total
			//Cycle drivers
				//Get point total, # of races, finishes, and points per event
		//TODO Future: Sort by class
		
		return output;
	}
	
	public String produceClassStandings()
	{
		String output = "";
		
		if (new PointsTemplate().shouldClassPoints())
		{
			int count = 0;
			while (!compiled && count++ < 2)
				compiled = compileResults();
			
			if (!compiled)
				return output;
			
			//TODO Output
		}
		
		return output;
	}
}