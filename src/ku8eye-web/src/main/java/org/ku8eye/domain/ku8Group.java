package org.ku8eye.domain;

/**
 * group in a kubernetes cluster ,some work node use a some label ,so service
 * deployed into this nodes
 * 
 * @author wuzhih
 *
 */
public class ku8Group {
	private int id;
	// belong to which cluster
	private int clusterId;
	private String name;
	// "," splitted labels
	private String labels;
	private java.util.Date lastUpdated;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getClusterId() {
		return clusterId;
	}

	public void setClusterId(int clusterId) {
		this.clusterId = clusterId;
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

	public java.util.Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(java.util.Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

}
