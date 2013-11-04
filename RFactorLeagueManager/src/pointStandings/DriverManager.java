package pointStandings;

import java.util.Iterator;
import java.util.LinkedList;

import utilities.LogFileManager;

/**
  * Manages drivers using their IDs
  * and tracks their points scored.
  * Implemented as a singleton.
  * 
  * @author Richard Matthews
  * @version 1 (5-29-2013)
  */
public final class DriverManager
{
	private LinkedList<StandingsDriver> drivers = new LinkedList<StandingsDriver>();
	private int driverCount;
	private static DriverManager manager;
	private static final String header = "DriverManager";
	
	private DriverManager()
	{
		driverCount = 0;
		drivers = new LinkedList<StandingsDriver>();
	}
	
	/**
	  * The key to singleton, either produces
	  * the single manager or creates it
	  * @return The only implementation of DriverManager allowed
	  */
	public static DriverManager getManager()
	{
		if (manager == null)
			manager = new DriverManager();
		
		return manager;
	}
	
	/**
	  * Makes an iterator for the drivers
	  * @return The iterator requested
	  */
	public Iterator<StandingsDriver> getIterator()	{	return drivers.iterator();	}
	
	/**
	  * Gets and/or creates a standing driver for the
	  * event driver passed in.
	  * @param driver - Driver to add
	  * @return Existing or new StandingsDriver based on the driver passed in
	  */
	public StandingsDriver addDriver(Driver driver)
	{
		String name = driver.getName();
		
		//Check existence based on primary key (name)
		Iterator<StandingsDriver> iterator = drivers.iterator();
		boolean found = false;
		StandingsDriver newDriver = new StandingsDriver();
		while(iterator.hasNext() && !found)
		{
			StandingsDriver temp = iterator.next();
			if (temp.getName().matches(name))
			{
				found = true;
				newDriver = temp;
			}
		}
		
		//New drivers have vars set, existing have them updated
		newDriver.setPlayer(driver.isPlayer());
		newDriver.setTeamName(driver.getTeamName());
		newDriver.setCarClass(driver.getCarClass());
		newDriver.setCarNumber(driver.getCarNumber());
		
		//Deal with creating a new driver
		if (!found)
		{
			newDriver.setName(name);
			newDriver.setID(this, driverCount);
			driverCount++;
			drivers.add(newDriver);//Not adding the driver is bad, mmmkay
					//If you forget to add the driver when adding a new driver,
					//		you're gonna have a bad time
		}
		
		return newDriver;
	}
	
	protected LinkedList<StandingsDriver> getList()	{	return drivers;	}
	
	/**
	  * Sorts the drivers by overall points
	  * @return True if successful
	  */
	public boolean sortByOverallPoints()
	{
		boolean check = false;
		
		LinkedList<StandingsDriver> drivers = getManager().getList();
		
		//Null check
		if (drivers != null)
		{
			if (!drivers.isEmpty())
			{
				//Get the array
				Object[] array = drivers.toArray();
				StandingsDriver[] tempList = new StandingsDriver[array.length];
				for (int k = 0; k < array.length; k++)
					tempList[k] = (StandingsDriver) array[k];
				
				//Quick Sort
				tempList = driverSort(tempList, true);
				
				//Assign the positions
				int lastPosition = 1;
				int lastPoints = tempList[0].getOverallPointTotal();
				for (int i = 0; i < tempList.length; i++)
				{
					//If not tied in points, change last points and increment position
					if (tempList[i].getOverallPointTotal() != lastPoints)
					{
						lastPosition = i+1;
						lastPoints = tempList[i].getOverallPointTotal();
					}
					tempList[i].setPositionOverall(lastPosition, this);
				}
				
				//Save array as a LinkedList
				LinkedList<StandingsDriver> tempArray = new LinkedList<StandingsDriver>();
				for (int j = 0; j < tempList.length; j++)
					tempArray.add(tempList[j]);
				
				this.drivers = tempArray;
				
				check = true;
			}
		}
		
		return check;
	}
	
	public StandingsDriver[] getDriversByClass(String className)
	{
		StandingsDriver[] list = null;
		
		//TODO Filter by class
		
		//TODO Sort by class points (Could probably get away with overall, but don't risk)
		
		return list;
	}
	
	public StandingsDriver[] driverSort(StandingsDriver[] drivers, boolean overall)
	{
		LogFileManager.logPrint(header, "QuickSorting an array of size " + drivers.length);
		
		//Kick out if zero or one element
		if (drivers.length < 2)
			return drivers;
		
		//Find last element (Least points)
		int element = 0;
		int points;
		if (overall)
			points = drivers[element].getOverallPointTotal();
		else
			points = drivers[element].getClassPointTotal();
		for (int i = 0; i < drivers.length; i++)
		{
			if ((points > drivers[i].getOverallPointTotal() && overall) ||
				(points > drivers[i].getClassPointTotal() && !overall))
			{
				element = i;
				if (overall)
					points = drivers[element].getOverallPointTotal();
				else
					points = drivers[element].getClassPointTotal();
			}
		}
		
		//Swap if the last element isn't the l
		if (element != drivers.length-1)
		{
			drivers = driverSwap(drivers, element, drivers.length-1);
		}
		
		//Initiate the sort
		drivers = driverSort(drivers, 0, drivers.length-2, overall);
		
		return drivers;
	}
	
	public StandingsDriver[] driverSort(StandingsDriver[] drivers, 
			int start, int end, boolean overall)
	{
		//TODO Fix this blasted thing!!!
		//Basic maintenance
		int lower = start + 1;
		int upper = end;
		drivers = driverSwap(drivers, start, (start+end)/2);
		
		//Sort into elements higher and lower
		int points;
		if (overall)
			points = drivers[start].getOverallPointTotal();
		else
			points = drivers[start].getClassPointTotal();
		while(lower <= upper)
		{
			while ((drivers[lower].getOverallPointTotal() >= points && overall) ||
					(drivers[lower].getClassPointTotal() >= points && !overall))
				lower++;
			while ((drivers[upper].getOverallPointTotal() < points && overall) ||
					(drivers[upper].getClassPointTotal() < points && !overall))
				upper--;
			if (lower < upper)
				drivers = driverSwap(drivers, upper--, lower++);
			else
				lower++;
		}
		drivers = driverSwap(drivers, start, upper);
		
		//Go Caesar/Napoleon on this sort
		if (start < upper-1)
			drivers = driverSort(drivers, start, upper-1, overall);
		if (upper+1 < end)
			drivers = driverSort(drivers, upper+1, end, overall);
		
		return drivers;
	}
	
	/**
	  * Swaps the drivers indicated in the array (Does not change base array)
	  * @param drivers - Raw list of drivers (Remains unchanged)
	  * @param upper - Location of first driver to swap
	  * @param lower - Location of other driver to swap
	  * @return List passed with the two drivers swapped
	  */
	public StandingsDriver[] driverSwap(StandingsDriver[] drivers, int upper, int lower)
	{
		StandingsDriver[] tempList = drivers.clone();
		StandingsDriver temp = tempList[upper];
		tempList[upper] = tempList[lower];
		tempList[lower] = temp;
		
		return tempList;
	}
}