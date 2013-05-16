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
 */
public class EmailUtils
{
	public static final String SMTP_SERVER_GMAIL = "smtp.gmail.com";
	public static final String SMTP_PORT_GMAIL = "465";
    
	public static void sendEmail(String to, String from, String subject, String body, String smtpServer, String port) throws Exception
	{
		sendEmail(to, from, subject, body, null, null, smtpServer, port);
	}
	
    public static void sendEmail(String to, String from, String subject, String body, String user, String pass, String smtpServer, String port) throws Exception
	{
    	boolean authenticatedSession = true;
    	if ((user == null) || (user.length() == 0)) { authenticatedSession = false; }
		Properties l_oSysProps = System.getProperties();
		l_oSysProps.put("mail.transport.protocol", "smtps");
		l_oSysProps.put("mail.smtp.host", smtpServer);
		if (authenticatedSession == true){
			l_oSysProps.put("mail.smtps.auth", "true");
		}
		
		Session l_oSession = Session.getDefaultInstance(l_oSysProps, null);
        
		MimeMessage l_oMessage = new MimeMessage(l_oSession);
		l_oMessage.setFrom(new InternetAddress(from));
		l_oMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
		l_oMessage.setSubject(subject);
		l_oMessage.setText(body);
        
		System.out.println("EmailUtils: Sending message to " + to + " from " + from + " w/ subject: " + subject);
		
		Transport l_oTransport = l_oSession.getTransport();
		if (authenticatedSession == true){
			l_oTransport.connect(smtpServer, ConversionUtils.toInt(port), user, pass);
		}
		l_oTransport.sendMessage(l_oMessage, l_oMessage.getAllRecipients());
		l_oTransport.close();
	}
}

