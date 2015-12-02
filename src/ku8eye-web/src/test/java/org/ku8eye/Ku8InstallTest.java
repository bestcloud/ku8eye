package org.ku8eye;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ku8eye.service.deploy.Ku8ClusterDeployService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { App.class })
public class Ku8InstallTest {
	

	private org.ku8eye.service.deploy.ProcessCaller ProcessCaller = new org.ku8eye.service.deploy.ProcessCaller();

	@Test
	public void getTemp() {
//		deployService.getAllTemplates();
	}

	@Test
	public void getRunCommand() throws Exception {

		ProcessCaller.call("cmd","/c","dir");
		for (String s : ProcessCaller.getOutputs()) {
			System.out.println(s);
		}
	}
}
