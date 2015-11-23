package org.ku8eye.bean.deploy;

import java.util.HashMap;
import java.util.List;

/**
 * Template for Ku8 Cluster install
 * @author wuzhih
 *
 */
public class Ku8ClusterTemplate {
	//min nodes required
	private int minNodes;
	//max node allowed 
	private int maxNodes;
	//template type to distinct different templates
	private int templateType;
    	
	HashMap<String,String> installParamMap;//存放安装参数
	List<InstallNode> nodes;//安装的节点列表
	String logoURL;
	String name;
	String descNote;
}
