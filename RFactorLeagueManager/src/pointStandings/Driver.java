package pointStandings;

import java.util.StringTokenizer;

import utilities.LogFileManager;

/**
  * A auperclass Driver to 
  * standardize the attributes shared 
  * by all versions of the drivers.
  * 
  * @author Richard Matthews
  * @version 1 (5-25-2013)
  */
public abstract class Driver
{
	private static final String header = "Driver";
	private String name, carNumber, carClass, teamName;
	private boolean isPlayer;
	
	/**
	  * SHOULD ONLY BE USED BY SUBCLASSES
	  * <br><br>
	  * Initializes the default values for
	  * the variables contained within
	  */
	protected void initializeDefault()
	{
		setName("");
		setCarNumber("26");
		setTeamName("");
		setCarClass("");
		setPlayer(false);
	}
	
	/**
	  * Shortens the time input to four decimal places
	  * 
	  * @param time - The time to shorten
	  * @return The edited float
	  */
	public static float shortenTime(float time)
	{
		LogFileManager.logPrint(header, "Shortening time output");
		
		float temp = time;
		
		StringTokenizer tokens = new StringTokenizer(""+temp, ".");
		String seconds = tokens.nextToken();
		String decimal = tokens.nextToken();
		if (decimal.length() > 4)
			decimal = decimal.substring(0, 4);
		String number = seconds+"."+decimal;
		temp = Float.parseFloat(number);
		
		return temp;
	}
	
		//Accessors
	public String getName()	{	return name;	}
	public String getCarNumber()	{	return carNumber;	}
	public String getCarClass()	{	return carClass;	}
	public String getTeamName()	{	return teamName;	}
	public boolean isPlayer()	{	return isPlayer;	}
	
		//Mutators
	public void setName(String name)	{	this.name = name;	}
	public void setCarNumber(String number)	{	carNumber = number;	}
	public void setCarClass(String className)	{	carClass = className;	}
	public void setTeamName(String name)	{	teamName = name;	}
	public void setPlayer(boolean player)	{	isPlayer = player;	}
}