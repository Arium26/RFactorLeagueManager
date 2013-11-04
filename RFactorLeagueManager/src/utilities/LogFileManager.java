package utilities;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


         /*-----------------------------------------/---------/-----------/   __________
        /     Richard E Matthews       ______      /   ______/           /    \    ___  \
_______/    Solo Project              /_____/     /___/_____/   /   /   /______\_  \   __\
\'''''/   Project Rock               /''''''\_____\''''''''/___/   /   /'''''''''   \  \___
 \___/  LogFileManager Class        /_____________________/    \  /   /______________\_____\
    /______________________________/          \_____\___________\/__*/

/**
  * Tracks the two log files: ReplayLog and TraceLog.<br>
  * Built so that everything can be used from a static call.
  * 
  * @author Richard Matthews
  * @version 2 (4-7-2013)
  * @version 3 (5-26-2013)	[Added minorError(int, String)]
  */
public final class LogFileManager
{
	private static final String directory = System.getProperty("user.dir") + 
			File.separator + "RFLM Files" + File.separator + "LOG";
	private static final String logFileLocation = directory + File.separator + "Trace.txt";
	private static final String replayFileLocation = directory + File.separator + "Output.txt";
	private static PrintWriter logPW, replayPW;//PrintWriters for logging and replay capture purposes
	private static boolean isCreated = false;//Tracks if this class was initialized
	private static String header = "LogFileManager";
	
	/**
	  * Creates the directory to save things to
	  * if the directory (Specified in "directory"
	  * String) exists and is a directory
	  */
	private static void initializeDirectory()
	{
		if (!new File(directory).isDirectory())
		{
			System.out.println("Creating Directory");
			
			//Success!  Now kick out.
			if (new File(directory).mkdir())
			{
				System.out.println("Success");
				return;
			}

			System.out.println("Well this is embarassing!");
			System.out.println();
			System.out.println("Try #2");
			
			//Success!  Now kick out.
			if (new File(directory).mkdirs())
			{
				System.out.println("Success");
				return;
			}
			
			//Error incoming
			System.out.println("Uh oh!");
			System.out.println("That's not good...");
			
			//Failed, exit program
			System.out.println("Error creating directory");
			System.out.println("Folder = " + directory);
			System.out.println("Contact program creator Richard Matthews"+
					"@ budster87@email.com");
			System.out.println("No, email.com is not a typo.");
			System.exit(404);
		}
	}
	/**
	  * Psuedo constructor that initializes the files for the logs
	  */
	private static void initializeFiles() throws IOException
	{
		initializeDirectory();
		
		System.out.println("Creating log files");
		
		//Delete old files
		if (new File(logFileLocation).exists())
			deleteFile(logFileLocation);//Delete the old log file
		if (new File(replayFileLocation).exists())
			deleteFile(replayFileLocation);//Delete the old replay file
		
		//Recreate new files
		createFile(logFileLocation);
		createFile(replayFileLocation);
		
		//instantiate file writers
		logPW = new PrintWriter(new FileWriter(logFileLocation));
		replayPW = new PrintWriter(new FileWriter(replayFileLocation));
		
		//Flag as created
		isCreated = true;
	}
	
	/**
	  * An easy way to create files.  Creates a blank file with given pathname.
	  * 
	  * @param filename : Full path of file to be created
	  * @return True, if created
	  */
	private static boolean createFile(String filename)
	{
		try
		{
			return new File(filename).createNewFile();
		}
		catch (IOException ex)
		{
			System.out.println("Error in creation");
			System.out.println(ex);
		}
		
		return false;
	}

	/**
	  * Attempts to delete the file specified
	  * 
	  * @param filename : String - File to delete
	  * @return true if successful
	  */
	private static boolean deleteFile(String filename)
	{
		try
		{
			File deleteFile = new File(filename);
			return deleteFile.delete();		
		}
		catch (Exception ex)
		{
			System.out.println("Error in deletion");
			System.out.println(ex);
		}
		
		return false;
	}
	
