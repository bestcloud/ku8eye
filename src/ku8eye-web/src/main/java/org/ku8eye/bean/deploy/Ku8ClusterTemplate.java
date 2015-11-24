package org.ku8eye.bean.deploy;

import java.util.HashMap;
import java.util.List;

/**
 * Template for Ku8 Cluster install
 * @author wuzhih
 *
 */
public class Ku8ClusterTemplate {
	public static String NODE_ROLE_ETCD="etcd";
	public static String NODE_ROLE_MASTER="kube-master";
	public static String NODE_ROLE_NODE="kube-node";
	public static String NODE_ROLE_REGISTRY="docker-registry";
	
	private String logoImage;
	private String detailPageUrl;
	private String name;
	private String describe;
	private String version="1.0";
	//min nodes required
	private int minNodes;
	//max node allowed 
	private int maxNodes;
	//template type to distinct different templates
	private int templateType;
    //global insall params,key is param's name 	
	private HashMap<String,InstallParam> globalParamMap;
	//nodes to install
	private List<InstallNode> nodes;
	public String getLogoImage() {
		return logoImage;
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
	public HashMap<String, InstallParam> getGlobalParamMap() {
		return globalParamMap;
	}
	public void setGlobalParamMap(HashMap<String, InstallParam> globalParamMap) {
		this.globalParamMap = globalParamMap;
	}
	public List<InstallNode> getNodes() {
		return nodes;
	}
	public void setNodes(List<InstallNode> nodes) {
		this.nodes = nodes;
	} 
	
	
}
