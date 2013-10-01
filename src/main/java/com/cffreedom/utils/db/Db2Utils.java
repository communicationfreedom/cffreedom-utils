package com.cffreedom.utils.db;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cffreedom.beans.DbConn;
import com.cffreedom.exceptions.InfrastructureException;
import com.cffreedom.exceptions.ProcessingException;
import com.cffreedom.exceptions.ValidationException;
import com.cffreedom.utils.SystemUtils;
import com.cffreedom.utils.file.FileUtils;

/**
 * Original Class: com.cffreedom.utils.db.Db2Utils
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
 * 2013-09-01 	markjacobsen.net 	Created
 * 2013-10-01	MarkJacobsen.net 	Added connectToAlias option necessary for catalog entries on remote servers
 */
public class Db2Utils
{
	private static final Logger logger = LoggerFactory.getLogger(Db2Utils.class);
	private static final String TYPE_IMPORT = "import";
	private static final String TYPE_EXPORT = "export";
	private static final String TYPE_RUNSTATS = "runstats";
	private static final String TYPE_REORG = "reorg";
	private static final String TYPE_TRUNCATE = "truncate";
	private static final String TYPE_RAW = "raw";
	
	public static void exportToFile(String fileDirectory, String name, Map<String, String> nameSqlMap, DbConn dbconn, boolean connectToAlias) throws ProcessingException
	{		
		try
		{
			execCommands(TYPE_EXPORT, fileDirectory, name, nameSqlMap, dbconn, connectToAlias);
		}
		catch (ProcessingException | ValidationException | InfrastructureException e)
		{
			logger.error("Error during processing: " + e.getMessage(), e);
			throw new ProcessingException(e);
		}
	}
	
	public static void importFromFile(String fileDirectory, String name, Map<String, String> nameSqlMap, DbConn dbconn, boolean connectToAlias) throws ProcessingException
	{
		try
		{
			execCommands(TYPE_IMPORT, fileDirectory, name, nameSqlMap, dbconn, connectToAlias);
		}
		catch (ProcessingException | ValidationException | InfrastructureException e)
		{
			logger.error("Error during processing: " + e.getMessage(), e);
			throw new ProcessingException(e);
		}
	}
	
	public static void runStatsOnTables(String fileDirectory, String name, ArrayList<String> tables, DbConn dbconn, boolean connectToAlias) throws ProcessingException
	{
		Map<String, String> nameSqlMap = new LinkedHashMap<String, String>();
		for (String table : tables)
		{
			nameSqlMap.put(table, table);
		}
		
		try
		{
			execCommands(TYPE_RUNSTATS, fileDirectory, name, nameSqlMap, dbconn, connectToAlias);
		}
		catch (ProcessingException | ValidationException | InfrastructureException e)
		{
			logger.error("Error during processing: " + e.getMessage(), e);
			throw new ProcessingException(e);
		}
	}
	
	public static void reorgTables(String fileDirectory, String name, ArrayList<String> tables, DbConn dbconn, boolean connectToAlias) throws ProcessingException
	{
		Map<String, String> nameSqlMap = new LinkedHashMap<String, String>();
		for (String table : tables)
		{
			nameSqlMap.put(table, table);
		}
		
		try
		{
			execCommands(TYPE_REORG, fileDirectory, name, nameSqlMap, dbconn, connectToAlias);
		}
		catch (ProcessingException | ValidationException | InfrastructureException e)
		{
			logger.error("Error during processing: " + e.getMessage(), e);
			throw new ProcessingException(e);
		}
	}
	
	public static void truncateTables(String fileDirectory, String name, ArrayList<String> tables, DbConn dbconn, boolean connectToAlias) throws ProcessingException
	{
		Map<String, String> nameSqlMap = new LinkedHashMap<String, String>();
		for (String table : tables)
		{
			nameSqlMap.put(table, table);
		}
		
		try
		{
			execCommands(TYPE_TRUNCATE, fileDirectory, name, nameSqlMap, dbconn, connectToAlias);
		}
		catch (ProcessingException | ValidationException | InfrastructureException e)
		{
			logger.error("Error during processing: " + e.getMessage(), e);
			throw new ProcessingException(e);
		}
	}
	
