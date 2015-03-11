package com.cffreedom.utils.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cffreedom.exceptions.FileSystemException;
import com.cffreedom.exceptions.InfrastructureException;
import com.cffreedom.utils.DateTimeUtils;
import com.cffreedom.utils.Convert;
import com.cffreedom.utils.Format;
import com.cffreedom.utils.SystemUtils;
import com.cffreedom.utils.db.ConnectionManager;
import com.cffreedom.utils.db.DbUtils;
import com.cffreedom.utils.file.FileUtils;

public class Db2TableInfo
{
	private static final Logger log = LoggerFactory.getLogger(Db2TableInfo.class);
	
	private static ConnectionManager cm = null;
	private static String dbConnFile = SystemUtils.getDirConfig() + SystemUtils.getPathSeparator() + "dbconn.properties";
	
	/**
	 * Here mostly for testing. You should call the run method
	 * @param args
	 */
	public static void main(String[] args)
	{
		String outputFolder = SystemUtils.getDirWork();
		String[] dbKeys = {"DBKEY"};  // A key from the DB Connection Manager
		String[] dbSchemas = {"XX"}; // DB Schemas to get details for
		String username = "";
		String password = "";
		
		Db2TableInfo.run(outputFolder, dbKeys, dbSchemas, username, password);
	}
	
	/**
	 * What you want to get info for, and where to place the output
	 * @param outputDir
	 * @param dbKeys
	 * @param dbSchemas
	 * @param username
	 * @param password
	 */
	public static void run(String outputDir, String[] dbKeys, String[] dbSchemas, String username, String password)
	{
		for (int x = 0; x < dbKeys.length; x++)
		{
			String dbKey = dbKeys[x];
			String outputFile = outputDir+dbKey+"-"+Format.date(Format.DATE_FILE_TIMESTAMP, new Date())+"-table-info.csv";
			getInfo(outputFile, dbKey, dbSchemas, username, password);
		}
		log.debug("Done");
	}
	
