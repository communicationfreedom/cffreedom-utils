package com.cffreedom.utils;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cffreedom.utils.file.FileUtils;
import com.cffreedom.utils.security.EncryptDecryptProxy;

/**
 * Simple java based serializable key/value pair manager with the option to
 * encrypt the values.
 * 
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
 */
public class KeyValueFileMgr
{
	private static final Logger logger = LoggerFactory.getLogger("com.cffreedom.utils.KeyValueFileMgr");
	
	private String file = null;
	TreeMap<String, Object> map = null;
	private EncryptDecryptProxy encDecProx = null;
	
	public KeyValueFileMgr(String file)
	{
		try
		{
			this.file = file;
			this.map = this.loadFile(file);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private boolean encryptVals()
	{
		if (this.encDecProx == null){
			return false;
		}else{
			return true;
		}
	}
	
	public void printEntryKeys() { printEntryKeys("Keys"); }
	public void printEntryKeys(String header) { printEntryKeys("Keys", false); }
	public void printEntryKeys(String header, boolean printValues)
	{
		Utils.output(header);
		Utils.output("======================");
		if ((this.map != null) && (this.map.size() > 0))
		{
			for(Map.Entry<String,Object> entry : this.map.entrySet())
			{
				  String key = entry.getKey();
				  if ((printValues == true) && (this.encryptVals() == false))
				  {
					  String value = (String)entry.getValue();
					  Utils.output(key + " - " + value);
				  }
				  else
				  {
					  Utils.output(key);
				  }
			}
		}
	}
	
	public boolean keyExists(String key)
	{
		return this.map.containsKey(key);
	}
	
	public Object getEntry(String key)
	{
		return this.map.get(key);
	}
	
	public String getEntryAsString(String key)
	{
		String val = (String)this.getEntry(key);
		if (this.encryptVals() == true)
		{
			val = this.encDecProx.decrypt(val);
		}
		return val;
	}
		
	public boolean addEntry(String key, Object value)
	{
		if (this.keyExists(key) == false)
		{
			this.map.put(key, value);
			saveFile();
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public boolean addEntry(String key, String value)
	{
		if (this.encryptVals() == true)
		{
			value = this.encDecProx.encrypt(value);
		}
		return this.addEntry(key, (Object)value);
	}
	
	public boolean updateEntry(String key, Object value)
	{
		if (this.keyExists(key) == true)
		{
			this.removeEntry(key);
			return this.addEntry(key, value);
		}
		else
		{
			return false;
		}
	}
	
	public boolean updateEntry(String key, String value)
	{
		if (this.keyExists(key) == true)
		{
			this.removeEntry(key);
			return this.addEntry(key, value);
		}
		else
		{
			return false;
		}
	}
	
	public boolean removeEntry(String key)
	{
		if (this.keyExists(key) == true)
		{
			this.map.remove(key);
			saveFile();
			return true;
		}
		else
		{
			return false;
		}
	}
	
	private void saveFile()
	{
		logger.debug("Saving to file: {}", this.file);
		FileUtils.writeObjectToFile(this.file, this.map);
	}
	
	@SuppressWarnings("unchecked")
	private TreeMap<String, Object> loadFile(String infile) throws IOException, ClassNotFoundException
	{
		if (FileUtils.fileExists(infile) == true)
		{
			logger.debug("Loading: {}", infile);
			return (TreeMap<String, Object>)FileUtils.readObjectFromFile(infile);
		}
		else
		{
			return new TreeMap<String, Object>();
		}
    }
}
