package com.cffreedom.utils.net;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cffreedom.beans.EmailMessage;
import com.cffreedom.utils.Convert;
import com.cffreedom.utils.Utils;

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
 * 2013-05-15 	markjacobsen.net	Added unauthenticated sendEmail() option
 * 2013-05-17 	markjacobsen.net 	Added sendGmail() option and added protocol to sendEmail()
 * 2013-05-18 	markjacobsen.net 	Added htmlBody options
 * 2013-10-05 	markjacobsen.net 	Additional sendEmail()
 * 2013-11-05 	MarkJacobsen.net 	Fix in sendEmail() for CC and BCC
 * 2014-09-13 	MarkJacobsen.net 	Added support for attachments
 * 2015-03-03 	MarkJacobsen.net 	Fix for html body w/ attachments
 */
public class EmailUtils
{
	private static final Logger logger = LoggerFactory.getLogger("com.cffreedom.utils.net.EmailUtils");
	
	public static final String SMTP_SERVER = "127.0.0.1";
	public static final String SMTP_PORT = "25";
	public static final String SMTP_SERVER_GMAIL = "smtp.gmail.com";
	public static final String SMTP_PORT_GMAIL = "465";
	public static final String PROTOCOL_SMTP = "smtp";
	public static final String PROTOCOL_SMTPS = "smtps";
    
	public static void sendGmail(String to, String from, String subject, String body, boolean htmlBody, String user, String pass) throws Exception {
		EmailMessage msg = new EmailMessage(to, from, subject, body);
		if (htmlBody) {
			msg.setBodyHtml(body);
		}
		sendGmail(msg, user, pass);
	}
	
	public static void sendGmail(EmailMessage msg, String user, String pass) throws Exception {
		if (!Utils.hasLength(user) || !Utils.hasLength(pass)) {
			throw new Exception("You must supply values for the username and password");
		}
		Map<String, String> additionalProps = new HashMap<>();
		additionalProps.put("mail.smtp.socketFactory.port", EmailUtils.SMTP_PORT_GMAIL);
		additionalProps.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		sendEmail(msg, user, pass, SMTP_SERVER_GMAIL, PROTOCOL_SMTPS, SMTP_PORT_GMAIL, additionalProps);
	}
	
	public static void sendEmail(String to, String from, String subject, String body, String smtpServer, String port) throws Exception {
		sendEmail(to, from, subject, body, false, null, null, smtpServer, null, port, null);
	}
	
	public static void sendHtmlEmail(String to, String from, String subject, String body, String smtpServer, String port) throws Exception {
		sendEmail(to, from, subject, body, true, null, null, smtpServer, null, port, null);
	}
	
    public static void sendEmail(String to, String from, String subject, String body, boolean htmlBody, String user, String pass, String smtpServer, String protocol, String port, Map<String, String> additionalProps) throws Exception {
    	EmailMessage msg = new EmailMessage(to, from, subject, body);
    	if (htmlBody == true) { msg.setBodyHtml(body); }
    	sendEmail(msg, user, pass, smtpServer, protocol, port, additionalProps);		
	}
    
    /**
     * Send an email message
     * @param msg The EmailMessage object containing details about the message to send
     * @param user SMTP username
     * @param pass SMTP password
     * @param smtpServer SMTP server
     * @param protocol SMTP protocol
     * @param port SMTP port
     * @throws Exception
     */
    public static void sendEmail(EmailMessage msg, String user, String pass, String smtpServer, String protocol, String port, Map<String, String> additionalProps) throws Exception {	
    	boolean authenticatedSession = true;
    	if ((user == null) || (user.length() == 0)) {
    		authenticatedSession = false; 
    	}
		Properties sysProps = System.getProperties();
		sysProps.put("mail.smtp.host", smtpServer);
		if (protocol != null) {
			sysProps.put("mail.transport.protocol", protocol);
		}
		if (authenticatedSession == true) {
			sysProps.put("mail.smtps.auth", "true");
		}
		if (Utils.hasLength(additionalProps)) {
			for (String prop : additionalProps.keySet()) {
				sysProps.put(prop, additionalProps.get(prop));
			}
		}
		
		String[] toArray = getRecipientArray(msg.getTo());
		String[] ccArray = getRecipientArray(msg.getCc());
		String[] bccArray = getRecipientArray(msg.getBcc());
		
		Session session = Session.getDefaultInstance(sysProps, null);
        
		MimeMessage message = new MimeMessage(session);
		
		if (Utils.hasLength(msg.getFromName()) == true) {
			message.setFrom(new InternetAddress(msg.getFrom(), msg.getFromName()));
		} else {
			message.setFrom(new InternetAddress(msg.getFrom()));
		}
		
		for (int y = 0; y < toArray.length; y++) {
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(toArray[y]));
		}
		
