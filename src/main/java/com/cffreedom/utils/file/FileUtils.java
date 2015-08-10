package com.cffreedom.utils.file;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.comparator.NameFileComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cffreedom.exceptions.FileSystemException;
import com.cffreedom.exceptions.ValidationException;
import com.cffreedom.utils.Format;
import com.cffreedom.utils.SystemUtils;
import com.cffreedom.utils.Utils;

/**
 * Original Class: com.cffreedom.utils.file.FileUtils
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
 * 2013-04-14 	markjacobsen.net 	Added concatFiles() 
 * 2013-04-26 	markjacobsen.net 	Added getDateStampedFileName() and getTimeStampedFileName() 
 * 2013-05-08 	markjacobsen.net 	Added getFirstXLines() and getLastXLines() 
 * 2013-05-08 	markjacobsen.net 	Added appendFile() 
 * 2013-05-17 	markjacobsen.net 	Fixed getFileContents() to not add an additional CRLF at the end of the file
 * 2013-08-01 	markjacobsen.net 	Much more efficient getLastXLines() added getLastLine()
 * 2013-09-12 	markjacobsen.net 	Added getDuplicateLines()
 * 2013-10-17 	markjacobsen.net 	Added chmod()
 * 2014-12-11 	MarkJacobsen.net 	Added getLinesFromLastOccurence()
 */
public class FileUtils
{
	private static final Logger logger = LoggerFactory.getLogger("com.cffreedom.utils.file.FileUtils");

	public enum SORT
	{
		NONE,
		DATE,
		DATE_DECENDING,
		NAME,
		NAME_DECENDING;
	}
	
	/**
	 * Get the file extension. If there is no period return a zero length string.
	 * 
	 * @param file File to get the extension for
	 * @return the file extension (does NOT include the .)
	 */
	public static String getFileExtension(String file)
	{
		int period = file.lastIndexOf(".");
		if (period < 0) {
			return "";
		} else {
			return file.substring(period + 1, file.length());
		}
	}

	/**
	 * Get just the file name from a full path ex: c:\temp\junk.txt would return junk.txt
	 * 
	 * @param fullPath Full path of the file to get the file name for
	 * @return File name
	 */
	public static String getFileName(String fullPath)
	{
		File file = new File(fullPath);
		return file.getName();
	}

	/**
	 * Same thing as getFileName, but removes the extension
	 * 
	 * @param fullPath Full path of the file to get the file name for
	 * @return File name (minus the extension)
	 */
	public static String getFileNameWithoutExtension(String fullPath)
	{
		String ret;
		File file = new File(fullPath);
		ret = file.getName();
		if (getFileExtension(ret) == "") {
			return ret;
		} else {
			return ret.substring(0, ret.length() - (getFileExtension(fullPath).length() + 1));
		}
	}

	/**
	 * Append a line to a file
	 * 
	 * @param line The text to append
	 * @param file File to append to
	 * @throws FileSystemException 
	 */
	public static void appendLine(String line, String file) throws FileSystemException
	{
		try
		{
			if (new File(file).exists() == false)
			{
				new File(file).createNewFile();
			}

			FileWriter fw = new FileWriter(file, true);
			fw.write(line + SystemUtils.getNewline());
			fw.flush();
			fw.close();
		}
		catch (Exception e)
		{
			throw new FileSystemException(e);
		}
	}

	public static void appendFile(String fileToAppend, String fileToAppendTo) throws FileSystemException
	{
		String[] files = { fileToAppend };
		concatFiles(files, fileToAppendTo);
	}

	/**
	 * Get the entire contents of a file as a string
	 * 
	 * @param file File to get contents of
	 * @return The entire contents of a file as a string
	 */
	public static String getFileContents(String file)
	{
		StringBuffer sb = new StringBuffer();
		BufferedReader br = null;
		String line = "";

		try
		{
			br = new BufferedReader(new FileReader(file));
			line = br.readLine();
			while (line != null)
			{
				sb.append(line);
				line = br.readLine();
				if (line != null)
				{
					sb.append(SystemUtils.getNewline());
				}
			}
			br.close();
		}
		catch (Exception e)
		{
			logger.error("Error getting file contents from: {}", file);
			e.printStackTrace();
		}

		return sb.toString();
	}
	
