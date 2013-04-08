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
public class PhoneNumber
{
	private int id = 0;
	private int orderId = 0;
	private String code = "";
	private String number = "";
	private boolean active = false;
	private String name = "";
	private String notes = "";
	private int userId = 0;
	private int companyId = 0;
	private int phoneNumberTypeId = 0;
	private boolean voiceEnabled = false;
	private boolean smsEnabled = false;
	private boolean cnameEnabled = false;
	private int extensionId = 0;
	private String defaultForward = "";
	private String defaultNotifyEmailList = "";
	private int defaultVoicemailRecordingId = 0;
	private String dailyCallReportEmailList = "";
	
	public PhoneNumber(int id, int orderId, String code, String number, boolean active, String name, String notes, int userId, int companyId, int phoneNumberTypeId, boolean voiceEnabled, boolean smsEnabled, boolean cnameEnabled, int extensionId, String defaultForward, String defaultNotifyEmailList, int defaultVoicemailRecordingId, String dailyCallReportEmailList)
	{
		super();
		this.id = id;
		this.orderId = orderId;
		this.code = code;
		this.number = number;
		this.active = active;
		this.name = name;
		this.notes = notes;
		this.userId = userId;
		this.companyId = companyId;
		this.phoneNumberTypeId = phoneNumberTypeId;
		this.voiceEnabled = voiceEnabled;
		this.smsEnabled = smsEnabled;
		this.cnameEnabled = cnameEnabled;
		this.extensionId = extensionId;
		this.defaultForward = defaultForward;
		this.defaultNotifyEmailList = defaultNotifyEmailList;
		this.defaultVoicemailRecordingId = defaultVoicemailRecordingId;
		this.dailyCallReportEmailList = dailyCallReportEmailList;
	}
	
	public int getId() 							{ return id; }
	public int getOrderId() 					{ return orderId; }
	public String getCode() 					{ return code; }
	public String getNumber() 					{ return number; }
	public boolean isActive()					{ return active; }
	public String getName() 					{ return name; }
	public String getNotes() 					{ return notes; }
	public int getUserId() 						{ return userId; }
	public int getCompanyId() 					{ return companyId; }
	public int getPhoneNumberTypeId() 			{ return phoneNumberTypeId; }
	public boolean isVoiceEnabled() 			{ return voiceEnabled; }
	public boolean isSmsEnabled() 				{ return smsEnabled; }
	public boolean isCnameEnabled() 			{ return cnameEnabled; }
	public int getExtensionId() 				{ return extensionId; }
	public String getDefaultForward() 			{ return defaultForward; }
	public String getDefaultNotifyEmailList() 	{ return defaultNotifyEmailList; }
	public int getDefaultVoicemailRecordingId() { return defaultVoicemailRecordingId; }
	public String getDailyCallReportEmailList() { return dailyCallReportEmailList; }
}
