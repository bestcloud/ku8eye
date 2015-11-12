package org.ku8eye.domain;

/**
 * Tenant value object
 * 
 * @author wuzhih
 *
 */
public class Tenant {
	private int id;
	private String name;
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
