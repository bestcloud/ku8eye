package org.ku8eye.service.deploy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
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
	private Logger LOGGER = Logger.getLogger(Ku8ClusterDeployService.class);
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
		HashMap<String, String> autoParams = template.getAutoComputedGlobalParams();
		InstallNode etcdNode = findNodeHashRole(template, Ku8ClusterTemplate.NODE_ROLE_ETCD);
		String ectdIP = etcdNode.getIp();
		String etcdPort = etcdNode.getNodeParam("etcd_binding_port");
		String etcd_servers = "http://" + ectdIP + ":" + etcdPort;
		// update global param

		autoParams.put("etcd_servers", etcd_servers);

		// master param
		InstallNode masterNode = findNodeHashRole(template, Ku8ClusterTemplate.NODE_ROLE_MASTER);
		String masterIp = masterNode.getIp();
		String apiserver_insecure_port = masterNode.getNodeParam("apiserver_insecure_port");
		String clusterDocker0IPAdd = masterNode.getNodeParam(Constants.k8sparam_cluster_docker0_ip_srange);
		String kube_master_url = "http://" + masterIp + ":" + apiserver_insecure_port;
		// update global param
		autoParams.put("kube_master_url", kube_master_url);
		autoParams.put("server_key_CN", masterIp);
		autoParams.put("ca_crt_CN", masterNode.getNodeParam("ca_crt_CN"));

		autoParams.put("apiserver_insecure_port", apiserver_insecure_port);
		autoParams.put(Constants.k8sparam_cluster_docker0_ip_srange, clusterDocker0IPAdd);
		autoParams.put("apiserver_service_cluster_ip_range",
				masterNode.getNodeParam("apiserver_service_cluster_ip_range"));
		autoParams.put("apiserver_service_node_port_range",
				masterNode.getNodeParam("apiserver_service_node_port_range"));
		autoParams.put("kube_node_sync_period", masterNode.getNodeParam("kube_node_sync_period"));

		// docker registry
		InstallNode dockerRegNode = findNodeHashRole(template, Ku8ClusterTemplate.NODE_ROLE_REGISTRY);
		String dockerRegIP = dockerRegNode.getIp();
		String docker_registry_server_name = dockerRegIP;
		String docker0_ip = dockerRegNode.getNodeParam("docker0_ip");
		autoParams.put("docker0_ip", docker0_ip);

		// update global param
		autoParams.put("docker_registry_server_name", docker_registry_server_name);
		autoParams.put("docker_registry_server_ip", dockerRegIP);
		autoParams.put("docker_registry_root_dir", dockerRegNode.getNodeParam("docker_registry_root_dir"));
		autoParams.put("docker_registry_image_id", dockerRegNode.getNodeParam("docker_registry_image_id"));
		autoParams.put("docker_registry_image_tag", dockerRegNode.getNodeParam("docker_registry_image_tag"));

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
			targetList.add(subNetPrex + i + ".3/24");
		}
		return targetList;

	}

	private Ku8ClusterTemplate createAllInOneTemplate() {
		Ku8ClusterTemplate temp = new Ku8ClusterTemplate();
		temp.setId(0);
		//bug
		//temp.getAllGlobParameters().get("install_quagga_router").setValue("false");
		temp.setName("All In One Cluster");
		temp.setTemplateType(1);
		temp.setDescribe("All service in one server");
		temp.setMinNodes(1);
		temp.setMaxNodes(1);
		return temp;
	}

	private List<String> validateTemplate(Ku8ClusterTemplate template) {
		Map<String, String> gloableParams = template.getCombinedGlobalParams();
		List<String> errMsg = new LinkedList<String>();
		String clusterDocker0IPAdd = gloableParams.get(Constants.k8sparam_cluster_docker0_ip_srange);
		if (!clusterDocker0IPAdd.endsWith("/16")) {
			errMsg.add(Constants.k8sparam_cluster_docker0_ip_srange + " must be xxx.xxx.xxx.xxx/16 ");
		}
		return errMsg;
	}

	public List<String> createInstallScripts(Ku8ClusterTemplate template) throws Exception {
		processTemplateAutoParams(template);
		List<String> errMsgs = validateTemplate(template);
		if (errMsgs.isEmpty()) {
			tmpUtil.createAnsibleFiles(template);
		}
		return errMsgs;
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
		temp.setMaxNodes(50);
		temp.getAllGlobParameters().get("install_quagga_router").setValue("true");

		return temp;
	}

	public void deployKeyFiles(int forceTimeOutSeconds, boolean clearOutputs) {
		checkProcessFinished();
		processCaller.asnyCall(ansibleWorkDir, "ansible-playbook", "-i", "ssh-hosts", "pre-setup/keys.yml");
		if (forceTimeOutSeconds > 0) {
			processCaller.asnyWaitFinish(forceTimeOutSeconds, clearOutputs);
		}
	}

	public void disableFirewalld(int forceTimeOutSeconds, boolean clearOutputs) {
		checkProcessFinished();
		processCaller.asnyCall(ansibleWorkDir, "ansible-playbook", "-i", "ssh-hosts", "pre-setup/disablefirewalld.yml");
		if (forceTimeOutSeconds > 0) {
			processCaller.asnyWaitFinish(forceTimeOutSeconds, clearOutputs);
		}
	}

	public void installK8s(int forceTimeOutSeconds, boolean clearOutputs) {
		checkProcessFinished();
		processCaller.asnyCall(ansibleWorkDir, "ansible-playbook", "-i", "hosts", "setup.yml");
		if (forceTimeOutSeconds > 0) {
			processCaller.asnyWaitFinish(forceTimeOutSeconds, clearOutputs);
		}
	}

	private void checkProcessFinished() {
		if (!processCaller.isFinished()) {
			throw new RuntimeException(" Ku8Cluser deploy is running...");
		}
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

	public void shutdownProcessCallerIfRunning(final Process process,boolean clearOutputs) {
		if (!processCaller.isFinished()) {
			LOGGER.warn("find ansible process runing ,kill it " + processCaller);
		}
		processCaller.shutdownCaller(process,clearOutputs);
	}

	public String deployHasError() throws Exception {
		if (!processCaller.isNormalExit())
			return processCaller.getErrorMsg();
		else
			return "no error message..";
	}

	public TemplateUtil getTmpUtil() {
		return tmpUtil;
	}

}