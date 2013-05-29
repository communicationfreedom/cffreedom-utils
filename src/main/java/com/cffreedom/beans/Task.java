package com.cffreedom.beans;

import java.util.ArrayList;
import java.util.Date;

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
 * 
 * Changes:
 * 2013-05-29 	markjacobsen.net 	Additional constructors and start/due dates added
 */
public class Task
{
	public final static String SYS_UNKNOWN = "unknown";
	public final static String SYS_ASANA = "asana";
	public final static String SYS_TOODLEDO = "toodledo";
	
	private String sourceSystem;
	private Container folder;
	private Project project;
	private String code;
	private String title;
	private String note;
	private String meta;
	private Date startDate;
	private Date dueDate;
	private ArrayList<Container> tags;

	public Task(String code, String title, String note)
	{
		this(Task.SYS_UNKNOWN, null, null, code, title, note, null, null, null, null);
	}
	
	public Task(String code, String title, String note, Date dueDate)
	{
		this(Task.SYS_UNKNOWN, null, null, code, title, note, null, dueDate, dueDate, null);
	}
	
	public Task(String folder, String code, String title, String note)
	{
		this(Task.SYS_UNKNOWN, new Container(folder, folder), null, code, title, note, null, null, null, null);
	}
	
	public Task(String folder, String code, String title, String note, Date dueDate)
	{
		this(Task.SYS_UNKNOWN, new Container(folder, folder), null, code, title, note, null, dueDate, dueDate, null);
	}
	
	public Task(String sourceSystem, Container folder, Project project, String code, String title, String note, String meta, Date startDate, Date dueDate, ArrayList<Container> tags)
	{
		this.sourceSystem = sourceSystem;
		this.folder = folder;
		this.project = project;
		this.code = code;
		this.title = title;
		this.note = note;
		this.meta = meta;
		this.startDate = startDate;
		this.dueDate = dueDate;
		this.setTags(tags);
	}

	public String toString()
	{
		String returnVal = "";
		returnVal += "Code: " + this.getCode() + "\n";
		returnVal += "Title: " + this.getTitle() + "\n";
		if (this.getStartDate() != null) { returnVal += "Start: " + this.getStartDate() + "\n"; }
		if (this.getDueDate() != null) { returnVal += "Due: " + this.getDueDate() + "\n"; }
		if (this.getFolder() != null) { returnVal += "Folder: " + this.getFolder().getValue() + "\n"; }
		if (this.getProject() != null) { returnVal += "Project: " + this.getProject().getName() + "\n"; }
		returnVal += "Note: " + this.getNote() + "\n";
		if (this.getTags() != null)
		{
			returnVal += "Tags: ";
			for (Container tag : this.getTags())
			{
				returnVal += tag.getValue() + ", ";
			}
			returnVal += "\n";
		}
		return returnVal;
	}
	
	public String getSourceSystem()
	{
		return this.sourceSystem;
	}

	public Container getFolder()
	{
		return this.folder;
	}

	public Project getProject()
	{
		return this.project;
	}

	public String getCode()
	{
		return this.code;
	}

	public String getTitle()
	{
		return this.title;
	}

	public String getNote()
	{
		return this.note;
	}

	public String getMeta()
	{
		return this.meta;
	}
	
	public Date getStartDate()
	{
		return this.startDate;
	}
	
	public Date getDueDate()
	{
		return this.dueDate;
	}

	public ArrayList<Container> getTags()
	{
		return this.tags;
	}

	public void setTags(ArrayList<Container> o)
	{
		this.tags = o;
	}
}
