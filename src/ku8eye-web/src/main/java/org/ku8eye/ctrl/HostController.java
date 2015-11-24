package org.ku8eye.ctrl;

import java.util.List;

import org.ku8eye.bean.GridData;
import org.ku8eye.domain.Host;
import org.ku8eye.service.HostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HostController {

	@Autowired
	private HostService hostService;

	@RequestMapping(value = "/hostlist/{zoneId}")
	public GridData getProjects(@PathVariable("zoneId") int zoneId) {
		GridData grid = new GridData();
		List<Host> pros = hostService.getHostsByZoneId(zoneId);
		grid.setData(pros);
		return grid;
	}
}
