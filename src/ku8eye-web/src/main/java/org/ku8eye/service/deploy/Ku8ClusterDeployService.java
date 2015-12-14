package org.ku8eye.service.deploy;

import java.util.LinkedList;
import java.util.List;

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
