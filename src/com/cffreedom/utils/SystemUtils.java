package com.cffreedom.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
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
 */
public class SystemUtils
{
	public static String getUsername()
	{
		if (isWindows() == true){
			return getEnvVal("USERNAME");
		}else{
			return getEnvVal("USER");
		}
	}
	
	public static String getHomeDir()
	{
		if (isWindows() == true){
			return getEnvVal("USERPROFILE");
		}else{
			return getEnvVal("HOME");
		}
	}
	
	public static String getMyDocDir()
	{
		if (isWindows() == true)
		{
			return getHomeDir() + "\\My Documents";
		}
		else
		{
			return getHomeDir();
		}
	}
	
	public static String getMyCFConfigDir()
	{
		String dir = getHomeDir() + getPathSeparator() + "CFConfig";
		FileUtils.createFolder(dir);
		return dir;
	}
	
	public static String getDefaultOutputFile()
	{
		String file = getMyCFConfigDir() + getPathSeparator() + "default.out";
		FileUtils.createFile(file, true);
 		return file;
	}
	
	public static String getEnvVal(String key)
	{
		return System.getenv().get(key);
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
					outputRedirectFile = getDefaultOutputFile();
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
				System.out.println("ExitValue: " + returnCode);
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
