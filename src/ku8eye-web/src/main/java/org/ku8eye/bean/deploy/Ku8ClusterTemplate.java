package org.ku8eye.bean.deploy;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Template for Ku8 Cluster install
 * 
 * @author wuzhih
 *
 */
public class Ku8ClusterTemplate implements Cloneable {
	public static String NODE_ROLE_ETCD = "etcd";
	public static String NODE_ROLE_MASTER = "kube-master";
	public static String NODE_ROLE_NODE = "kube-node";
	public static String NODE_ROLE_REGISTRY = "docker-registry";

	private String logoImage;
	private String detailPageUrl;
	private String name;
	private String describe;
	private String version = "1.0";
	// min nodes required
	private int minNodes;
	// max node allowed
	private int maxNodes;
	// template type to distinct different templates
	private int templateType;
	// global insall params,key is param's name
	private List<InstallParam> globalParams = new LinkedList<InstallParam>();
	// nodes to install
	private List<InstallNode> nodes;
    private List<String> allowedNewRoles=new LinkedList<String>();
	public String getLogoImage() {
		return logoImage;
	}

	// add a "pre paramed" node of specified nodeRole
	public InstallNode addNewNode(String nodeRole) {
		for (InstallNode node : nodes) {
			List<InstallParam> roleParams = node.getNodeRoleParams().get(nodeRole);
			if (roleParams != null) {
				InstallNode newNode = node.clone();
				newNode.getNodeRoleParams().clear();
				List<InstallParam> newRoleParams = new LinkedList<InstallParam>();
				for (InstallParam roleParam : roleParams) {
					newRoleParams.add(roleParam.clone());
				}
				newNode.getNodeRoleParams().put(nodeRole, newRoleParams);
				newNode.setIp(null);
				newNode.setHostId(0);
				newNode.setHostName(null);
				nodes.add(newNode);
				return newNode;
			}
		}
		throw new RuntimeException("no exists node of role find " + nodeRole);

	}

	public void setLogoImage(String logoImage) {
		this.logoImage = logoImage;
	}

	public String getDetailPageUrl() {
		return detailPageUrl;
	}

	public void setDetailPageUrl(String detailPageUrl) {
		this.detailPageUrl = detailPageUrl;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public int getMinNodes() {
		return minNodes;
	}

	public void setMinNodes(int minNodes) {
		this.minNodes = minNodes;
	}

	public int getMaxNodes() {
		return maxNodes;
	}

	public void setMaxNodes(int maxNodes) {
		this.maxNodes = maxNodes;
	}

	public int getTemplateType() {
		return templateType;
	}

	public void setTemplateType(int templateType) {
		this.templateType = templateType;
	}

	public List<InstallNode> getNodes() {
		return nodes;
	}

	public void setNodes(List<InstallNode> nodes) {
		this.nodes = nodes;
	}

	public Ku8ClusterTemplate clone() {
		try {
			Ku8ClusterTemplate newTemp = (Ku8ClusterTemplate) super.clone();
			LinkedList<InstallParam> newGlParams = new LinkedList<InstallParam>();
			for (InstallParam param : this.globalParams) {
				newGlParams.add(param.clone());
			}
			newTemp.globalParams = newGlParams;

			LinkedList<InstallNode> newNodes = new LinkedList<InstallNode>();
			for (InstallNode node : this.nodes) {
				newNodes.add(node.clone());
			}
			newTemp.nodes = newNodes;
			return newTemp;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}
}
