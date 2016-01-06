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
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.UUID;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.ku8eye.Constants;
import org.ku8eye.bean.GridData;
import org.ku8eye.domain.DockerImage;
import org.ku8eye.domain.Host;
import org.ku8eye.service.ImageService;
import org.ku8eye.util.SystemUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

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
	
	@RequestMapping(value = "/dockerimg/deletedocker/{dockerId}")
	public String deleteDocker(@PathVariable("dockerId") int dockerId) {
		return imageService.deleteImagesId(dockerId);
	}

	@RequestMapping(value = "/dockerimg/create")
	public String createImage(HttpServletRequest request) throws Exception {
		DockerImage dkImg=new DockerImage();
		dkImg.setBuildFile(request.getParameter("addbuild_file"));
		dkImg.setImageIconUrl(request.getParameter("addImageUrl"));
		dkImg.setPublicImage(new Byte(request.getParameter("addpublicImage")));
		dkImg.setSize(Integer.parseInt(request.getParameter("size")) * 1024);
		dkImg.setVersionType(new Byte(request.getParameter("addversionType")));
		dkImg.setTitle(request.getParameter("addtitle"));
		dkImg.setVersion(request.getParameter("addversion"));
		dkImg.setImageName(request.getParameter("addImageName"));
//		dkImg.setStatus(new Byte("0"));
		dkImg.setCategory(request.getParameter("addcategory"));
		dkImg.setRegistryId(Integer.parseInt(request.getParameter("addpublicImage")));
		dkImg.setClusterId(1);
		dkImg.setAutoBuildCommand(request.getParameter("addauto_build_command"));
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		dkImg.setLastUpdated(curDate);
		return imageService.createImage(dkImg);
	}
	

	@RequestMapping(value = "/dockerimg/upload-image")
	public String uploadImage(HttpServletRequest request,
			@RequestParam(value = "fileAttach") MultipartFile[] files)throws IOException {

		String savePath = "/ku8_ext_files/logo_pic/";
		
		
		//this.getClass().getResource(name)
		//String path = this.getClass().getResource("/").getPath();
//		Properties props = SystemUtil.getSpringAppProperties();
//		String extFile = props.getProperty("ku8.externalRes");
//		ResourceHandlerRegistry registry;
//		if (extFile != null) {
//			registry.addResourceHandler(Constants.EXTERNAL_URL_ROOT + "/**").addResourceLocations(extFile + "/");
//			System.out.println("mapping external resources " + extFile);
//			super.addResourceHandlers(registry);
//		}
//		System.out.println("path="+path);
		
		//ClassLoader.getSystemResource("java.jpg");

		DiskFileItemFactory fac = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(fac);
		upload.setHeaderEncoding("utf-8");
		List fileList = null;
		for (MultipartFile mf : files) {
			if (!mf.isEmpty()) {
				File file = new File(savePath+mf.getOriginalFilename());
				
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
		return "ok";
	}

}
