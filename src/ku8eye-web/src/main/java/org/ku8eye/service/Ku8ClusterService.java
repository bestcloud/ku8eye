package org.ku8eye.service;

import java.util.List;
import java.util.logging.Logger;

import org.ku8eye.domain.Ku8Cluster;
import org.ku8eye.mapping.Ku8ClusterMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class Ku8ClusterService {
	@Autowired
	private Ku8ClusterMapper Ku8ClusterDao;
	private List<Ku8Cluster> Ku8ClusterList;
	private Logger log = Logger.getLogger(this.toString());
	/**
	 * find User by userid
	 * @param pUserId
	 * @return User
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<Ku8Cluster> getClusterByZoneId(int  zoneId){	
		return Ku8ClusterDao.selectAll();
	}
	
}