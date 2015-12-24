package org.ku8eye.ctrl;

import java.util.List;
import org.apache.log4j.Logger;
import org.ku8eye.bean.GridData;
import org.ku8eye.domain.Ku8Cluster;
import org.ku8eye.service.Ku8ClusterService;
import org.ku8eye.service.deploy.Ku8ClusterDeployService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

@RestController
@SessionAttributes("ku8template")
public class Ku8ClusterController {

	@Autowired
	private Ku8ClusterService clusterService;
	private Ku8ClusterDeployService deployService;
	private Logger log = Logger.getLogger(this.toString());
	@RequestMapping(value = "/clusterlist/{zoneId}")
	public List<Ku8Cluster> getcluster(@PathVariable("zoneId") int zoneId) {
		List<Ku8Cluster> pros = clusterService.getClusterByZoneId(zoneId);
		return pros;
	}
}