		if (ccArray != null) {
			for (int y = 0; y < ccArray.length; y++) {
				message.addRecipient(Message.RecipientType.CC, new InternetAddress(ccArray[y]));
			}
		}
		
		if (bccArray != null) {
			for (int y = 0; y < bccArray.length; y++) {
				message.addRecipient(Message.RecipientType.BCC, new InternetAddress(bccArray[y]));
			}
		}
		
		message.setSubject(msg.getSubject());
		
		try
		{
			if ((msg.getAttachments() != null) && (msg.getAttachments().length > 0))
			{
				// Adapeted From: http://www.codejava.net/java-ee/javamail/send-e-mail-with-attachment-in-java
				MimeMultipart multipart = new MimeMultipart();
				
		        // add text body part
				MimeBodyPart messageBodyPart = new MimeBodyPart();
				if (Utils.hasLength(msg.getBodyHtml()) == true){
					messageBodyPart.setContent(msg.getBodyHtml(), "text/html; charset=utf-8");
				}else{
					messageBodyPart.setText(msg.getBody());
				}
		        multipart.addBodyPart(messageBodyPart);
				
		        // adds attachments
	            for (String[] attachment : msg.getAttachments()) 
	            {
	            	MimeBodyPart attachPart = new MimeBodyPart();
	            	attachPart.setContent(message, "multipart/mixed");
	            	
	                try 
	                {
	                	String file = attachment[0];
	                	logger.trace("Adding attachment: {}", file);
	                	DataSource source = new FileDataSource(file);
	                    attachPart.setDataHandler(new DataHandler(source));
	                    
	                    if (attachment.length > 1)
	                    {
		                    String name = attachment[1];
		                    if (Utils.hasLength(name) == true) {
		                    	attachPart.setFileName(name);
		                    }
	                    }
	                    
	                    multipart.addBodyPart(attachPart);
	                } catch (Exception ex) {
	                    ex.printStackTrace();
	                }
	            }
		 
		        // sets the multi-part as e-mail's content
		        message.setContent(multipart);
			}
			else
			{
				if (Utils.hasLength(msg.getBodyHtml()) == true){
					message.setContent(msg.getBodyHtml(), "text/html; charset=utf-8");
				}else{
					message.setText(msg.getBody());
				}
			}
		}
		catch (Exception e)
		{
			// this is what was working so use it as a fallback
			if ((msg.getBodyHtml() != null) && (msg.getBodyHtml().trim().length() > 0)){
				message.setText(msg.getBodyHtml(), "utf-8", "html");
			}else{
				message.setText(msg.getBody());
			}
		}
        
		logger.trace("Sending message to {}, cc {}, bcc {}, from {} w/ subject: {}", msg.getTo(), msg.getCc(), msg.getBcc(), msg.getFrom(), msg.getSubject());
		
		if (authenticatedSession == true){
			Transport transport = session.getTransport();
			transport.connect(smtpServer, Convert.toInt(port), user, pass);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
		}else{
			Transport.send(message);
		}
    }
    
    private static String[] getRecipientArray(String recipients)
    {
    	String[] returnArray = null;
		if (Utils.hasLength(recipients) == true) {
			recipients = recipients.replace(',', ';');
			recipients = recipients.replace(' ', ';');
			returnArray = recipients.split(";");
		}
		return returnArray;
    }
    
    /**
     * Return all email addresses contained in a block of text
     * @param text
     * @return
     */
    public static List<String> getEmailAddresses(String text) {
    	List<String> emails = new ArrayList<String>();
    	
    	if (Utils.hasLength(text) == true) {
	    	Pattern p = Pattern.compile("\\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}\\b", Pattern.CASE_INSENSITIVE);
			Matcher matcher = p.matcher(text);
			while(matcher.find()) {
				emails.add(matcher.group());
			}
    	}
    		
    	return emails;
    }
}

