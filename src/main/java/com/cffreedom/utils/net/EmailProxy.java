package com.cffreedom.utils.net;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cffreedom.beans.EmailMessage;
import com.cffreedom.utils.ConversionUtils;

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
public class EmailProxy
{
	public final String PROTOCOL_POP3 = "pop3";
	public final String PROTOCOL_POP3S = "pop3s";
	public final String PROTOCOL_IMAP = "imap";
	private static final String G_ORDER_NEW2OLD = "NEW2OLD";
	private static final String G_ORDER_OLD2NEW = "OLD2NEW";
	
	private static final Logger logger = LoggerFactory.getLogger("com.cffreedom.utils.net.EmailProxy");
	
	private String protocol = null;
	private String user = null;
	private String pass = null;
	private String host = null;
	private int port = 0;
	private Session session;
	private Store store;
	
	public EmailProxy(String protocol, String user, String pass, String host) throws MessagingException
	{
		this(protocol, user, pass, host, 110);
	}
	
	public EmailProxy(String protocol, String user, String pass, String host, int port) throws MessagingException
	{
		if (host.equalsIgnoreCase("imap.gmail.com") == true)
		{
			protocol = PROTOCOL_POP3S;
			port = 993;
		}
		else if (host.equalsIgnoreCase("pop.mail.yahoo.com") == true)
		{
			protocol = PROTOCOL_POP3S;
		}
		else if (host.equalsIgnoreCase("pop.gmail.com") == true)
		{
			port = 995;
		}
		
		if (protocol == null) { protocol = PROTOCOL_POP3; }
		
		this.protocol = protocol;
		this.user = user;
		this.pass = pass;
		this.host = host;
		this.port = port;
		
		this.session = this.getSession(System.getProperties());
    	this.store = this.getStore(session, protocol);
    	
    	if (port > 0)
    	{
    		store.connect(host, port, user, pass);
    	}
    	else
    	{
    		store.connect(host, user, pass);    		
    	}
	}
	
	public String getProtocol() { return this.protocol; }
	public String getUsername() { return this.user; }
	public String getHost() { return this.host; }
	public int getPort() { return this.port; }
	
	private Session getSession(Properties props)
    {
    	props.put("mail.pop3.rsetbeforequit", "true");
    	Session session = Session.getInstance(props, null);
	    session.setDebug(false);
	    return session;
    }
	
