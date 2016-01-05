package org.ku8eye.bean.service;

import java.util.List;

import org.ku8eye.bean.project.Port;

public class ServiceAndRC 
{
	private String rcName;  //rc name
	private String podNum; //pod 数量
	private String serviceName ;//名称
	private String creationTimestamp ;//创建时间
	private java.util.HashMap<String,String> labels;//标签 
	private String clusterIP ;//实际地址
	private List<Port> ports; //使用端口
	
	private String selector  ;//选择器

	public String getRcName() {
		return rcName;
	}

	public void setRcName(String rcName) {
		this.rcName = rcName;
	}

	public String getPodNum() {
		return podNum;
	}

	public void setPodNum(String podNum) {
		this.podNum = podNum;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getCreationTimestamp() {
		return creationTimestamp;
	}

	public void setCreationTimestamp(String creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
	}

	public java.util.HashMap<String, String> getLabels() {
		return labels;
	}

	public void setLabels(java.util.HashMap<String, String> labels) {
		this.labels = labels;
	}

	public String getClusterIP() {
		return clusterIP;
	}

	public void setClusterIP(String clusterIP) {
		this.clusterIP = clusterIP;
	}

	public List<Port> getPorts() {
		return ports;
	}

	public void setPorts(List<Port> ports) {
		this.ports = ports;
	}

	public String getSelector() {
		return selector;
	}

	public void setSelector(String selector) {
		this.selector = selector;
	}
	
}
