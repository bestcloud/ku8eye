package org.ku8eye.ctrl;

import java.util.List;

import org.ku8eye.bean.GridData;
import org.ku8eye.bean.service.Pod;
import org.ku8eye.bean.service.ServiceAndRC;
import org.ku8eye.service.ServiceAndRcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/serviceAndRc")
public class ServiceAndRcController
{
	@Autowired
	private ServiceAndRcService service;
	
	@RequestMapping(value = {"/list"})
	public GridData getAllServiceAndRc()
	{
        GridData grid = new GridData();
		List<ServiceAndRC> sr = service.getAllServiceAndRc();
		grid.setData(sr);
        return grid;
	}
	
	@RequestMapping(value = {"/pods"})
	public GridData getAllPodsBySelector(@RequestParam("selector") String selector)
	{
        GridData grid = new GridData();
		List<Pod> pods = service.getAllPodsBySelector(selector);
		grid.setData(pods);
        return grid;
	}
}
