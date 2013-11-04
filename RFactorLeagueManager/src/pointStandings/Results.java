package pointStandings;

import java.util.Iterator;

import utilities.LogFileManager;

/**
  * An identifier superclass detailing
  * some of the attributes shared by all
  * versions of results.
  * 
  * @author Richard Matthews
  * @version 1 (5-21-2013)
  */
public abstract class Results
{
	private String[] classes;
	private String trackName, eventName, modName;
	private float trackLength;
	private long eventID;
	private boolean idLock;
	private String header = "Results";
	
	/**
	  * For use by child classes only.
	  * <br><br>
	  * Initializes variables with default values
	  */
	protected void initializeDefault()
	{
		trackName = eventName = modName = "";
		trackLength = 0.0f;
		classes = null;
		eventID = -1;
		idLock = false;
	}
	
		//Accessors
	/**	Event ID is shared among sessions at an event (Practice, Race, Quali...	*/
	public long getEventID()	{	return eventID;	}
	public String getTrackName()	{	return trackName;	}
	public String getEventName()	{	return eventName;	}
	public String getModName()	{	return modName;	}
	public float getTrackLength()	{	return trackLength;	}
	
		//Mutators
	public void setEventID(long id)
	{
		if (!idLock)
			eventID = id;
		idLock = true;
	}
	public void setModName(String name)	{	modName = name;	}
	public void setTrackName(String name)	{	trackName = name;	}
	public void setEventName(String name)	{	eventName = name;	}
	public void setTrackLength(float length)	{	trackLength = length;	}
	
	//Car class related functions
	/**
	  * Attempts to add a class name to the list
	  * @param className - Name of the class to try to add
	  * @return True if the class name was added to the list
	  */
	public boolean setCarClass(String className)
	{
		LogFileManager.logPrint(header, "Checking car class");
		
		if (classes != null)
		{
			boolean check = false;
			for(int i = 0; i < classes.length && !check; i++)
				if (className.matches(classes[i]))
					check = true;
			
			//Add new String to the array
			if (!check)
			{
				String[] temp = new String[classes.length+1];
				for(int i = 0; i < classes.length; i++)
					temp[i] = classes[i];
				
				temp[classes.length] = className;
				
				classes = temp;
				
				return true;
			}
		}
		else
		{
			classes = new String[1];
			classes[0] = className;
			return true;
		}
		
		return false;
	}
	
	/**
	  * Gets an iterator for the car classes
	  * @return Iterator of Strings
	  */
	public Iterator<String> getClassIterator()	{	return new ClassIterator(classes.clone());	}
	
	/**
	  * Gets a list that iterates through
	  * the classes in the parent class.
	  * 
	  * @author Richard Matthews
	  */
	private class ClassIterator implements Iterator<String>
	{
		private int position;
		private String[] list;
		
		public ClassIterator(String[] classes)
		{
			list = classes;
			position = 0;
		}
		
		public boolean hasNext()
		{
			if (position < list.length)
				return true;
			return false;
		}
		
		public String next()
		{
			String cla = null;
			cla = list[position++];
			return cla;
		}
		
		/**	Empty method as removal is not an option	*/
		public void remove() {	}
	}
}