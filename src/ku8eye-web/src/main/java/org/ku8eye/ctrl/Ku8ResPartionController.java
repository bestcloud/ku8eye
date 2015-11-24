package org.ku8eye.ctrl;

import java.util.List;

import org.ku8eye.bean.GridData;
import org.ku8eye.domain.Ku8ResPartion;
import org.ku8eye.service.Ku8ResPartionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Ku8ResPartionController {
	
	@Autowired
	private Ku8ResPartionService resPartionService;
	
	@RequestMapping(value="/respartionlist/{clusterId}")
	public GridData getProjects(@PathVariable("clusterId") int clusterId){
        GridData grid = new GridData();
		List<Ku8ResPartion> pros = resPartionService.getAllResPartions(clusterId);
		grid.setData(pros);
        return grid;
	}
}
