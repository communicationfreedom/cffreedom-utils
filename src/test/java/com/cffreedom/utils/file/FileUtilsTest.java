package com.cffreedom.utils.file;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class FileUtilsTest
{
	@Test
	public void testGetLastLine() throws IOException
	{
		File file = new File("src/test/java/com/cffreedom/utils/file/test.txt");
		String line = FileUtils.getLastLine(file.getAbsolutePath());
		assertEquals("This is line 3", line);
	}
	
	@Test
	public void testGetLastXLines() throws IOException
	{
		int linesToRead = 2;
		File file = new File("src/test/java/com/cffreedom/utils/file/test.txt");
		List<String> lines = FileUtils.getLastXLines(file.getAbsolutePath(), linesToRead);
		assertEquals(linesToRead, lines.size());
		assertEquals("This is line 3", lines.get(0));
		assertEquals("This is line 2", lines.get(1));
	}
	
	@Test
	public void testGetFirstXLines() throws IOException
	{
		int linesToRead = 2;
		File file = new File("src/test/java/com/cffreedom/utils/file/test.txt");
		List<String> lines = FileUtils.getFirstXLines(file.getAbsolutePath(), linesToRead);
		assertEquals(linesToRead, lines.size());
		assertEquals("This is line 1", lines.get(0));
		assertEquals("This is line 2", lines.get(1));
	}
}
