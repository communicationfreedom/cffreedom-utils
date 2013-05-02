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
 */
public class Extension
{
	private int extensioniId = 0;
	private int parentId = 0;
	private int companyId = 0;
	private int userId = 0;
	private int extTypeId = 0;
	private String extType = "";
	private int extNumber = 0;
	private String extName = "";
	private String recordingUrl = "";
	private String recordingPath = "";
	private int voicemailRecId = 0;
	private String voicemailRecordingUrl = "";
	private String voicemailRecordingPath = "";
	private String phoneDesc = "";
	private String phoneVoice = "M";
	private String smsDesc = "";
	private String forwardingNumber = "";
	private int extensionLength = 1;
	private int defaultChildExtNumber = 0;
	private String notifyEmailList = "";
	private Date modified = new Date();
	private int mirrorExtId = 0;
	
	public Extension(int extensioniId, int parentId, int companyId, int userId, int extTypeId, String extType, int extNumber, String extName, String recordingUrl, String recordingPath, int voicemailRecId, String voicemailRecordingUrl, String voicemailRecordingPath, String phoneDesc, String phoneVoice, String smsDesc, String forwardingNumber, int extensionLength, int defaultChildExtNumber, String notifyEmailList, Date modified, int mirrorExtId)
	{
		super();
		this.extensioniId = extensioniId;
		this.parentId = parentId;
		this.companyId = companyId;
		this.userId = userId;
		this.extTypeId = extTypeId;
		this.extType = extType;
		this.extNumber = extNumber;
		this.extName = extName;
		this.recordingUrl = recordingUrl;
		this.recordingPath = recordingPath;
		this.voicemailRecId = voicemailRecId;
		this.voicemailRecordingUrl = voicemailRecordingUrl;
		this.voicemailRecordingPath = voicemailRecordingPath;
		this.phoneDesc = phoneDesc;
		this.phoneVoice = phoneVoice;
		this.smsDesc = smsDesc;
		this.forwardingNumber = forwardingNumber;
		this.extensionLength = extensionLength;
		this.defaultChildExtNumber = defaultChildExtNumber;
		this.notifyEmailList = notifyEmailList;
		this.modified = modified;
		this.mirrorExtId = mirrorExtId;
	}
	
	public int getExtensioniId()
	{
		return extensioniId;
	}
	public int getParentId()
	{
		return parentId;
	}
	public int getCompanyId()
	{
		return companyId;
	}
	public int getUserId()
	{
		return userId;
	}
	public int getExtTypeId()
	{
		return extTypeId;
	}
	public String getExtType()
	{
		return extType;
	}
	public int getExtNumber()
	{
		return extNumber;
	}
	public String getExtName()
	{
		return extName;
	}
	public String getRecordingUrl()
	{
		return recordingUrl;
	}
	public String getRecordingPath()
	{
		return recordingPath;
	}
	public int getVoicemailRecId()
	{
		return voicemailRecId;
	}
	public String getVoicemailRecordingUrl()
	{
		return voicemailRecordingUrl;
	}
	public String getVoicemailRecordingPath()
	{
		return voicemailRecordingPath;
	}
	public String getPhoneDesc()
	{
		return phoneDesc;
	}
	public String getPhoneVoice()
	{
		return phoneVoice;
	}
	public String getSmsDesc()
	{
		return smsDesc;
	}
	public String getForwardingNumber()
	{
		return forwardingNumber;
	}
	public int getExtensionLength()
	{
		return extensionLength;
	}
	public int getDefaultChildExtNumber()
	{
		return defaultChildExtNumber;
	}
	public String getNotifyEmailList()
	{
		return notifyEmailList;
	}
	public Date getModified()
	{
		return modified;
	}
	public int getMirrorExtId()
	{
		return mirrorExtId;
	}
	
	
}
