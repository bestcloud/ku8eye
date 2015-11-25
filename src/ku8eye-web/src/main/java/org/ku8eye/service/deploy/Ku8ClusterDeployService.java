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
		node.setDefautNode(true);
		node.setHostId(1);
		node.setHostName("Etcd");
		node.setIp("192.168.1.2");
		
		List<InstallParam> etcdParams = new LinkedList<InstallParam>();
		etcdParams.add(new InstallParam("peer_ip", "192.168.1.2", "etcd所在主机的IP地址"));
		etcdParams.add(new InstallParam("etcd_data_dir", " /var/lib/etcd/etcd_data", "etcd数据存储目录"));
		node.getNodeRoleParams().put(Ku8ClusterTemplate.NODE_ROLE_ETCD, etcdParams);
		
		List<InstallParam> kuberMasterParams  = new LinkedList<InstallParam>();
		kuberMasterParams.add(new InstallParam("etcd_servers", "http://192.168.1.201:4001", "kube-apiserver所需etcd服务的URL"));
		kuberMasterParams.add(new InstallParam("apiserver_insecure_port", "1100", " kube-apiserver监听的非安全端口号"));
		kuberMasterParams.add(new InstallParam("apiserver_service_cluster_ip_range", "20.1.0.0/16", "Kubernetes Services可分配IP地址池"));
		kuberMasterParams.add(new InstallParam("apiserver_service_node_port_range", "1000-5000", "NodePort 类型的 Service 可用端口范围，含两端"));
		kuberMasterParams.add(new InstallParam("kube_master_url", "http://192.168.1.2:1100", "kube-apiserver服务URL"));
		kuberMasterParams.add(new InstallParam("kube_node_sync_period", "10s", "master与node信息同步时间间隔"));
		node.getNodeRoleParams().put(Ku8ClusterTemplate.NODE_ROLE_MASTER, kuberMasterParams);
		
		
		List<InstallParam> kuberNdoeParams  = new LinkedList<InstallParam>();
		kuberNdoeParams.add(new InstallParam("kube_master_url", "192.168.1.2:1100", " kube-apiserver服务URL"));
		kuberNdoeParams.add(new InstallParam("quagga_router_image_id", "f96cfe685533", "quagga router 镜像ID"));
		kuberNdoeParams.add(new InstallParam("quagga_router_image_tag", "index.alauda.cn/georce/router", "index.alauda.cn/georce/router quagga router 镜像tag"));
		node.getNodeRoleParams().put(Ku8ClusterTemplate.NODE_ROLE_NODE, kuberNdoeParams);
		
		
		List<InstallParam> dockerRegistryParams  = new LinkedList<InstallParam>();
		dockerRegistryParams.add(new InstallParam("docker0_ip", "192.168.1.1/240", "docker0网桥的IP地址"));
		dockerRegistryParams.add(new InstallParam("docker_runtime_root_dir", "/hadoop1/docker", "docker运行根目录"));
		dockerRegistryParams.add(new InstallParam("docker_registry_url", "{{docker_registry_server_name}}:5000", "docker registry URL"));
		dockerRegistryParams.add(new InstallParam("docker_registry_root_dir", "/hadoop1/docker_registry", " docker registry 运行目录"));
		dockerRegistryParams.add(new InstallParam("docker_registry_image_id", "774242a00f13", "docker registry 镜像ID"));
		dockerRegistryParams.add(new InstallParam("docker_registry_image_tag", "registry:2.2.0", "docker registry 镜像tag"));
		node.getNodeRoleParams().put(Ku8ClusterTemplate.NODE_ROLE_REGISTRY, dockerRegistryParams);
		temp.getNodes().add(node);
	 	
		return temp;

	}
}
