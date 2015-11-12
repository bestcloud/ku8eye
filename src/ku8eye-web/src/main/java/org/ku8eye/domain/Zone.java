package org.ku8eye.domain;
/**
 * Zone value object 
 * @author wuzhih
 *
 */
public class Zone {
private int id;
private String name;
private String location;
private String note;
private java.util.Date lastUpdated;
public int getId() {
	return id;
}
public void setId(int id) {
	this.id = id;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public String getLocation() {
	return location;
}
public void setLocation(String location) {
	this.location = location;
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
