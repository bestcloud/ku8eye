package org.ku8eye.rest;

import java.util.List;

import org.ku8eye.bean.GridData;
import org.ku8eye.domain.Ku8Project;
import org.ku8eye.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProjectController {
	
	@Autowired
	private ProjectService projectService;
	
	@RequestMapping(value="/projectlist")
	public GridData getProjects(){
        GridData grid = new GridData();
		List<Ku8Project> pros = projectService.getAllProjects();
		grid.setData(pros);
        return grid;
	}
}
