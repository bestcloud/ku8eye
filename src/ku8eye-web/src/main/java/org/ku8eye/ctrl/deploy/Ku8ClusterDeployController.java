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
	private HostService hostService;
	@Autowired
	private Ku8ClusterDeployService deployService;
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

	@RequestMapping(value = "/deploycluster/getnodemodal/{status}")
	public InstallNode getNodemodal(HttpServletRequest request, @RequestParam("addnode") String addnode,
			@PathVariable("status") String status, ModelMap model) {
		InstallNode node;
		// session中获取当前模板对象
		Ku8ClusterTemplate template = getCurTemplate(model);
		if (status.equals("singleNode")) {
			node = template.getStandardAllIneOneNode();
		} else if (status.equals("multiNode")) {
			node = template.getStandardMasterWithEtcdNode();
		} else {
			node = template.getStandardK8sNode();
		}
		String arr[]=addnode.split(",");
		for(int i=0;i<arr.length;i=i+4){
			node.setIp(arr[i]);
			node.setHostId(Integer.parseInt(arr[i+1]));
			node.setHostName(arr[i + 2]);
			node.setRootPassword(arr[i + 3]);
			template.addNewNode(node);
		}
		
		return node;
	}

	@RequestMapping(value = "/deploycluster/addk8snodes/{id}")
	public List<InstallNode> addk8snodes(@PathVariable("id") String id, ModelMap model) {
		// session中获取当前模板对象
				Ku8ClusterTemplate template = getCurTemplate(model);
		List<InstallNode> nodes = new LinkedList<InstallNode>();
		
		String strList[] = id.split(",");
		for (String s : strList) {
			if (!s.isEmpty()) {
				InstallNode node = template.getStandardK8sNode();
				Host pros = hostService.getHostsByZoneString(Integer.parseInt(s));
				node.setDefautNode(true);
				node.setHostId(pros.getId());
				node.setHostName(pros.getHostName());
				node.setIp(pros.getIp());
				node.setRootPassword(pros.getRootPasswd());
				template.addNewNode(node);
				nodes.add(node);
			}
		}
		return nodes;
	}

	@RequestMapping(value = "/deploycluster/modifytemplate/{id}", method = RequestMethod.GET)
	public InstallNode modifyTemplate(HttpServletRequest request, @RequestParam("templateString") String templateString,
			@PathVariable("id") int templateId, ModelMap model) {
		String arr[] = templateString.split(",");
		// session中获取当前模板对象
		Ku8ClusterTemplate template = getCurTemplate(model);
		if (template.getId() == 0) {
			// all in one节点模板
			System.out.println("todo .........modifyTemplate ");
		     return null;

		} else {
			if(true)
			{
				System.out.println("todo .........modifyTemplate ,muti nodes ");
			     return null;

			}
			// 192.168.1.6,mynode_4,123456,
			// 192.168.1.6,123456,123456,root,root,
			InstallNode node = template.getStandardMasterWithEtcdNode();
			node.setIp(arr[0]);
			node.setHostName(arr[1]);
			node.setRootPassword(arr[2]);

			List<InstallNode> k8sNodes = template.findAllK8sNodes();
			for (InstallNode nodes : k8sNodes) {
				nodes.setRoleParam(Ku8ClusterTemplate.NODE_ROLE_NODE, "kubelet_hostname_override", nodes.getIp());
			}

			for (int i = 3; i < arr.length; i = i + 5) {
				node = template.getStandardK8sNode();
				node.setIp(arr[i]);
				node.setHostName(arr[i + 2]);
				node.setRootPassword(arr[i + 3]);
				template.addNewNode(node);
			}
			return node;
		}
	}
	
	@RequestMapping(value = "/deploycluster/getcurtemplate", method = RequestMethod.GET)
	public Ku8ClusterTemplate getCurTemplate(ModelMap model) {
		return (Ku8ClusterTemplate) model.get("ku8template");
	}

}