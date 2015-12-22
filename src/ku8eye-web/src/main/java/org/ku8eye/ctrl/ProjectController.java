package org.ku8eye.ctrl;


import java.util.List;

import org.ku8eye.bean.GridData;
import org.ku8eye.bean.project.Project;
import org.ku8eye.domain.Ku8Project;
import org.ku8eye.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
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
	
	@RequestMapping(value="/createProject")
	public String createPorjects(String creatJson) throws Exception
	{
		 Project pr=om.readValue(creatJson, Project.class);
		
		
		return "";
	}
}
