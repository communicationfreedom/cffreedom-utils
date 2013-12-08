package com.cffreedom.utils.db;

import static org.junit.Assert.*;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

public class HSQLDBUtilsTest
{

	@Test
	public void test()
	{
		String name = "MyTable";
		Map<String, String> columns = new LinkedHashMap<String, String>();
		columns.put("NAME", "VARCHAR(100) NOT NULL");
		columns.put("EMAIL", "VARCHAR(500) NOT NULL");
		assertEquals("CREATE TABLE MyTable (NAME VARCHAR(100) NOT NULL, EMAIL VARCHAR(500) NOT NULL)", HSQLDBUtils.getCreateTableSql(name, columns));
	}

}
