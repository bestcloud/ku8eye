package org.ku8eye.ctrl.deploy;

import java.util.LinkedList;
import java.util.List;

import org.ku8eye.bean.deploy.Ku8ClusterTemplate;
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

	@RequestMapping(value = "/deploycluster/getcurtemplate", method = RequestMethod.GET)
	public Ku8ClusterTemplate getCurTemplate(ModelMap model) {
		return (Ku8ClusterTemplate) model.get("ku8template");
	}

}