package org.ku8eye.service;

/**
 * for docker image mangement
 */
import java.util.List;

import org.ku8eye.domain.DockerImage;
import org.ku8eye.mapping.DockerImageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author wuzhi
 *
 */
@Service
public class ImageService {

	@Autowired
	private DockerImageMapper imgeDao;

	/**
	 * search images
	 * 
	 * @param imageName
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<DockerImage> searchImages(String imageName) {
		return imgeDao.selectByImageName("%"+imageName+"%");
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public DockerImage getImagesId(int dockerId) {
		return imgeDao.selectByPrimaryKey(dockerId);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteImagesId(int dockerId) {
		imgeDao.deleteByPrimaryKey(dockerId);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public String createImage(DockerImage image) {
		int id = imgeDao.insert(image);
		return "SUCCESS:";
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateImage(DockerImage image) {
		int id = imgeDao.updateByPrimaryKey(image);
	}
	
}
