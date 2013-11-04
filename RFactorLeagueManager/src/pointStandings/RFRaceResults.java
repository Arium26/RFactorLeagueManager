package pointStandings;

import java.util.Iterator;
import java.util.LinkedList;

import utilities.LogFileManager;

/**
  * A class for storing the results of 
  * a race session.
  * 
  * @author Richard Matthews
  */
public class RFRaceResults extends Results
{
	private String header = "Race Results";
	
	//TODO Add IDs to drivers?		(The job of the DriverManager)
	//TODO Incidents
	//TODO Add in config file stats
		//List of drivers competing
	private LinkedList<RFDriverR> drivers;//The drivers in qualifying
		//Timing stats
	private RFDriverR[] fastestDrivers;//Fastest lap in each class
	private RFDriverR fastestOverallDriver;//Fastest driver overall
	private RFDriverR driverS1, driverS2, driverS3;//Driver holding fast time
		//Race specific
	private LinkedList<RFDriverR> driversFinPos;//Sorted by finish position
	private RFDriverR winner;
	private RFDriverR lapLeader;
	private RFDriverR mostGained;
	private RFDriverR mostLost;
	private int mostLapsLed;
	private int maxLaps, maxTime;
	private float contactThreshold;
	private int cautionCount;
	private int[] cautionStartLap;
	private int[] cautionEndLap;
	
	/**	Mandatory empty constructor, do not use	*/
	public RFRaceResults()	{	initializeDefault();	}

	/**
	  * Full constructor accepting all basic 
	  * information about the session.   Mod
	  * name excluded as it is usually 
	  * optional.
	  * 
	  * @param track - The name of the track
	  * @param event - The name of the event
	  * @param length - The length of the track
	  * @param laps - Lap goal for the race
	  * @param time - Time limit for the race
	  */
	public RFRaceResults(String track, String event, String mod,
			float length, int laps, int time)
	{
		LogFileManager.logPrint(header, "Constructor");
		initializeDefault();
		setTrackName(track);
		setEventName(event);
		setModName(mod);
		setTrackLength(length);
		maxLaps = laps;
		maxTime = time;
	}
	
	/**
	  * Creates the driversFinPos object by
	  * sorting the drivers object by finish
	  * position.  Fair warning, may only be
	  * done once. 
	  * <br><br>
	  * An inefficient algorithm is used to 
	  * sort.  You can change this if you 
	  * wish to provide an insignificant 
	  * speed increase here.  A large list
	  * here is 64, so O(n^2) isn't a 
	  * disaster.
	  */
	private void createFinishList()
	{
		LogFileManager.logPrint(header, "Sorting by finish");
		if (driversFinPos.isEmpty())
		{
			//Setup the sort
			int size = drivers.size();
			RFDriverR[] tempList = new RFDriverR[size];
			tempList = drivers.toArray(tempList);
			
			//Sort list
			for (int i = 1; i <= size; i++)
			{
				boolean check = false;
				for (int j = 0; j < tempList.length && !check; j++)
				{
					//Check if the position is met
					if (tempList[j].getOverallFinish() == i)
					{
						driversFinPos.add(tempList[j]);
						check = true;
					}
				}
			}
		}
	}
	
	/**
	  * Finds the drives that gained the most
	  * positions from the start of the race
	  * to the end.
	  * 
	  * @return Driver gaining the most spots
	  */
	public RFDriverR getBiggestGainer()
	{
		LogFileManager.logPrint(header, "Finding biggest gainer");
		Iterator<RFDriverR> drivers = getBasicDriverIterator();
		RFDriverR gainer = null;
		int gain = 0;
		while (drivers.hasNext())
		{
			RFDriverR tempDriver = drivers.next();
			int temp = tempDriver.getOverallStart()-tempDriver.getOverallFinish();
			if (gainer != null)
			{
				if (temp > gain || (temp == gain &&
						tempDriver.getOverallFinish() < gainer.getOverallFinish()))
				{
					gain = temp;
					gainer = tempDriver;
					mostGained = gainer;
				}
			}
			else
			{
				gain = temp;
				gainer = tempDriver;
				mostGained = gainer;
			}
		}
		
		return gainer;
	}
	
