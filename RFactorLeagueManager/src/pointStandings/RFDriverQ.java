package pointStandings;

import utilities.LogFileManager;

/**
  * A record of a driver in a race.  The
  * constructors only set the basic stats
  * of the driver.  Any other stats for the 
  * driver (Times, position, etc) will 
  * need to be entered using a mutator.
  * 
  * @author Richard Matthews
  * @version 1 (5-5-2013) [Original]
  * @version 2 (5-25-2013) [Like Race version]
  */
public class RFDriverQ extends Driver
{
	private static final String header = "Quali Driver";
	
		//Time vars
	private Lap[] laps;
	private int timedLaps;
	private float sector1, sector2, sector3;
	private int sector1Lap, sector2Lap, sector3Lap;
	private double avgLap;
	private int fastLap;//Tracks lap number of the fastest lap
		//Status vars
	private String reason;
	private int position, classPosition;
	private boolean isDisqualified, finished;
	//TODO Track escapes?
	
	/**
	  * Mandatory empty constructor.  Do not use!!!
	  */
	public RFDriverQ()	{	initializeDefault();	}
	
	/**
	  * Constructor for only using a name 
	  * as the primary key.
	  * 
	  * @param name - The name of the driver
	  * @param carClass - The class of the car
	  */
	public RFDriverQ(String name, String carClass)
	{
		LogFileManager.logPrint(header, "Partial constructor 1");

		initializeDefault();
		setName(name);
		setCarClass(carClass);
		sector1 = sector2 = sector3 = 999.99f;
	}
	
	/**
	  * Constructor for only using the car number
	  * as the primary key.
	  * 
	  * @param carNumber - The number of the car
	  * @param teamName - The name of the team
	  * @param carClass - The class of the car
	  */
	public RFDriverQ(String carNumber, String teamName, String carClass)
	{
		LogFileManager.logPrint(header, "Partial constructor 2");
		
		initializeDefault();
		setCarNumber(carNumber);
		setTeamName(teamName);
		setCarClass(carClass);
		sector1 = sector2 = sector3 = 999.99f;
	}
	
	/**
	  * Full constructor inputting all basic information
	  * 
	  * @param name - The name of the driver
	  * @param carNumber - The number of the car
	  * @param teamName - The name of the team
	  * @param carClass - The class of the car
	  * @param isPlayer - True if controlled by a player
	  */
	public RFDriverQ(String name, String carNumber, String teamName, 
			String carClass, boolean isPlayer)
	{
		LogFileManager.logPrint(header, "Full constructor");
		
		initializeDefault();
		setName(name);
		setCarNumber(carNumber);
		setTeamName(teamName);
		setCarClass(carClass);
		setPlayer(isPlayer);
		sector1 = sector2 = sector3 = 999.99f;
	}
	
	protected void initializeDefault()
	{
		super.initializeDefault();
		laps = null;
		sector1 = 999.99f;
		sector2 = 999.99f;
		sector3 = 999.99f;
		sector1Lap = sector2Lap = sector3Lap = 0;
		avgLap = 999.99;
		fastLap = -1;
		timedLaps = 0;
		reason = null;
		position = 0;
		classPosition = 0;
		laps = null;
		isDisqualified = false;
		finished = true;
	}
	
	/**
	  * Submit the times for this driver for this session.
	  * The calculations for average and fast times are done here.
	  * Only submit non-zero times (Throws off average and fast).
	  * 
	  * @param times - The lap times for the session
	  * @return True if successful
	  */
	public boolean submitTimes(Lap[] times)
	{
		LogFileManager.logPrint(header, "Handling lap times");
		
		//Handle empty array
		if (times.length == 0 || times == null)
			return submitTimes("Did not compete");
		
		//Handle lap stats
		int fast = fastLap;
		double total = 0.0;
		int count = 0;
		for(int i = 0; i < times.length; i++)
		{
			//Average and fast laps
			if (times[i].getTotalTime() < 800.0 && times[i].isComplete())
			{
				count++;
				total += times[i].getTotalTime();
				if (fast == -1)
					fast = i;
				else
					if (times[fast].getTotalTime() > times[i].getTotalTime())
						fast = i;
			}
			
			//Find sector stats
			setSector1Time(times[i].getSector1(), i+1);
			setSector2Time(times[i].getSector2(), i+1);
			setSector3Time(times[i].getSector3(), i+1);
		}
		
		//Record said laps
		laps = times;
		fastLap = fast;
		avgLap = total/count;
		timedLaps = count;
		
		return true;
	}
	
