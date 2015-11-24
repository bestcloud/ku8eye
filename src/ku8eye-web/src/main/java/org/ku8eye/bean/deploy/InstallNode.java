package org.ku8eye.bean.deploy;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * it used in Ku8ClusterTemplate ,which is an install node
 * 
 * @author wuzhih
 *
 */
public class InstallNode {
	private int hostId;
	private String ip;
	private String hostName;
	// node role ,for example etcd 、master、 node ,docker registry
	private String nodeRole;
	// node specific params,key is param name,values is InstallParam
	private Map<String, InstallParam> nodeParams = new LinkedHashMap<String, InstallParam>();
	// if it's a default node from template,if true ,it can't be deleted
	private boolean defautNode;

	public int getHostId() {
		return hostId;
	}

	public void setHostId(int hostId) {
		this.hostId = hostId;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getNodeRole() {
		return nodeRole;
	}

	public void setNodeRole(String nodeRole) {
		this.nodeRole = nodeRole;
	}

	public Map<String, InstallParam> getNodeParams() {
		return nodeParams;
	}

	public void setNodeParams(Map<String, InstallParam> nodeParams) {
		this.nodeParams = nodeParams;
	}

	public boolean isDefautNode() {
		return defautNode;
	}

	public void setDefautNode(boolean defautNode) {
		this.defautNode = defautNode;
	}

}
