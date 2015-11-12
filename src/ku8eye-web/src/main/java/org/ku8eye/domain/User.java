package org.ku8eye.domain;

/**
 * user value object include following: Tenant user admin user
 * 
 * @author wuzhih
 *
 */
public class User {
	// user id ,used for login and also primary key
	private String userId;
	private String password;
	private int tenantId;
	// tenent user or admin usr
	private int userType;
	// account status ,used for control login
	private int status;
	private String note;
	private java.util.Date lastUpdated;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getTenantId() {
		return tenantId;
	}

	public void setTenantId(int tenantId) {
		this.tenantId = tenantId;
	}

	public int getUserType() {
		return userType;
	}

	public void setUserType(int userType) {
		this.userType = userType;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
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
