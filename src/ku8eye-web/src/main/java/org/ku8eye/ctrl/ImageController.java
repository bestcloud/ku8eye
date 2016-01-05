package org.ku8eye.ctrl;

/**
 * for docker image management 
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.ku8eye.bean.GridData;
import org.ku8eye.domain.DockerImage;
import org.ku8eye.domain.Host;
import org.ku8eye.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class ImageController extends HttpServlet {

	@Autowired
	private ImageService imageService;
	private Logger log = Logger.getLogger(this.toString());

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

	@RequestMapping(value = "/dockerimg/{dockerId}")
	public DockerImage getProjects(@PathVariable("dockerId") int dockerId) {
		DockerImage images = imageService.getImagesId(dockerId);
		return images;
	}

	@RequestMapping(value = "/dockerimg/create")
	public String createImage(@RequestParam("creteJson") String creatJson,
			@RequestParam("imagefile") String imagefile) throws Exception {
		DockerImage dkImg = om.readValue(creatJson, DockerImage.class);
		return imageService.createImage(dkImg, imagefile);

	}

	@RequestMapping(value = "/dockerimg/uploadImage")
	public void uploadImage(HttpServletRequest request,
			@RequestParam(value = "fileAttach") MultipartFile[] files)throws IOException {

		String savePath = "e://";
		savePath = savePath + "/uploads/resourses/";

		DiskFileItemFactory fac = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(fac);
		upload.setHeaderEncoding("utf-8");
		List fileList = null;
		for (MultipartFile mf : files) {
			if (!mf.isEmpty()) {
				File file = new File("E://def.png");
				InputStream inputStream = mf.getInputStream();
				OutputStream outputStream = new FileOutputStream(file);

				int bytesWritten = 0;
				int byteCount = 0;

				byte[] bytes = new byte[10240000];

				while ((byteCount = inputStream.read(bytes)) != -1) {
					outputStream.write(bytes, bytesWritten, byteCount);
					bytesWritten += byteCount;
				}
				inputStream.close();
				outputStream.close();
			}
		}
	}

}
