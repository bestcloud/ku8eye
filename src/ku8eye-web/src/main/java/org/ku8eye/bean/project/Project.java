package org.ku8eye.bean.project;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Project {

	public static void main(String[] args) {
		Project p=new Project();
		System.out.println(p.getJsonStr());
	}

	public String getJsonStr() {
		try {
			return (new ObjectMapper()).writeValueAsString(this);
		} catch (Exception e) {
			return null;
		}

	}

	private String projectName;
	private String version;
	private String author;
	private String kuberneteVersion;
	private String describe;
	private List<Service> services;
	private List<Port> ports;

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

	public List<Port> getPorts() {
		return ports;
	}

	public void setPorts(List<Port> ports) {
		this.ports = ports;
	}

}

class Service {
	private String name;
	private String describe;
	private String tag;
	private String replica;
	private String version;
	private Images image;

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

	public Images getImage() {
		return image;
	}

	public void setImage(Images image) {
		this.image = image;
	}
}

class Images {
	String name;
	String version;
	String registry;
	String imageName;
	String command;
	String imagePullPolicy;
	String quotas_limits;
	String quotas_cpu;
	String quotas_memory;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getRegistry() {
		return registry;
	}

	public void setRegistry(String registry) {
		this.registry = registry;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getQuotas_limits() {
		return quotas_limits;
	}

	public void setQuotas_limits(String quotas_limits) {
		this.quotas_limits = quotas_limits;
	}

	public String getQuotas_cpu() {
		return quotas_cpu;
	}

	public void setQuotas_cpu(String quotas_cpu) {
		this.quotas_cpu = quotas_cpu;
	}

	public String getQuotas_memory() {
		return quotas_memory;
	}

	public void setQuotas_memory(String quotas_memory) {
		this.quotas_memory = quotas_memory;
	}
}

class Port {
	String containerPort;
	String servicePort;
	String nodePort;

	public String getContainerPort() {
		return containerPort;
	}

	public void setContainerPort(String containerPort) {
		this.containerPort = containerPort;
	}

	public String getServicePort() {
		return servicePort;
	}

	public void setServicePort(String servicePort) {
		this.servicePort = servicePort;
	}

	public String getNodePort() {
		return nodePort;
	}

	public void setNodePort(String nodePort) {
		this.nodePort = nodePort;
	}

}
