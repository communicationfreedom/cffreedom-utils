package com.cffreedom.utils.file;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import junit.framework.Assert;

import org.junit.Test;

public class FileUtilsTest
{
	@Test
	public void testGetLastLine() throws IOException
	{
		int linesToRead = 2;
		File file = new File("src/test/java/com/cffreedom/utils/file/test.txt");
		ArrayList<String> lines = FileUtils.getLastLineOfFile(file.getAbsolutePath(), linesToRead);
		Assert.assertEquals(linesToRead, lines.size());
		Assert.assertEquals("This is line 3", lines.get(0));
		Assert.assertEquals("This is line 2", lines.get(1));
	}
}
