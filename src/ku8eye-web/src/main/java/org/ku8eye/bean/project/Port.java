package org.ku8eye.bean.project;

public class Port {
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
