package com.cffreedom.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
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
public class StreamGobbler extends Thread
{
	InputStream is;
	String type;
	OutputStream os;
	
	StreamGobbler(InputStream is, String type)
	{
		this(is, type, null);
	}
	
	StreamGobbler(InputStream is, String type, OutputStream redirect)
	{
		this.is = is;
		this.type = type;
		this.os = redirect;
	}
	
	public void run()
	{
		try
		{
			PrintWriter pw = null;
			if (this.os != null) { pw = new PrintWriter(this.os); }
			InputStreamReader isr = new InputStreamReader(this.is);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null)
			{
				if (pw != null){
					pw.println(line);
				}
				System.out.println(type + ">" + line);
			}
			if (pw != null){
				pw.flush();
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