	/**
	 * Will create the replay and log files.
	 * 
	 * @return true, if successful
	 */
	private static boolean fileFactory()
	{
		if (!isCreated)
		{
			try
			{
				initializeFiles();
			}
			catch (IOException ex)
			{
				System.out.println("Error initializing files");
				System.out.println(ex);
			}
			
			return true;
		}
		
		return false;
	}
	
	/**
	  * Prints the information to the replay file.
	  * The replay file should only be used to store 
	  * "replays" of game events.  Prints to stdout
	  * with no line return afterwards.
	  * 
	  * @param text: String - text to print
	  */
	public static void replayPrint(String text)
	{
		if(!isCreated)
		{
			fileFactory();
			
			//Fail catch
			if (!isCreated)
			{
				System.out.println("Error creating file");
				System.exit(404);
			}
		}
		
		replayPW.print(text);
		System.out.print(text);
	}
	
	/**
	  * Prints the information to the log file, which is used
	  * to trace things that happen inside the system.  Log file 
	  * is used to track and debug issues within the system.  
	  * Does not print out to stdout.
	  * 
	  * @param section - Name of the section
	  * @param textToPrint - text that will be printed
	  */
	public static void logPrint(String section, String textToPrint)
	{
		if(!isCreated)
		{
			fileFactory();
			
			//Fail catch
			if (!isCreated)
			{
				System.out.println("Error creating file");
				System.exit(404);
			}
		}
		
		logPW.print("["+section+"]"+textToPrint+System.getProperty("line.separator"));
//		System.out.println("["+section+"]"+textToPrint);
	}
	
	/**
	  * Prints an error to the console and error log, then exits
	  * using the code passed to the function.  This function is
	  * needed to close off any files that might be left open.
	  * This also provides a convenient place to store all error
	  * conditions.  Callers should properly shutdown before
	  * calling this as it will shut down the program.
	  * <br><br>
	  * List of error codes:<br>
	  *   1: Error in loading a file<br>
	  *   2: Error creating a file<br>
	  *   3: Error deleting the file<br>
	  *   4: Input improperly parsed<br>
	  *   5: Switch-Case error<br>
	  *   6: Array overflow<br>
	  *   7: Reached non-existent state in switch-case<br>
	  * 
	  * @param code - code of the error passed to this
	  */
	public static void exitError(int code)
	{
		String errorText = "Error #" + code + ".  ";
		
		switch(code)
		{
			case 1: errorText = errorText.concat("Error loading a file.");
				break;
			case 2: errorText = errorText.concat("Error creating a file.");
				break;
			case 3: errorText = errorText.concat("Error deleting a file.");
				break;
			case 4: errorText = errorText.concat("Error parsing input.");
				break;
			case 5: errorText = errorText.concat("Error within a switch-case clause.");
				break;
			case 6: errorText = errorText.concat("Array overflow.");
				break;
			case 7: errorText = errorText.concat("Error: Reached a nonexistent attack");
				break;
			default: errorText = errorText.concat("Unknown error.");
				break;
		}
		
		logPrint(header, errorText);
		
		shutdown();
		
		System.exit(code);
	}
	
	/**
	  * Prints an error to the error file, but does not exit.  
	  * Like the version that does exit, this version prints
	  * an error based on the code passed.
	  * <br><br>
	  * List of error codes:<br>
	  *   1: Improper tag parsed in Config file [arg[0] = tag]
	  *   2: Improper value parsed in Config file [arg[0] = tag, arg[1] = value]
	  * 
	  * @param code - error code (See above list)
	  * @param arg - Arguments causing issue (If applicable)
	  */
	public static void minorError(int code, String[] arg)
	{
		String errorText = "               Error #" + code + ".  ";
		
		switch(code)
		{
			case 1: errorText = errorText.concat("Could not parse "+arg[0]+".");
				break;
			case 2: errorText = errorText.concat("Could not parse "+arg[1]+" in <"+arg[0]+">.");
				break;
			default: errorText = errorText.concat("Unknown error.");
				break;
		}
		
		logPrint(header, errorText);
	}
	
	/**
	 * Releases the resources used by this class
	 * 
	 * @return true if successful
	 */
	public static boolean shutdown()
	{
		if (isCreated)
		{
			isCreated = false;
			replayPW.close();
			logPW.close();
			
			return true;
		}
		
		return false;
	}
}