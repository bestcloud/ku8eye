package org.ku8eye.ctrl;

/**
 * for docker image management 
 */
import java.util.List;

import org.ku8eye.bean.GridData;
import org.ku8eye.domain.DockerImage;
import org.ku8eye.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class ImageController {

	@Autowired
	private ImageService imageService;

	public void setImageService(ImageService imageService) {
		this.imageService = imageService;
	}

	static final ObjectMapper om = new ObjectMapper();

	@RequestMapping(value = "/dockerimg/search")
	public GridData searchImages(@RequestParam("key") String key) {
		GridData grid = new GridData();
		List<DockerImage> images = imageService.searchImages(key);
		grid.setData(images);
		return grid;
	}

	@RequestMapping(value = "/dockerimg/create")
	public String createImage(@RequestParam("creteJson") String creatJson, @RequestParam("imagefile") String imagefile)
			throws Exception {
		DockerImage dkImg = om.readValue(creatJson, DockerImage.class);
		return imageService.createImage(dkImg, imagefile);

	}

}
