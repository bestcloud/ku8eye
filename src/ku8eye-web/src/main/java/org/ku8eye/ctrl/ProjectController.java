package org.ku8eye.ctrl;


import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.ku8eye.bean.GridData;
import org.ku8eye.bean.deploy.Ku8ClusterTemplate;
import org.ku8eye.bean.project.Project;
import org.ku8eye.ctrl.deploy.Ku8ClusterDeployController;
import org.ku8eye.domain.Ku8Project;
import org.ku8eye.domain.Tenant;
import org.ku8eye.domain.User;
import org.ku8eye.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@SessionAttributes("user")
public class ProjectController {
	
	@Autowired
	private ProjectService projectService;
	
	static final ObjectMapper om=new ObjectMapper();
	
	@RequestMapping(value="/projectlist")
	public GridData getProjects(){
        GridData grid = new GridData();
		List<Ku8Project> pros = projectService.getAllProjects();
		grid.setData(pros);
        return grid;
	}
	
	@RequestMapping(value="/createProject2")
	public String createPorjects(String creatJson) throws Exception
	{
		 Project pr=om.readValue(creatJson, Project.class);
		
		
		return "";
	}
	
	@RequestMapping(value="/addApplication")
	public boolean addApplication(HttpServletRequest request,
			@RequestParam("name") String name,
			@RequestParam("version") String version,
			@RequestParam("k8sVersion") String k8sVersion,
			@RequestParam("note") String note,ModelMap model) {

		User user = (User) model.get("user");
		
		if(user == null)
		{
			System.err.println("ERROR USER NOT LOGGED IN");
			return false;
		}
		
		if(name.isEmpty() || version.isEmpty() || k8sVersion.isEmpty() || note.isEmpty())
		{
			System.err.println("EMPTY FIELDS");
			return false;
		}
		
		int res = projectService.addProject(user.getTenantId(), user.getUserId(), name, version, k8sVersion, note);
		
		if(res == -1)
			return false;
		else
			return true;
	}
}
