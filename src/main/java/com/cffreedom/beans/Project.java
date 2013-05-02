package com.cffreedom.beans;

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
public class Project
{
	private String code;
	private String syncCode;
	private String name;
	private String notes;
	
	public Project(String code, String syncCode, String name, String notes)
	{
		this.code = code;
		this.syncCode = syncCode;
		this.name = name;
		this.notes = notes;
	}
	
	public String getCode() { return this.code; }
	public String getSyncCode() { return this.syncCode; }
	public String getName() { return this.name; }
	public String getNotes() { return this.notes; }
}
