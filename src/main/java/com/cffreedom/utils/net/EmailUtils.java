package com.cffreedom.utils.net;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

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
 * 
 * Changes:
 * 2013-05-15 	markjacobsen.net	Added unauthenticated sendEmail() option
 * 2013-05-17 	markjacobsen.net 	Added sendGmail() option and added protocol to sendEmail()
 */
public class EmailUtils
{
	public static final String SMTP_SERVER_GMAIL = "smtp.gmail.com";
	public static final String SMTP_PORT_GMAIL = "465";
	public static final String PROTOCOL_SMTP = "smtp";
	public static final String PROTOCOL_SMTPS = "smtps";
    
	public static void sendGmail(String to, String from, String subject, String body, String user, String pass) throws Exception
	{
		sendEmail(to, from, subject, body, user, pass, SMTP_SERVER_GMAIL, PROTOCOL_SMTPS, SMTP_PORT_GMAIL);
	}
	
	public static void sendEmail(String to, String from, String subject, String body, String smtpServer, String port) throws Exception
	{
		sendEmail(to, from, subject, body, null, null, smtpServer, null, port);
	}
	
    public static void sendEmail(String to, String from, String subject, String body, String user, String pass, String smtpServer, String protocol, String port) throws Exception
	{
    	boolean authenticatedSession = true;
    	if ((user == null) || (user.length() == 0)) { authenticatedSession = false; }
		Properties sysProps = System.getProperties();
		sysProps.put("mail.smtp.host", smtpServer);
		if (protocol != null)
		{
			sysProps.put("mail.transport.protocol", protocol);
		}
		if (authenticatedSession == true){
			sysProps.put("mail.smtps.auth", "true");
		}
		
		to = to.replace(',', ';');
		String[] toArray = to.split(";");
		
		Session session = Session.getDefaultInstance(sysProps, null);
        
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(from));
		for (int y = 0; y < toArray.length; y++) {
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(toArray[y]));
		}
		message.setSubject(subject);
		message.setText(body);
        
		System.out.println("EmailUtils: Sending message to " + to + " from " + from + " w/ subject: " + subject);
		
		if (authenticatedSession == true){
			Transport transport = session.getTransport();
			transport.connect(smtpServer, ConversionUtils.toInt(port), user, pass);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
		}else{
			Transport.send(message);
		}
		
	}
}

