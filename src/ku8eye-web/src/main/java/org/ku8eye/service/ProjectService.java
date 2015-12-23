package org.ku8eye.service;

import java.util.Date;
import java.util.List;

import org.ku8eye.domain.Ku8Project;
import org.ku8eye.mapping.Ku8ProjectMapper;
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
public class ProjectService {

	@Autowired
	private Ku8ProjectMapper projectDao;

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<Ku8Project> getAllProjects() {
		return projectDao.selectAll();
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public int addProject(int tenantId, String owner, String name, String version, String k8sVersion, String note) {
		Ku8Project ku8p = new Ku8Project();
		ku8p.setTenantId(tenantId);
		ku8p.setOwner(owner);
		ku8p.setName(name);
		ku8p.setVersion(version);
		ku8p.setK8sVersion(k8sVersion);
		ku8p.setNote(note);
		ku8p.setLastUpdated(new Date());
		
		return projectDao.insert(ku8p);
	}
}
