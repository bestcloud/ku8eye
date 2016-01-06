package org.ku8eye.bean.project;

import io.fabric8.kubernetes.api.model.EnvVar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Service {
	private String name;
	private String describe;
	private String tag;
	private String replica;
	private String version;
	private int containerPort;
	private int servicePort;
	private int nodePort;
	private List<Images> image = new ArrayList<Images>();
	private List<EnvVar> envVariable = new ArrayList<>();
	
	public List<EnvVar> getEnvVariable()
	{
		return envVariable;
	}

	public void setEnvVariable(List<EnvVar> envVariable)
	{
		this.envVariable = envVariable;
	}

	public void addEnvVariable(String name, String value)
	{
		EnvVar var = new EnvVar(name, value, null);
		this.envVariable.add(var);
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
	
	public void addImage(Images i) {
		image.add(i);
	}

}
