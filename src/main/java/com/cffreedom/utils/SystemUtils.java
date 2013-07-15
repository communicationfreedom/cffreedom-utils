package com.cffreedom.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cffreedom.utils.file.FileUtils;

/**
 * Original Class: com.cffreedom.utils.SystemUtils
 * @author markjacobsen.net (http://mjg2.net/code)
 * Copyright: Communication Freedom, LLC - http://www.communicationfreedom.com
 * 
 * Free to use, modify, redistribute.  Must keep full class header including 
 * copyright and note your modifications.
 * 
 * If this helped you out or saved you time, please consider...
 * 1) Donating: http://www.communicationfreedom.com/go/donate/
 * 2) Shoutout on twitter: @MarkJacobsen or @cffreedom
 * 3) Linking to: http://visit.markjacobsen.net
 * 
 * Changes:
 * 2013-04-11 	markjacobsen.net 	Changed getHomeDir() to use HOMEPATH and HOMEDRIVE when on Windows
 * 2013-04-11 	markjacobsen.net 	Added getTempDir()
 * 2013-04-13 	markjacobsen.net 	Added getMyCFWorkDir() and getMyCFWorkDir(String[] dirs)
 * 2013-04-23 	markjacobsen.net	Added execIt()
 * 2013-05-02 	markjacobsen.net 	Added sleep()
 * 2013-06-07 	markjacobsen.net 	Added exception handling to getHomeDir()
 * 2013-06-21 	markjacobsen.net 	Added sleep(double) so that you can pass in fractions of a second
 * 2013-07-15	markjacobsen.net 	Changed method names around
 */
public class SystemUtils
{
	private static final Logger logger = LoggerFactory.getLogger("com.cffreedom.utils.SystemUtils");
	
	public static String getUsername()
	{
		if (isWindows() == true){
			return getEnvVal("USERNAME");
		}else{
			return getEnvVal("USER");
		}
	}
	
	public static String getDirHome()
	{
		String ret = null;
		try
		{
			if (isWindows() == true){
				String homePath = getEnvVal("HOMEPATH");
				if (homePath.substring(homePath.length() - 1).equalsIgnoreCase("\\") == true)
				{
					// Strip the any trailing \
					homePath = homePath.substring(0, homePath.length() - 1);
				}
				ret = getEnvVal("HOMEDRIVE") + homePath;
			}else{
				ret = getEnvVal("HOME");
			}
		}
		catch (Exception e)
		{
			ret = null;
		}
		
		return ret;
	}
	
	public static String getDirDocs()
	{
		if (isWindows() == true)
		{
			return getDirHome() + getPathSeparator() + "My Documents";
		}
		else
		{
			return getDirHome();
		}
	}
	
	public static String getDirConfig()
	{
		String dir = getDirHome() + getPathSeparator() + "CFConfig";
		FileUtils.createFolder(dir);
		return dir;
	}
	
	public static String getDirWork()
	{
		String dir = getDirHome() + getPathSeparator() + "CFWork";
		FileUtils.createFolder(dir);
		return dir;
	}
	
	/**
	 * Get a subdirectory off the working directory being sure to create
	 * each subdir if it does not exist
	 * @param dirs Array of directories off the working dir
	 * @return The full path
	 */
	public static String getDirWork(String[] dirs)
	{
		String dir = getDirWork();
		
		for (int x = 0; x < dirs.length; x++)
		{
			dir += getPathSeparator() + dirs[x];
			FileUtils.createFolder(dir);
		}
		
		return dir;
	}
	
	public static String getDirTemp()
	{
		String dir = getDirWork() + getPathSeparator() + "temp";
		FileUtils.createFolder(dir);
		return dir;
	}
	
	public static String getFileDefaultOutput()
	{
		String file = getDirWork() + getPathSeparator() + "default.out";
		FileUtils.createFile(file, true);
 		return file;
	}
	
	public static String getEnvVal(String key)
	{
		return System.getenv().get(key);
	}
	
	public static void sleep(double seconds)
    {
        try {
            Thread.sleep(ConversionUtils.toInt(seconds * 1000));
        } catch (InterruptedException e) {
            logger.error("ERROR: Sleeping");
        }
    }
	
	public static void sleep(int seconds)
    {
        sleep((double)seconds);
    }
	
	/**
	 * Run a random command and don't care if it succeeds.  Useful for popping a file in notepad or something
	 * @param command The command to run (ex: "\"C:\\Program Files\\Notepad++\\notepad++.exe\" \"" + file + "\"")
	 */
	public static int execIt(String command)
	{
		int returnVal = -1;
		
		try{
			Process process = Runtime.getRuntime().exec(command);
			returnVal = process.exitValue();
		}catch (Exception e){}
		
		return returnVal;
	}
	
	public static int exec(String command) { return exec(command, null); }
	public static int exec(String command, String[] args) { return exec(command, args, null); }
	public static int exec(String command, String[] args, String workingDir) { return exec(command, args, workingDir, null); }
	public static int exec(String command, String[] args, String workingDir, String outputRedirectFile) { return exec(command, args, workingDir, outputRedirectFile, false); }
	public static int exec(String command, String[] args, String workingDir, String outputRedirectFile, boolean returnImmediately)
	{
		int returnCode = -1;
		File dir = null;
		String[] cmd = null;
		
		try
		{
			if (FileUtils.folderExists(workingDir) == true)
			{
				dir = new File(workingDir);
			}
			
			if (isWindows() == true)
			{
				cmd = new String[3];
				cmd[0] = "cmd.exe";
				cmd[1] = "/C";
				cmd[2] = command;
			}
			else
			{
				cmd = new String[1];
				cmd[0] = command;
			}
			Process process = Runtime.getRuntime().exec(cmd, args, dir);
			
			if(returnImmediately == false)
			{
				if (outputRedirectFile == null){
					outputRedirectFile = getFileDefaultOutput();
				}
				FileOutputStream fos = null;
				if (FileUtils.fileExists(outputRedirectFile) == true){
					fos = new FileOutputStream(outputRedirectFile);
				}
				StreamGobbler stderr = new StreamGobbler(process.getErrorStream(), "ERROR");
				StreamGobbler stdout = new StreamGobbler(process.getInputStream(), "OUTPUT", fos);
				
				stderr.start();
				stdout.start();
				
				returnCode = process.waitFor();
				logger.debug("ExitValue: " + returnCode);
				if (fos != null)
				{
					fos.flush();
					fos.close();
				}
			}
		}
		catch (InterruptedException e) { returnCode = -1; e.printStackTrace(); }
		catch (NullPointerException e) { returnCode = -1; e.printStackTrace(); }
		catch (IOException e) { returnCode = -1; e.printStackTrace(); }
		
		return returnCode;
	}
	
	public static boolean isWindows()
	{
		if (System.getProperty("os.name").indexOf("Windows") >= 0){
			return true;
		}else{
			return false;
		}
	}
	
	public static String getPathSeparator()
	{
		return File.separator;
	}
	
	public static String getNewline()
	{
		if (isWindows() == true)
		{
			return "\r\n";
		}
		else
		{
			return "\n";
		}
	}
}