	/**
	  * Submit the fact that this player did not 
	  * complete a lap in qualifying and the reason why.
	  * This will set lap times to null and fast and avaerage laps to 0.0.
	  * 
	  * @param out - The reason for not completing a lap
	  * @return True if successful
	  */
	public boolean submitTimes(String out)
	{
		LogFileManager.logPrint(header, "Handling DNF");
		
		finished = false;
		reason = out;
		laps = null;
		fastLap = 0;
		avgLap = 0.0;
		return true;
	}
	
	//Accessors
		//Status functions
	/**	Did the player run too many laps, crash a competitor, etc?	*/
	public boolean isDisqualified()	{	return isDisqualified;	}
	/**	Did the player complete any laps?<br>(Race has a different definition)	*/
	public boolean isFinished()	{	return finished;	}
	public String getReason()	{	return reason;	}
	public int getOverallPosition()	{	return position;	}
	public int getClassPosition()	{	return classPosition;	}
		//Lap functions
	public Lap[] getLapTimes()	{	return laps;	}
	public double getAverageLap()	{	return avgLap;	}
		/**	Gets the time for the fastest lap	*/
	public float getFastestTime()
	{
		if (fastLap != -1)
			return laps[fastLap].getTotalTime();
		else
			return 999.9f;
	}
		/**	Gets the lap number on which the fastest lap occurred	*/
	public int getFastestLap()	{	return fastLap;	}
	/**	Gets the lap specified by the three best sector times	*/
	public float getDreamLap()	{	return sector1+sector2+sector3;	}
	public int getLapCount()	{	return laps.length;	}
	public int getTimedLapCount()	{	return timedLaps;	}
	/**
	  * Gets the lap number that the driver
	  * set the fastest sector time on.
	  * @param sectorNumber - Sector number (Range 1-3)
	  * @return Lap number (0 if invalid sector or not set)
	  */
	public int getSectorLap(int sectorNumber)
	{
		if (sectorNumber == 1)	return sector1Lap;
		if (sectorNumber == 2)	return sector2Lap;
		if (sectorNumber == 3)	return sector3Lap;
		return 0;
	}
	/**
	  * Gets the fast time for the sector specified,
	  * 
	  * @param sector - The sector to check (1, 2, or 3)
	  * @return The fast time for the sector, or
	  * 	999.99 if an unspecified sector or no time set.
	  */
	public float getSectorTime(int sector)
	{
		if (sector == 1)	return sector1;
		if (sector == 2)	return sector2;
		if (sector == 3)	return sector3;
		return 999.99f;
	}
	
	//Mutators
	public void setPosition(int position)	{	this.position = position;	}
	public void setClassPosition(int position)	{	classPosition = position;	}
	public void setSector1Time(float time, int lapNum)
	{
		if (time < sector1)
		{
			sector1 = time;
			sector1Lap = lapNum;
		}
	}
	public void setSector2Time(float time, int lapNum)
	{
		if (time < sector2)
		{
			sector2 = time;
			sector2Lap = lapNum;
		}
	}
	public void setSector3Time(float time, int lapNum)
	{
		if (time < sector3)
		{
			sector3 = time;
			sector3Lap = lapNum;
		}
	}
	public void setDisqualified(boolean disq, String why)
	{
		isDisqualified = disq;
		reason = why;
	}
}