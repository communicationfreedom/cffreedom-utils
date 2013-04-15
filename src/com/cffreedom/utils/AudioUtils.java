package com.cffreedom.utils;

import java.io.File;

import com.cffreedom.file.FilterNameEnd;

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
public class AudioUtils
{
	/**
	 * Append appendFile to all the mp3 files in srcDir one at a time, and output the 
	 * resulting files to outputDir
	 * @param srcDir Directory containing mp3 files we want to prepend
	 * @param appendFile mp3 file to append to each mp3 in srcDir
	 * @param outputDir Directory to put joined files
	 */
	public static void batchMp3Append(String srcDir, String appendFile, String outputDir)
	{
		final String METHOD = "batchMp3Append";
		File dir = new File(srcDir);
		File[] files = dir.listFiles(new FilterNameEnd(".mp3"));
		for (File file : files)
		{
			String prependFile = file.getAbsolutePath();
			String outputFile = outputDir + SystemUtils.getPathSeparator() + file.getName().replaceFirst(".mp3", "-joined.mp3");
			if (FileUtils.fileExists(outputFile) == true)
			{
				LoggerUtil.log(METHOD, "WARN: Deleting: " + outputFile);
				FileUtils.deleteFile(outputFile);
			}
			LoggerUtil.log(METHOD, "Prepending " + prependFile + " to " + appendFile + " --> " + outputFile);
			String[] concatFiles = {prependFile, appendFile};
			FileUtils.concatFiles(concatFiles, outputFile);
		}
	}
	
	/**
	 * Prepend prependFile to all the mp3 files in srcDir one at a time, and output the 
	 * resulting files to outputDir
	 * @param srcDir Directory containing mp3 files we want to append
	 * @param prependFile mp3 file to prepend to each mp3 in srcDir
	 * @param outputDir Directory to put joined files
	 */
	public static void batchMp3Prepend(String srcDir, String prependFile, String outputDir)
	{
		final String METHOD = "batchMp3Append";
		File dir = new File(srcDir);
		File[] files = dir.listFiles(new FilterNameEnd(".mp3"));
		for (File file : files)
		{
			String appendFile = file.getAbsolutePath();
			String outputFile = outputDir + SystemUtils.getPathSeparator() + file.getName().replaceFirst(".mp3", "-joined.mp3");
			if (FileUtils.fileExists(outputFile) == true)
			{
				LoggerUtil.log(METHOD, "WARN: Deleting: " + outputFile);
				FileUtils.deleteFile(outputFile);
			}
			LoggerUtil.log(METHOD, "Prepending " + prependFile + " to " + appendFile + " --> " + outputFile);
			String[] concatFiles = {prependFile, appendFile};
			FileUtils.concatFiles(concatFiles, outputFile);
		}
	}
}
