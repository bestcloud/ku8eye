package org.ku8eye.bean.service;

import java.util.HashMap;

public class Pod {
	private String name ;// 名称
	private String creationTimestamp ;// 创建时间
	private String nodeName;//  节点名称
	private String status_phase;// 当前状态
	private String podIP   ;//虚拟地址
	private String hostIP ;// 所在物理机地址
	private HashMap<String,String> labels;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCreationTimestamp() {
		return creationTimestamp;
	}
	public void setCreationTimestamp(String creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
	}
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	public String getStatus_phase() {
		return status_phase;
	}
	public void setStatus_phase(String status_phase) {
		this.status_phase = status_phase;
	}
	public String getPodIP() {
		return podIP;
	}
	public void setPodIP(String podIP) {
		this.podIP = podIP;
	}
	public String getHostIP() {
		return hostIP;
	}
	public void setHostIP(String hostIP) {
		this.hostIP = hostIP;
	}
	public HashMap<String, String> getLabels() {
		return labels;
	}
	public void setLabels(HashMap<String, String> labels) {
		this.labels = labels;
	}
}
