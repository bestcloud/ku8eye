package org.ku8eye.ctrl.deploy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.ku8eye.bean.deploy.InstallNode;
import org.ku8eye.bean.deploy.InstallOutputBean;
import org.ku8eye.bean.deploy.InstallParam;
import org.ku8eye.bean.deploy.InstallStepOutInfo;
import org.ku8eye.bean.deploy.Ku8ClusterTemplate;
import org.ku8eye.domain.Host;
import org.ku8eye.service.HostService;
import org.ku8eye.service.deploy.AnsibleCallResult;
import org.ku8eye.service.deploy.AnsibleResultParser;
import org.ku8eye.service.deploy.Ku8ClusterDeployService;
import org.ku8eye.service.deploy.ProcessCaller;
import org.ku8eye.service.k8s.K8sAPIService;
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
	@Autowired
	private K8sAPIService apiService;
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

	@RequestMapping(value = "/deploycluster/create-ansible-scripts", method = RequestMethod.GET)
	public List<String> createAnsibleScripts(ModelMap model) throws Exception {
		return deployService.createInstallScripts(getCurTemplate(model));

	}

	@RequestMapping(value = "/deploycluster/start-install", method = RequestMethod.GET)
	public String startInstall(ModelMap model) {
		try {
			this.getCurTemplate(model).clearStatus();
			final Process pross = deployService.getProcessCaller().getCurProcess();
			deployService.shutdownProcessCallerIfRunning(pross, true);
			deployService.deployKeyFiles(10 * 60, false);
			Ku8ClusterTemplate curTemplate = this.getCurTemplate(model);
			curTemplate.setCurInstallStep("ssh-key-task");
			return "SUCCESS:";
		} catch (Exception e) {
			return "ERROR:" + e.toString();
		}
	}

	// @RequestMapping(value = "/deploycluster/fetch-ansilbe-result", method =
	// RequestMethod.GET)
	// public List<String> fetchAnsilbeResult2(ModelMap model,
	// HttpServletRequest request) {
	// System.out.println("fetch ansible result ");
	// if ("false".equals(request.getParameter("mock"))) {
	// return DemoDataUtil.getFakeAnsibleResult().printInfo()
	// }
	// }

	@RequestMapping(value = "/deploycluster/fetch-ansilbe-result", method = RequestMethod.GET)
	public InstallOutputBean fetchAnsilbeResult(ModelMap model, HttpServletRequest request) {
		// System.out.println("fetch ansible result ");
		// if (true) {
		//
		// Map<String, InstallStepOutInfo> stepInfos = new LinkedHashMap<String,
		// InstallStepOutInfo>();
		// if(System.currentTimeMillis()%2==1)
		// {
		// stepInfos.put("ssh-key-task", new InstallStepOutInfo(
		// "ssh-key-task", true, DemoDataUtil.getFakeAnsibleResult(),
		// DemoDataUtil.getFakeAnsibleOutput()));
		// }else
		// {
		// System.out.println("not add data");
		// }
		// InstallOutputBean out = new InstallOutputBean(stepInfos, false,
		// true);
		// return out;
		// }
		Ku8ClusterTemplate curTemplate = this.getCurTemplate(model);
		ProcessCaller curCaller = deployService.getProcessCaller();
		boolean finished = curCaller.isFinished();
		String errmsg = curCaller.getErrorMsg();
		ArrayList<String> results = new ArrayList<String>(curCaller.getOutputs());
		AnsibleCallResult parseResult = AnsibleResultParser.parseResult(results);
		parseResult.setAnsibleFinished(finished);
		if (finished && results.isEmpty()) {
			parseResult.setTaskResult("INIT", "INIT", false, errmsg);
		}
		String curStep = curTemplate.getCurInstallStep();
		boolean installFinished = (finished && curStep.equals("install-kubernetes-task"))
				|| (finished && !parseResult.isSuccess());

		if (finished && parseResult.isSuccess()) {
			// begin next step
			if (curStep.equals("ssh-key-task")) {
				final Process pross = deployService.getProcessCaller().getCurProcess();
				deployService.shutdownProcessCallerIfRunning(pross, true);
				deployService.disableFirewalld(10 * 60, false);
				// next step
				curTemplate.setCurInstallStep("close-firewall-task");
				parseResult.setAnsibleFinished(finished);

			} else if (curStep.equals("close-firewall-task")) {
				final Process pross = deployService.getProcessCaller().getCurProcess();
				deployService.shutdownProcessCallerIfRunning(pross, true);
				deployService.installK8s(30 * 60, false);
				// next step
				curTemplate.setCurInstallStep("install-kubernetes-task");
				parseResult.setAnsibleFinished(finished);
			} else {
				// all finished
				installFinished = finished;
			}
		}
		curTemplate.setCurStepResult(curStep, new InstallStepOutInfo(curStep, finished, parseResult, results),
				installFinished);
		InstallOutputBean out = new InstallOutputBean(curTemplate.fetchStepResults(), installFinished,
				parseResult.isSuccess());
		System.out.println("fetch ansible result " + curStep + " out " + Arrays.toString(results.toArray()));
		return out;

	}

	@RequestMapping(value = "/deploycluster/ansible-final-result-report/{type}", method = RequestMethod.GET)
	public Object getAnsibleFinalResult(ModelMap model, @PathVariable("type") String type) {
		// if (true) {
		// if ("sumary".equals(type)) {
		// return DemoDataUtil.getFakeAnsibleResult().getNodeTotalSumaryMap();
		// } else {
		// return DemoDataUtil.getFakeAnsibleResult();
		// }
		// }

		Ku8ClusterTemplate template = getCurTemplate(model);
		AnsibleCallResult result = template.fetchLastAnsibleResult();
		if ("sumary".equals(type)) {
			return result.getNodeTotalSumaryMap();
		} else {
			return result;
		}

	}

	@RequestMapping(value = "/deploycluster/write-cluster-info2db", method = RequestMethod.GET)
	public String writeClusterInfo2db(ModelMap model) throws Exception {
		if (this.deployService.addClusterRecordToDB(this.getCurTemplate(model))) {
			return "SUCCESS:";
		} else {
			return "FAILED:";
		}

	}

	@RequestMapping(value = "/deploycluster/selecttemplate/{id}/{clusterId}", method = RequestMethod.GET)
	public Ku8ClusterTemplate selectTemplate(@PathVariable("id") int templateId,@PathVariable("clusterId") int clusterId, ModelMap model) {
		Ku8ClusterTemplate template = deployService.getAndCloneTemplate(templateId);
		template.setClusterId(clusterId);
		model.addAttribute("ku8template", template);
		return template;

	}

	@RequestMapping(value = "/deploycluster/global-params", method = RequestMethod.GET)
	public Map<String, InstallParam> getGlobParameter(ModelMap model) {
		Ku8ClusterTemplate template = getCurTemplate(model);
		Map<String, InstallParam> Parameter = template.getAllGlobParameters();
		return Parameter;

	}

	@RequestMapping(value = "/deploycluster/update-global-params", method = RequestMethod.GET)
	public String updatetGlobParameter(ModelMap model, HttpServletRequest request) {
		Ku8ClusterTemplate template = getCurTemplate(model);
		String paramName = request.getParameter("param");
		String paramValue = request.getParameter("value");
		Map<String, InstallParam> params = template.getAllGlobParameters();
		InstallParam paramObj = params.get(paramName);
		if (paramObj != null) {
			paramObj.setValue(paramValue);
			return "SUCCESS:";
		} else {
			return "FAILED:";
		}

	}
	@RequestMapping(value = "/deploycluster/deletenode/{hostId}")
	public void deleteTemplateNode(ModelMap model, @PathVariable("hostId") int hostId) {
		Ku8ClusterTemplate template = getCurTemplate(model);
		template.deleteNodeOfHostId(hostId);
	}

	@RequestMapping(value = "/deploycluster/nodeslist", method = RequestMethod.GET)
	public List<InstallNode> getNodesList(ModelMap model, HttpServletRequest request) {
		Ku8ClusterTemplate template = getCurTemplate(model);
		if ("node".equals(request.getParameter("type"))) {
			return template.getAllMinionNodes();
		}
		List<InstallNode> nodes = template.getNodes();
		return nodes;
	}
	
	
	@RequestMapping(value = "/deploycluster/getnodemodal/modal")
	public Ku8ClusterTemplate getmodal(ModelMap model) {
		// session中获取当前模板对象
		Ku8ClusterTemplate template = getCurTemplate(model);
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
		String arr[] = addnode.split(",");
		for (int i = 0; i < arr.length; i = i + 4) {
			node.setIp(arr[i]);
			node.setHostId(Integer.parseInt(arr[i + 1]));
			node.setHostName(arr[i + 2]);
			node.setRootPassword(arr[i + 3]);
			template.addNewNode(node);
		}

		return node;
	}

	@RequestMapping(value = "/deploycluster/addk8snodes/{id}")
	public void addk8snodes(@PathVariable("id") String id, ModelMap model) {
		// session中获取当前模板对象
		Ku8ClusterTemplate template = getCurTemplate(model);
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
			}
		}
		
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
			if (true) {
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
				log.info(i);

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
	
	@RequestMapping(value = "/deploycluster/getk8s-alive-nodes/{clusterId}", method = RequestMethod.GET)
	public List<String> getK8sAliveNodes(@PathVariable("clusterId") int clusterId,ModelMap model) {
		List<String> cluster=apiService.getK8sAliveNodes(clusterId);
		return cluster;
	}

}