	/**
	  * Finds the drives that lost the most
	  * positions from the start of the race
	  * to the end.
	  * 
	  * @return Driver losing the most spots
	  */
	public RFDriverR getBiggestLoser()
	{
		LogFileManager.logPrint(header, "Finding biggest loser");
		Iterator<RFDriverR> drivers = getBasicDriverIterator();
		RFDriverR loser = null;
		int loss = 0;
		while (drivers.hasNext())
		{
			RFDriverR tempDriver = drivers.next();
			int temp = tempDriver.getOverallStart()-tempDriver.getOverallFinish();
			if (loser != null)
			{
				if (temp < loss || (temp == loss &&
						tempDriver.getOverallFinish() > loser.getOverallFinish()))
				{
					loss = temp;
					loser = tempDriver;
					mostLost = loser;
				}
			}
			else
			{
				loss = temp;
				loser = tempDriver;
				mostLost = loser;
			}
		}
		
		return loser;
	}
	
	/**
	  * Finds the difference between
	  * this driver and the overall winner.
	  * <br><br>
	  * Winner output: "------"
	  * Same lap: "+(time difference)"
	  * Different lap:  "-# lap(s)"
	  * 
	  * @param driver - Driver to check
	  * @return String representation 
	  */
	public String findDifference(RFDriverR driver)
	{
		LogFileManager.logPrint(header, "Finding difference in drivers");
		String output = "------";
		
		//Check if the winner
		if (driver.getOverallFinish() != 1)
		{
			//If different lap count
			if (winner.getLapCount() > driver.getLapCount())
			{
				int difference = winner.getLapCount()-driver.getLapCount();
				if (difference == 1)
					output = "-1 lap";
				else
					output = "-"+difference+" laps";
			}
			else
			{
				float difference = (float) (driver.getFinishTime()-winner.getFinishTime());
				output = "+"+RFDriverR.shortenTime(difference);
			}
		}
		
		return output;
	}
	
	//Accessors
	/**
	  * Gets an iterator for the drivers sorted by fastest lap time
	  * @return The Iterator in question
	  */
	public Iterator<RFDriverR> getBasicDriverIterator()	{	return new DriverIterator(drivers);	}
	/**
	  * Gets an iterator for the drivers 
	  * sorted by finishing position.
	  * 
	  * @return The Iterator in question
	  */
	public Iterator<RFDriverR> getFinishDriverIterator()
	{
		createFinishList();
		return new DriverIterator(driversFinPos);
	}
		// Timing accessors
	public RFDriverR getFastestDriver()	{	return fastestOverallDriver;	}
	public RFDriverR[] getFastestDrivers()	{	return fastestDrivers;	}
	public RFDriverR getDriverSector1()	{	return driverS1;	}
	public RFDriverR getDriverSector2()	{	return driverS2;	}
	public RFDriverR getDriverSector3()	{	return driverS3;	}
		//Race specific
	public float getContactThreshold()	{	return contactThreshold;	}
	public int getMaxLaps()	{	return maxLaps;	}
	public int getMaxTime()	{	return maxTime;	}
	public int getMostLapsLed()	{	return mostLapsLed;	}
	public int getCautionCount()	{	return cautionCount;	}
	/**
	  * Gets the start and end lap for a caution period
	  * 
	  * @param number - Caution number
	  * @return New int[2] where int[0] = Start lap, int[1] = End lap,
	  * 		or null if the caution doesn't exist
	  */
	public int[] getCautionNumber(int number)
	{
		int[] caution = null;
		
		//Check validity
		if (number > 0 && number <= cautionCount)
		{
			caution = new int[2];
			caution[0] = cautionStartLap[number-1];
			caution[1] = cautionEndLap[number-1];
		}
		
		return caution;
	}
	public RFDriverR getWinner()	{	return winner;	}
	public RFDriverR getLapLeader()	{	return lapLeader;	}
	public RFDriverR getMostGainedDriver()	{	return mostGained;	}
	public RFDriverR getMostLostDriver()	{	return mostLost;	}
	/**
	  * Gets the driver placing in a certain position
	  * 
	  * @param position - The position to search for
	  * @param start - True if searching for starting position (False = finish)
	  * @return The driver at that position, or null if none
	  */
	public RFDriverR getDriverByPosition(int position, boolean start)
	{
		RFDriverR driver = null;
		
		Iterator<RFDriverR> iterator = getBasicDriverIterator();
		boolean check = iterator.hasNext();
		while(check)
		{
			//Check position
			RFDriverR temp = iterator.next();
			if ((temp.getOverallFinish() == position && !start) || 
				(temp.getOverallStart() == position && start))
			{
				driver = temp;
				check = false;
			}
			
			//Check if iterator has next
			if (check)
				check = iterator.hasNext();
		}
		
		return driver;
	}
	