	private static void getInfo(String outputFile, String dbKey, String[] dbSchemas, String username, String password)
	{
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement stmt = null;
		
		try
		{
			for (int schemaIndex = 0; schemaIndex < dbSchemas.length; schemaIndex++)
			{
				String dbSchema = dbSchemas[schemaIndex];
				log.debug("Processing Schema: " + dbSchema);
			
				FileUtils.writeStringToFile(outputFile, "Schema,Table,Data (MB),Long Data (MB),XML Data (MB),LOB Data (MB),Total Data (MB),Total Data (GB),Index (MB)," +
						"Rows,Rows (stats),Rows Diff,Rows % Diff,Avg Row Size,Append Mode," +
						"Volatile,Table Space,Long Table Space,Index Space,Has PK,Has RI,Pages w Rows,Total Pages,Empty Pages," +
						"Stats Updated,Stat Days,Last Accessed,Days Since Last Access,Created,Altered,Columns,Indexes,Index Stat Min,Index Stat Max\n", false);
				
				cm = new ConnectionManager(dbConnFile);
				String sql = "SELECT * FROM SYSCAT.TABLES WHERE TABSCHEMA = '"+dbSchema+"' ORDER BY TABNAME FOR READ ONLY WITH UR";
				conn = cm.getConnection(dbKey, username, password);
				stmt = conn.prepareStatement(sql);
				rs = stmt.executeQuery();
				
				while (rs.next())
				{
					String schema = rs.getString("TABSCHEMA").trim();
					String table = rs.getString("TABNAME").trim();
					
					log.debug("Processing: " + dbKey + " - " + schema + "." + table);
					
					String sqlSize = "SELECT * FROM TABLE(SYSPROC.ADMIN_GET_TAB_INFO_V97( '"+schema+"', '"+table+"')) FOR READ ONLY WITH UR";
					String sqlCount = "SELECT COUNT(*) AS ROWS_NB FROM "+schema+"."+table+" FOR READ ONLY WITH UR";
					String sqlIndex = "SELECT COUNT(*) AS INDEX_Q, MIN(STATS_TIME) AS MIN_STATS_TIME, MAX(STATS_TIME) AS MAX_STATS_TIME FROM SYSCAT.INDEXES WHERE TABSCHEMA = '"+schema+"' AND TABNAME = '"+table+"' FOR READ ONLY WITH UR";
					long dataPhysicalSize = -1;
					long lobPhysicalSize = -1;
					long xmlPhysicalSize = -1;
					long longPhysicalSize = -1;
					long indexPhysicalSize = -1;
					long dataPhysicalSizeKB = -1;
					long indexPhysicalSizeKB = -1;
					long rowCount = -1;
					int rowCountStats = rs.getInt("CARD");
					Calendar created = Convert.toCalendar(rs.getTimestamp("CREATE_TIME"));
					Calendar altered = Convert.toCalendar(rs.getTimestamp("ALTER_TIME"));
					Calendar statsUpdated = Convert.toCalendar(rs.getTimestamp("STATS_TIME"));
					int columns = rs.getInt("COLCOUNT");
					int pagesWithRows = rs.getInt("NPAGES");
					int pagesTotal = rs.getInt("FPAGES");
					int pkeyCols = rs.getInt("KEYCOLUMNS");
					int riParents = rs.getInt("PARENTS");
					int riChildren = rs.getInt("CHILDREN");
					String tableSpace = rs.getString("TBSPACE");
					String tableSpaceLong = rs.getString("LONG_TBSPACE");
					String indexSpace = rs.getString("INDEX_TBSPACE");
					String appendMode = rs.getString("APPEND_MODE");
					String volatileFlag = rs.getString("VOLATILE");
					int avgRowSize = rs.getInt("AVGROWSIZE");
					Calendar lastAccessed = null;
					try
					{
						lastAccessed = Convert.toCalendar(rs.getTimestamp("LASTUSED"));
					}
					catch (SQLException e) {}
					int indexCount = -1;
					Calendar indexMinStats = null;
					Calendar indexMaxStats = null;
					
					PreparedStatement stmtSize = null;
					ResultSet rsSize = null;
					PreparedStatement stmtCount = null;
					ResultSet rsCount = null;
					PreparedStatement stmtIndex = null;
					ResultSet rsIndex = null;
					
					try
					{
						stmtCount = conn.prepareStatement(sqlCount);
						rsCount = stmtCount.executeQuery();
						while (rsCount.next())
						{
							rowCount = rsCount.getInt("ROWS_NB");
						}
					}
					catch (SQLException e)
					{
						log.error("Unable to get physical row count for " + table);
					}
					finally
					{
						DbUtils.cleanup(null, stmtCount, rsCount);
					}
						
					try
					{
						stmtSize = conn.prepareStatement(sqlSize);
						rsSize = stmtSize.executeQuery();
						while (rsSize.next())
						{
							dataPhysicalSize = rsSize.getLong("DATA_OBJECT_P_SIZE");
							lobPhysicalSize = rsSize.getLong("LOB_OBJECT_P_SIZE");
							xmlPhysicalSize = rsSize.getLong("XML_OBJECT_P_SIZE");
							longPhysicalSize = rsSize.getLong("LONG_OBJECT_P_SIZE");
							indexPhysicalSize = rsSize.getLong("INDEX_OBJECT_P_SIZE");
							
							dataPhysicalSizeKB = dataPhysicalSize + lobPhysicalSize + xmlPhysicalSize + longPhysicalSize;
							indexPhysicalSizeKB = indexPhysicalSize;
						}
					}
					catch (SQLException e)
					{
						e.printStackTrace();
					}
					finally
					{
						DbUtils.cleanup(null, stmtSize, rsSize);
					}
					
					try
					{
						stmtIndex = conn.prepareStatement(sqlIndex);
						rsIndex = stmtIndex.executeQuery();
						while (rsIndex.next())
						{
							indexCount = rsIndex.getInt("INDEX_Q");
							indexMinStats = Convert.toCalendar(rsIndex.getTimestamp("MIN_STATS_TIME"));
							indexMaxStats = Convert.toCalendar(rsIndex.getTimestamp("MAX_STATS_TIME"));
						}
					}
					catch (SQLException e)
					{
						e.printStackTrace();
					}
					finally
					{
						DbUtils.cleanup(null, stmtIndex, rsIndex);
					}
					
					
					// Evaluated values
					String hasPK = "No";
					String hasRI = "No";
					String rowCountChange = "?";
					String rowCountPctChange = "?";
					String lastAccessedStr = Format.date(Format.DATE_FILE_TIMESTAMP, lastAccessed);
					String lastAccessedDays = "?";
					int statsDays = -1;
					if (volatileFlag.equalsIgnoreCase("C") == true) { volatileFlag = "Yes (C)"; }
					if (pkeyCols > 0) { hasPK = "Yes"; }
					if ((riParents > 0) || (riChildren > 0)) { hasRI = "Yes"; }
					if ((rowCountStats >= 0) && (rowCount >= 0)) { rowCountChange = (rowCount - rowCountStats) + ""; }
					if ((rowCountStats > 0) && (rowCount >= 0)) { rowCountPctChange = (((rowCount - rowCountStats) / rowCountStats) * 100) + "%"; }
					if ((rowCountStats == 0) && (rowCount == 0)) { rowCountPctChange = "0%"; }
					if (statsUpdated != null) { statsDays = DateTimeUtils.dateDiff(statsUpdated, Calendar.getInstance(), 'd'); }
					if ((lastAccessed != null) && (lastAccessedStr.equalsIgnoreCase("0001-01-01") == false)) { lastAccessedDays = DateTimeUtils.dateDiff(lastAccessed, Calendar.getInstance(), 'd') + ""; }
					
					FileUtils.writeStringToFile(outputFile, schema + "," + 
															table + "," + 
															dataPhysicalSize / 1024 + "," + 
															longPhysicalSize / 1024 + "," + 
															xmlPhysicalSize / 1024 + "," + 
															lobPhysicalSize / 1024 + "," + 
															dataPhysicalSizeKB / 1024 + "," + 
															dataPhysicalSizeKB / 1024 / 1024 + "," + 
															indexPhysicalSizeKB / 1024 + "," + 
															rowCount + "," + 
															rowCountStats + "," + 
															rowCountChange + "," + 
															rowCountPctChange + "," +
															avgRowSize + "," +
															appendMode + "," +
															volatileFlag + "," +
															tableSpace + "," +
															tableSpaceLong + "," +
															indexSpace + "," +
															hasPK + "," +
															hasRI + "," +
															pagesWithRows + "," +
															pagesTotal + "," +
															(pagesTotal - pagesWithRows) + "," +
															Format.date(Format.DATE_DB2_TIMESTAMP, statsUpdated) + "," +
															statsDays + "," +
															lastAccessedStr + "," +
															lastAccessedDays + "," +
															Format.date(Format.DATE_DB2_TIMESTAMP, created) + "," +
															Format.date(Format.DATE_DB2_TIMESTAMP, altered) + "," +
															columns + "," +
															indexCount + "," +
															Format.date(Format.DATE_DB2_TIMESTAMP, indexMinStats) + "," +
															Format.date(Format.DATE_DB2_TIMESTAMP, indexMaxStats) +
															"\n"
															, true);
				}
			}
		}
		catch (FileSystemException | InfrastructureException | SQLException e) 
		{
			e.printStackTrace();
		}
		finally
		{
			DbUtils.cleanup(conn, stmt, rs);
		}
	}
}
