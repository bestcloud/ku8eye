package org.ku8eye.ctrl;


import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.ku8eye.bean.GridData;
import org.ku8eye.bean.project.Project;
import org.ku8eye.domain.DockerImage;
import org.ku8eye.domain.Ku8Project;
import org.ku8eye.domain.User;
import org.ku8eye.service.ProjectService;
import org.ku8eye.util.AjaxReponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

@RestController
@SessionAttributes("user")
public class ProjectController {
	
	private static final Logger log = Logger.getLogger(ProjectController.class);
	
	@Autowired
	private ProjectService projectService;
	
	@RequestMapping(value="/getApplications")
	public GridData getApplications(){
        GridData grid = new GridData();
		List<Ku8Project> apps = projectService.getAllApplications();
		grid.setData(apps);
        return grid;
	}

	@RequestMapping(value="/getImages")
	public GridData getImages(){
        GridData grid = new GridData();
		List<DockerImage> images = projectService.getAllDockerImages();
		grid.setData(images);
        return grid;
	}
	
	@RequestMapping(value="/getApplication")
	public Ku8Project getApplication(int id){
		return projectService.getApplication(id);
	}
	
	@RequestMapping(value="/createApplication")
	public AjaxReponse createApplication(int id, String jsonStr, ModelMap model)
	{
		System.out.println("received str:" + jsonStr);
		
		User user = (User) model.get("user");
		
		if(user == null)
		{
			log.error("ERROR USER NOT LOGGED IN");
			return new AjaxReponse(-1, "USER NOT LOGGED");
		}
		
		if(jsonStr != null && jsonStr.length() > 0)
		{
			 Project p = Project.getFromJSON(jsonStr);
			 
			 int tenantId = user.getTenantId();
			 String owner = p.getAuthor(); //TODO CHECK
			 String name = p.getProjectName();
			 String version = p.getVersion();
			 String k8sVersion = p.getK8sVersion();
			 String notes = p.getNotes();
			 String yamlSpec = jsonStr;
			 
			 if(name.isEmpty() || version.isEmpty() || k8sVersion.isEmpty() || notes.isEmpty())
			 {
				log.error("EMPTY FIELDS");
				return new AjaxReponse(-1, "EMPTY FIELDS");
			 }
			 
			 projectService.updateApplication(id, tenantId, owner, name, version, k8sVersion, notes, yamlSpec);
			
			 return new AjaxReponse(1, "UPDATED");
		}
		return new AjaxReponse(-1, "FAILED");
	}
	
	@RequestMapping(value="/addApplication")
	public AjaxReponse addApplication(HttpServletRequest request, String name, String version, String k8sVersion, String note, String jsonStr, ModelMap model) {
		
		User user = (User) model.get("user");
		
		if(user == null)
		{
			log.error("ERROR USER NOT LOGGED IN");
			return new AjaxReponse(-1, "USER NOT LOGGED");
		}
		
		if(name.isEmpty() || version.isEmpty() || k8sVersion.isEmpty() || note.isEmpty() || jsonStr.isEmpty())
		{
			log.error("EMPTY FIELDS");
			return new AjaxReponse(-1, "EMPTY FIELDS");
		}
		
		int res = projectService.addApplication(user.getTenantId(), user.getUserId(), name, version, k8sVersion, note, jsonStr);
		
		if(res == -1)
			return new AjaxReponse(res, "ADD FAILED");
		else
			return new AjaxReponse(res, "ADDED");
	}
}
