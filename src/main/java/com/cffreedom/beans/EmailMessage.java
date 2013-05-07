package com.cffreedom.beans;

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
 * 2013-05-07 	markjacobsen.net 	Additional constructor
 */
public class EmailMessage
{
	private String from;
	private String replyTo;
	private String to;
	private String cc;
	private String bcc;
	private String subject;
	private String body;
	private String bodyHtml;
	private Date msgDate;
	private int msgId;
	private String msgHeaders;
	
	public EmailMessage(String from, String replyTo, String to, String cc, String subject, String body, String bodyHtml, Date msgDate, int msgId, String headers)
	{
		super();
		this.from = from;
		this.replyTo = replyTo;
		this.to = to;
		this.cc = cc;
		this.subject = subject;
		this.body = body;
		this.bodyHtml = bodyHtml;
		this.msgDate = msgDate;
		this.msgId = msgId;
		this.msgHeaders = headers;
	}
	
	/**
	 * Useful for creating objects to pass to getMailtoLink()
	 * @param to
	 * @param cc
	 * @param bcc
	 * @param subject
	 * @param body
	 */
	public EmailMessage(String to, String cc, String bcc, String subject, String body)
	{
		super();
		this.to = to;
		this.cc = cc;
		this.bcc = bcc;
		this.subject = subject;
		this.body = body;
	}
	
	public String getFrom() { return this.from; }
	public String getReplyTo() { return this.replyTo; }
	public String getTo() { return this.to; }
	public String getCc() { return this.cc; }
	public String getBcc() { return this.bcc; }
	public String getSubject() { return this.subject; }
	public String getBody() { return this.body; }
	public String getBodyHtml() { return this.bodyHtml; }
	public Date getDate() { return this.msgDate; }
	public int getId() { return this.msgId; }
	public String getHeaders() { return this.msgHeaders; }
}
