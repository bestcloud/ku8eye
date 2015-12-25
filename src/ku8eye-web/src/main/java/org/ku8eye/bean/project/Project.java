package org.ku8eye.bean.project;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Project {



	public void addService(Service s) {
		services.add(s);
	}


	private String projectName;
	private String version;
	private String author;
	private String kuberneteVersion;
	private String describe;
	private List<Service> services = new ArrayList<Service>();


	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getKuberneteVersion() {
		return kuberneteVersion;
	}

	public void setKuberneteVersion(String kuberneteVersion) {
		this.kuberneteVersion = kuberneteVersion;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	public List<Service> getServices() {
		return services;
	}

	public void setServices(List<Service> services) {
		this.services = services;
	}
}
