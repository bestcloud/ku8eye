package org.ku8eye.domain;

public class Host {
	private int id;
	//belong to which zone
	private int zoneId;
	private String hostName;
	private String ip;
	private String rootPasswd;
	private String note;
	private java.util.Date lastUpdated;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getZoneId() {
		return zoneId;
	}
	public void setZoneId(int zoneId) {
		this.zoneId = zoneId;
	}
	public String getHostName() {
		return hostName;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getRootPasswd() {
		return rootPasswd;
	}
	public void setRootPasswd(String rootPasswd) {
		this.rootPasswd = rootPasswd;
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
	
}
