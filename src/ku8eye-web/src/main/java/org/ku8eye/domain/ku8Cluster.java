package org.ku8eye.domain;
/**
 * a kubernetes cluster
 * @author wuzhih
 *
 */
public class ku8Cluster {
private int id;
//owner tenant id
private int tenantId;
private String name;
// "," splitted labels
private String labels;
//kubernetes version
private String k8sVersion="1.0";

//witch install type :custom, all in one, normal ,ha ...
private int installType;

private String note;
private java.util.Date lastUpdated;
public int getId() {
	return id;
}
public void setId(int id) {
	this.id = id;
}
public int getTenantId() {
	return tenantId;
}
public void setTenantId(int tenantId) {
	this.tenantId = tenantId;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public String getLabels() {
	return labels;
}
public void setLabels(String labels) {
	this.labels = labels;
}
public String getK8sVersion() {
	return k8sVersion;
}
public void setK8sVersion(String k8sVersion) {
	this.k8sVersion = k8sVersion;
}
public int getInstallType() {
	return installType;
}
public void setInstallType(int installType) {
	this.installType = installType;
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
