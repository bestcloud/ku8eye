package org.ku8eye.service.deploy;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

	private ProcessCaller processCaller = new ProcessCaller();

	private final List<Ku8ClusterTemplate> allTemplates;

	{
		allTemplates = new LinkedList<Ku8ClusterTemplate>();
		allTemplates.add(createAllInOneTemplate());
		allTemplates.add(createStandardTemplate());
	}

	public List<Ku8ClusterTemplate> getAllTemplates() {
		return allTemplates;
	}

	private InstallNode findNodeHashRole(Ku8ClusterTemplate template, String role) {
		for (InstallNode node : template.getNodes()) {
			if (node.hasRole(role)) {
				return node;
			}
		}
		return null;
	}

	private void processTemplateAutoParams(Ku8ClusterTemplate template) {
		//etcd param
		Map<String, InstallParam> gloableParams = template.getAllGlobParameters();
		InstallNode etcdNode = findNodeHashRole(template, Ku8ClusterTemplate.NODE_ROLE_ETCD);
		String ectdIP = etcdNode.getIp();
		String etcdPort = judgeParam(gloableParams, "etcd_binding_port", etcdNode);
		String etcd_servers = "http://" + ectdIP + ":" + etcdPort;
		// update global param
		gloableParams.get("etcd_servers").setValue(etcd_servers);
		
		//master param
		InstallNode masterNode = findNodeHashRole(template, Ku8ClusterTemplate.NODE_ROLE_MASTER);
		String masterIp = masterNode.getIp();
		String apiserver_insecure_port = judgeParam(gloableParams, "apiserver_insecure_port", masterNode);
		String kube_master_url = "http://" + masterIp + ":" + apiserver_insecure_port;
		// update global param
		gloableParams.get("kube_master_url").setValue(kube_master_url);
		gloableParams.get("server_key_CN").setValue(masterIp);
		

		// docker registry
		InstallNode dockerRegNode = findNodeHashRole(template, Ku8ClusterTemplate.NODE_ROLE_REGISTRY);
		String dockerRegIP = dockerRegNode.getIp();
		String docker_registry_server_name = dockerRegIP;
		// update global param
		gloableParams.get("docker_registry_server_name").setValue(docker_registry_server_name);
		gloableParams.get("docker_registry_server_ip").setValue(dockerRegIP);
		
		//every node
		List<InstallNode> k8sNodes=template.findAllK8sNodes();
		for(InstallNode node:k8sNodes)
		{
			node.setRoleParam(Ku8ClusterTemplate.NODE_ROLE_NODE, "kubelet_hostname_override", node.getIp());
		}
		
		
	}

	private String judgeParam(Map<String, InstallParam> gloableParams, String paramName, InstallNode node) {
		InstallParam param = gloableParams.get(paramName);
		String paramVal = (param == null) ? null : param.getValue();
		String nodeParamVal = node.getNodeParam(paramName);
		if (nodeParamVal != null) {
			paramVal = nodeParamVal;
		}
		return paramVal;
	}

	private Ku8ClusterTemplate createAllInOneTemplate() {
		Ku8ClusterTemplate temp = new Ku8ClusterTemplate();
		temp.setName("All In One Cluster");
		temp.setTemplateType(1);
		temp.setDescribe("All service in one server");
		temp.setMinNodes(1);
		temp.setMaxNodes(1);
		return temp;
	}

	public List<String> validateTemplate(Ku8ClusterTemplate template) {
		/**
		 * @todo
		 */
		return null;
	}

	public void createInstallScripts(Ku8ClusterTemplate template) throws Exception {
		processTemplateAutoParams(template);
		tmpUtil.createAnsibleFiles(template);
	}

	public Ku8ClusterTemplate getAndCloneTemplate(int templateId) {
		for (Ku8ClusterTemplate temp : allTemplates) {
			if (temp.getId() == templateId) {
				return temp.clone();
			}
		}
		return null;
	}

	private Ku8ClusterTemplate createStandardTemplate() {

		Ku8ClusterTemplate temp = new Ku8ClusterTemplate();
		temp.setId(1);
		temp.setName("Standard Cluster");
		temp.setTemplateType(1);
		temp.setDescribe("Standard server");
		temp.setMinNodes(3);
		temp.setMaxNodes(20);

		return temp;
	}

	public void setup(Ku8ClusterTemplate temp) throws Exception {
		if (!processCaller.isFinished()) {
			throw new Exception(" Ku8Cluser deploy is running...");
		}
		processCaller.asnyCall("ansible-playbook -i hosts setup.ym");
	}

	public void deployKeyFiles() {
		processCaller.asnyCall("ansible-playbook -i hosts pre-setup/keys.yml");
	}

	public void disableFirewalld() {
		processCaller.asnyCall("ansible-playbook -i hosts pre-setup/disablefirewalld.yml");
	}

	public List<String> deployResult() throws Exception {
		return processCaller.getOutputs();
	}

	public boolean deployIsFinish() throws Exception {
		return processCaller.isFinished();
	}

	public String deployHasError() throws Exception {
		if (!processCaller.isNormalExit())
			return processCaller.getErrorMsg();
		else
			return "no error message..";
	}
}
