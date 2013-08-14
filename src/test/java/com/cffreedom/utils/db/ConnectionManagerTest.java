package com.cffreedom.utils.db;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;

import com.cffreedom.beans.DbConn;
import com.cffreedom.exceptions.FileSystemException;
import com.cffreedom.exceptions.InfrastructureException;
import com.cffreedom.utils.SystemUtils;
import com.cffreedom.utils.file.FileUtils;

public class ConnectionManagerTest
{
	@Test
	public void testGetConnection() throws FileSystemException, IOException, InfrastructureException
	{
		ConnectionManager cm = new ConnectionManager();
		Assert.assertNull(cm.getConnection("junkkeythatshouldnotexit", null, null));
	}
	
	@Test
	public void testReadWrite() throws Exception
	{
		String connkey = "test";
		String file = FileUtils.buildPath(SystemUtils.getDirConfig(), "junit.test.dbconn.properties");
		
		// Create the test file
		ConnectionManager cm = new ConnectionManager(file, true);
		DbConn dbconn = new DbConn("jdbcDriverClass", "jdbcUrlVal", DbUtils.TYPE_MYSQL, "hostName", "dbName", 0);
		cm.addConnection(connkey, dbconn);
		cm.close();
		
		// Read from the test file
		ConnectionManager cm2 = new ConnectionManager(file);
		DbConn dbconn2 = cm2.getDbConn(connkey);
		Assert.assertEquals(DbUtils.TYPE_MYSQL, dbconn2.getType());
		
		// Update the value and check it again
		DbConn dbconn3 = new DbConn("jdbcDriverClass", "jdbcUrlVal", DbUtils.TYPE_DB2, "hostName", "dbName", 0);
		cm2.updateConnection(connkey, dbconn3);
		DbConn dbconn4 = cm2.getDbConn(connkey);
		Assert.assertEquals(DbUtils.TYPE_DB2, dbconn4.getType());
		
		// Delete the value and check it again
		cm2.deleteConnection(connkey);
		DbConn dbconn5 = cm2.getDbConn(connkey);
		Assert.assertNull(dbconn5);		
		
		// Cleanup
		cm2.close();
		FileUtils.deleteFile(file);
	}
}
