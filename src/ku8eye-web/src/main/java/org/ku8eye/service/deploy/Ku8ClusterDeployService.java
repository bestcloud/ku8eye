package org.ku8eye.service.deploy;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.ku8eye.bean.deploy.InstallNode;
import org.ku8eye.bean.deploy.InstallParam;
import org.ku8eye.bean.deploy.Ku8ClusterTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Ku8Cluster delploy service ,used to deploy an kubernetes cluster
 * 
 * @author wuzhih
 *
 */

@Service
public class Ku8ClusterDeployService {

	@Autowired
	private TemplateUtil tmpUtil;
	@Autowired
	private ProcessCaller processCaller;
	private final List<Ku8ClusterTemplate> allTemplates;

	{
		allTemplates = new LinkedList<Ku8ClusterTemplate>();
		allTemplates.add(createAllInOneTemplate());
		allTemplates.add(createDistributeTemplate());
	}

	public List<Ku8ClusterTemplate> getAllTemplates() {
		return allTemplates;
	}

	private Ku8ClusterTemplate createAllInOneTemplate() {
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
		node.getNodeRoleParams().put(Ku8ClusterTemplate.NODE_ROLE_ETCD,
				initInstallParameter());
		node.getNodeRoleParams().put(Ku8ClusterTemplate.NODE_ROLE_MASTER,
				initInstallParameter());
		node.getNodeRoleParams().put(Ku8ClusterTemplate.NODE_ROLE_NODE,
				initInstallParameter());
		node.getNodeRoleParams().put(Ku8ClusterTemplate.NODE_ROLE_REGISTRY,
				initInstallParameter());
		temp.getNodes().add(node);
		return temp;
	}

	private List<InstallParam> initInstallParameter() {
		List<InstallParam> list = new ArrayList<InstallParam>();
		list.add(new InstallParam("ansible_ssh_user", "root", "login uername"));
		list.add(new InstallParam("ansible_ssh_pass", "root", "login pass"));
		return list;
	}

	private Ku8ClusterTemplate createDistributeTemplate() {

		Ku8ClusterTemplate temp = new Ku8ClusterTemplate();
		temp.setName("Distribute Cluster");
		temp.setTemplateType(1);
		temp.setDescribe("Distribute server");
		temp.setMinNodes(3);
		temp.setMaxNodes(20);

		InstallNode etcd_node = new InstallNode();
		etcd_node.setDefautNode(true);
		etcd_node.setHostId(2);
		etcd_node.setHostName("Etcd");
		etcd_node.setIp("192.168.1.2");
		etcd_node.getNodeRoleParams().put(Ku8ClusterTemplate.NODE_ROLE_ETCD,
				initInstallParameter());

		InstallNode master_node = new InstallNode();
		master_node.setDefautNode(true);
		master_node.setHostId(3);
		master_node.setHostName("Kuber Master");
		master_node.setIp("192.168.1.3");
		master_node.getNodeRoleParams().put(
				Ku8ClusterTemplate.NODE_ROLE_MASTER, initInstallParameter());

		InstallNode nodes_node1 = new InstallNode();
		nodes_node1.setDefautNode(true);
		nodes_node1.setHostId(4);
		nodes_node1.setHostName("Kuber Node 1");
		nodes_node1.setIp("192.168.1.4");
		nodes_node1.getNodeRoleParams().put(Ku8ClusterTemplate.NODE_ROLE_NODE,
				initInstallParameter());

		InstallNode nodes_node2 = new InstallNode();
		nodes_node2.setDefautNode(false);
		nodes_node2.setHostId(5);
		nodes_node2.setHostName("Kuber Node 2");
		nodes_node2.setIp("192.168.1.5");
		nodes_node2.getNodeRoleParams().put(Ku8ClusterTemplate.NODE_ROLE_NODE,
				initInstallParameter());

		InstallNode resistry = new InstallNode();
		resistry.setDefautNode(true);
		resistry.setHostId(5);
		resistry.setHostName("Kuber Node 2");
		resistry.setIp("192.168.1.5");
		resistry.getNodeRoleParams().put(Ku8ClusterTemplate.NODE_ROLE_REGISTRY,
				initInstallParameter());

		temp.getNodes().add(etcd_node);
		temp.getNodes().add(master_node);
		temp.getNodes().add(nodes_node1);
		temp.getNodes().add(nodes_node2);
		temp.getNodes().add(resistry);

		return temp;
	}

	public void deploy(Ku8ClusterTemplate temp) throws Exception {
		if (!processCaller.isFinished()) {
			throw new Exception(" Ku8Cluser deploy is running...");
		}
		tmpUtil.createAnsibleFiles(temp);
		String[] command = { "ansible-playbook -i hosts pre-setup/keys.yml",
				"ansible-playbook -i hosts pre-setup/disablefirewalld.yml",
				"ansible-playbook -i hosts setup.ym" };
		processCaller.asnyCall(command);
	}
	
	public List<String> deployResult()throws Exception
	{
		return processCaller.getOutputs();
	}
	
	public boolean deployIsFinish() throws Exception
	{
		return processCaller.isFinished();
	}
	
	public String deployHasError()throws Exception
	{
		if(!processCaller.isNormalExit())
			return processCaller.getErrorMsg();
		else
			return "no error message..";
	}
}
