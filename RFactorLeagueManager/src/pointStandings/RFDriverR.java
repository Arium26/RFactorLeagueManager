package pointStandings;

import utilities.LogFileManager;

/**
  * A record of a driver in a race.  The
  * constructors only set the basic stats of
  * driver.  Any other stats for the driver
  * (Times, position, etc) will need to be
  * entered using a mutator.
  * <br><br>
  * Currently only tracks overall laps led.
  * 
  * @author Richard Matthews
  * @version 1 (5-18-2013) [Original]
  */
public class RFDriverR extends Driver
{
	private static final String header = "Race Driver";
	
		//Time vars
	private Lap[] laps;
	private float sector1, sector2, sector3;
	private int sector1Lap, sector2Lap, sector3Lap;//Lap of best sector
	private float avgLap;
	private int fastLap, pitStahps;//Lap # of fastest lap, & lol @ stahp
	/**
	  * The amount of time spent in the pits.
	  * <br><br>
	  * This is approximated by finding the 
	  * difference between the laps with pit 
	  * stops (And both adjacent laps) and 
	  * the average lap time.<br><br>
	  * DO NOT CONSIDER THIS ACCURATE!!!
	  */
	private float pitTime;
	private double finishTime;//Time of driver's finish
		//Status vars
	private String reason;
	private int overallFinish, classFinish, classStart, overallStart;
	private int lapsComplete, overallLapsLed, classLapsLed;
	private boolean isDisqualified, finished;
	
	/**
	  * Mandatory empty constructor.  Do not use!!!
	  */
	public RFDriverR()	{	initializeDefault();	}
	
	/**
	  * Constructor for only using a name 
	  * as the primary key.
	  * 
	  * @param name - The name of the driver
	  * @param carClass - The class of the car
	  */
	public RFDriverR(String name, String carClass)
	{
		LogFileManager.logPrint(header, "Partial constructor 2");
		
		initializeDefault();

		setName(name);
		setCarClass(carClass);
	}
	
