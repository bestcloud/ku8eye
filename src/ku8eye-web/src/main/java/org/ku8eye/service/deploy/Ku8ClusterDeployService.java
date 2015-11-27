package org.ku8eye.service.deploy;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.ku8eye.bean.deploy.InstallNode;
import org.ku8eye.bean.deploy.InstallParam;
import org.ku8eye.bean.deploy.Ku8ClusterTemplate;

/**
 * Ku8Cluster delploy service ,used to deploy an kubernetes cluster
 * 
 * @author wuzhih
 *
 */
public class Ku8ClusterDeployService {

	private static final List<Ku8ClusterTemplate> allTemplates;

	static {
		allTemplates = new LinkedList<Ku8ClusterTemplate>();
		allTemplates.add(createAllInOneTemplate());
	}

	public static List<Ku8ClusterTemplate> getAllTemplates() {
		return allTemplates;
	}

	private static Ku8ClusterTemplate createAllInOneTemplate() {
		Ku8ClusterTemplate temp = new Ku8ClusterTemplate();
		temp.setName("All In One Cluster");
		temp.setTemplateType(1);
		temp.setDescribe("All service in one server");
		temp.setMinNodes(1);
		temp.setMaxNodes(1);
		
		InstallNode node = new InstallNode();
		node.setDefautNode(true);
		node.setHostId(1);
		node.setHostName("Etcd");
		node.setIp("192.168.1.2");
		
		List<InstallParam> etc =new ArrayList<InstallParam>();
		etc.add(new InstallParam("etcd_parameter", "123123", "desc"));
//		etcdParams.add(new InstallParam("peer_ip", "192.168.1.2", "etcd所在主机的IP地址"));
		node.getNodeRoleParams().put(Ku8ClusterTemplate.NODE_ROLE_ETCD, etc);
		
		node.getNodeRoleParams().put(Ku8ClusterTemplate.NODE_ROLE_MASTER, new ArrayList<InstallParam>());
		
		node.getNodeRoleParams().put(Ku8ClusterTemplate.NODE_ROLE_NODE, new ArrayList<InstallParam>());
		
		node.getNodeRoleParams().put(Ku8ClusterTemplate.NODE_ROLE_REGISTRY, new ArrayList<InstallParam>());
//		List<InstallNode> nodeList=new ArrayList<InstallNode>();
//		nodeList.add(node);
//		for(String key:node.getNodeRoleParams().keySet())
//		{
//			System.out.println("===>????>>>>"+key);
//		}
		
		
		temp.getNodes().add(node);
		return temp;
	}
}
