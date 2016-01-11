package org.ku8eye.bean.project;

import io.fabric8.kubernetes.api.model.EnvVar;

import java.util.ArrayList;
import java.util.List;

public class Service {
	private String name;
	private String describe;
	private String tag;
	private int replica;
	private String version;
	private int containerPort;
	private int servicePort;
	private int nodePort;
	private List<Images> images = new ArrayList<Images>();
	private List<EnvVar> envVariables = new ArrayList<>();
	
	public List<EnvVar> getEnvVariables()
	{
		return envVariables;
	}

	public void setEnvVariables(List<EnvVar> envVariables)
	{
		this.envVariables = envVariables;
	}

	public void addEnvVariable(String name, String value)
	{
		EnvVar var = new EnvVar(name, value, null);
		this.envVariables.add(var);
	}

	public int getContainerPort()
	{
		return containerPort;
	}

	public void setContainerPort(int containerPort)
	{
		this.containerPort = containerPort;
	}

	public int getServicePort()
	{
		return servicePort;
	}

	public void setServicePort(int servicePort)
	{
		this.servicePort = servicePort;
	}

	public int getNodePort()
	{
		return nodePort;
	}

	public void setNodePort(int nodePort)
	{
		this.nodePort = nodePort;
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

	public int getReplica() {
		return replica;
	}

	public void setReplica(int replica) {
		this.replica = replica;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public List<Images> getImages() {
		return images;
	}

	public void setImages(List<Images> images) {
		this.images = images;
	}
	
	public void addImage(Images i) {
		images.add(i);
	}

}

