package org.ku8eye;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ku8eye.bean.deploy.InstallNode;
import org.ku8eye.bean.deploy.Ku8ClusterTemplate;
import org.ku8eye.service.deploy.Ku8ClusterDeployService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { App.class })
public class Ku8InstallTest {

	@Autowired
	private Ku8ClusterDeployService deployService;

	private Ku8ClusterTemplate getTemp() throws Exception {
		Ku8ClusterTemplate template = deployService.getAllTemplates().get(0).clone();
		InstallNode node=template.getStandardAllIneOneNode();
		node.setIp("192.168.18.133");
		node.setp
		template.addNewNode(node);
		return template;

	}

	@Test
	public void createScripts() throws Exception {
		deployService.createInstallScripts(getTemp());
	}

	@Test
	public void getRunCommand() throws Exception {
		deployService.deployKeyFiles();
	}
}
