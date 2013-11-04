package pointStandings;

import java.util.Iterator;
import java.util.LinkedList;

import utilities.LogFileManager;

/**
  * A class for storing the results of 
  * a qualifying session.
  * 
  * @author Richard Matthews
  */
public class RFQualifyingResults extends Results
{
	private String header = "Qualifying Results";
	
		//List of drivers competing
	private LinkedList<RFDriverQ> drivers;//The drivers in qualifying
		//Timing stats
	private int laps, time;
	private RFDriverQ[] fastestDrivers;//Fastest for each class
	private RFDriverQ driverS1, driverS2, driverS3;//Driver holding fast time
	private RFDriverQ pole;//Driver on pole position
	private float contactThreshold;
	
	//Setting up for a future version here
//	private boolean calculated;
	
	//TODO Detect incidents in future
	//TODO Track overall lead progression
	//TODO Track individual position progression?
	
	/**
	  * Empty Constructor establishing basic information
	  */
	public RFQualifyingResults()	{	initializeDefault();	}
	
	/**
	  * Full constructor accepting all basic 
	  * information about the session.
	  * 
	  * @param track - The name of the track
	  * @param event - The name of the event
	  * @param length - The length of the track
	  * @param laps - The maximum amount of laps allowed
	  * @param time - The maximum amount of time allowed
	  */
	public RFQualifyingResults(String track, String event, String mod,
			float length, int laps, int time)
	{
		LogFileManager.logPrint(header, "Constructor");
		initializeDefault();
		setTrackName(track);
		setEventName(event);
		setModName(mod);
		setTrackLength(length);
		this.laps = laps;
		this.time = time;
	}
	
	/**
	  * For use in future versions where edits can be made.
	  * 
	  * @return True, if successful
	  * @deprecated
	  */
	public boolean calculatePositions()	{	return false;	}
	
	/**
	  * Gets the driver placing in a certain position
	  * 
	  * @param position - The position to search for
	  * @return The driver at that position, or null if none
	  */
	public RFDriverQ getDriverByPosition(int position)
	{
		RFDriverQ driver = null;
		
		Iterator<RFDriverQ> iterator = getDriverIterator();
		boolean check = iterator.hasNext();
		while(check)
		{
			//Check position
			RFDriverQ temp = iterator.next();
			if (temp.getOverallPosition() == position)
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
	
	//Accessors
	public int getMaxLaps()	{	return laps;	}
	public int getMaxTime()	{	return time;	}
	public float getContactThreshold()	{	return contactThreshold;	}
	public RFDriverQ[] getFastestLap()	{	return fastestDrivers;	}
	public RFDriverQ getPoleWinner()	{	return pole;	}
	public float getDreamLap()
	{
		return getSectorDriver(1).getSectorTime(1)+
				getSectorDriver(2).getSectorTime(2)+
				getSectorDriver(3).getSectorTime(3);
	}
	/**
	  * Gets the driver that set the fast time 
	  * in the sector in question
	  * @param sector - The sector to find
	  * @return The driver with the fastest time, or null if none
	  */
	public RFDriverQ getSectorDriver(int sector)
	{
		if (sector == 1)	return driverS1;
		if (sector == 2)	return driverS2;
		if (sector == 3)	return driverS3;
		return null;
	}
	public Iterator<RFDriverQ> getDriverIterator()	{	return new DriverIterator(drivers);	}
	
	//Mutators
	/**
	  * Adds a driver to those listed for the session.
	  * This is where the list of classes is built.
	  * This should only be done after the driver
	  * is completely fleshed out.
	  * 
	  * @param driver - The driver to be added
	  * @return True, if successful
	  */
	public boolean addDriver(RFDriverQ driver)
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
			RFDriverQ[] tempName = new RFDriverQ[newLength];
			for(int i = 0; i < oldLength; i++)
				tempName[i] = fastestDrivers[i];
			tempName[oldLength] = driver;
			fastestDrivers = tempName;
		}
		
		//Check if pole winner
		if (driver.getOverallPosition() == 1)
			pole = driver;
		
		//Check stats vs. best
		setFastestTimes(driver);
		
		return drivers.add(driver);
	}
	public void setMaxLaps(int laps)	{	this.laps = laps;	}
	public void setMaxTime(int time)	{	this.time = time;	}
	public void setContactThreshold(float amount)	{	contactThreshold = amount;	}
	public boolean setFastestTimes(RFDriverQ driver)
	{
		LogFileManager.logPrint(header, "Checking fast times");
		//Null catch
		if (fastestDrivers[0] == null)
			fastestDrivers[0] = driver;
		
		//Find class
		int classPos = 0;
		for (int i = 0; i < fastestDrivers.length; i++)
			if (driver.getCarClass() == fastestDrivers[i].getCarClass())
				classPos = i;
		
		//Set fast lap (If fastest)
		double lap = driver.getFastestTime();
		boolean fastest = false;
		if (lap != 0.0f || lap != 999.9f)
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
			//Check each sector
			for (int i = 1; i < 4; i++)
				if (setSectorTime(driver, i))
					fastest = true;
		}
		
		return fastest;
	}
	/**
	  * Attempt to set fastest sector time for sector specfied
	  * 
	  * @param sector - The sector to check
	  * @param driver - The driver setting the time
	  * @return True if new fast sector time
	  */
	private boolean setSectorTime(RFDriverQ driver, int sector)
	{
		LogFileManager.logPrint(header, "Checking sector "+sector+" time");
		
		if (sector == 1)
			if (driver.getSectorTime(1) < driverS1.getSectorTime(1))
			{
				driverS1 = driver;
				return true;
			}
		
		if (sector == 2)
			if (driver.getSectorTime(2) < driverS2.getSectorTime(2))
			{
				driverS2 = driver;
				return true;
			}
		
		if (sector == 3)
			if (driver.getSectorTime(3) < driverS3.getSectorTime(3))
			{
				driverS3 = driver;
				return true;
			}
		
		return false;
	}
	
	//Other functions
	protected void initializeDefault()
	{
		super.initializeDefault();
		drivers = new LinkedList<RFDriverQ>();
		fastestDrivers = new RFDriverQ[1];
		fastestDrivers[0] = null;
		driverS1 = null;
		driverS2 = null;
		driverS3 = null;
		pole = null;
		contactThreshold = 0.15f;
	}
	
	/**
	  * Finds the difference between the leader's
	  * qualifying time and this driver's time.
	  * 
	  * @param driver - Driver to check
	  * @return String representing time difference
	  */
	public String findDifference(RFDriverQ driver)
	{
		LogFileManager.logPrint(header, "Finding difference in drivers");
		String time = "-------";
		
		//If not pole winner
		if (driver.getOverallPosition() != 1)
		{
			float difference = (float) (driver.getFastestTime()-pole.getFastestTime());
			time = "+"+RFDriverR.shortenTime(difference);
		}
		
		return time;
	}
	
	//Iterator
	/**
	  * Gets a list that iterates through
	  * the drivers in the parent class.
	  * 
	  * @author Richard Matthews
	  */
	private class DriverIterator implements Iterator<RFDriverQ>
	{
		private int position;
		private LinkedList<RFDriverQ> list;
		
		public DriverIterator(LinkedList<RFDriverQ> drivers)
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
		
		public RFDriverQ next()
		{
			RFDriverQ driver = null;
			driver = list.get(position++);
			return driver;
		}
		
		/**	Empty method as removal is not an option	*/
		public void remove() {	}
	}
}