	private Store getStore(Session session, String protocol)
    {
    	Store store = null;
	    
    	try
		{
		    if (protocol != null)
		    {
		    	store = session.getStore(protocol);
		    }
			else
			{
			
				store = session.getStore();
			}
		}
		catch (NoSuchProviderException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    return store;
    }
	
	public void disconnect()
	{
		try
		{
			this.store.close();
		}
		catch (MessagingException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
     * Get the folder from the store
     * @param a_oStore The message store containing the folder
     * @param a_sFolder Name of the folder to get
     * @return
     * @throws Exception
     */
    public Folder getFolder(Store a_oStore, String a_sFolder) throws Exception
    {
    	Folder l_oFolder = a_oStore.getDefaultFolder();
	    if (l_oFolder == null) {
	    	logger.error("No default folder");
	        System.exit(1);
	    }
	    
	    if (a_sFolder == null) a_sFolder = "INBOX";
	    l_oFolder = l_oFolder.getFolder(a_sFolder);
		if (l_oFolder == null) {
			logger.error("Invalid folder");
		    System.exit(1);
		}
		
		return l_oFolder;
    }
    
    public ArrayList<EmailMessage> getMail(String a_sName, String a_sAction, String folderName, 
    	int[] a_iMessageNumbers, int a_iStartRow, int a_iMaxRows, String a_sOrder) throws Exception
    {
    	ArrayList<EmailMessage> mail = new ArrayList<EmailMessage>();
    	String l_sBody = null;
    	Folder folder = this.getFolder(this.store, folderName);
    	folder.open(Folder.READ_ONLY);
    	int l_iMsgCount = folder.getMessageCount();
    	    	
    	if (l_iMsgCount > 0)
    	{
	    	Message[] l_oMail = null;
	    	if ( (a_iMessageNumbers != null) && (a_iMessageNumbers.length > 0) )
	    	{
	    		int l_iCounter = 0;
	    		Message[] l_oTempMail = new Message[a_iMessageNumbers.length];
	    		for (int i = 0; i < a_iMessageNumbers.length; i++)
	    		{
	    			if (a_iMessageNumbers[i] <= l_iMsgCount)
	    			{
	    				l_iCounter++;
	    				l_oTempMail[i] = folder.getMessage(a_iMessageNumbers[i]);
	    			}
	    		}
	    		l_oMail = new Message[l_iCounter];
	    		
	    		for (int i = 0; i < l_iCounter; i++)
	    		{
	    			if (a_sOrder.equals(G_ORDER_OLD2NEW) == true)
	    			{
	    				l_oMail[i] = l_oTempMail[i];
	    			}
	    			else
	    			{
	    				l_oMail[i] = l_oTempMail[l_iCounter - i - 1];
	    			}
	    		}
	    	}
	    	else if ( (a_iStartRow >= 0) && (a_iMaxRows >= 0) )
	    	{
	    		if (a_iStartRow + a_iMaxRows > l_iMsgCount)
	    		{
	    			// Make sure our max rows in no more than the actual number of rows we have
	    			a_iMaxRows = l_iMsgCount - a_iStartRow + 1;
	    		}
	    		
	    		if (a_sOrder.equals(G_ORDER_NEW2OLD) == true)
	    		{
	    			Message[] l_oTempMail1 = folder.getMessages();
	    			Message[] l_oTempMail2 = new Message[l_oTempMail1.length];
	    			for (int i = 0; i < a_iStartRow + a_iMaxRows - 1; i++)
	        		{
	    				l_oTempMail2[i] = l_oTempMail1[l_oTempMail1.length - i - 1];
	        		}
	    			l_oMail = new Message[a_iMaxRows];
	    			int l_iIndex = 0;
	    			for (int i = a_iStartRow - 1; i < a_iStartRow + a_iMaxRows - 1; i++)
	    			{
	    				l_oMail[l_iIndex] = l_oTempMail2[i];
	    				l_iIndex++;
	    			}
	    		}
	    		else
	    		{
	    			l_oMail = folder.getMessages(a_iStartRow, a_iStartRow + a_iMaxRows - 1);
	    		}
	    	}
	    	else
	    	{
	    		l_oMail = folder.getMessages();
	    		if (a_sOrder.equals(G_ORDER_NEW2OLD) == true)
	    		{
	    			Message[] l_oTempMail = l_oMail;
	    			for (int i = 0; i < l_oMail.length; i++)
	    			{
	    				l_oMail[i] = l_oTempMail[l_oMail.length - i - 1];
	    			}
	    		}
	    	}
	    	
	    	if (l_oMail != null)
	    	{
		    	// Try using a fetch profile for optimization
		    	FetchProfile l_oFP = new FetchProfile();
		    	l_oFP.add(FetchProfile.Item.ENVELOPE);
		    	l_oFP.add(FetchProfile.Item.FLAGS);
		    	l_oFP.add("X-Mailer");
				folder.fetch(l_oMail, l_oFP);
				    	
				for (int x = 0; x < l_oMail.length; x++)
				{
					String from;
					String replyTo;
					String to;
					String cc;
					String subject;
					String body;
					String bodyHtml;
					Date msgDate = null;
					int msgId;
					String headers;
					
					Message l_oMsg = l_oMail[x];
				
					logger.debug("Message: {}", l_oMsg.getMessageNumber());
					msgId = l_oMsg.getMessageNumber();
					subject = l_oMsg.getSubject();
					logger.debug("Subject: {}", l_oMsg.getSubject());
					
					if (l_oMsg.getSentDate() != null)
					{
						msgDate = l_oMsg.getSentDate();
					}
					
					// FROM
					String l_sFrom = "";
					try
					{
						Address[] l_oFrom = l_oMsg.getFrom();
						if (l_oFrom != null)
						{
							for (int i = 0; i < l_oFrom.length; i++)
							{
								l_sFrom += l_oFrom[i].toString();
								if (i < l_oFrom.length - 1) l_sFrom += ", ";
							}
						}
					} catch (Exception e) {}
					from = l_sFrom;
					//this.printDebug("From: " + l_sFrom);
					
					// TO
					String l_sTo = "";
					try
					{
						Address[] l_oTo = l_oMsg.getRecipients(Message.RecipientType.TO);
						if (l_oTo != null)
						{
							for (int i = 0; i < l_oTo.length; i++)
							{
								l_sTo += l_oTo[i].toString();
								if (i < l_oTo.length - 1) l_sTo += ", ";
							}
						}
					} catch (Exception e){}
					to = l_sTo;
					//this.printDebug("To: " + l_sTo);
					
					// CC
					String l_sCc = "";
					Address[] l_oCc = l_oMsg.getRecipients(Message.RecipientType.CC);
					if (l_oCc != null)
					{
						for (int i = 0; i < l_oCc.length; i++)
						{
							l_sCc += l_oCc[i].toString();
							if (i < l_oCc.length - 1) l_sCc += ", ";
						}
					}
					cc = l_sCc;
					//this.printDebug("CC: " + l_sCc);
					
					// REPLYTO
					String l_sReplyTo = "";
					Address[] l_oReplyTo = l_oMsg.getReplyTo();
					if (l_oReplyTo != null)
					{
						for (int i = 0; i < l_oReplyTo.length; i++)
						{
							l_sReplyTo += l_oReplyTo[i].toString();
							if (i < l_oReplyTo.length - 1) l_sReplyTo += ", ";
						}
					}
					replyTo = l_sReplyTo;
					//this.printDebug("ReplyTo: " + l_sReplyTo);
					
					// HEADERS
					String l_sHeaders = "";
					Enumeration l_oHeaders = l_oMsg.getAllHeaders(); 
					if (l_oHeaders != null)
					{
						while (l_oHeaders.hasMoreElements())
						{
							Header l_oHeader = (Header)l_oHeaders.nextElement();
							l_sHeaders += l_oHeader.getName() + ": " + l_oHeader.getValue() + " ";
							if (l_oHeader.getName().startsWith("Message-Id") == true)
							{
								msgId = ConversionUtils.toInt(l_oHeader.getValue());
							}
						}
					}
					headers = l_sHeaders;
					//this.printDebug("CC: " + l_sCc);
					
					// BODY
					if (a_sAction.equals("GETALL") == true)
					{
						l_sBody = "";
						String l_sTextBody = "";
						String l_sHtmlBody = "";
						
						logger.debug("Type: {}", l_oMsg.getContentType());
						if ( (l_oMsg.isMimeType("text/plain")) || (l_oMsg.isMimeType("message/rfc822")) )
						{
							l_sBody = l_oMsg.getContent().toString();
							l_sTextBody = l_sBody;
						}
						else if (l_oMsg.isMimeType("text/html"))
						{
							l_sBody = l_oMsg.getContent().toString();
							l_sHtmlBody = l_sBody;
						}
						else if ( (l_oMsg.isMimeType("multipart/*")) || (l_oMsg.isMimeType("multipart/alternative")) )
						{
							Multipart l_oMp = (Multipart)l_oMsg.getContent();
							Multipart l_oInnerMp = null;
							
							for (int i = 0; i < l_oMp.getCount(); i++)
							{
								BodyPart l_oBodyPart = l_oMp.getBodyPart(i);
								l_sBody = l_oBodyPart.getContent().toString();
								
								logger.debug("Part {} type: {}", i, l_oBodyPart.getContentType());
								if ( (l_oBodyPart.isMimeType("multipart/*")) || (l_oBodyPart.isMimeType("multipart/alternative")) )
								{
									logger.debug("Getting inner multipart");
									l_oInnerMp = (Multipart)l_oBodyPart.getContent();
									l_oBodyPart = l_oInnerMp.getBodyPart(0);
									l_sBody = l_oBodyPart.getContent().toString();
								}
								
								if (l_oBodyPart.getContentType().toLowerCase().indexOf("text/plain") >= 0)
								{
									l_sTextBody = l_sBody;
									i = l_oMp.getCount() + 1;
									break;
								}
								else if (l_oBodyPart.getContentType().toLowerCase().indexOf("text") >= 0)
								{
									l_sTextBody = l_sBody;
								}
								else if (l_oBodyPart.getContentType().toLowerCase().indexOf("html") >= 0)
								{
									l_sHtmlBody = l_sBody;
								}
							}
						}
						
						body = l_sBody;
						body = l_sTextBody;
						bodyHtml = l_sHtmlBody;
						
						mail.add(  new EmailMessage(from, 
													replyTo, 
													to, 
													cc, 
													subject, 
													body, 
													bodyHtml, 
													msgDate, 
													msgId, 
													headers));
					}
				}
	    	}
    	}
    	folder.close(false);
    	
    	return mail;
    }
    
    public void deleteMail(Folder a_oFolder, int[] a_iMessageNumbers, int a_iStartRow, int a_iMaxRows) throws Exception
    {
    	int l_iMsgCount = a_oFolder.getMessageCount();
    	
    	Message[] l_oMail = null;
    	if ( (a_iMessageNumbers != null) && (a_iMessageNumbers.length > 0) )
    	{
    		l_oMail = new Message[a_iMessageNumbers.length];
    		for (int i = 0; i < a_iMessageNumbers.length; i++)
    		{
    			l_oMail[i] = a_oFolder.getMessage(a_iMessageNumbers[i]);
    		}
    	}
    	else if ( (a_iStartRow >= 0) && (a_iMaxRows >= 0) && (a_iStartRow + a_iMaxRows <= l_iMsgCount) )
    	{
    		l_oMail = a_oFolder.getMessages(a_iStartRow, a_iStartRow + a_iMaxRows);
    	}
    	else
    	{
    		logger.debug("Invalid options for delete");
    		System.exit(1);
    	}
    	
    	for (int x = 0; x < l_oMail.length; x++)
		{
    		Message l_oMsg = l_oMail[x];
    		logger.debug("Deleting message: {}", l_oMsg.getMessageNumber());
    		l_oMsg.setFlag(Flags.Flag.DELETED, true);
		}
    }
}
