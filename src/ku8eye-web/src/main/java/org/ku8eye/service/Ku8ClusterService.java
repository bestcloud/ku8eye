package org.ku8eye.service;

import java.util.List;

import org.ku8eye.domain.Host;
import org.ku8eye.domain.Ku8Cluster;
import org.ku8eye.mapping.HostMapper;
import org.ku8eye.mapping.Ku8ClusterMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class Ku8ClusterService {
	@Autowired
	private Ku8ClusterMapper Ku8ClusterDao;
	@Autowired
	private HostMapper hostDao;

	/**
	 * find User by userid
	 * 
	 * @param pUserId
	 * @return User
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Ku8Cluster getClustersByClusterId(int clusterId) {
		return Ku8ClusterDao.selectByPrimaryKey(clusterId);
	}

	public List<Host> getClusterHosts(int clusterId) {
		return hostDao.selectByClusterId(clusterId);
	}

}