package com.cffreedom.beans;

import java.util.ArrayList;

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
public class Task
{
	public final static String SYS_ASANA = "asana";
	public final static String SYS_TOODLEDO = "toodledo";
	
	private String sourceSystem;
	private Container folder;
	private Project project;
	private String code;
	private String title;
	private String note;
	private String meta;
	private ArrayList<Container> tags;

	public Task(String sourceSystem, Container folder, Project project, String code, String title, String note, String meta, ArrayList<Container> tags)
	{
		this.sourceSystem = sourceSystem;
		this.folder = folder;
		this.project = project;
		this.code = code;
		this.title = title;
		this.note = note;
		this.meta = meta;
		this.setTags(tags);
	}

	public String toString()
	{
		String returnVal = "";
		returnVal += "Code: " + this.getCode() + "\n";
		returnVal += "Title: " + this.getTitle() + "\n";
		returnVal += "Folder: " + this.getFolder().getValue() + "\n";
		returnVal += "Project: " + this.getProject().getName() + "\n";
		returnVal += "Note: " + this.getNote() + "\n";
		returnVal += "Tags: ";
		for (Container tag : this.getTags())
		{
			returnVal += tag.getValue() + ", ";
		}
		returnVal += "\n";
		return returnVal;
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

	public ArrayList<Container> getTags()
	{
		return this.tags;
	}

	public void setTags(ArrayList<Container> o)
	{
		this.tags = o;
	}
}