	/**
	  * Constructor for only using the car number
	  * as the primary key.
	  * 
	  * @param carNumber - The number of the car
	  * @param teamName - The name of the team
	  * @param carClass - The class of the car
	  */
	public RFDriverR(String carNumber, String teamName, String carClass)
	{
		LogFileManager.logPrint(header, "Partial constructor 2");
		
		initializeDefault();
		setCarNumber(carNumber);
		setTeamName(teamName);
		setCarClass(carClass);
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
	public RFDriverR(String name, String carNumber, String teamName, 
			String carClass, boolean isPlayer)
	{
		LogFileManager.logPrint(header, "Full constructor");
		
		initializeDefault();
		setName(name);
		setCarNumber(carNumber);
		setTeamName(teamName);
		setCarClass(carClass);
		setPlayer(isPlayer);
	}
	
	protected void initializeDefault()
	{
		super.initializeDefault();
		pitStahps = 0;
		pitTime = 0.0f;
		sector1 = 999.99f;
		sector2 = 999.99f;
		sector3 = 999.99f;
		sector1Lap = 0;
		sector2Lap = 0;
		sector3Lap = 0;
		finishTime = 0.0;
		isDisqualified = false;
		finished = true;
		lapsComplete = 0;
		overallLapsLed = 0;
		classLapsLed = 0;
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
		LogFileManager.logPrint(header, "Checking laps");
		
		//Set lap amount
		lapsComplete = times.length;
		
		//Handle empty array
		if (times.length == 0 || times == null)
		{
			setDNF("No laps completed");
			return false;
		}
		
		//Deal with laps
		int fast = 0;
		double total = 0.0;
		float tempPit = 0.0f;
		pitStahps = 0;
		int count = 0, pitLaps = 0, lastPit = -1;
		for(int i = 0; i < lapsComplete; i++)
		{
			//Find average lap
			if (times[i].getTotalTime() < 800.0 && !times[i].isPitIn())
			{
				count++;
				total += times[i].getTotalTime();
				if (times[fast].getTotalTime() > times[i].getTotalTime())
					fast = i;
			}
			
			//Find sector stats
			setSector1Time(times[i].getSector1(), i+1);
			setSector2Time(times[i].getSector2(), i+1);
			setSector3Time(times[i].getSector3(), i+1);
			
			//Find pit stats
			if (times[i].isPitIn())
			{
				pitStahps++;
				pitLaps++;
				tempPit += times[i+1].getTotalTime();
				
				//Handle pit stops on previous laps 
				if (lastPit < i-1)
				{
					pitLaps++;
					tempPit += times[i].getTotalTime();
				}
				if (lastPit < i-2)
				{
					pitLaps++;
					tempPit += times[i-1].getTotalTime();
				}
				
				lastPit = i;
			}
			
			//Record overall laps led
			if (times[i].getPosition() == 1)
				overallLapsLed++;
			
			//Record finish time on a DNF
			if (finishTime < 1 && i == (lapsComplete-1))
				finishTime = times[i].getStartTime();
		}
		
		//Record lap related variables
		laps = times;
		fastLap = fast;
		avgLap = (float) (total/count);
		avgLap = shortenTime(avgLap);
		pitTime = tempPit-(avgLap*pitLaps);
		classLapsLed = overallLapsLed;//TODO Change in future versions
		
		return true;
	}
	
	//Accessors
		//Status functions
	/**	Did the player run too many laps, crash a competitor, etc?	*/
	public boolean isDisqualified()	{	return isDisqualified;	}
	/** Did the player's car break or did the player quit?	*/
	public boolean isFinished()	{	return finished;	}
	public String getReason()	{	return reason;	}
	public int getOverallFinish()	{	return overallFinish;	}
	public int getClassFinish()	{	return classFinish;	}
	public int getOverallStart()	{	return overallStart;	}
	public int getClassStart()	{	return classStart;	}
	public float getPitTime()	{	return pitTime;	}
	public int getPitStops()	{	return pitStahps;	}
		//Lap functions
	public Lap[] getLapTimes()	{	return laps;	}
	public double getAverageLap()	{	return avgLap;	}
		/**	Gets the lap specified by the three best sector times	*/
	public float getDreamLap()	{	return sector1+sector2+sector3;	}
		/**	Gets the time for the fastest lap	*/
	public float getFastestTime()	{	return shortenTime(laps[fastLap].getTotalTime());	}
		/**	Gets the lap number on which the fastest lap occurred	*/
	public int getFastestLap()	{	return fastLap;	}
	public int getLapCount()	{	return lapsComplete;	}
	public int getOverallLapsLed()	{	return overallLapsLed;	}
	public int getClassLapsLed()	{	return classLapsLed;	}
	public double getFinishTime()	{	return finishTime;	}
	/**
	  * Gets the driver's position on the given lap number
	  * @param lapNum - Lap number to check
	  * @return Position on the given lap,
	  * or 0 if lap not specified (Due to 
	  * bad input or less laps completed)
	  */
	public int getLapPosition(int lapNum)
	{
		LogFileManager.logPrint(header, "Checking lap position for lap "+lapNum);
		
		int position = 0;
		
		try
		{
			Lap lap = laps[lapNum-1];
			position = lap.getPosition();
		}
		catch (ArrayIndexOutOfBoundsException ex)
		{
			ex.printStackTrace();
		}
		
		return position;
	}
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
	public void setOverallFinish(int position)	{	overallFinish = position;	}
	public void setClassFinish(int position)	{	classFinish = position;	}
	public void setOverallStart(int position)	{	overallStart = position;	}
	public void setClassStart(int position)	{	classStart = position;	}
	public void setOverallLapsLed(int laps)
	{
		overallLapsLed = laps;
		setClassLapsLed(laps);//TODO Change in future versions
	}
	public void setClassLapsLed(int laps)	{	classLapsLed = laps;	}
	public void setFinishTime(double time)	{	if (time > finishTime) finishTime = time;	}
	/**
	  * This seems pretty self explanatory (Same with other sectors)
	  */
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
	/**
	  * Sets the driver as not finishing.
	  * 
	  * @param why - The reason for not finishing
	  */
	public void setDNF(String why)
	{
		reason = why;
		finished = false;
	}
}