	public static String getFileContents(InputStream stream)
	{
		Reader reader = new BufferedReader(new InputStreamReader(stream));
	    StringBuffer content = new StringBuffer();
	    char[] buffer = new char[500];
	    int n;
	     
	    try
	    {
		    while ( ( n = reader.read(buffer)) != -1 ) {
		        content.append(buffer,0,n);
		    }
	    }
	    catch (IOException e)
	    {
	    	logger.error("IOException getting file contents from stream");
	    	e.printStackTrace();
	    }
	     
	    return content.toString();
	}

	/**
	 * Get the contents of a file line by line into an List
	 * 
	 * @param file File to get contents of
	 * @return List of lines in the file
	 */
	public static List<String> getFileLines(String file)
	{
		return getFileLines(file, null);
	}

	public static List<String> getFileLines(String file, String appendToLines)
	{
		return getFileLines(file, appendToLines, null);
	}

	public static List<String> getFileLines(String file, String appendToLines, String prependToLines)
	{
		List<String> lines = new ArrayList<String>();

		try
		{
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = "";

			while ((line = br.readLine()) != null)
			{
				if (prependToLines != null)
				{
					line = prependToLines + line;
				}
				if (appendToLines != null)
				{
					line += appendToLines;
				}
				lines.add(line);
			}
			br.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return lines;
	}

	public static void writeStringToFile(String file, String content, boolean append) throws FileSystemException
	{
		try
		{
			BufferedWriter bw = new BufferedWriter(new FileWriter(file, append));
			bw.write(content);
			bw.close();
		}
		catch (Exception e)
		{
			throw new FileSystemException(e);
		}
	}

	/**
	 * Write the contents of a List to a file
	 * 
	 * @param file File to write to
	 * @param lines List of strings to write
	 * @param append True to append the lines to the file, false to write from the top
	 * @throws FileSystemException 
	 */
	public static void writeLinesToFile(String file, List<String> lines, boolean append) throws FileSystemException
	{
		String term = SystemUtils.getNewline();

		try
		{
			BufferedWriter bw = new BufferedWriter(new FileWriter(file, append));

			for (int i = 0; i < lines.size(); i++)
			{
				bw.write((String) lines.get(i) + term);
			}
			bw.close();
		}
		catch (Exception e)
		{
			throw new FileSystemException(e);
		}
	}

	/**
	 * Write the contents of a List to a file (does NOT append)
	 * 
	 * @param file File to write to
	 * @param lines List of strings to write to the file
	 * @throws FileSystemException 
	 */
	public static void writeLinesToFile(String file, List<String> lines) throws FileSystemException
	{
		writeLinesToFile(file, lines, false);
	}

	public static void writeObjectToFile(String file, Object content) throws FileSystemException
	{
		writeObjectToFile(file, content, false);
	}

	public static void writeObjectToFile(String file, Object content, boolean append) throws FileSystemException
	{
		try
		{
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
			oos.writeObject(content);
			oos.close();
		}
		catch (Exception e)
		{
			throw new FileSystemException(e);
		}
	}

	public static Object readObjectFromFile(String file) throws FileNotFoundException, IOException, ClassNotFoundException
	{
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
		Object ret = ois.readObject();
		ois.close();
		return ret;
	}

	public static boolean deleteFile(String file)
	{
		if (fileExists(file) == true)
		{
			File temp = new File(file);
			return temp.delete();
		}
		else
		{
			return false;
		}
	}

	public static boolean deleteFolder(String folder)
	{
		if (folderExists(folder) == true)
		{
			File temp = new File(folder);
			return temp.delete();
		}
		else
		{
			return false;
		}
	}

	/**
	 * Move a file from one location to another (Note: Must specify full src and
	 * dest - including file for both)
	 * 
	 * @param source File to move (full path - including file if necessary)
	 * @param destination New file (full path - including file if necessary)
	 * @return true if the move works, false if it fails
	 * @throws Exception
	 */
	public static boolean moveFile(String source, String destination)
	{
		File oSrc = new File(source);
		File oDst = new File(destination);

		if (oSrc.exists() == false)
		{
			return false;
		}
		return oSrc.renameTo(oDst);
	}

	/**
	 * Move a directory from one location to another (Note: Just calls the
	 * moveFile() function)
	 * 
	 * @param source Folder to move
	 * @param destination New folder
	 * @return true if the move works, false if it failes
	 * @throws Exception
	 */
	public static boolean moveFolder(String source, String destination)
	{
		return moveFile(source, destination);
	}

	/**
	 * Copy a file from one location to another
	 * 
	 * @param source File to copy
	 * @param destination Destination to copy the source file to
	 * @throws FileSystemException 
	 */
	public static void copyFile(String source, String destination) throws FileSystemException
	{
		File src;
		File dst;

		try
		{
			logger.debug("Copying \"{}\" to \"{}\"", source, destination);
			if (new File(destination).exists() == false)
			{
				new File(destination).createNewFile();
			}

			src = new File(source);
			dst = new File(destination);

			FileInputStream fis = new FileInputStream(src);
			FileOutputStream fos = new FileOutputStream(dst);
			byte[] buf = new byte[1024];
			int i = 0;
			while ((i = fis.read(buf)) != -1)
			{
				fos.write(buf, 0, i);
			}
			fis.close();
			fos.close();
		}
		catch (Exception e)
		{
			throw new FileSystemException(e);
		}
	}

	/**
	 * Does a byte by byte concatenation of 1 or more files
	 * 
	 * @param files Array of full file names to concatenate together
	 * @param outputFile The file to output them to
	 * @return true on success
	 * @throws FileSystemException 
	 */
	public static void concatFiles(String[] files, String outputFile) throws FileSystemException
	{
		File src;
		File dst;

		try
		{
			dst = new File(outputFile);
			if (dst.exists() == false)
			{
				new File(outputFile).createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(dst);

			// Concatenate the files
			for (int x = 0; x < files.length; x++)
			{
				src = new File(files[x]);
				FileInputStream fis = new FileInputStream(src);

				byte[] buf = new byte[1024];

				// Get file 1
				int i = 0;
				while ((i = fis.read(buf)) != -1)
				{
					fos.write(buf, 0, i);
				}
				fis.close();
			}
			fos.close();
		}
		catch (Exception e)
		{
			throw new FileSystemException(e);
		}
	}

	/**
	 * Copy an entire folder and it's contents from one location to another
	 * (Note: Recurses dir to copy all items)
	 * 
	 * @param source Folder to copy
	 * @param destination Destination to copy the source folder to
	 * @throws FileSystemException 
	 */
	public static void copyFolder(String source, String destination) throws FileSystemException
	{
		try
		{
			org.apache.commons.io.FileUtils.copyDirectory(new File(source), new File(destination));
		}
		catch (IOException e)
		{
			throw new FileSystemException(e);
		}
	}

	/**
	 * Tell if a file exists
	 * 
	 * @param file File to check existance of
	 * @return true if it exists, false if not
	 */
	public static boolean fileExists(String file)
	{
		if (file == null)
		{
			return false;
		}

		File oFile = new File(file);

		if ((oFile.exists() == true) && (oFile.isFile() == true))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * Tell if a folder exists
	 * 
	 * @param folder Folder to check existance of
	 * @return true if it exists, false if not
	 */
	public static boolean folderExists(String folder)
	{
		if (folder == null)
		{
			return false;
		}

		File oFolder = new File(folder);

		if ((oFolder.exists() == true) && (oFolder.isDirectory() == true))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * Get a directory listing of file names (file name only)
	 * 
	 * @param folder Folder to get listing for
	 * @param filter Filter for files (ex: .log) (pass null, a ZLS, or * to return all files), default = *
	 * @param sort Order you want results returned (see SORT enum), default = NONE
	 * @param includeFullPath true to include the full path, false for just file name
	 * @return Array of file names in the folder matching the filter
	 * @throws Exception
	 */
	public static String[] list(String folder, String filter, SORT sort, boolean includeFullPath)
	{
		String[] ret = new String[0];
		File dir = new File(folder);
		File[] files = null;
		
		if (FileUtils.folderExists(folder) == false) {
			logger.warn("{} is not a valid folder", folder);
		} else {
			files =  dir.listFiles(new DirFilter(filter));
			if (files != null)
			{
				if (files.length == 1)
				{
					ret = new String[]{ files[0].getName() };
				}
				else if (files.length > 1)
				{
					if (sort != null)
					{
						if (sort.equals(SORT.DATE) == true) {
							Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_COMPARATOR);
						} else if (sort.equals(SORT.DATE_DECENDING) == true) {
							Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
						} else if (sort.equals(SORT.NAME) == true) {
							Arrays.sort(files, NameFileComparator.NAME_COMPARATOR);
						} else if (sort.equals(SORT.NAME_DECENDING) == true) {
							Arrays.sort(files, NameFileComparator.NAME_REVERSE);
						}
					}
					
					List<String> result = new ArrayList<String>();
					for (File file : files){
				    	result.add(file.getName());
				    }
					ret = result.toArray(new String[result.size()]);
				}
				
				if ((includeFullPath == true) && (ret.length > 0))
				{
					for (int x = 0; x < ret.length; x++)
					{
						File file = new File(folder, ret[x]);
						ret[x] = file.getAbsolutePath();
					}
				}
			}
		}
		
		return ret;
	}
		
	@Deprecated
	public static String[] list(String folder) 
	{
		return list(folder, null, SORT.NONE, false);
	}
	
	@Deprecated
	public static String[] list(String folder, String filter) 
	{
		return list(folder, filter, SORT.NONE, false);
	}

	@Deprecated
	public static String[] listFullPath(String folder) 
	{
		return list(folder, null, SORT.NONE, true);
	}

	@Deprecated
	public static String[] listFullPath(String folder, String filter) 
	{
		return list(folder, filter, SORT.DATE, true);
	}

	/**
	 * Create a folder
	 * 
	 * @param path Full path name for the folder to create
	 * @param overwrite If true -> delete folder if it already exists
	 * @return True on success, false otherwise
	 * @throws Exception
	 */
	public static boolean createFolder(String path, boolean overwrite)
	{
		try
		{
			File folder = new File(path);

			if ((overwrite == true) && (folder.exists() == true))
			{
				folder.delete();
			}

			if (folder.exists() == false)
			{
				return folder.mkdir();
			}
			else
			{
				return false;
			}
		}
		catch (Exception e)
		{
			return false;
		}
	}

	/**
	 * Create a folder
	 * 
	 * @param path Full path name for the folder to create
	 * @return True on success, false otherwise
	 * @throws Exception
	 */
	public static boolean createFolder(String path)
	{
		return createFolder(path, false);
	}

	public static boolean createFile(String path)
	{
		return createFile(path, false);
	}

	public static boolean createFile(String path, boolean overwrite)
	{
		try
		{
			File file = new File(path);

			if ((overwrite == true) && (file.exists() == true))
			{
				file.delete();
			}

			if (file.exists() == false)
			{
				return file.createNewFile();
			}
			else
			{
				return false;
			}
		}
		catch (IOException e)
		{
			return false;
		}
	}

	/**
	 * Take two pieces of a file (directory and filename) and return a valid full absolute file path 
	 * @param parent prepended value
	 * @param child appended value
	 * @return Absolute path for the combination
	 */
	public static String buildPath(String parent, String child)
	{
		return (new File(parent, child)).getAbsolutePath();
	}

	/**
	 * Extract the contents of a zip/jar file to a directory
	 * 
	 * @param zipFile File to unzip
	 * @param destDir Directory to unzip to
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static void extractZip(String zipFile, String destDir) throws Exception
	{
		JarFile jar = new java.util.jar.JarFile(zipFile);
		Enumeration entries = jar.entries();
		while (entries.hasMoreElements())
		{
			JarEntry file = (JarEntry) entries.nextElement();
			File f = new File(destDir + File.separator + file.getName());
			if (file.isDirectory()) // if its a directory, create it
			{
				f.mkdir();
				continue;
			}
			InputStream is = jar.getInputStream(file); // get the input
														// stream
			FileOutputStream fos = new FileOutputStream(f);
			while (is.available() > 0) // write contents of 'is' to 'fos'
			{
				fos.write(is.read());
			}
			fos.close();
			is.close();
		}
		jar.close();
	}

	public static void replaceInFile(String file, String find, String replace) throws FileSystemException
	{
		File theFile = new File(file);
		if (theFile.exists() == false)
		{
			throw new FileSystemException("File does not exist: " + file);
		}

		try
		{
			logger.debug("Replacing \""+find+"\" with \""+replace+"\" in: "+file);
			BufferedReader br = new BufferedReader(new FileReader(file));
			List<String> lines = new ArrayList<String>();
			String line;

			// Read the contents of the file
			while ((line = br.readLine()) != null)
			{
				lines.add(line);
			}
			br.close();

			// Delete the file because we're going to replace it
			theFile.delete();

			// Now write the contents of the original file with the changes
			// needed
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			for (String writeLine : lines)
			{
				bw.write(writeLine.replaceAll(find, replace) + SystemUtils.getNewline());
			}
			bw.close();
		}
		catch (IOException e)
		{
			throw new FileSystemException(e);
		}
	}

	public static void stripLinesInFileContaining(String file, String find) throws FileSystemException
	{
		File theFile = new File(file);
		if (theFile.exists() == false)
		{
			throw new FileSystemException("File does not exist: " + file);
		}

		try
		{
			logger.debug("Stripping lines containing \"{}\" in: {}", find, file);
			BufferedReader br = new BufferedReader(new FileReader(file));
			List<String> lines = new ArrayList<String>();
			String line;

			// Read the contents of the file
			while ((line = br.readLine()) != null)
			{
				lines.add(line);
			}
			br.close();

			// Delete the file because we're going to replace it
			theFile.delete();

			// Now write the contents of the original file with the changes
			// needed
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			for (String writeLine : lines)
			{
				if (writeLine.indexOf(find) < 0)
				{
					bw.write(writeLine + SystemUtils.getNewline());
				}
			}
			bw.close();
		}
		catch (IOException e)
		{
			throw new FileSystemException(e);
		}
	}

	public static String getDateStampedFileName(String prefix, String suffix)
	{
		return getTimeStampedFileName(prefix, suffix, "yyyy-MM-dd");
	}

	public static String getTimeStampedFileName(String prefix, String suffix)
	{
		return getTimeStampedFileName(prefix, suffix, "yyyyMMddHHmmss");
	}

	public static String getTimeStampedFileName(String prefix, String suffix, String mask)
	{
		return prefix + Format.date(mask, new Date()) + suffix;
	}

	public static boolean touch(String file)
	{
		try
		{
			File temp = new File(file);
			if (temp.exists() == false)
			{
				temp.createNewFile();
			}
			return temp.setLastModified((new Date()).getTime());
		}
		catch (IOException e)
		{
			return false;
		}
	}

	/**
	 * Get the first X lines in the file specified
	 * 
	 * @param file File to read
	 * @param lines Number of lines at the beginning to return
	 * @return The first X lines in the file specified
	 */
	public static List<String> getFirstXLines(String file, int lines)
	{
		int counter = 0;
		List<String> ret = new ArrayList<String>();

		try
		{
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = "";

			while (((line = br.readLine()) != null) && (counter < lines))
			{
				counter++;
				ret.add(line);
			}
			br.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return ret;
	}

	/**
	 * Get the last X lines in the file specified. Does not include/return empty
	 * lines at the end of the file.
	 * 
	 * @param file File to read
	 * @param lines Number of lines at the end to return
	 * @return The last X lines in the file specified
	 */
	public static List<String> getLastXLines(String file, int lines) throws IOException
	{
		List<String> ret = new ArrayList<String>();
		boolean foundNewlineInBuffer = false;
		RandomAccessFile raf = new RandomAccessFile(file, "r");
		int bufferSize = 500;
		byte[] byteBuffer = new byte[bufferSize];
		StringBuilder lastLine = new StringBuilder(1000);
		long lastFilePointer = raf.length();
		
		logger.debug("File size: {}", lastFilePointer);
		while (true)
		{
			long seekTo = lastFilePointer - bufferSize;
			if (seekTo <= 0){
				logger.trace("Last chunk");
				seekTo = 0;
			}
			logger.trace("Seeking back to {}", seekTo);
			raf.seek(seekTo);
			lastFilePointer = raf.getFilePointer();

			// read from the file
			raf.read(byteBuffer);
			String buffer = new String(byteBuffer, "UTF-8");

			if (raf.getFilePointer() == 0)
			{
				String tmp = buffer.substring(0, buffer.length());
				lastLine.insert(0, tmp);
				ret.add(lastLine.toString().trim());
				logger.trace("Breaking because either hit the beginning of the file or the file is empty");
				break; // out of the while loop
			}

			foundNewlineInBuffer = false;
			int lastBufferStop = buffer.length();
			for (int i = buffer.length() - 1; i >= 0; i--)
			{
				if ((buffer.charAt(i) == '\n') && (i < lastBufferStop))
				{
					foundNewlineInBuffer = true;
					
					logger.trace("Found newline at position {} of the buffer", i);
					String tmp = buffer.substring(i, lastBufferStop);
					lastLine.insert(0, tmp);
					String addThis = lastLine.toString().trim();
					if ((ret.size() == 0) && (addThis.length() == 0)){
						logger.debug("Skipping because last line is blank");
					}else{
						logger.debug("Adding line: {}", addThis);
						ret.add(addThis);
					}

					if (ret.size() == lines)
					{
						logger.debug("Found {} lines so breaking", lines);
						break; // out of for loop
					}
					else
					{
						logger.debug("Found {} of {} lines. Looking for more.", ret.size(), lines);
						lastLine = new StringBuilder(1000);
						lastBufferStop = i;
					}
				} // end of newline processing
			} // end of looping over the buffer

			if (ret.size() != lines)
			{
				if (foundNewlineInBuffer == false)
				{
					logger.trace("Didn't find a newline in the buffer so saving the entire buffer");
					lastLine.insert(0, buffer);
				}
				else if (lastBufferStop > 0)
				{
					String tmp = buffer.substring(0, lastBufferStop);
					logger.trace("Last buffer stop at {}, so saving buffer up to that point: {}", lastBufferStop, tmp);
					lastLine.append(tmp);
				}
				
				if (lastFilePointer <= 0)
				{
					logger.trace("We would read past the beginning of the file so stopping here");
					break;  // out of while loop
				}
			}
			else
			{
				logger.trace("We have everything we need. Breaking out of while loop.");
				break;
			}
		}

		raf.close();

		return ret;
	}
	
	public static String getLastLine(String file) throws IOException
	{
		return getLastXLines(file, 1).get(0);
	}
	
	/**
	 * Given a file, find all duplicate lines and return back the lines that are duplicated and 
	 * what line numbers they are duplicated on.
	 * 
	 * @param file File to find dups in
	 * @param caseSensative Set to true to perform a case in-sensative comparison
	 * @param trim Set to true to have each line trimmed before comparing
	 * @return
	 */
	public static Map<String, List<Integer>> getDuplicateLines(String file, boolean caseSensative, boolean trim)
	{
		Map<String, Integer> unique = new HashMap<String, Integer>();
		Map<String, List<Integer>> dups = new LinkedHashMap<String, List<Integer>>();
		List<String> lines = getFileLines(file);
		
		Integer origLine = null;
		int lineNo = 0;
		for (String line : lines)
		{
			if (caseSensative == false) { line = line.toLowerCase(); }
			if (trim == true) { line = line.trim(); }
			
			lineNo++;
			origLine = unique.get(line);
			if (origLine == null)
			{
				unique.put(line, new Integer(lineNo));
			}
			else
			{
				List<Integer> lineNos = dups.get(line);
				if (lineNos == null)
				{
					lineNos = new ArrayList<Integer>();
					lineNos.add(origLine);
				}
				lineNos.add(new Integer(lineNo));
				dups.put(line, lineNos);
			}
		}
		
		return dups;
	}
	
	/**
	 * Do a UNIX chmod based on the values passed in
	 * @param path The thing to chmod
	 * @param octalCode The chmod octal code (ex: 777 or 755)
	 * @throws ValidationException
	 * @throws FileSystemException
	 */
	public static void chmod(String path, String octalCode) throws ValidationException, FileSystemException
	{
		if (octalCode.length() != 3) { throw new ValidationException("Length of octalCode " + octalCode + " != 3"); }
		if (Utils.isNumeric(octalCode) == false) { throw new ValidationException("octalCode " + octalCode + " is not numeric"); }
	
		logger.debug("chmod {} {}", octalCode, path);
		Set<PosixFilePermission> perms = new HashSet<PosixFilePermission>();
		
		// Owner
		char bit = octalCode.charAt(0);
		if ((bit == '7') || (bit == '5') || (bit == '3') || (bit == '1')) {
			perms.add(PosixFilePermission.OWNER_EXECUTE);
		}
		if ((bit == '7') || (bit == '6') || (bit == '3') || (bit == '2')) {
			perms.add(PosixFilePermission.OWNER_WRITE);
		}
		if ((bit == '7') || (bit == '6') || (bit == '5') || (bit == '4')) {
			perms.add(PosixFilePermission.OWNER_READ);
		}

		// Group
		bit = octalCode.charAt(1);
		if ((bit == '7') || (bit == '5') || (bit == '3') || (bit == '1')) {
			perms.add(PosixFilePermission.GROUP_EXECUTE);
		}
		if ((bit == '7') || (bit == '6') || (bit == '3') || (bit == '2')) {
			perms.add(PosixFilePermission.GROUP_WRITE);
		}
		if ((bit == '7') || (bit == '6') || (bit == '5') || (bit == '4')) {
			perms.add(PosixFilePermission.GROUP_READ);
		}

		// Other / All
		bit = octalCode.charAt(2);
		if ((bit == '7') || (bit == '5') || (bit == '3') || (bit == '1')) {
			perms.add(PosixFilePermission.OTHERS_EXECUTE);
		}
		if ((bit == '7') || (bit == '6') || (bit == '3') || (bit == '2')) {
			perms.add(PosixFilePermission.OTHERS_WRITE);
		}
		if ((bit == '7') || (bit == '6') || (bit == '5') || (bit == '4')) {
			perms.add(PosixFilePermission.OTHERS_READ);
		}
		
		try
		{
			Files.setPosixFilePermissions(Paths.get(path), perms);
		} catch (IOException e) {
			logger.error(e.getClass().getSimpleName() + " attempting to set permissions", e);
			e.printStackTrace();
			throw new FileSystemException("Unable to chmod "+octalCode+" "+path, e);
		}
	}
	
	/**
	 * Return a Map for each unique line in a file along with how many times that line is found in the file  
	 * @param file File to read
	 * @return Map of unique lines in file w how many times that line is found
	 */
	public static Map<String, Integer> getLineCounts(String file)
	{
		Map<String, Integer> ret = new HashMap<String, Integer>();
		List<String> lines = getFileLines(file);
		
		for (String line : lines)
		{
			if (ret.containsKey(line) == false)
			{
				ret.put(line, 1);
			}
			else
			{
				ret.put(line, ret.get(line) + 1);
			}
		}
		
		return ret;
	}

	/**
	 * Get the lines in the file specified after finding the last occurrence of the string passed in (includes line w/ String).
	 * Does not include/return empty lines at the end of the file.
	 * 
	 * @param file File to read
	 * @param find String to search backwards in the file for
	 * @return The lines in the file specified
	 */
	public static List<String> getLinesFromLastOccurence(String file, String find) throws IOException
	{
		List<String> ret = new ArrayList<String>();
		boolean foundIt = false;
		boolean foundNewlineInBuffer = false;
		RandomAccessFile raf = new RandomAccessFile(file, "r");
		int bufferSize = 500;
		byte[] byteBuffer = new byte[bufferSize];
		StringBuilder lastLine = new StringBuilder(1000);
		long lastFilePointer = raf.length();
		
		logger.trace("File size: {}", lastFilePointer);
		while (true)
		{
			long seekTo = lastFilePointer - bufferSize;
			if (seekTo <= 0){
				logger.trace("Last chunk");
				seekTo = 0;
			}
			logger.trace("Seeking back to {}", seekTo);
			raf.seek(seekTo);
			lastFilePointer = raf.getFilePointer();

			// read from the file
			raf.read(byteBuffer);
			String buffer = new String(byteBuffer, "UTF-8");

			if (raf.getFilePointer() == 0)
			{
				String tmp = buffer.substring(0, buffer.length());
				lastLine.insert(0, tmp);
				ret.add(lastLine.toString().trim());
				logger.trace("Breaking because either hit the beginning of the file or the file is empty");
				break; // out of the while loop
			}

			foundNewlineInBuffer = false;
			int lastBufferStop = buffer.length();
			for (int i = buffer.length() - 1; i >= 0; i--)
			{
				if ((buffer.charAt(i) == '\n') && (i < lastBufferStop))
				{
					foundNewlineInBuffer = true;
					
					logger.trace("Found newline at position {} of the buffer", i);
					String tmp = buffer.substring(i, lastBufferStop);
					lastLine.insert(0, tmp);
					String addThis = lastLine.toString().trim();
					if ((ret.size() == 0) && (addThis.length() == 0)){
						logger.trace("Skipping because last line is blank");
					}else{
						logger.trace("Adding line: {}", addThis);
						ret.add(addThis);
					}

					if (addThis.indexOf(find) >= 0)
					{
						foundIt = true;
						logger.debug("Found \"{}\" so breaking", find);
						break; // out of for loop
					}
					else
					{
						logger.trace("Searched {} lines. Looking for more.", ret.size());
						lastLine = new StringBuilder(1000);
						lastBufferStop = i;
					}
				} // end of newline processing
			} // end of looping over the buffer

			if (foundIt != true)
			{
				if (foundNewlineInBuffer == false)
				{
					logger.trace("Didn't find a newline in the buffer so saving the entire buffer");
					lastLine.insert(0, buffer);
				}
				else if (lastBufferStop > 0)
				{
					String tmp = buffer.substring(0, lastBufferStop);
					logger.trace("Last buffer stop at {}, so saving buffer up to that point: {}", lastBufferStop, tmp);
					lastLine.append(tmp);
				}
				
				if (lastFilePointer <= 0)
				{
					logger.trace("We would read past the beginning of the file so stopping here");
					break;  // out of while loop
				}
			}
			else
			{
				logger.trace("We have everything we need. Breaking out of while loop.");
				break;
			}
		}

		raf.close();

		return ret;
	}
}

class DirFilter implements FilenameFilter
{
	String find;

	DirFilter(String find)
	{
		this.find = find;
	}

	public boolean accept(File dir, String name)
	{
		boolean accept = false;
		if ((Utils.hasLength(find) == false) || (find.equalsIgnoreCase("*") == true) || (find.equalsIgnoreCase("*.*") == true)) {
			accept = true;
		} else {
			String f = new File(name).getName(); // Strip path information (get just the filename)
			if (f.indexOf(find) != -1) {
				accept = true;
			}
		}
		return accept;
	}
}