	public static void grantAccessToTables(String fileDirectory, String name, ArrayList<String> tables, DbConn dbconn, boolean connectToAlias, String grantTo, boolean select, boolean insert, boolean update, boolean delete) throws ProcessingException
	{
		Map<String, String> nameSqlMap = new LinkedHashMap<String, String>();
		for (String table : tables)
		{
			if (select == true) { nameSqlMap.put(table + ".SELECT", "GRANT SELECT ON TABLE " + table + " TO " + grantTo + " WITH GRANT OPTION;"); }
			if (insert == true) { nameSqlMap.put(table + ".INSERT", "GRANT INSERT ON TABLE " + table + " TO " + grantTo + " WITH GRANT OPTION;"); }
			if (update == true) { nameSqlMap.put(table + ".UPDATE", "GRANT UPDATE ON TABLE " + table + " TO " + grantTo + " WITH GRANT OPTION;"); }
			if (delete == true) { nameSqlMap.put(table + ".DELETE", "GRANT DELETE ON TABLE " + table + " TO " + grantTo + " WITH GRANT OPTION;"); }
		}
		
		try
		{
			execCommands(TYPE_RAW, fileDirectory, name, nameSqlMap, dbconn, connectToAlias);
		}
		catch (ProcessingException | ValidationException | InfrastructureException e)
		{
			logger.error("Error during processing: " + e.getMessage(), e);
			throw new ProcessingException(e);
		}
	}
	
