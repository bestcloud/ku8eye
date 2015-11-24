package org.ku8eye.service;

import java.util.List;

import org.ku8eye.domain.Ku8ResPartion;
import org.ku8eye.mapping.Ku8ResPartionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author jackchen
 *
 */
/**
 * 
 * @author jackchen
 *
 */
@Service
public class Ku8ResPartionService {

	@Autowired
	private Ku8ResPartionMapper resPartionDao;

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<Ku8ResPartion> getAllResPartions(int clusterId) {
		return resPartionDao.selectAll();
	}
}
