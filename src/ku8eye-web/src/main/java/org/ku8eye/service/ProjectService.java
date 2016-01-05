package org.ku8eye.service;

import java.util.Date;
import java.util.List;

import org.ku8eye.domain.DockerImage;
import org.ku8eye.domain.Ku8Project;
import org.ku8eye.mapping.DockerImageMapper;
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
@Service
public class ProjectService {

	@Autowired
	private Ku8ProjectMapper projectDao;
	
	@Autowired
	private DockerImageMapper imagesDao;

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<Ku8Project> getAllApplications() {
		return projectDao.selectAll();
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<DockerImage> getAllDockerImages() {
		return imagesDao.selectAll();
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Ku8Project getApplication(int id) {
		return projectDao.selectByPrimaryKey(id);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public int addApplication(int tenantId, String owner, String name, String version, String k8sVersion, String note, String yamlSpec) {
		Ku8Project ku8p = new Ku8Project();
		ku8p.setTenantId(tenantId);
		ku8p.setOwner(owner);
		ku8p.setName(name);
		ku8p.setVersion(version);
		ku8p.setK8sVersion(k8sVersion);
		ku8p.setNote(note);
		ku8p.setJsonSpec(yamlSpec);
//		ku8p.setYamlSpec(yamlSpec);
		ku8p.setLastUpdated(new Date());
		
		return projectDao.insert(ku8p);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public int updateApplication(int id, int tenantId, String owner, String name, String version, String k8sVersion, String note, String yamlSpec) {
		Ku8Project ku8p = new Ku8Project();
		ku8p.setId(id);
		ku8p.setTenantId(tenantId);
		ku8p.setOwner(owner);
		ku8p.setName(name);
		ku8p.setVersion(version);
		ku8p.setK8sVersion(k8sVersion);
		ku8p.setNote(note);
		ku8p.setJsonSpec(yamlSpec);
		ku8p.setLastUpdated(new Date());
		
		return projectDao.updateByPrimaryKey(ku8p);
	}
}
