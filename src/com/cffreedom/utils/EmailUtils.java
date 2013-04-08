package com.cffreedom.utils;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

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
public class EmailUtils
{
	public static final String SMTP_SERVER_GMAIL = "smtp.gmail.com";
	public static final String SMTP_PORT_GMAIL = "465";
    
    public static void sendEmail(String a_sTo, String a_sFrom, String a_sSubject, String a_sBody, String a_sUser, String a_sPass, String a_sSmtpServer, String a_sPort) throws Exception
	{		
		Properties l_oSysProps = System.getProperties();
		l_oSysProps.put("mail.transport.protocol", "smtps");
		l_oSysProps.put("mail.smtp.host", a_sSmtpServer);
		l_oSysProps.put("mail.smtps.auth", "true");
        
		Session l_oSession = Session.getDefaultInstance(l_oSysProps, null);
        
		MimeMessage l_oMessage = new MimeMessage(l_oSession);
		l_oMessage.setFrom(new InternetAddress(a_sFrom));
		l_oMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(a_sTo));
		l_oMessage.setSubject(a_sSubject);
		l_oMessage.setText(a_sBody);
        
		System.out.println("EmailUtils: Sending message to " + a_sTo + " from " + a_sFrom + " w/ subject: " + a_sSubject);
		
		Transport l_oTransport = l_oSession.getTransport();
		l_oTransport.connect(a_sSmtpServer, ConversionUtils.toInt(a_sPort), a_sUser, a_sPass);
		l_oTransport.sendMessage(l_oMessage, l_oMessage.getAllRecipients());
		l_oTransport.close();
	}
}

