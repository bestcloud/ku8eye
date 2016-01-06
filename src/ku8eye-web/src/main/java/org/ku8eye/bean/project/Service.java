package org.ku8eye.bean.project;

import java.util.ArrayList;
import java.util.List;

public class Service {
	private String name;
	private String describe;
	private String tag;
	private String replica;
	private String version;

	int containerPort;
	int servicePort;
	int nodePort;

	private List<Images> image = new ArrayList<Images>();

	private List<EnvVariables> envVariables = new ArrayList<EnvVariables>();
	
	public void addEnvVariables(EnvVariables env)
	{
		envVariables.add(env);
	}
	
	public List<EnvVariables> getEnvVariables() {
		return envVariables;
	}


	public void setEnvVariables(List<EnvVariables> envVariables) {
		this.envVariables = envVariables;
	}


	public int getContainerPort() {
		return containerPort;
	}

	
	public void setContainerPort(int containerPort) {
		this.containerPort = containerPort;
	}

	public int getServicePort() {
		return servicePort;
	}

	public void setServicePort(int servicePort) {
		this.servicePort = servicePort;
	}

	public int getNodePort() {
		return nodePort;
	}

	public void setNodePort(int nodePort) {
		this.nodePort = nodePort;
	}

	public void addImage(Images i) {
		image.add(i);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getReplica() {
		return replica;
	}

	public void setReplica(String replica) {
		this.replica = replica;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public List<Images> getImage() {
		return image;
	}

	public void setImage(List<Images> image) {
		this.image = image;
	}
}
