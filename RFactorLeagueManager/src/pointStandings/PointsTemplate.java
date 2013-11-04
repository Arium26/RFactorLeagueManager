package pointStandings;

/**
  * Keeps track of the amount of points scored for accomplishing a variety of feats
  * 
  * @author Richard Matthews
  * @version 1 (6-18-2013)
  */
public final class PointsTemplate
{
		//Basic
	private boolean mirrorOwner;//Driver points = owner points
	private boolean outputFile, fullOutput;//Output file vars
	private boolean individualQualiCSVs, individualRaceCSVs;//CSV vars
	private boolean classPoints;//Also find out class points
		//Qualifying
	private int[] qualifyingPosition;//Points for qualifying position
	private int qualiFastSector;//Points for fast sector in qualifying
		//Race
	private int[] finishPosition;//Points for finishing by driver
	private int[] ownerFinishPoints;//Points for finishing by car number
	private int minimumFinishPoints;//Minimum points for racing
	private int mostLapsLed, lapsLed;//Points for leading laps
	private int raceFastLap, raceFastSector;//Points for fastest lap/sector
	private int mostGainedBonus;//Bonus points for gaining the most positions
	private int dnfPoints, dnfPenalty;//Points on dnf and penalty for dnf (Not both)
	
	public PointsTemplate()
	{
		initializeDefault();
	}
	
	/**
	  * Initialize the variables with default values
	  */
	private void initializeDefault()
	{
			//Basic variables
		outputFile = true;
		fullOutput = true;
		individualQualiCSVs = true;
		individualRaceCSVs = true;
		mirrorOwner = true;
		classPoints = true;//TODO Make this true
			//Qualifying points
		int[] tempInt1 = {3, 0, 0};
		qualifyingPosition = tempInt1;
		qualiFastSector = 0;
			//Race points
		int[] tempInt2 = {75, 70, 65, 61, 58, 55, 52, 49, 46, 43,
				40, 38, 36, 34, 32, 30, 28, 26, 24, 22,
				20, 19, 18, 17, 16, 15, 14, 13, 12, 11, 10, 9, 8, 7};
		finishPosition = tempInt2;
		if (mirrorOwner)
			ownerFinishPoints = tempInt2;
		else
			ownerFinishPoints = tempInt2;//False variable read
		minimumFinishPoints = 6;
		mostLapsLed = 3;
		lapsLed = 2;
		raceFastLap = 2;
		raceFastSector = 0;
		mostGainedBonus = 0;
		dnfPoints = -1;//-1 means inactive
		dnfPenalty = 0;
	}
	
	/**
	  * A backup of my initial initialization file
	  */
	private void initializeDefaultBackup()
	{
			//Basic variables
		outputFile = true;
		fullOutput = true;
		individualQualiCSVs = true;
		individualRaceCSVs = true;
		mirrorOwner = true;
		classPoints = true;//TODO Make this true
			//Qualifying points
		int[] tempInt1 = {3, 0, 0};
		qualifyingPosition = tempInt1;
		qualiFastSector = 0;
			//Race points
		int[] tempInt2 = {75, 70, 65, 61, 58, 55, 52, 49, 46, 43,
				40, 38, 36, 34, 32, 30, 28, 26, 24, 22,
				20, 19, 18, 17, 16, 15, 14, 13, 12, 11, 10, 9, 8, 7};
		finishPosition = tempInt2;
		if (mirrorOwner)
			ownerFinishPoints = tempInt2;
		else
			ownerFinishPoints = tempInt2;//False variable read
		minimumFinishPoints = 6;
		mostLapsLed = 3;
		lapsLed = 2;
		raceFastLap = 2;
		raceFastSector = 0;
		mostGainedBonus = 0;
		dnfPoints = -1;//-1 means inactive
		dnfPenalty = 0;
	}
	
		//Accessors
	public boolean shouldClassPoints()	{	return classPoints;	}
	public int pointsMostGained()	{	return mostGainedBonus;	}
	public int pointsQualiFastSector()	{	return qualiFastSector;	}
	public int pointsRaceFastLap()	{	return raceFastLap;	}
	public int pointsRaceFastSector()	{	return raceFastSector;	}
	public int pointsMostLapsLed()	{	return mostLapsLed;	}
	public int pointsLapsLed()	{	return lapsLed;	}
	public boolean shouldIndividualQCSV()	{	return individualQualiCSVs;	}
	public boolean shouldIndividualRCSV()	{	return individualRaceCSVs;	}
	public boolean shouldOutput()	{	return outputFile;	}
	public boolean shouldFullOutput()
	{
		if (!outputFile)
			return false;
		return fullOutput;
	}
	
	//TODO Mutators
	
	/**
	  * Gets the amount of points for qualifying in given position
	  * @param position - The qualifying position to check (Can be out of known range)
	  * @return Amount of points for qualifying in given position (Or min if out of range)
	  */
	public int getPointsQualifyingPosition(int position)
	{
		int points = 0;
		try
		{
			points = qualifyingPosition[position-1];
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			points = 0;
		}
		
		return points;
	}
	
	/**
	  * Gets the amount of points for finishing in given position
	  * @param position - The finish position to check (Can be out of known range)
	  * @param dnf - True if the driver failed to finish
	  * @return Amount of points for finishing in given position (Or min if out of range)
	  */
	public int getPointsFinishPosition(int position, boolean dnf)
	{
		int points = 0;
		try
		{
			points = finishPosition[position-1];
			if (dnf)
			{
				points -= dnfPenalty;
				if (dnfPoints > -1)
					points = dnfPoints;
			}
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			points = minimumFinishPoints;
			if (dnf)
			{
				points -= dnfPenalty;
				if (dnfPoints > -1)
					points = dnfPoints;
			}
		}
		
		return points;
	}
	
	/**
	  * Gets the amount of points for finishing in given position for car owners
	  * @param position - The finish position to check (Can be out of known range)
	  * @param dnf - True if the driver failed to finish
	  * @return Amount of points for finishing in given position (Or min if out of range)
	  */
	public int getOwnerPointsFinishPosition(int position, boolean dnf)
	{
		int points = 0;
		try
		{
			points = ownerFinishPoints[position-1];
			if (dnf)
			{
				points -= dnfPenalty;
				if (dnfPoints > -1)
					points = dnfPoints;
			}
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			points = minimumFinishPoints;
			if (dnf)
			{
				points -= dnfPenalty;
				if (dnfPoints > -1)
					points = dnfPoints;
			}
		}
		
		return points;
	}
}