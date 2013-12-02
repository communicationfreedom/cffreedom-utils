package com.cffreedom.utils.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cffreedom.beans.DbConn;
import com.cffreedom.beans.DbDriver;
import com.cffreedom.beans.DbType;
import com.cffreedom.exceptions.FileSystemException;
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
 * 2013-10-17 	MarkJacobsen.net 	Improvements for when running on windows vs *nux
 * 2013-11-26 	MarkJacobsen.net 	Added runRawSql() and resetIdentities()
 */
public class Db2Utils
{
	public final static String DRIVER = DbUtils.getDefaultDriver(DbType.DB2);
	public final static String DRIVER_JCC = DbDriver.DB2_JCC.value;
	public final static String DRIVER_APP = DbDriver.DB2_APP.value;
	public final static String DRIVER_NET = DbDriver.DB2_NET.value;
	
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
		catch (ProcessingException | ValidationException | InfrastructureException | FileSystemException e)
		{
			logger.error("Error during processing: " + e.getMessage(), e);
			throw new ProcessingException(e);
		}
	}
	
	/**
	 * Given a Map with key=table and value=column, reset the IDENTITY pointer to MAX(column)+1 on table
	 * @param fileDirectory
	 * @param name
	 * @param tableColumnMap
	 * @param dbconn
	 * @param connectToAlias
	 * @throws ProcessingException
	 */
	public static void resetIdentities(String fileDirectory, String name, Map<String, String> tableColumnMap, DbConn dbconn, boolean connectToAlias) throws ProcessingException
	{
		try
		{
			StringBuffer sb = new StringBuffer();
			int counter = 0;
			for (String table : tableColumnMap.keySet())
			{
				counter++;
				String column = tableColumnMap.get(table);
				sb.append("SELECT 'ALTER TABLE " + table + " ALTER COLUMN " + column + " RESTART WITH ' || TRIM(CHAR(MAX(COALESCE(" + column + ", 0)) + 1)) AS SQL_X FROM " + table + " ");
				if (counter < tableColumnMap.size())
				{
					sb.append("UNION ");
				}
			}
			
			String url = DbUtils.getUrl(dbconn.getType(), dbconn.getHost(), dbconn.getDb(), dbconn.getPort());
			Connection conn = DbUtils.getConnection(dbconn.getDriver(), url, dbconn.getUser(), dbconn.getPassword());
			PreparedStatement stmt = conn.prepareStatement(sb.toString());
			ResultSet rs = stmt.executeQuery();
			
			ArrayList<String> alterSql = new ArrayList<String>();
			while (rs.next() == true)
			{
				alterSql.add(rs.getString("SQL_X"));
				logger.debug(alterSql.get(alterSql.size() - 1));
			}
			
			runRawSql(fileDirectory, name, alterSql, dbconn, connectToAlias);
		}
		catch (Exception e)
		{
			logger.error(e.getClass().getSimpleName() + " during processing: " + e.getMessage(), e);
			throw new ProcessingException(e);
		}
	}
	
	public static void importFromFile(String fileDirectory, String name, Map<String, String> nameSqlMap, DbConn dbconn, boolean connectToAlias) throws ProcessingException
	{
		try
		{
			execCommands(TYPE_IMPORT, fileDirectory, name, nameSqlMap, dbconn, connectToAlias);
		}
		catch (ProcessingException | ValidationException | InfrastructureException | FileSystemException e)
		{
			logger.error("Error during processing: " + e.getMessage(), e);
			throw new ProcessingException(e);
		}
	}
	
	/**
	 * Run the SQL statements in the sql ArrayList
	 * @param fileDirectory
	 * @param name
	 * @param sql
	 * @param dbconn
	 * @param connectToAlias
	 * @throws ProcessingException
	 */
	public static void runRawSql(String fileDirectory, String name, ArrayList<String> sql, DbConn dbconn, boolean connectToAlias) throws ProcessingException
	{
		Map<String, String> nameSqlMap = new LinkedHashMap<String, String>(); // LinkedHashMap to maintain order
		int stmtCnt = 0;
		for (String curSql : sql)
		{
			stmtCnt++;
			nameSqlMap.put("stmt-" + stmtCnt, curSql);
		}
		
		try
		{
			execCommands(TYPE_RAW, fileDirectory, name, nameSqlMap, dbconn, connectToAlias);
		}
		catch (ProcessingException | ValidationException | InfrastructureException | FileSystemException e)
		{
			logger.error("Error during processing: " + e.getMessage(), e);
			throw new ProcessingException(e);
		}
	}
	
	public static void runStatsOnTables(String fileDirectory, String name, List<String> tables, DbConn dbconn, boolean connectToAlias) throws ProcessingException
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
		catch (ProcessingException | ValidationException | InfrastructureException | FileSystemException e)
		{
			logger.error("Error during processing: " + e.getMessage(), e);
			throw new ProcessingException(e);
		}
	}
	
	public static void reorgTables(String fileDirectory, String name, List<String> tables, DbConn dbconn, boolean connectToAlias) throws ProcessingException
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
		catch (ProcessingException | ValidationException | InfrastructureException | FileSystemException e)
		{
			logger.error("Error during processing: " + e.getMessage(), e);
			throw new ProcessingException(e);
		}
	}
	
	public static void truncateTables(String fileDirectory, String name, List<String> tables, DbConn dbconn, boolean connectToAlias) throws ProcessingException
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
		catch (ProcessingException | ValidationException | InfrastructureException | FileSystemException e)
		{
			logger.error("Error during processing: " + e.getMessage(), e);
			throw new ProcessingException(e);
		}
	}
	
	public static void grantAccessToTables(String fileDirectory, String name, List<String> tables, DbConn dbconn, boolean connectToAlias, String grantTo, boolean select, boolean insert, boolean update, boolean delete) throws ProcessingException
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
		catch (ProcessingException | ValidationException | InfrastructureException | FileSystemException e)
		{
			logger.error("Error during processing: " + e.getMessage(), e);
			throw new ProcessingException(e);
		}
	}
	
	private static void execCommands(String type, String fileDirectory, String name, Map<String, String> nameSqlMap, DbConn dbconn, boolean connectToAlias) throws ProcessingException, ValidationException, InfrastructureException, FileSystemException
	{
		int execRet = -1;
		List<String> filesToDelete = new ArrayList<String>();
		
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
		
		filesToDelete.add(dbCommandFile);
		
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
		List<String> lines = new ArrayList<String>();
		String connectTo = dbconn.getDb();
		if ((connectToAlias == true) && (dbconn.getAlias() != null)) { connectTo = dbconn.getAlias(); }
		lines.add("connect to "+connectTo+" user "+dbconn.getUser()+" using \""+dbconn.getPassword()+"\";");
		lines.add("");
		for (String mapKey : nameSqlMap.keySet())
		{
			String mapValue = nameSqlMap.get(mapKey);
			String dataFileName = FileUtils.buildPath(fileDirectory, name + ".data." + mapKey + ".ixf");
			String dataLogFileName = FileUtils.buildPath(fileDirectory, name + ".data." + mapKey + "." + type + ".log");
			
			if (type.equalsIgnoreCase(TYPE_EXPORT) == true)
			{
				if (FileUtils.fileExists(dataFileName) == true)
				{
					logger.debug("Deleting old data file: {}", dataFileName);
					FileUtils.deleteFile(dataFileName);
				}
			}
			else
			{
				// Note: We do not want to delete the data after an export because we're assuming an import will happen
				// and that process should delete the file if appropriate
				logger.info("Clean up {} after your export. It will not happen here.", dataFileName);
				filesToDelete.add(dataFileName);
			}
			
			if (FileUtils.fileExists(dataLogFileName) == true)
			{
				logger.debug("Deleting old data log file: {}", dataLogFileName);
				FileUtils.deleteFile(dataLogFileName);
			}
			
			if (type.equalsIgnoreCase(TYPE_EXPORT) == true) {
				lines.add("export to "+dataFileName+" of ixf messages "+dataLogFileName+" "+mapValue+";");
			} else if (type.equalsIgnoreCase(TYPE_IMPORT) == true) {
				lines.add("import from "+dataFileName+" of ixf commitcount 20000 messages "+dataLogFileName+" "+mapValue+";");
			} else if (type.equalsIgnoreCase(TYPE_RUNSTATS) == true) {
				lines.add("RUNSTATS ON TABLE "+mapKey+" ON KEY COLUMNS WITH DISTRIBUTION ON ALL COLUMNS AND INDEX ALL ALLOW READ ACCESS;");
			} else if (type.equalsIgnoreCase(TYPE_REORG) == true) {
				lines.add("REORG TABLE "+mapKey+" ALLOW READ ACCESS;");
			} else if (type.equalsIgnoreCase(TYPE_TRUNCATE) == true) {
				lines.add("TRUNCATE TABLE "+mapKey+" IMMEDIATE;");
				lines.add("COMMIT;");
			} else if (type.equalsIgnoreCase(TYPE_RAW) == true) {
				lines.add(mapValue+";");
			}
			lastDataLogFileName = dataLogFileName;
			
			lines.add("");
		}
		lines.add("connect reset;");
		lines.add("terminate;");
		FileUtils.writeLinesToFile(dbCommandFile, lines);
		
		String execCmd = "db2 -stf " + dbCommandFile + " -z" + dbCommandLogFile;
		String command;
		if (SystemUtils.isWindows() == true)
		{
			command = "db2cmd " + execCmd;
			logger.debug("Executing command file \"" + dbCommandFile + "\" with output in \"" + dbCommandLogFile + "\"");
			execRet = SystemUtils.exec(command);
		}
		else
		{
			String stubFile = FileUtils.buildPath(fileDirectory, name + ".stub.cmd");
			List<String> commands = new ArrayList<String>();
			if ((dbconn.getProfileFile() != null) && (dbconn.getProfileFile().length() > 0))
			{
				commands.add(". " + dbconn.getProfileFile());  // ex: /dba/db2/cdXX/sqllib/db2profile
			}
			else
			{
				logger.warn("No ProfileFile specified in DbConn for " + dbconn.getDb() + ". Unexpected results may ensue.");
			}
			commands.add(execCmd);
			FileUtils.writeLinesToFile(stubFile, commands);
			command = stubFile;
			filesToDelete.add(stubFile);
			
			try
			{
				FileUtils.chmod(stubFile, "744");
			}
			catch (Exception e)
			{
				logger.error("Unable to set permissions on " + stubFile + ": " + e.getMessage(), e);
			}
			
			logger.debug("Executing command file \"" + dbCommandFile + "\" via \""+command+"\" with output in \"" + dbCommandLogFile + "\"");
			execRet = SystemUtils.exec(command, new String[]{}, fileDirectory);
		}
		logger.debug("Execution returned {}", execRet);
		
		if (execRet != 0)
		{
			throw new ProcessingException("Attempt to run "+command+" returned "+execRet);
		}
		
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
		
		if (cmdLogFileContents.contains("There is at least one warning message in the message file") == true)
		{
			throw new ProcessingException("There is a warning in a message file");
		}
		
		for (String file : filesToDelete)
		{
			if (FileUtils.fileExists(file) == true)
			{
				logger.debug("Deleting: {}", file);
				FileUtils.deleteFile(file);
			}
		}
		
		logger.debug("Returning");
	}
}
