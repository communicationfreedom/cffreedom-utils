package com.cffreedom.utils;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cffreedom.utils.file.FileUtils;
import com.cffreedom.utils.file.FilterNameEnd;

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
 * 
 * Changes:
 * 2013-04-23 	markjacobsen.net 	Added optional joinedFileNamePrefix param to batchMp3Append() and batchMp3Prepend()
 */
public class AudioUtils
{
	private static final Logger logger = LoggerFactory.getLogger("com.cffreedom.utils.AudioUtils");
	
	public static int batchMp3Append(String srcDir, String appendFile, String outputDir) {return batchMp3Append(srcDir, appendFile, outputDir, "");}
	/**
	 * Append appendFile to all the mp3 files in srcDir one at a time, and output the 
	 * resulting files to outputDir
	 * @param srcDir Directory containing mp3 files we want to prepend
	 * @param appendFile mp3 file to append to each mp3 in srcDir
	 * @param outputDir Directory to put joined files
	 * @param joinedFileNamePrefix A string to prepend to the joined filename
	 * @return The number of files joined
	 */
	public static int batchMp3Append(String srcDir, String appendFile, String outputDir, String joinedFileNamePrefix)
	{
		int count = 0;
		File dir = new File(srcDir);
		File[] files = dir.listFiles(new FilterNameEnd(".mp3"));
		
		if (joinedFileNamePrefix == null) { joinedFileNamePrefix = ""; }
		
		for (File file : files)
		{
			count++;
			String prependFile = file.getAbsolutePath();
			String outputFile = outputDir + SystemUtils.getPathSeparator() + joinedFileNamePrefix + file.getName().replaceFirst(".mp3", "-joined.mp3");
			if (FileUtils.fileExists(outputFile) == true)
			{
				logger.warn("Deleting: {}", outputFile);
				FileUtils.deleteFile(outputFile);
			}
			logger.debug("Prepending {} to {} --> {}", prependFile, appendFile, outputFile);
			String[] concatFiles = {prependFile, appendFile};
			FileUtils.concatFiles(concatFiles, outputFile);
		}
		return count;
	}
	
	
	public static int batchMp3Prepend(String srcDir, String prependFile, String outputDir) { return batchMp3Prepend(srcDir, prependFile, outputDir, ""); } 
	
	/**
	 * Prepend prependFile to all the mp3 files in srcDir one at a time, and output the 
	 * resulting files to outputDir
	 * @param srcDir Directory containing mp3 files we want to append
	 * @param prependFile mp3 file to prepend to each mp3 in srcDir
	 * @param outputDir Directory to put joined files
	 * @param joinedFileNamePrefix A string to prepend to the joined filename
	 * @return The number of files joined
	 */
	public static int batchMp3Prepend(String srcDir, String prependFile, String outputDir, String joinedFileNamePrefix)
	{
		int count = 0;
		File dir = new File(srcDir);
		File[] files = dir.listFiles(new FilterNameEnd(".mp3"));
		
		if (joinedFileNamePrefix == null) { joinedFileNamePrefix = ""; }
		
		for (File file : files)
		{
			count++;
			String appendFile = file.getAbsolutePath();
			String outputFile = outputDir + SystemUtils.getPathSeparator() + joinedFileNamePrefix + file.getName().replaceFirst(".mp3", "-joined.mp3");
			if (FileUtils.fileExists(outputFile) == true)
			{
				logger.warn("WARN: Deleting: {}", outputFile);
				FileUtils.deleteFile(outputFile);
			}
			logger.debug("Prepending {} to {} --> {}", prependFile, appendFile, outputFile);
			String[] concatFiles = {prependFile, appendFile};
			FileUtils.concatFiles(concatFiles, outputFile);
		}
		
		return count;
	}
}
