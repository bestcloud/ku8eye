package org.ku8eye.bean.deploy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.ku8eye.Constants;
import org.ku8eye.service.deploy.AnsibleCallResult;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
	public static String DEFAULT_GLOBAL = "default-global";
	private volatile String curInstallStep;
	private volatile boolean installFinished = false;
	private LinkedHashMap<String, InstallStepOutInfo> stepResults = new LinkedHashMap<String, InstallStepOutInfo>();

	public Ku8ClusterTemplate() {

		initDefGlobalParams();
	}

	private int id;
	private int clusterId;
	private String logoImage;
	private String detailPageUrl;
	// kube-dns服务设置的domain名
	private String name;
	private String describe;
	private String version = "1.0";
	// min nodes required
	private int minNodes;
	// max node allowed
	private int maxNodes;
	// template type to distinct different templates
	private int templateType;
	// global insall params,key is role,value is params with this role
	private HashMap<String, List<InstallParam>> globalParams;
	// 自动计算的全局参数
	private HashMap<String, String> autoComputedGlobalParams = new HashMap<String, String>();
	// nodes to install
	private List<InstallNode> nodes = new ArrayList<InstallNode>();
	private List<String> allowedNewRoles = new ArrayList<String>();

	public int getClusterId() {
		return clusterId;
	}

	public void setClusterId(int clusterId) {
		this.clusterId = clusterId;
	}

	public String getLogoImage() {
		return logoImage;
	}

	public boolean jugdgeInstallFinished() {
		return installFinished;
	}

	public void setCurStepResult(String stepName, InstallStepOutInfo oupputInfo, boolean installFinished) {
		this.installFinished = installFinished;
		stepResults.put(stepName, oupputInfo);
	}

	public Map<String, InstallStepOutInfo> fetchStepResults() {
		return stepResults;
	}

	public InstallNode getStandardMasterWithEtcdNode() {
		InstallNode node = new InstallNode();
		node.setDefautNode(true);
		node.setHostName("Not Selected");
		node.setIp("Not Selected");
		node.getNodeRoleParams().putAll(initInstallParameter(Ku8ClusterTemplate.NODE_ROLE_ETCD));
		node.getNodeRoleParams().putAll(initInstallParameter(Ku8ClusterTemplate.NODE_ROLE_MASTER));
		node.getNodeRoleParams().putAll(initInstallParameter(Ku8ClusterTemplate.NODE_ROLE_REGISTRY));
		return node.clone();
	}

	public String getCurInstallStep() {
		return curInstallStep;
	}

	public void setCurInstallStep(String curInstallStep) {
		this.curInstallStep = curInstallStep;
	}

	public InstallNode getStandardAllIneOneNode() {
		InstallNode node = new InstallNode();
		node.setDefautNode(true);
		node.setHostName("Not Selected");
		node.setIp("Not Selected");
		node.getNodeRoleParams().putAll(initInstallParameter(Ku8ClusterTemplate.NODE_ROLE_ETCD));
		node.getNodeRoleParams().putAll(initInstallParameter(Ku8ClusterTemplate.NODE_ROLE_MASTER));
		node.getNodeRoleParams().putAll(initInstallParameter(Ku8ClusterTemplate.NODE_ROLE_REGISTRY));
		node.getNodeRoleParams().putAll(initInstallParameter(Ku8ClusterTemplate.NODE_ROLE_NODE));
		return node.clone();
	}

	public List<InstallNode> getAllMinionNodes() {
		List<InstallNode> filterNodes = new LinkedList<InstallNode>();
		for (InstallNode node : nodes) {
			if (node.hasRole(Ku8ClusterTemplate.NODE_ROLE_NODE)) {
				filterNodes.add(node);
			}
		}
		return filterNodes;
	}

	public InstallNode getStandardK8sNode() {
		InstallNode node = new InstallNode();
		node.setDefautNode(true);
		node.setHostName("Not Selected");
		node.setIp("Not Selected");
		node.getNodeRoleParams().putAll(initInstallParameter(Ku8ClusterTemplate.NODE_ROLE_NODE));
		return node.clone();
	}

	private Map<String, List<InstallParam>> initInstallParameter(String role) {
		Map<String, List<InstallParam>> paramMap = new LinkedHashMap<String, List<InstallParam>>();
		List<InstallParam> list = new ArrayList<InstallParam>();
		if (NODE_ROLE_NODE.equalsIgnoreCase(role)) {
			//
		} else if (NODE_ROLE_MASTER.equalsIgnoreCase(role)) {
			list.add(new InstallParam("apiserver_insecure_port", "8080", " kube-apiserver监听的非安全端口号"));
			list.add(new InstallParam("ca_crt_CN", "ku8eye.org", ""));
			list.add(new InstallParam("kube_node_sync_period", "10s", "master与node信息同步时间间隔"));
		} else if (NODE_ROLE_REGISTRY.equalsIgnoreCase(role))

		{
			list.add(new InstallParam("docker0_ip", "10.17.42.3/24", "docker0网桥的IP地址"));
			list.add(new InstallParam("docker_registry_port", "5000", " docker registry 服务目录"));
			list.add(new InstallParam("docker_registry_root_dir", "/var/lib/registry", " docker registry 运行目录"));
			list.add(new InstallParam("docker_registry_image_id", "774242a00f13", "docker registry 镜像ID"));
			list.add(new InstallParam("docker_registry_image_tag", "registry:2.2.0", "docker registry 镜像tag"));
		} else if (NODE_ROLE_ETCD.equalsIgnoreCase(role)) {
			list.add(new InstallParam("etcd_binding_port", "4001", "etcd binding (linsten) port "));
		}
		paramMap.put(role, list);
		return paramMap;
	}

	public void addNewNode(InstallNode node) {
		for (InstallNode existNode : this.nodes) {
			if (existNode.getHostId() == node.getHostId() || existNode.getIp().equals(node.getIp())) {
				return;
			}
		}
		nodes.add(node);

	}

	public List<InstallParam> getGlobParameterByRole(String role) {
		return globalParams.get(role);
	}

	public Map<String, InstallParam> getAllGlobParameters() {
		Map<String, InstallParam> allParms = new HashMap<String, InstallParam>();
		for (Collection<InstallParam> params : globalParams.values()) {
			for (InstallParam param : params) {
				allParms.put(param.getName(), param);
			}

		}
		return allParms;
	}

	public Map<String, String> getCombinedGlobalParams() {
		Map<String, String> allParams = new HashMap<String, String>();
		for (Map.Entry<String, InstallParam> entry : getAllGlobParameters().entrySet()) {
			allParams.put(entry.getKey(), entry.getValue().getValue());
		}
		allParams.putAll(this.autoComputedGlobalParams);
		return allParams;
	}

	public HashMap<String, String> getAutoComputedGlobalParams() {
		return autoComputedGlobalParams;
	}

	public InstallNode getCurrentMasterNode()
	{
		for (InstallNode node : this.nodes) {
			if (node.hasRole(NODE_ROLE_MASTER)) {
				return node;
			}
		}
		return null;
	}
	public List<InstallNode> findAllK8sNodes() {
		List<InstallNode> results = new LinkedList<InstallNode>();
		for (InstallNode node : this.nodes) {
			if (node.hasRole(NODE_ROLE_NODE)) {
				results.add(node);
			}
		}
		return results;
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Ku8ClusterTemplate clone() {
		try {
			Ku8ClusterTemplate newTemp = (Ku8ClusterTemplate) super.clone();
			HashMap<String, List<InstallParam>> newGlParams = new HashMap<String, List<InstallParam>>();
			for (String role_key : this.globalParams.keySet()) {
				LinkedList<InstallParam> new_list = new LinkedList<InstallParam>();
				for (InstallParam param : this.globalParams.get(role_key)) {
					new_list.add(param.clone());
				}

				newGlParams.put(role_key, new_list);
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

	private void initDefGlobalParams() {
		globalParams = new HashMap<String, List<InstallParam>>();
		// def
		List<InstallParam> def_list = new ArrayList<InstallParam>();
		def_list.add(new InstallParam(Constants.k8sparam_cluster_docker0_ip_srange, "10.0.0.1/16",
				"Kubernetes集群里的Node节点所用IP地址范围"));
		def_list.add(
				new InstallParam("apiserver_service_cluster_ip_range", "20.1.0.0/16", "Kubernetes Services可分配IP地址池"));
		def_list.add(
				new InstallParam("apiserver_service_node_port_range", "1000-5000", "NodePort 类型的 Service 可用端口范围，含两端"));
		def_list.add(new InstallParam("install_quagga_router", "false", "是否安装Quagga路由"));
		def_list.add(new InstallParam("quagga_router_image_id", "f96cfe685533", "quagga router 镜像ID"));
		def_list.add(new InstallParam("quagga_router_image_tag", "index.alauda.cn/georce/router",
				"index.alauda.cn/georce/router quagga router 镜像tag"));
		def_list.add(new InstallParam("docker_runtime_root_dir", "/var/lib/docker", "docker运行根目录"));
		def_list.add(new InstallParam("cluster_domain_name", "cluster.local", "kube-dns服务设置的domain名"));
		def_list.add(new InstallParam("cluster_dns_ip", "20.1.0.100", "kube-dns服务IP地址"));
		def_list.add(new InstallParam("push_pause_image", "true", "是否将 Kubernetes pause 镜像push到 docker registry"));
		def_list.add(new InstallParam("kubernetes_pause_image_id", "6c4579af347b ", "pause镜像ID"));
		def_list.add(new InstallParam("kubernetes_pause_image_tag", "{{docker_registry_url}}/google_containers/pause",
				"pause镜像在 docker registry 的URL"));
		globalParams.put(DEFAULT_GLOBAL, def_list);

		// etcd
		List<InstallParam> etcdParams = new ArrayList<InstallParam>();
		etcdParams.add(new InstallParam("etcd_data_dir", " /var/lib/etcd/etcd_data", "etcd数据存储目录"));
		etcdParams.add(new InstallParam("peer_ip", " 192.168.1.201", ""));
		etcdParams.add(new InstallParam("etcd_servers", "http://192.168.1.2:4001", "kube-apiserver所需etcd服务的URL"));
		globalParams.put(NODE_ROLE_ETCD, etcdParams);
		// kub master
		List<InstallParam> kuberMasterParams = new ArrayList<InstallParam>();
		globalParams.put(NODE_ROLE_MASTER, kuberMasterParams);
		// docker reg
		List<InstallParam> dockerRegistryParams = new ArrayList<InstallParam>();
		globalParams.put(NODE_ROLE_REGISTRY, dockerRegistryParams);
		// kubnode
		List<InstallParam> kuberNdoeParams = new ArrayList<InstallParam>();
		globalParams.put(NODE_ROLE_NODE, kuberNdoeParams);
	}

	public void clearStatus() {
		this.curInstallStep = null;
		this.stepResults.clear();
		this.installFinished = false;
	}

	@JsonIgnore
	public AnsibleCallResult fetchLastAnsibleResult() {
		return this.stepResults.get(this.curInstallStep).fetchAnsibleCallResult();
	}

	public void deleteNodeOfHostId(int hostId) {
		Iterator<InstallNode> itor = this.nodes.iterator();
		while (itor.hasNext()) {
			InstallNode node = itor.next();
			if (node.getHostId() == hostId) {
				itor.remove();

			}
		}

	}
}
