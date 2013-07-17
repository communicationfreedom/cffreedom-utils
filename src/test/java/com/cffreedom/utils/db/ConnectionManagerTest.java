package com.cffreedom.utils.db;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;

import com.cffreedom.exceptions.FileSystemException;
import com.cffreedom.exceptions.InfrastructureException;

public class ConnectionManagerTest
{
	@Test
	public void testGetConnection() throws FileSystemException, IOException, InfrastructureException
	{
		ConnectionManager cm = new ConnectionManager();
		Assert.assertNull(cm.getConnection("junkkeythatshouldnotexit", null, null));
	}
}
