package org.ku8eye.domain;

/**
 * kubernetes service endpoint ,such as etcd service ,api service ,docker
 * registry serivce
 * 
 * @author wuzhih
 *
 */
public class Ku8SrvEndpoint {
private int id;
//see Ku8ServiceBean
private int serviceType;
//at which host
private int hostId;
private String serviceURL;
private String note;
//service status see Ku8ServiceBean
private int serviceStatus;
private java.util.Date lastUpdated;
public int getId() {
	return id;
}
public void setId(int id) {
	this.id = id;
}
public int getServiceType() {
	return serviceType;
}
public void setServiceType(int serviceType) {
	this.serviceType = serviceType;
}
public int getHostId() {
	return hostId;
}
public void setHostId(int hostId) {
	this.hostId = hostId;
}
public String getServiceURL() {
	return serviceURL;
}
public void setServiceURL(String serviceURL) {
	this.serviceURL = serviceURL;
}
public String getNote() {
	return note;
}
public void setNote(String note) {
	this.note = note;
}
public java.util.Date getLastUpdated() {
	return lastUpdated;
}
public void setLastUpdated(java.util.Date lastUpdated) {
	this.lastUpdated = lastUpdated;
}
public int getServiceStatus() {
	return serviceStatus;
}
public void setServiceStatus(int serviceStatus) {
	this.serviceStatus = serviceStatus;
}

}
