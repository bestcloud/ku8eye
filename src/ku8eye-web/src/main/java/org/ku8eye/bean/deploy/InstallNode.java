package org.ku8eye.bean.deploy;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * it used in Ku8ClusterTemplate ,which is an install node
 * 
 * @author wuzhih
 *
 */
public class InstallNode implements Cloneable {
	private int hostId;
	private String ip;
	private String hostName;
	// node role ,for example etcd 、master、 node ,docker registry
	// node specific params,key is node Role,values is related role's InstallParam
	private Map<String, List<InstallParam>> nodeRoleParams = new HashMap<String, List<InstallParam>>();
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

	
	public boolean isDefautNode() {
		return defautNode;
	}

	public void setDefautNode(boolean defautNode) {
		this.defautNode = defautNode;
	}

	public Map<String, List<InstallParam>> getNodeRoleParams() {
		return nodeRoleParams;
	}

	public void setNodeRoleParams(Map<String, List<InstallParam>> nodeRoleParams) {
		this.nodeRoleParams = nodeRoleParams;
	}

	public InstallNode clone() {
		try {
			InstallNode newNode = (InstallNode) super.clone();
			Map<String, List<InstallParam>> nodeParams = new LinkedHashMap<String, List<InstallParam>>();
			for (Map.Entry<String, List<InstallParam>> entry : this.getNodeRoleParams().entrySet()) {
				List<InstallParam> roleParams=new LinkedList<InstallParam>();
				for(InstallParam param:entry.getValue())
				{
					roleParams.add(param.clone());
				}
				nodeParams.put(entry.getKey(),roleParams);
			}
			newNode.setNodeRoleParams(nodeParams);
			return newNode;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}
}
