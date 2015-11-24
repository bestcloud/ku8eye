package org.ku8eye.service.deploy;

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
		List<InstallParam> roleParams = new LinkedList<InstallParam>();
		node.getNodeRoleParams().put(Ku8ClusterTemplate.NODE_ROLE_ETCD, roleParams);
		roleParams = new LinkedList<InstallParam>();
		node.getNodeRoleParams().put(Ku8ClusterTemplate.NODE_ROLE_MASTER, roleParams);
		roleParams = new LinkedList<InstallParam>();
		node.getNodeRoleParams().put(Ku8ClusterTemplate.NODE_ROLE_REGISTRY, roleParams);
		roleParams = new LinkedList<InstallParam>();
		node.getNodeRoleParams().put(Ku8ClusterTemplate.NODE_ROLE_NODE, roleParams);
		temp.getNodes().add(node);
		return temp;

	}
}
