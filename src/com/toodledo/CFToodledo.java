package com.toodledo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.cffreedom.beans.Container;
import com.cffreedom.beans.Project;
import com.cffreedom.beans.Task;
import com.cffreedom.utils.ConversionUtils;
import com.cffreedom.utils.HttpUtils;
import com.cffreedom.utils.JsonUtils;
import com.cffreedom.utils.LoggerUtil;
import com.cffreedom.utils.Utils;

/**
 * Class to make working with the Toodledo API easier
 * 
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
public class CFToodledo
{
	private final LoggerUtil logger = new LoggerUtil(LoggerUtil.FAMILY_TASK, this.getClass().getPackage().getName() + "." + this.getClass().getSimpleName());
	private final String APP_ID = "cffreedom";
	private final String HTTP_PROTOCOL = "https://";

	private String userEmail = null;
	private String userPass = null;
	private String apiToken = null;
	private String token = null;
	private String key = null;
	
	/**
	 * Create an instance of the ToodledoDAO
	 * 
	 * @param userEmail your account email
	 * @param userPassword your account password
	 * @param apiToken from your account settings
	 * @throws Exception
	 */
	public CFToodledo(String userEmail, String userPassword, String apiToken) throws Exception
	{
		this.userEmail = userEmail;
		this.userPass = userPassword;
		this.apiToken = apiToken;

		String sig = ConversionUtils.toMd5(this.getUserEmail() + this.apiToken);
		String url = HTTP_PROTOCOL + "api.toodledo.com/2/account/lookup.php?appid=" + this.APP_ID + ";sig=" + sig + ";email=" + this.getUserEmail() + ";pass=" + this.getUserPass();
		String response = HttpUtils.httpGet(url);
		JSONObject jsonObj = JsonUtils.getJsonObject(response);
		String userId = JsonUtils.getJsonObjectStringVal(jsonObj, "userid");

		String encodedLogin = ConversionUtils.toMd5(userId + this.apiToken);
		url = HTTP_PROTOCOL + "api.toodledo.com/2/account/token.php?userid=" + userId + ";appid=" + this.APP_ID + ";sig=" + encodedLogin;
		response = HttpUtils.httpGet(url);
		jsonObj = JsonUtils.getJsonObject(response);
		this.token = JsonUtils.getJsonObjectStringVal(jsonObj, "token");
		System.out.println(this.token);

		this.key = ConversionUtils.toMd5(ConversionUtils.toMd5(this.getUserPass()) + this.apiToken + this.getToken());
	}

	private String getUserEmail()
	{
		return this.userEmail;
	}

	private String getUserPass()
	{
		return this.userPass;
	}

	public String getToken()
	{
		return this.token;
	}

	private String getKey()
	{
		return this.key;
	}

	private String getProjectSyncCode(ArrayList<Container> tags)
	{
		String code = "srUNKNOWN";

		for (Container tag : tags)
		{
			String tagName = tag.getName();
			if ((tagName.length() >= 3) 
					&& (tagName.startsWith("sr") == true) 
					&& (Utils.isInt(tagName.replaceFirst("sr", "")) == true))
			{
				code = tagName;
				break;
			}
		}
		
		return code;
	}

	public ArrayList<Task> getTasks() throws IOException, ParseException
	{
		final String FIELDS = "meta,folder,context,tag,startdate,starttime,duedate,duetime,note";
		ArrayList<Task> tasks = new ArrayList<Task>();
		String url = HTTP_PROTOCOL + "api.toodledo.com/2/tasks/get.php?key=" + this.getKey() + ";comp=0;fields=" + FIELDS;
		String response = HttpUtils.httpGet(url);
		JSONArray tasksArray = JsonUtils.getJsonArray(response);

		Iterator<JSONObject> iterator = tasksArray.iterator();
		while (iterator.hasNext())
		{
			JSONObject task = iterator.next();
			ArrayList<Container> tags = new ArrayList<Container>();
			String code = JsonUtils.getJsonObjectStringVal(task, "id");
			String title = JsonUtils.getJsonObjectStringVal(task, "title");
			String meta = JsonUtils.getJsonObjectStringVal(task, "meta");
			String note = JsonUtils.getJsonObjectStringVal(task, "note");
			String folderName = JsonUtils.getJsonObjectStringVal(task, "folder");
			String tagList = JsonUtils.getJsonObjectStringVal(task, "tag");

			if ((tagList != null) && (tagList.trim().length() > 0))
			{
				String[] tagArray = tagList.split(",");
				for (int x = 0; x < tagArray.length; x++)
				{
					String tag = tagArray[x].trim();
					// System.out.println("tag --> " + tag);
					tags.add(new Container(tag, tag));
				}
			}

			String projectSyncCode = this.getProjectSyncCode(tags);
			Project project = new Project(projectSyncCode, projectSyncCode, projectSyncCode, "");
			Container folder = new Container(folderName, folderName);

			if (code != null)
			{
				tasks.add(new Task(Task.SYS_TOODLEDO, folder, project, code, title, note, meta, tags));
			}
		}

		return tasks;
	}

	public ArrayList<Task> getTasks(Container folder) throws IOException, ParseException
	{
		final String METHOD = "getTasks";
		ArrayList<Task> tasks = new ArrayList<Task>();

		for (Task task : this.getTasks())
		{
			if (task.getFolder().getName().equalsIgnoreCase(folder.getName()) == true)
			{
				tasks.add(task);
			}
		}

		logger.logInfo(METHOD, "Returning "+tasks.size()+" tasks for folder "+folder.getName());
		return tasks;
	}
	
	public boolean insertTask(Task task) throws IOException, ParseException
	{
		final String METHOD = "insertTask";
		String url = HTTP_PROTOCOL + "api.toodledo.com/2/tasks/add.php?key="+this.getKey()+";tasks=[{\"title\"%3A\""+task.getTitle()+"\"%2C\"folder\"%3A\""+task.getFolder().getCode()+"\"}];fields=folder";
		String response = HttpUtils.httpGet(url);
		logger.logDebug(METHOD, response);
		JSONArray tasksArray = JsonUtils.getJsonArray(response);
		JSONObject newTask = (JSONObject)(tasksArray.get(0));
		Long id = JsonUtils.getJsonObjectLongVal(newTask, "id");
		logger.logDebug(METHOD, "New task id: " + id);
		return false;
	}
}