	//Mutators
	/**
	  * Adds a driver to those listed for the session.
	  * This is where the list of classes is built.
	  * 
	  * @param driver - The driver to be added
	  * @return True, if successful
	  */
	public boolean addDriver(RFDriverR driver)
	{
		//Check existing drivers
		if(drivers.contains(driver))
			return false;
		
		LogFileManager.logPrint(header, "Adding Driver");
		//If new class...
		if (setCarClass(driver.getCarClass()) && fastestDrivers[0] != null)
		{
			//Increase driver size
			int oldLength = fastestDrivers.length;
			int newLength = fastestDrivers.length+1;
			RFDriverR[] tempName = new RFDriverR[newLength];
			for(int i = 0; i < oldLength; i++)
				tempName[i] = fastestDrivers[i];
			tempName[oldLength] = driver;
			fastestDrivers = tempName;
		}
		
		//Check stats vs. best
		setFastestTimes(driver);
		
		//Winner laps
		setWinnerLaps(driver);
		
		//Laps led
		setLapLeader(driver);
		
		return drivers.add(driver);
	}
		//Timing mutators
	/**
	  * Checks if the driver set fastest laps and
	  * sectors.
	  * <br><br>
	  * Precondition requires times to be set.
	  * 
	  * @param driver - Driver in question
	  * @return True if a time was set
	  */
	public boolean setFastestTimes(RFDriverR driver)
	{
		LogFileManager.logPrint(header, "Checking fast times");
		//Null catch
		if (fastestOverallDriver == null)
		{
			fastestOverallDriver = driver;
			fastestDrivers[0] = driver;
		}
		
		//Check fastest overall regardless of class
		if (fastestOverallDriver.getFastestTime() > driver.getFastestTime())
			fastestOverallDriver = driver;
		
		//Find class
		int classPos = 0;
		for (int i = 0; i < fastestDrivers.length; i++)
			if (driver.getCarClass() == fastestDrivers[i].getCarClass())
				classPos = i;
		
		//Set fast lap (If fastest)
		double lap = driver.getFastestTime();
		boolean fastest = false;
		if (lap != 0.0f)
			if (lap < fastestDrivers[classPos].getFastestTime())
			{
				fastestDrivers[classPos] = driver;
				fastest = true;
			}
		

			//If one is null, all are null
		if (driverS1 == null)
		{
			driverS1 = driver;
			driverS2 = driver;
			driverS3 = driver;
			fastest = true;
		}
		else
		{
			//Set each sector time
			float sector = driver.getSectorTime(1);
				//Sector 1
			if (sector != 0.0f)
				if (sector < driverS1.getSectorTime(1))
				{
					driverS1 = driver;
					fastest = true;
				}
				//Sector 2
			sector = driver.getSectorTime(2);
			if (sector != 0.0f)
				if (sector < driverS2.getSectorTime(2))
				{
					driverS2 = driver;
					fastest = true;
				}		
				//Sector 3
			sector = driver.getSectorTime(3);
			if (sector != 0.0f)
				if (sector < driverS3.getSectorTime(3))
				{
					driverS3 = driver;
					fastest = true;
				}
		}
		
		return fastest;
	}
		//Race mutators
	public void setContactThreshold(float amount)	{	contactThreshold = amount;	}
	public void setMaxLaps(int laps)	{	maxLaps = laps;	}
	public void setMaxTime(int time)	{	maxTime = time;	}
	/**
	  * Checks to see if the driver is the winner
	  * and changes internal variables accordingly. 
	  * For reference sake, the winner is the driver
	  * with the most laps completed in the shortest
	  * amount of time.
	  * 
	  * @param driver - The driver to check
	  * @return True if the driver is the winner
	  */
	public boolean setWinnerLaps(RFDriverR driver)
	{
		LogFileManager.logPrint(header, "Checking on winner");
		//Null check
		if (winner == null)
		{
			winner = driver;
			return true;
		}
			
		//Three conditions:  Most laps or equal laps + less time
		if (winner.getLapCount() < driver.getLapCount() || 
				(winner.getLapCount() == driver.getLapCount() && 
				winner.getFinishTime() > driver.getFinishTime()))
		{
			winner = driver;
			return true;
		}
		
		return false;
	}
	/**
	  * Checks to see if the driver led the most laps
	  * @param driver - Driver to check
	  * @return True if a new lap leader
	  * @version 1 (No ties version)
	  */
	public boolean setLapLeader(RFDriverR driver)
	{
		//TODO Adjust to allow multiple lap leaders (Ties)
		LogFileManager.logPrint(header, "Checking for most laps led");
		
		//Null catch
		if (lapLeader == null)
		{
			lapLeader = driver;
			mostLapsLed = driver.getOverallLapsLed();
			return true;
		}
		
		//Tie goes to higher positioned driver at the moment
		if (lapLeader.getOverallLapsLed() < driver.getOverallLapsLed() || (
				lapLeader.getOverallLapsLed() == driver.getOverallLapsLed() && 
				lapLeader.getOverallFinish() > driver.getOverallLapsLed()))
		{
			lapLeader = driver;
			mostLapsLed = driver.getOverallLapsLed();
			return true;
		}
		
		return false;
	}
	/**
	  * Adds a caution period to the results
	  * 
	  * @param start - Start lap of the caution period
	  * @param end - End lap of the caution period
	  * @return True if the caution period was added
	  */
	public boolean setCautionPeriod(int start, int end)
	{
		boolean check = true;
		
		//Null case check
		if (cautionStartLap == null || cautionEndLap == null)
		{
			cautionStartLap = new int[1];
			cautionStartLap[0] = start;
			cautionEndLap = new int[1];
			cautionEndLap[0] = end;
			
			return true;
		}
		
		//Check if the caution exists already
		for(int i = 0; i < cautionCount; i++)
			if (cautionStartLap[i] == start)
				check = false;
		
		if (check)
		{
			//Copy arrays and add new entries
				//cautionStartLap[]
			int[] tempStart = new int[cautionStartLap.length+1];
			for(int j = 0; j < cautionStartLap.length; j++)
				tempStart[j] = cautionStartLap[j];
			tempStart[cautionStartLap.length+1] = start;
			cautionStartLap = tempStart;
				//cautionEndLap[]
			int[] tempEnd = new int[cautionEndLap.length+1];
			for(int k = 0; k < cautionEndLap.length; k++)
				tempEnd[k] = cautionEndLap[k];
			tempEnd[cautionEndLap.length+1] = end;
			cautionEndLap = tempEnd;
		}
		
		return check;
	}
	
