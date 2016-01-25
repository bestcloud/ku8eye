package org.ku8eye.ctrl;

/**
 * for docker image management 
 */
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.ku8eye.bean.GridData;
import org.ku8eye.bean.Ku8PackageImportProcess;
import org.ku8eye.domain.DockerImage;
import org.ku8eye.service.ImageService;
import org.ku8eye.service.image.DockerImageImportBean;
import org.ku8eye.service.image.ImageRegistry;
import org.ku8eye.service.image.ImportImageResult;
import org.ku8eye.service.image.util.DBOperator;
import org.ku8eye.util.SystemUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class ImageController {

	private Logger log = Logger.getLogger(this.toString());
	@Autowired
	private ImageService imageService;
	@Autowired
	private ImageRegistry imageRegistry;

	public void setImageService(ImageService imageService) {
		this.imageService = imageService;
	}

	public void setImageRegistry(ImageRegistry imageRegistry) {
		this.imageRegistry = imageRegistry;
	}

	static final ObjectMapper om = new ObjectMapper();

	/**
	 * 列出数据库中私库地址
	 * 
	 * @param request
	 * @param clusterId
	 * @return
	 */
	@RequestMapping(value = "/dockerimg/validateRgistry")
	public List<String> listRgistry(HttpServletRequest request,
			@RequestParam("clusterId") int clusterId) {
		List<String> list = null;
		try {
			list = DBOperator.getRegistryUrl(clusterId);

		} catch (Exception e) {
			log.error(e);
		}
		return list;
	}

	/**
	 * 增加私库地址到数据库
	 * 
	 * @param request
	 * @param clusterId
	 * @param url
	 * @return
	 */
	@RequestMapping(value = "/dockerimg/addRgistry")
	public String addRgistry(HttpServletRequest request,
			@RequestParam("clusterId") int clusterId,
			@RequestParam("url") String url) {
		String re = null;
		try {
			re = DBOperator.addRgistry(clusterId, url);
		} catch (Exception e) {
			log.error(e);
			return e.getMessage();
		}
		return re;
	}

	/**
	 * 解析.tar.gz文件，解压并解析文件
	 * 
	 * @param request
	 * @param clusterId
	 * @param imagePackageFile
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/dockerimg/parsegz")
	public String parseGzFile(HttpServletRequest request,
			@RequestParam("clusterId") int clusterId,
			@RequestParam("imagePackageFile") String imagePackageFile)
			throws Exception {
		List<DockerImageImportBean> dockerImageImportBeanList = new ArrayList<DockerImageImportBean>();
		try {
			String[] dbArray = DBOperator.getDBInfo(clusterId);
			if (dbArray[0].isEmpty()) {
				String err = "err:no registry info in table ku8s_srv_endpoint";
				log.error(err);
				return err;
			}
			int registryId = Integer.parseInt(dbArray[0]);
			String registryUrl = dbArray[1];

			Properties props = SystemUtil.getSpringAppProperties();
			String externalRes = props.getProperty("ku8.externalRes");
			String prex = "file:";
			externalRes = externalRes.substring(prex.length());
			dockerImageImportBeanList = ImageRegistry
					.unZipAndParseKu8ImagePackage(externalRes + File.separator
							+ props.getProperty("ku8.uploadedDockerImagesPath")
							+ File.separator + imagePackageFile, externalRes,
							registryId, registryUrl, clusterId);
		} catch (Exception e) {
			log.error(e);
			return e.getMessage();
		}

		if (dockerImageImportBeanList.size() > 0) {
			imageRegistry.existImage(dockerImageImportBeanList);
		} else {
			String erro = "json file erro, check your json file";
			log.error(erro);
			return erro;
		}
		request.getSession().setAttribute("dockerImageImportBeanList",
				dockerImageImportBeanList);
		return "SUCCESS:";
	}

	/**
	 * 获取session中的要处理的对象dockerImageImportBeanList
	 * 
	 * @param request
	 * @param clusterId
	 * @param imagePackageFile
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/dockerimg/getImageImportBeanList")
	public List<DockerImageImportBean> getDockerImageImportBeanList(
			HttpServletRequest request) throws Exception {
		@SuppressWarnings("unchecked")
		List<DockerImageImportBean> dockerImageImportBeanList = (List<DockerImageImportBean>) (request
				.getSession().getAttribute("dockerImageImportBeanList"));
		if (dockerImageImportBeanList == null) {
			return null;
		}
		return dockerImageImportBeanList;
	}

	/**
	 * 导入镜像
	 * 
	 * @param request
	 * @param dockerImageImportBeanList
	 */
	@RequestMapping(value = "/dockerimg/importimg")
	public String startImport(HttpServletRequest request,
			@RequestParam("excuteInternalSeq") String excuteInternalSeq) {
		try {
			@SuppressWarnings("unchecked")
			List<DockerImageImportBean> dockerImageImportBeanList = (List<DockerImageImportBean>) (request
					.getSession().getAttribute("dockerImageImportBeanList"));
			if ("all".equals(excuteInternalSeq)) {
				for (DockerImageImportBean dockerImageImportBean : dockerImageImportBeanList) {
					dockerImageImportBean.setExecute("1");
				}
			} else {
				String[] split = excuteInternalSeq.split(",");
				for (int i = 0; i < split.length; i++) {
					for (DockerImageImportBean dockerImageImportBean : dockerImageImportBeanList) {
						if (split[i].equals(dockerImageImportBean
								.getInternalSeq() + "")) {
							dockerImageImportBean.setExecute("1");
							break;
						}
					}
				}
			}

			Ku8PackageImportProcess ku8PackageImportProcess = new Ku8PackageImportProcess(
					imageRegistry, dockerImageImportBeanList);
			ku8PackageImportProcess.start();
			request.getSession().setAttribute("imageimportprocess",
					ku8PackageImportProcess);
		} catch (Exception e) {
			log.error(e);
			return "success:";
		}
		return "SUCCESS:";
	}

	/**
	 * 查看导入线程的运行状态
	 * 
	 * @param request
	 * @return
	 * @throws InterruptedException
	 */
	@RequestMapping(value = "/dockerimg/importstatus")
	public ImportImageResult getImagesImportStatus(HttpServletRequest request)
			throws InterruptedException {
		Ku8PackageImportProcess ku8PackageImportProcess = ((Ku8PackageImportProcess) request
				.getSession().getAttribute("imageimportprocess"));
		if (ku8PackageImportProcess == null) {
			return null;
		}
		Collection<DockerImageImportBean> dockerImages = ku8PackageImportProcess
				.getDockerImages();
		ImportImageResult importImageResult = new ImportImageResult();
		importImageResult.setFinish(ku8PackageImportProcess.isFinished());
		boolean success = ku8PackageImportProcess.isSuccess() == false ? false
				: true;
		importImageResult.setSuccess(success);
		importImageResult.setDockerImages(dockerImages);

		if (ku8PackageImportProcess.isFinished()) {
			log.info("push image "
					+ (ku8PackageImportProcess.isSuccess() == false ? "fail"
							: "success"));
			if(!dockerImages.isEmpty()){
			}
		}
		return importImageResult;
	}

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

	/**
	 * 新增镜像
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/dockerimg/create")
	public String createImage(HttpServletRequest request) throws Exception {
		DockerImage dkImg = new DockerImage();
		dkImg.setBuildFile(request.getParameter("addbuild_file"));
		Properties props = SystemUtil.getSpringAppProperties();
		String externalRes = props.getProperty("ku8.uploadedPicturePath");
		dkImg.setImageIconUrl(externalRes + File.separator
				+ request.getParameter("addImageUrl"));
		dkImg.setPublicImage(new Byte(request.getParameter("addpublicImage")));
		dkImg.setSize(Integer.parseInt(request.getParameter("size")) * 1024);
		dkImg.setVersionType(new Byte(request.getParameter("addversionType")));
		dkImg.setTitle(request.getParameter("addtitle"));
		dkImg.setVersion(request.getParameter("addversion"));
		dkImg.setImageName(request.getParameter("addImageName"));
		dkImg.setStatus(new Byte("0"));// 状态默认为0，代表有效
		dkImg.setCategory(request.getParameter("addcategory"));
		dkImg.setRegistryId(Integer.parseInt(request
				.getParameter("addpublicImage")));
		dkImg.setClusterId(1);
		dkImg.setAutoBuildCommand(request.getParameter("addauto_build_command"));
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		dkImg.setLastUpdated(curDate);
		List<DockerImageImportBean> dockerImageImportBeanList = new ArrayList<DockerImageImportBean>();
		DockerImageImportBean dockerImageImportBean = new DockerImageImportBean();
		imageRegistry.getRegistry(dkImg);
		dockerImageImportBean.setImage(dkImg);
		dockerImageImportBean.setInternalSeq(0);
		dockerImageImportBean.setSaveImageName(request
				.getParameter("addPathUrl"));
		dockerImageImportBeanList.add(dockerImageImportBean);
		imageRegistry.existImage(dockerImageImportBeanList);
		request.getSession().setAttribute("dockerImageImportBeanList",
				dockerImageImportBeanList);
		if (dockerImageImportBean.isExist()) {
			return "EXIST";
		} else {
			startImport(request, "all");
			return "SUCCESS:";
		}

	}

	/**
	 * 上传图片
	 * 
	 * @param request
	 * @param files
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/dockerimg/upload-picture")
	public String uploadPicture(HttpServletRequest request,
			@RequestParam(value = "file") MultipartFile[] files)
			throws IOException {
		Properties props = SystemUtil.getSpringAppProperties();
		String externalRes = props.getProperty("ku8.externalRes");
		String prex = "file:";
		externalRes = externalRes.substring(prex.length());
		String savePath = externalRes + File.separator
				+ props.getProperty("ku8.uploadedPicturePath");

		DiskFileItemFactory fac = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(fac);
		upload.setHeaderEncoding("utf-8");
		for (MultipartFile mf : files) {
			if (!mf.isEmpty()) {
				File file = new File(savePath + File.separator
						+ mf.getOriginalFilename());

				InputStream inputStream = mf.getInputStream();
				OutputStream outputStream = new FileOutputStream(file);

				// int bytesWritten = 0;
				int byteCount = 0;

				byte[] bytes = new byte[1024 * 1024];

				while ((byteCount = inputStream.read(bytes)) != -1) {
					outputStream.write(bytes, 0, byteCount);
					// bytesWritten += byteCount;
				}
				inputStream.close();
				outputStream.close();
			}
		}
		return "ok";
	}

	/**
	 * 上传镜像文件
	 * 
	 * @param request
	 * @param files
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/dockerimg/upload-image")
	public String uploadImage(HttpServletRequest request) throws IOException {
		Properties props = SystemUtil.getSpringAppProperties();
		String externalRes = props.getProperty("ku8.externalRes");
		String prex = "file:";
		externalRes = externalRes.substring(prex.length());
		String savePath = externalRes + File.separator
				+ props.getProperty("ku8.uploadedDockerImagesPath");
		String filename = request.getParameter("name");
		File file = new File(savePath + File.separator + filename);
		File fileTmp = new File(savePath +File.separator+ filename+".tmp");

		InputStream inputStream = null;
		OutputStream outputStream = null;
		try {
			inputStream = request.getInputStream();
			outputStream = new FileOutputStream(fileTmp);

			// int bytesWritten = 0;
			int byteCount = 0;

			byte[] bytes = new byte[1024 * 1024];

			while ((byteCount = inputStream.read(bytes)) != -1) {
				outputStream.write(bytes, 0, byteCount);
				// bytesWritten += byteCount;
			}

			file.delete();
		} catch (IOException ioe) {
			log.error(ioe);
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
			if (outputStream != null) {
				outputStream.close();
			}
			fileTmp.renameTo(file);
			fileTmp.delete();
		}
		
		return "ok";
	}

	/**
	 * 上传镜像文件
	 * 
	 * @param request
	 * @param files
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/dockerimg/upload-gzFile")
	public String uploadGzFile(HttpServletRequest request) throws IOException {
		Properties props = SystemUtil.getSpringAppProperties();
		String externalRes = props.getProperty("ku8.externalRes");
		String prex = "file:";
		externalRes = externalRes.substring(prex.length());
		String savePath = externalRes + File.separator
				+ props.getProperty("ku8.uploadedGZFilePath");
		String filename = request.getParameter("name");
		File file = new File(savePath + File.separator + filename);
		File fileTmp = new File(savePath +File.separator+ filename+".tmp");

		InputStream inputStream = null;
		OutputStream outputStream = null;
		try {
			inputStream = request.getInputStream();
			outputStream = new FileOutputStream(fileTmp);

			// int bytesWritten = 0;
			int byteCount = 0;

			byte[] bytes = new byte[1024 * 1024];

			while ((byteCount = inputStream.read(bytes)) != -1) {
				outputStream.write(bytes, 0, byteCount);
				// bytesWritten += byteCount;
			}

			file.delete();
		} catch (IOException ioe) {
			log.error(ioe);
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
			if (outputStream != null) {
				outputStream.close();
			}
			fileTmp.renameTo(file);
			fileTmp.delete();
		}
		
		return "ok";
	}
	
}
