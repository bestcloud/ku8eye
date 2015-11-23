package org.ku8eye.service;

import java.util.List;

import org.ku8eye.domain.Ku8Project;
import org.ku8eye.mapping.Ku8ProjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
	
	public List<Ku8Project> getAllProjects(){
		return projectDao.selectAll();
	}
}
