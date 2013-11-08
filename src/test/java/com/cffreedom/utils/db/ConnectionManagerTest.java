package com.cffreedom.utils.db;

import java.io.IOException;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.cffreedom.beans.DbConn;
import com.cffreedom.beans.DbType;
import com.cffreedom.exceptions.FileSystemException;
import com.cffreedom.exceptions.InfrastructureException;
import com.cffreedom.utils.SystemUtils;
import com.cffreedom.utils.file.FileUtils;

public class ConnectionManagerTest
{
	private String testFile = FileUtils.buildPath(SystemUtils.getDirConfig(), "junit.test.dbconn.properties");
	ConnectionManager cm;
	
	@Test
	public void testGetConnection() throws FileSystemException, IOException, InfrastructureException
	{
		cm = new ConnectionManager("nofile", false);
		Assert.assertNull(cm.getConnection("junkkeythatshouldnotexit", null, null));		
	}
	
	@Test
	public void testReadWrite() throws Exception
	{
		cm = new ConnectionManager(testFile);
		String connkey = "test";
		
		// Create the test file and make sure it exists on disk
		DbConn dbconn = new DbConn("jdbcDriverClass", "jdbcUrlVal", DbType.MYSQL, "hostName", "dbName", 0);
		cm.addConnection(connkey, dbconn);
		cm.close();
		cm = null;
		assertTrue(FileUtils.fileExists(testFile));
		
		// Read the file created back in and make sure we can get the values that were stored
		cm = new ConnectionManager(testFile);
		DbConn actual = cm.getDbConn(connkey);
		assertNotNull(actual);
		assertEquals(DbType.MYSQL, actual.getType());
		assertEquals("hostName", actual.getHost());
		assertEquals("dbName", actual.getDb());
		assertEquals(0, actual.getPort());
		
		// Delete the file and make sure it's actually gone
		FileUtils.deleteFile(testFile);
		assertFalse(FileUtils.fileExists(testFile));
		
	}
}
