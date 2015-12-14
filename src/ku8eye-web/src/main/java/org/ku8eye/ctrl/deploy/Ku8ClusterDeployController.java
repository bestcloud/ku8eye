package org.ku8eye.ctrl.deploy;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.ku8eye.bean.deploy.InstallNode;
import org.ku8eye.bean.deploy.InstallParam;
import org.ku8eye.bean.deploy.Ku8ClusterTemplate;
import org.ku8eye.domain.Host;
import org.ku8eye.service.HostService;
import org.ku8eye.service.deploy.Ku8ClusterDeployService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

@RestController
@SessionAttributes("ku8template")
public class Ku8ClusterDeployController {

	@Autowired
	private Ku8ClusterDeployService deployService;
	private HostService hostService;
	InstallNode node;
	private Logger log = Logger.getLogger(this.toString());
	
	@RequestMapping(value = "/deploycluster/listtemplates")
	public List<Ku8ClusterTemplate> listTemplates() {
		List<Ku8ClusterTemplate> templates = deployService.getAllTemplates();
		List<Ku8ClusterTemplate> simpleTemplates = new LinkedList<Ku8ClusterTemplate>();
		for (Ku8ClusterTemplate template : templates) {
			Ku8ClusterTemplate simple = new Ku8ClusterTemplate();
			simple.setId(template.getId());
			simple.setName(template.getName());
			simple.setDescribe(template.getDescribe());
			simple.setLogoImage(template.getLogoImage());
			simple.setDetailPageUrl(template.getDetailPageUrl());
			simple.setMinNodes(template.getMinNodes());
			simple.setMaxNodes(template.getMaxNodes());
			simpleTemplates.add(simple);
		}
		return simpleTemplates;
	}

	@RequestMapping(value = "/deploycluster/selecttemplate/{id}", method = RequestMethod.GET)
	public Ku8ClusterTemplate selectTemplate(@PathVariable("id") int templateId, ModelMap model) {
		Ku8ClusterTemplate template = deployService.getAndCloneTemplate(templateId);
		model.addAttribute("ku8template", template);
		return template;

	}
	
	@RequestMapping(value = "/deploycluster/modifytemplate/{id}", method = RequestMethod.GET)
	public InstallNode modifyTemplate(HttpServletRequest request,
			@RequestParam("templateString") String templateString,@PathVariable("id") int templateId, ModelMap model) {
		
		if(templateId==0){
			Ku8ClusterTemplate template = deployService.getAllTemplates().get(0);	
			node = template.getStandardAllIneOneNode();
			String arr[]=templateString.split(",");
			node.setIp(arr[0]);
			node.setHostName(arr[1]);
			node.setRootPassword(arr[2]);	
			node.getNodeRoleParams().put(Ku8ClusterTemplate.NODE_ROLE_ETCD, initInstallParameter(arr[7],arr[8]));
			node.getNodeRoleParams().put(Ku8ClusterTemplate.NODE_ROLE_MASTER, initInstallParameter(arr[10],arr[11]));
			node.getNodeRoleParams().put(Ku8ClusterTemplate.NODE_ROLE_REGISTRY, initInstallParameter(arr[13],arr[14]));
		}else{
			Ku8ClusterTemplate template = deployService.getAllTemplates().get(1);	
//			node = template.getStandardMasterWithEtcdNode();
//			node.setIp(hosts[0]);
//			node.setRootPassword(rootPass);
//			template.addNewNode(node);
//			// minion nodes
//			for (int i = 1; i < hosts.length; i++) {
//				node = template.getStandardK8sNode();
//				node.setIp(hosts[i]);
//				node.setRootPassword(rootPass);
//				template.addNewNode(node);
//			}
			
		}

		return node;

	}

	private List<InstallParam> initInstallParameter(String user,String pass) {
		List<InstallParam> list = new ArrayList<InstallParam>();
		list.add(new InstallParam("ansible_ssh_user", user, "login uername"));
		list.add(new InstallParam("ansible_ssh_pass", pass, "login pass"));
		return list;
	}
	
	@RequestMapping(value = "/deploycluster/getcurtemplate", method = RequestMethod.GET)
	public Ku8ClusterTemplate getCurTemplate(ModelMap model) {
		return (Ku8ClusterTemplate) model.get("ku8template");
	}

}