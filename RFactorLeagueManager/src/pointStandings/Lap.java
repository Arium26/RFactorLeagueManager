package pointStandings;

/**
  * A lap around the circuit.  Used in race related sessions.
  * @author Richard Matthews
  */
public class Lap
{
		//Variables
	private float sector1, sector2, sector3;
	private float startTime, totalTime;
	private int lapNum, position;
	private float fuel;
	private boolean pit, completed;
	
	public Lap()
	{
		setSector1(999.99f);
		setSector2(999.99f);
		setSector3(999.99f);
		lapNum = 0;
		position = 100;
		fuel = 0.0f;
		pit = completed = false;		
	}
	
		//Mutators
	public void setSector1(float time)
	{
		sector1 = time;
		handleTime();
	}
	public void setSector2(float time)
	{
		sector2 = time;
		handleTime();
	}
	public void setSector3(float time)
	{
		sector3 = time;
		handleTime();
	}
	public void setFuel(float amount)	{	fuel = amount;	}
	public void setPit(boolean pit)	{	this.pit = pit;	}
	public void setLapNumber(int number)	{	lapNum = number;	}
	public void setStartTime(float time)	{	startTime = time;	}
	public void setPosition(int position)	{	this.position = position;	}
	public void setCompleted(boolean isTrue)	{	completed = isTrue;	}
	public void setLapTime(float time)	{	totalTime = time;	setCompleted(true);	}
	
		//Accessor
	public float getSector1()	{	return sector1;	}
	public float getSector2()	{	return sector2;	}
	public float getSector3()	{	return sector3;	}
	public float getStartTime()	{	return startTime;	}
	public float getTotalTime()	{	return totalTime;	}
	public int getLapNumber()	{	return lapNum;	}
	public int getPosition()	{	return position;	}
	public float getFuel()	{	return fuel;	}
	public boolean isPitIn()	{	return pit;	}
	public boolean isComplete()	{	return completed;	}
	
	/**
	  * Creates a lap time out of the established 
	  * sector times (If possible)
	  */
	private void handleTime()
	{
		if (sector1 > 800.0 || sector2 > 800.0 || sector3 > 800.0)
		{
			completed = false;
			return;
		}
		
		completed = true;
		totalTime = sector1+sector2+sector3;
	}
}