package org.ku8eye.ctrl;

/**
 * for docker image management 
 */
import java.util.List;

import org.apache.log4j.Logger;
import org.ku8eye.bean.GridData;
import org.ku8eye.domain.DockerImage;
import org.ku8eye.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class ImageController {

	private Logger log = Logger.getLogger(this.toString());
	@Autowired
	private ImageService imageService;
	@Autowired

	public void setImageService(ImageService imageService) {
		this.imageService = imageService;
	}

	static final ObjectMapper om = new ObjectMapper();


	/**
	 * 查找镜像
	 * 
	 * @param key
	 * @return
	 */
	@RequestMapping(value = "/dockerimg/search")
	public GridData searchImages(@RequestParam("key") String key) {
		GridData grid = new GridData();
		List<DockerImage> images = imageService.searchImages(key);
		grid.setData(images);
		return grid;
	}

	/**
	 * 查询指定id镜像的详细信息
	 * 
	 * @param dockerId
	 * @return
	 */
	@RequestMapping(value = "/dockerimg/{dockerId}")
	public DockerImage getProjects(@PathVariable("dockerId") int dockerId) {
		DockerImage images = imageService.getImagesId(dockerId);
		return images;
	}

	/**
	 * 删除镜像信息
	 * 
	 * @param dockerId
	 * @return
	 */
	@RequestMapping(value = "/dockerimg/deletedocker/{dockerId}")
	public String deleteDocker(@PathVariable("dockerId") int dockerId) {
		return imageService.deleteImagesId(dockerId);
	}
}
