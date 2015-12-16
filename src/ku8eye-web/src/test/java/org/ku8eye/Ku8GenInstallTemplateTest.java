package org.ku8eye;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.ku8eye.bean.deploy.InstallNode;
import org.ku8eye.bean.deploy.Ku8ClusterTemplate;
import org.ku8eye.service.deploy.Ku8ClusterDeployService;
import org.ku8eye.service.deploy.TemplateUtil;
//@Ignore("not ready on mymachine")
public class Ku8GenInstallTemplateTest {

	private Ku8ClusterDeployService deployService;
	 
	public Ku8GenInstallTemplateTest() {
		deployService = new Ku8ClusterDeployService();
		TemplateUtil tempUtil = new TemplateUtil();
		tempUtil.setHostsFile("/templates/hosts:hosts");
		tempUtil.setScriptRoot("kubernetes_cluster_setup");
		tempUtil.setSshKeyHostsFile("/templates/ssh-hosts:ssh-hosts");
		
		tempUtil.setTmpFileYML(
				"default-global:/templates/all.yml:group_vars/all.yml,docker-registry:/templates/docker_registry.yml:roles/docker-registry/defaults/main.yml,etcd:/templates/etcd.yml:roles/etcd/defaults/main.yml,kube-master:/templates/kuber_master.yml:roles/kube-master/defaults/main.yml,kube-node:/templates/kuber_node.yml:roles/kube-node/defaults/main.yml");
		deployService.setTmpUtil(tempUtil);
	}

	private Ku8ClusterTemplate getTemp() throws Exception {
		Ku8ClusterTemplate template = deployService.getAllTemplates().get(0).clone();
		InstallNode node = template.getStandardAllIneOneNode();
		node.setIp("192.168.18.133");
		node.setRootPassword("1111111");
		template.addNewNode(node);
		return template;

	}

	
	@Test 
	public void createScripts() throws Exception {
		Ku8ClusterTemplate template =getTemp();
		List<String> errMsgs=deployService.createInstallScripts(template);
		Assert.assertTrue(errMsgs.size()==0);
		
	}

}
