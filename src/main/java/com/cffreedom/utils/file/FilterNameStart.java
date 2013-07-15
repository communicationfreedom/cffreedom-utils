package com.cffreedom.utils.file;

import java.io.File;
import java.io.FilenameFilter;

/**
 * FilenameFilter implementation to get only files starting with the specified text
 * 
 * Credit: http://www.java-samples.com/showtutorial.php?tutorialid=384
 * 
 * Original Class: com.cffreedom.utils.file.FilterNameStart
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
public class FilterNameStart implements FilenameFilter
{
	String find;
	
	public FilterNameStart(String find)
	{
		this.find = find.toLowerCase();
	}
	
	public boolean accept(File dir, String name)
	{ 
		return name.toLowerCase().startsWith(this.find); 
	}
}
