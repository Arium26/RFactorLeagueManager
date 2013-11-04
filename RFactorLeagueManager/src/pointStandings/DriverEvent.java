package pointStandings;

/**
  * A race weekend from a StandingsDriver's point of view.
  * 
  * @author Richard Matthews
  * @version 1 (5-29-2013)
  */
public final class DriverEvent
{
		//Basic information
	private long eventID;//Primary key for the event
	private String carNumber, teamName, carClass;//Can change between races
		//Results-based variables
			//Qualifying
	private int pointsStartOverall, pointsStartClass;//Points scored for quali spot
			//Race
	private int lapsLed;//Number of laps led
	private int pointsFinishOverall, pointsFinishClass;//Points scored for finish
	private boolean fastestLapRace;//Did this driver get the fast lap
	private boolean mostLapsLed;//Did this driver lead the most laps
	private boolean mostGained;//Did you gain the most positions?
			//Both
	private int classStart, classFinish, overallStart, overallFinish;//Start/Finish spots
	private int fastSectorsQualifying, fastSectorsRace;//Number of fast sectors set (overall)
	private boolean dnf;//Did ya finish?
	private int bonusPointsClass, bonusPoints;//Points scored beyond start/finish position
		//Timing variables
	private float qualifyingTime, fastTimeRace, fastTimeOverall;//Fastest times
	private float fastSector1, fastSector2, fastSector3;//Fastest sector times overall
	
	public void initializeDefault()
	{
		carNumber = "";
		teamName = "";
		pointsStartOverall = 0;
		pointsStartClass = 0;
		pointsFinishOverall = 0;//Impossible, I know
		pointsFinishClass = 0;//Impossible, I know
		lapsLed = 0;
		fastestLapRace = false;
		mostLapsLed = false;
		classStart = 0;
		classFinish = 0;
		overallStart = 0;
		overallFinish = 0;
		fastSectorsQualifying = 0;
		fastSectorsRace = 0;
		dnf = false;
		bonusPointsClass = 0;
		bonusPoints = 0;
		qualifyingTime = 999.9f;
		fastTimeRace = 999.9f;
		fastSector1 = 999.9f;
		fastSector2 = 999.9f;
		fastSector3 = 999.9f;
		fastTimeOverall = 999.9f;;
	}
	
		//Accessors
			//Information variables
	public long getEventID()	{	return eventID;	}
	public String getCarNumber()	{	return carNumber;	}
	public String getTeamName()		{	return teamName;	}
	public String getCarClass()	{	return carClass;	}
			//Qualifying variables
	public int getQualifyingPointsOverall()	{	return pointsStartOverall;	}
	public int getQualifyingPointsClass()	{	return pointsStartClass;	}
	public int getStartingPositionOverall()	{	return overallStart;	}
	public int getStartingPositionClass()	{	return classStart;	}
			//Race variables
	public int getLapsLed()	{	return lapsLed;	}
	public int getRacePointsOverall()	{	return pointsFinishOverall;	}
	public int getRacePointsClass()	{	return pointsFinishClass;	}
	public boolean hasFastestLap()	{	return fastestLapRace;	}
	public boolean hasMostLapsLed()	{	return mostLapsLed;	}
	public int getFinishPositionOverall()	{	return overallFinish;	}
	public int getFinishPositionClass()	{	return classFinish;	}
	public boolean hasFinished()	{	return dnf;	}
	public boolean hasMostGained()	{	return mostGained;	}
	public int getPositionsGainedOverall()	{	return overallFinish-overallStart;	}
	public int getPositionsGainedClass()	{	return classFinish-classStart;	}
			//Bonus point variables
	public int getQualifyingFastSectorCount()	{	return fastSectorsQualifying;	}
	public int getRaceFastSectorCount()	{	return fastSectorsRace;	}
	public int getBonusPointsOverall()	{	return bonusPoints;	}
	public int getBonusPointsClass()	{	return bonusPointsClass;	}
			//Timing variables
	public float getQualifyingTime()	{	return qualifyingTime;	}
	public float getFastestRaceLap()	{	return fastTimeRace;	}
	public float getOverallFastTime()	{	return fastTimeOverall;	}
	public float getFastSectorTime(int sector)
	{
		if (sector == 1)
			return fastSector1;
		if (sector == 2)
			return fastSector2;
		if (sector == 3)
			return fastSector3;
		return 999.9f;
	}
	public float getDreamLap()
	{
		return fastSector1+fastSector2+fastSector3;
	}
	
		//Mutators
			//Information variables
	public void setEventID(long id)	{	eventID = id;	}
	public void setCarNumber(String number)	{	carNumber = number;	}
	public void setTeamName(String name)	{	teamName = name;	}
	public void setCarClass(String cla)	{	carClass = cla;	}
			//Qualifying variables
	public void setQualifyingPointsOverall(int points)	{	pointsStartOverall = points;	}
	public void setQualifyingPointsClass(int points)	{	pointsStartClass = points;	}
	public void setStartingPositionOverall(int position)	{	overallStart = position;	}
	public void setStartingPositionClass(int position)	{	classStart = position;	}
			//Race variables
	public void setLapsLed(int laps)	{	lapsLed = laps;	}
	public void setRacePointsOverall(int points)	{	pointsFinishOverall = points;	}
	public void setRacePointsClass(int points)	{	pointsFinishClass = points;	}
	public void setFastestLap(boolean fast)	{	fastestLapRace = fast;	}
	public void setMostLapsLed(boolean most)	{	mostLapsLed = most;	}
	public void setFinishPositionOverall(int position)	{	overallFinish = position;	}
	public void setFinishPositionClass(int position)	{	classFinish = position;	}
	public void setFinished(boolean finish)	{	dnf = finish;	}
	public void setMostGained(boolean gain)	{	mostGained = gain;	}
			//Bonus point variables
	public void setQualifyingFastSectorCount(int sectors)	{	fastSectorsQualifying = sectors;	}
	public void setRaceFastSectorCount(int sectors)	{	fastSectorsRace = sectors;	}
	public void setBonusPointsOverall(int points)	{	bonusPoints = points;	}
	public void addBonusPointsOverall(int points)	{	bonusPoints += points;	}
	public void setBonusPointsClass(int points)	{	bonusPointsClass = points;	}
	public void addBonusPointsClass(int points)	{	bonusPointsClass += points;	}
			//Timing variables
	public void setQualifyingTime(float time)
	{
		qualifyingTime = time;
		setOverallFastTime(time);
	}
	public void setFastestRaceLap(float time)
	{
		fastTimeRace = time;
		setOverallFastTime(time);
	}
	public void setOverallFastTime(float time)
	{
		if (time > 0.0f && time < fastTimeOverall)
			fastTimeOverall = time;
	}
	public void setFastSectorTime(int sector, float time)
	{
		if (sector == 1 && time < fastSector1 && time > 0.0f)
			fastSector1 = time;
		if (sector == 2 && time < fastSector2 && time > 0.0f)
			fastSector2 = time;
		if (sector == 3 && time < fastSector3 && time > 0.0f)
			fastSector3 = time;
		
		return;
	}
	
	
		//Private functions
	
		//Calculate points
	public int getTotalPoints()
	{
		return bonusPoints+pointsStartOverall+pointsFinishOverall;
	}
	public int getTotalClassPoints()
	{
		return bonusPointsClass+pointsStartClass+pointsFinishClass;		
	}
}