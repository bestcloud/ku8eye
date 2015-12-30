package org.ku8eye.bean.project;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.ku8eye.util.JSONUtil;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class Project
{
	private static final Logger log = Logger.getLogger(JSONUtil.class);

	public static Project getFromJSON(String json)
	{
		try
		{
			return JSONUtil.mapper.readValue(json, Project.class);
		}
		catch (JsonParseException e)
		{
			log.error("Couldn't parse Project from String, " + e);
			e.printStackTrace();
		}
		catch (JsonMappingException e)
		{
			log.error("Json mapping error in Project, " + e);
			e.printStackTrace();
		}
		catch (IOException e)
		{
			log.error("IO Error in Project, " + e);
			e.printStackTrace();
		}
		return null;
	}

	public void addService(Service s)
	{
		services.add(s);
	}

	private String projectName;
	private String version;
	private String author;
	private String k8sVersion;
	private String note;
	private List<Service> services = new ArrayList<Service>();
	
	public String getProjectName()
	{
		return projectName;
	}

	public void setProjectName(String projectName)
	{
		this.projectName = projectName;
	}

	public String getVersion()
	{
		return version;
	}

	public void setVersion(String version)
	{
		this.version = version;
	}

	public String getAuthor()
	{
		return author;
	}

	public void setAuthor(String author)
	{
		this.author = author;
	}

	public String getK8sVersion()
	{
		return k8sVersion;
	}

	public void setK8sVersion(String k8sVersion)
	{
		this.k8sVersion = k8sVersion;
	}

	public String getNote()
	{
		return note;
	}

	public void setNote(String note)
	{
		this.note = note;
	}

	public List<Service> getServices()
	{
		return services;
	}

	public void setServices(List<Service> services)
	{
		this.services = services;
	}

	@Override
	public String toString()
	{
		return projectName;
	}
}
