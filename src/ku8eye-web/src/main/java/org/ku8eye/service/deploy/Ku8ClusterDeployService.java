package org.ku8eye.service.deploy;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.ku8eye.Constants;
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

	private String ansibleWorkDir = "/root/kubernetes_cluster_setup/";

	public void setAnsibleWorkDir(String ansibleWorkDir) {
		this.ansibleWorkDir = ansibleWorkDir;
	}

	public void setTmpUtil(TemplateUtil tmpUtil) {
		this.tmpUtil = tmpUtil;
	}

	private final List<Ku8ClusterTemplate> allTemplates;

	public Ku8ClusterDeployService() {
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
		// etcd param
		Map<String, InstallParam> gloableParams = template.getAllGlobParameters();
		InstallNode etcdNode = findNodeHashRole(template, Ku8ClusterTemplate.NODE_ROLE_ETCD);
		String ectdIP = etcdNode.getIp();
		String etcdPort = judgeParam(gloableParams, "etcd_binding_port", etcdNode);
		String etcd_servers = "http://" + ectdIP + ":" + etcdPort;
		// update global param
		gloableParams.get("etcd_servers").setValue(etcd_servers);

		// master param
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

		String clusterDocker0IPAdd = gloableParams.get(Constants.k8sparam_cluster_docker0_ip_srange).getValue();
		List<String> docker0IpList = get24maskChildNetIP(clusterDocker0IPAdd);
		// every k8s node ,calc params
		List<InstallNode> k8sNodes = template.findAllK8sNodes();
		int index = 0;
		for (InstallNode node : k8sNodes) {
			node.setRoleParam(Ku8ClusterTemplate.NODE_ROLE_NODE, "kubelet_hostname_override", node.getIp());
			node.setRoleParam(Ku8ClusterTemplate.NODE_ROLE_NODE, "docker0_ip", docker0IpList.get(index));
			index++;
		}
		// every node ,set root and password

	}

	/**
	 * mask16IP is some address like 172.0.0.0/16 , sub networks will be
	 * 172.0.1.0/24,172.0.2.0/24,172.0.3.0/24.....
	 * 
	 * @param mask16IP
	 * @return
	 */
	private static List<String> get24maskChildNetIP(String mask16IP) {
		List<String> targetList = new ArrayList<String>(254);
		String[] temp1 = mask16IP.trim().split("\\.");
		String subNetPrex = temp1[0] + "." + temp1[1] + ".";
		for (int i = 0; i < 254; i++) {
			targetList.add(subNetPrex + i + ".1/24");
		}
		return targetList;

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
		Map<String, InstallParam> gloableParams = template.getAllGlobParameters();
		List<String> errMsg = new LinkedList<String>();
		String clusterDocker0IPAdd = gloableParams.get(Constants.k8sparam_cluster_docker0_ip_srange).getValue();
		if (!clusterDocker0IPAdd.endsWith("/16")) {
			errMsg.add(Constants.k8sparam_cluster_docker0_ip_srange + " must be xxx.xxx.xxx.xxx/16 ");
		}
		return errMsg;
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

	public void deployKeyFiles() {
		checkProcessFinished();
		processCaller.asnyCall(ansibleWorkDir, "ansible-playbook", "-i", "ssh-hosts", "pre-setup/keys.yml");
	}

	public void disableFirewalld() {
		checkProcessFinished();
		processCaller.asnyCall(ansibleWorkDir, "ansible-playbook", "-i", "ssh-hosts", "pre-setup/disablefirewalld.yml");
	}

	private void checkProcessFinished() {
		if (!processCaller.isFinished()) {
			throw new RuntimeException(" Ku8Cluser deploy is running...");
		}
//		processCaller.reset();
	}

	public void installK8s() {
		checkProcessFinished();
		processCaller.asnyCall(ansibleWorkDir, "ansible-playbook", "-i", "hosts", "setup.yml");
	}

	public List<String> deployResult() throws Exception {
		return processCaller.getOutputs();
	}

	public boolean deployIsFinish() throws Exception {
		return processCaller.isFinished();
	}

	public ProcessCaller getProcessCaller() {
		return processCaller;
	}

	public String deployHasError() throws Exception {
		if (!processCaller.isNormalExit())
			return processCaller.getErrorMsg();
		else
			return "no error message..";
	}

}