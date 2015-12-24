package org.ku8eye.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.ku8eye.domain.Host;
import org.ku8eye.mapping.HostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author jackchen
 *
 */
@Service
public class HostService {

	@Autowired
	private HostMapper hostDao;

	/**
	 * find User by userid
	 * 
	 * @param pUserId
	 * @return User
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<Host> getHostsByZoneId(int zoneId) {
		return hostDao.selectAll();
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Host getHostsByZoneString(int zoneId) {
		return hostDao.selectByPrimaryKey(zoneId);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public int addHost(Host host) {
		return hostDao.insert(host);
	}
}
