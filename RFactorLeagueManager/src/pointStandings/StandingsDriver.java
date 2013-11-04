package pointStandings;

import java.util.Iterator;
import java.util.LinkedList;

import utilities.LogFileManager;

/**
  * A driver for storing in the standings index.
  * Number, car class, and car number are all
  * based on the latest results entered into
  * the driver manager and are not indicative of
  * the driver's related information.
  * 
  * @author Richard Matthews
  * @version 1 (5-29-2013)
  */
public final class StandingsDriver extends Driver
{
	private int driverID;
	private int positionOverall;
	private int positionClass;
	private LinkedList<DriverEvent> events;
	private static final String header = "StandingsDriver";
	
	public StandingsDriver()
	{
		driverID = 0;
		positionOverall = 0;
		positionClass = 0;
		events = new LinkedList<DriverEvent>();
	}
	
	/**
	  * Checks if the event with the given id exists
	  * @param id - The id of the event (datetime in XML file)
	  * @return The event with the ID, or an empty event otherwise
	  */
	public DriverEvent getEvent(long id)
	{
		DriverEvent event = new DriverEvent();
		event.setEventID(id);
		
		Iterator<DriverEvent> iterate = getEventIterator();
		while (iterate.hasNext())
		{
			DriverEvent tempEvent = iterate.next();
			if (tempEvent.getEventID() == id)
				event = tempEvent;
		}
		
		return event;
	}
	
	public void addEvent(DriverEvent event)
	{
		if (!events.contains(event))
			events.add(event);
	}
	
	public int getOverallPointTotal()
	{
		int points = 0;
		
		Iterator<DriverEvent> iterator = getEventIterator();
		while(iterator.hasNext())
		{
			DriverEvent event = iterator.next();
			int temp = event.getTotalPoints();
			points += temp;
			LogFileManager.logPrint(header, getName()+" scored "+temp+
					" points overall at event #"+event.getEventID());
		}
		
		LogFileManager.logPrint(header, getName()+" has "+points+" points [Overall]");
		
		return points;
	}
	
	public int getClassPointTotal()
	{
		int points = 0;
		
		Iterator<DriverEvent> iterator = getEventIterator();
		while(iterator.hasNext())
		{
			DriverEvent event = iterator.next();
			int temp = event.getTotalPoints();
			points += temp;
			LogFileManager.logPrint(header, getName()+" scored "+temp+
					" points in class at event #"+event.getEventID());
		}
		
		LogFileManager.logPrint(header, getName()+" has "+points+" points [Class]");
		
		return points;
	}
	
	public int getOverallPosition()	{	return positionOverall;	}
	public int getClassPosition()	{	return positionClass;	}
	
	public Iterator<DriverEvent> getEventIterator()	{	return events.iterator();	}
	
	public int getNumberOfRaces()
	{
		int count = 0;
		Iterator<DriverEvent> iterator = getEventIterator();
		while(iterator.hasNext())
		{
			iterator.next();
			count++;
		}
		return count;
	}
	
	public void setPositionClass(int position, Object caller)
	{
		if (caller instanceof DriverManager)
			positionClass = position;
	}
	
	public void setPositionOverall(int position, Object caller)
	{
		if (caller instanceof DriverManager)
			positionOverall = position;
	}
	
	/**
	  * Sets the id for this driver (Can 
	  * only be called by a class that has
	  * permission)
	  * @param caller - The object calling this function
	  */
	public void setID(Object caller, int id)
	{
		if (caller instanceof DriverManager)
		{
			driverID = id;
		}
	}
	
	public int getDriverID()	{	return driverID;	}
}