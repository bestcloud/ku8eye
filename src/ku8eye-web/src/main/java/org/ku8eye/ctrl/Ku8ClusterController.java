package org.ku8eye.ctrl;

import org.ku8eye.domain.Ku8Cluster;
import org.ku8eye.service.Ku8ClusterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Ku8ClusterController {

	@Autowired
	private Ku8ClusterService clusterService;
	@RequestMapping(value = "/cluster/{clusterId}")
	public Ku8Cluster getcluster(@PathVariable("clusterId") int clusterId) {
		return clusterService.getClustersByClusterId(clusterId);
		
	}
}