	//Private functions
	/**
	  * NOT TO BE USED BY EXTERNAL CLASSES
	  * <br><br>
	  * Initializes non-vital variables to default values.
	  */
	protected void initializeDefault()
	{
		super.initializeDefault();
		driversFinPos = new LinkedList<RFDriverR>();
		drivers = new LinkedList<RFDriverR>();
		fastestDrivers = new RFDriverR[1];
		fastestDrivers[0] = null;
		driverS1 = driverS2 = driverS3 = null;
		winner = null;
		lapLeader = null;
		setContactThreshold(0.15f);
		cautionCount = 0;
		cautionStartLap = null;
		cautionEndLap = null;
		mostGained = null;
		mostLost = null;
		mostLapsLed = 0;
		fastestOverallDriver = null;
	}
	
	//Iterators
	/**
	  * Gets a list that iterates through
	  * the drivers in the parent class.
	  * 
	  * @author Richard Matthews
	  */
	private class DriverIterator implements Iterator<RFDriverR>
	{
		private int position;
		private LinkedList<RFDriverR> list;
		
		public DriverIterator(LinkedList<RFDriverR> drivers)
		{
			list = drivers;
			position = 0;
		}
		
		public boolean hasNext()
		{
			if (position < list.size())
				return true;
			return false;
		}
		
		public RFDriverR next()
		{
			RFDriverR driver = null;
			driver = list.get(position++);
			return driver;
		}
		
		/**	Empty method as removal is not an option	*/
		public void remove() {	}
	}
}