	private static void execCommands(String type, String fileDirectory, String name, Map<String, String> nameSqlMap, DbConn dbconn, boolean connectToAlias) throws ProcessingException, ValidationException, InfrastructureException
	{
		if (
			(type.equalsIgnoreCase(TYPE_EXPORT) == false) &&
			(type.equalsIgnoreCase(TYPE_IMPORT) == false) &&
			(type.equalsIgnoreCase(TYPE_RUNSTATS) == false) &&
			(type.equalsIgnoreCase(TYPE_REORG) == false) &&
			(type.equalsIgnoreCase(TYPE_TRUNCATE) == false) &&
			(type.equalsIgnoreCase(TYPE_RAW) == false)
			)
		{
			throw new ValidationException("Invalid type: " + type);
		}
		
		logger.debug("File Directory for {} exection: {}", type, fileDirectory);
		
		String dbCommandFile = FileUtils.buildPath(fileDirectory, name + "." + type + ".cmd");
		String dbCommandLogFile = FileUtils.buildPath(fileDirectory, name + "." + type + ".cmd.log");
		String lastDataLogFileName = null;
		
		if (FileUtils.fileExists(dbCommandFile) == true)
		{
			logger.debug("Deleting old command file: {}", dbCommandFile);
			FileUtils.deleteFile(dbCommandFile);
		}
		
		if (FileUtils.fileExists(dbCommandLogFile) == true)
		{
			logger.debug("Deleting old command log file: {}", dbCommandLogFile);
			FileUtils.deleteFile(dbCommandLogFile);
		}
		
		logger.debug("Creating command file");
		ArrayList<String> lines = new ArrayList<String>();
		String connectTo = dbconn.getDb();
		if ((connectToAlias == true) && (dbconn.getAlias() != null)) { connectTo = dbconn.getAlias(); }
		lines.add("connect to "+connectTo+" user "+dbconn.getUser()+" using \""+dbconn.getPassword()+"\";");
		lines.add("");
		for (String curName : nameSqlMap.keySet())
		{
			String sql = nameSqlMap.get(curName);
			String dataFileName = FileUtils.buildPath(fileDirectory, name + ".data." + curName + ".ixf");
			String dataLogFileName = FileUtils.buildPath(fileDirectory, name + ".data." + curName + "." + type + ".log");
			
			if ((FileUtils.fileExists(dataFileName) == true) && (type.equalsIgnoreCase(TYPE_EXPORT) == true))
			{
				logger.debug("Deleting old data file: {}", dataFileName);
				FileUtils.deleteFile(dataFileName);
			}
			
			if (FileUtils.fileExists(dataLogFileName) == true)
			{
				logger.debug("Deleting old data log file: {}", dataLogFileName);
				FileUtils.deleteFile(dataLogFileName);
			}
			
			if (type.equalsIgnoreCase(TYPE_EXPORT) == true) {
				lines.add("export to "+dataFileName+" of ixf messages "+dataLogFileName+" "+sql+";");
			} else if (type.equalsIgnoreCase(TYPE_IMPORT) == true) {
				lines.add("import from "+dataFileName+" of ixf commitcount 20000 messages "+dataLogFileName+" "+sql+";");
			} else if (type.equalsIgnoreCase(TYPE_RUNSTATS) == true) {
				lines.add("RUNSTATS ON TABLE "+curName+" ON KEY COLUMNS WITH DISTRIBUTION ON ALL COLUMNS AND INDEX ALL ALLOW READ ACCESS;");
			} else if (type.equalsIgnoreCase(TYPE_REORG) == true) {
				lines.add("REORG TABLE "+curName+" ALLOW READ ACCESS;");
			} else if (type.equalsIgnoreCase(TYPE_TRUNCATE) == true) {
				lines.add("TRUNCATE TABLE "+curName+" IMMEDIATE;");
				lines.add("COMMIT;");
			} else if (type.equalsIgnoreCase(TYPE_RAW) == true) {
				lines.add(sql);
			}
			lastDataLogFileName = dataLogFileName;
			
			lines.add("");
		}
		lines.add("connect reset;");
		lines.add("terminate;");
		FileUtils.writeLinesToFile(dbCommandFile, lines);
		
		logger.debug("Executing command file \"" + dbCommandFile + "\" with output in \"" + dbCommandLogFile + "\"");
		int ret = SystemUtils.exec("db2cmd db2 -stf " + dbCommandFile + " -z" + dbCommandLogFile);
		logger.debug("Execution returned {}", ret);
		
		// TODO: Enhancement - Check for errors or else we could camp out here all day
		
		String logToWaitFor = dbCommandLogFile;
		if  (
			(type.equalsIgnoreCase(TYPE_IMPORT) == true) ||
			(type.equalsIgnoreCase(TYPE_EXPORT) == true)
			)
		{
			logToWaitFor = lastDataLogFileName;
		}
		
		String cmdLogFileContents = null;
		logger.debug("Waiting for log file to be created: " + logToWaitFor);
		while (FileUtils.fileExists(logToWaitFor) == false)
		{
			if (FileUtils.fileExists(dbCommandLogFile) == true)
			{
				cmdLogFileContents = FileUtils.getFileContents(dbCommandLogFile);
				if (cmdLogFileContents.indexOf("SQL1013N") != -1)
				{
					throw new InfrastructureException("Unable to connect to Database. " + cmdLogFileContents);
				}
			}
			SystemUtils.sleep(5);
		}
			
		logger.debug("Make sure command log file contains expected verbage");
		int counter = 0;
		cmdLogFileContents = FileUtils.getFileContents(dbCommandLogFile);
		while (cmdLogFileContents.contains("The TERMINATE command completed successfully") == false) 
		{
			if (counter > 10)
			{
				logger.warn("\n" + cmdLogFileContents);
				throw new ProcessingException("Command log file has not completed");
			}
			counter++;
			SystemUtils.sleep(6);
			cmdLogFileContents = FileUtils.getFileContents(dbCommandLogFile);
		}
		
		logger.info(dbCommandLogFile + " contents:\n" + cmdLogFileContents);
	}
}
