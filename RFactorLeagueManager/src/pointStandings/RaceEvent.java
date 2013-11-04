package pointStandings;

/**
  * Holds the information pertaining to
  * the events of a race weekend.  This
  * includes all session results and 
  * complete with list of drivers 
  * competing.  The track, venue, and
  * other global variables for the event
  * will by set by the race session.
  * 
  * @author Richard Matthews
  * @version 1 (5-29-2013)
  */
public final class RaceEvent
{
	//TODO Create manager for this shit
	private RFRaceResults race;
	private RFQualifyingResults quali;
//	private RFPracticeResults[] practice;
//	private RFWarmupResults warmup;
	private String trackName, eventName, modName;
	private long eventID;
	private float trackLength;
	
		//Accessors
	public float getTrackLength()	{	return trackLength;	}
	public long getEventID()	{	return eventID;	}
	public String getTrackName()	{	return trackName;	}
	public String getEventName()	{	return eventName;	}
	public String getModName()	{	return modName;	}
//	public RFWarmupResults getWarmupResults()	{	return warmup;	}
	public RFQualifyingResults getQualifyingResults()	{	return quali;	}
	public RFRaceResults getRaceResults()	{	return race;	}
		//Mutators
	private void setTrackLength(float trackLength)	{	this.trackLength = trackLength;	}
	private void setEventID(long eventID)	{	this.eventID = eventID;	}
	private void setTrackName(String trackName)	{	this.trackName = trackName;	}
	private void setEventName(String eventName)	{	this.eventName = eventName;	}
	private void setModName(String modName)	{	this.modName = modName;	}
//	public void setWarmupResults(RFWarmupResults results)	{	warmup = results;	}
	public void setQualifyingResults(RFQualifyingResults quali)	{	this.quali = quali;	}
	public void setRaceResults(RFRaceResults results)
	{
		race = results;
		setTrackLength(results.getTrackLength());
		setEventID(results.getEventID());
		setTrackName(results.getTrackName());
		setEventName(results.getEventName());
		setModName(results.getModName());
	}
}