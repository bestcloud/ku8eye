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
import org.ku8eye.bean.image.DockerImageImportBean;
import org.ku8eye.bean.image.ImageResponse;
import org.ku8eye.bean.image.ImportImageResult;
import org.ku8eye.domain.DockerImage;
import org.ku8eye.service.ImageService;
import org.ku8eye.service.image.ImageRegistry;
import org.ku8eye.service.image.registry.RegistryClient;
import org.ku8eye.service.image.registry.entity.Repositories;
import org.ku8eye.service.image.util.CommonUtil;
import org.ku8eye.service.image.util.DBOperator;
import org.ku8eye.util.SystemUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ImageController {

	private Logger log = Logger.getLogger(this.toString());
	private static final String BASEIMAGE = "google_containers";

	@Autowired
	private ImageService imageService;
	@Autowired
	private ImageRegistry imageRegistry;
	@Autowired
	private RegistryClient registryClient;

	public void setRegistryClient(RegistryClient registryClient) {
		this.registryClient = registryClient;
	}

	public void setImageService(ImageService imageService) {
		this.imageService = imageService;
	}

	public void setImageRegistry(ImageRegistry imageRegistry) {
		this.imageRegistry = imageRegistry;
	}

	/**
	 * 查找镜像
	 * 
	 * @param key
	 * @return
	 */
	@RequestMapping(value = "/dockerimg/searchAll")
	public GridData searchImagesAll(@RequestParam("key") String key) {
		GridData grid = new GridData();
		List<DockerImage> images = imageService.searchImages(key);
		grid.setData(images);
		return grid;
	}

	/**
	 * 查找镜像,并在传入参数为空的情况下同步私库中已经存在的数据到数据库中
	 * 
	 * @param key
	 * @return
	 */
	@RequestMapping(value = "/dockerimg/search")
	public ImageResponse searchImages(@RequestParam("key") String key) {
		// 查询私库中的镜像，和现有的数据进行比对，如果没有则将私库中的数据增加到数据库中
		ImageResponse imageResponse = new ImageResponse(0);
		try {
			if (CommonUtil.isBlank(key)) {
				log.info("registry sync");
				int clusterId = 0;
				clusterId = "".equals(clusterId + "") ? 0 : clusterId;
				String[] dbArray = DBOperator.getDBInfo(clusterId);
				if (dbArray[0].isEmpty()) {
					String warn = "warn:库表中没有私库信息，请先设置镜像私库";
					log.warn(warn);
					imageResponse.setStatus(1);
					imageResponse.setMessage(warn);
					return imageResponse;
				}
				int registryId = Integer.parseInt(dbArray[0]);
				String registryUrl = dbArray[1];
				List<Repositories> repositoriesList = new ArrayList<Repositories>();
				try {
					repositoriesList = registryClient.getTags(registryUrl);
				} catch (Exception e) {
					log.error(e);
					imageResponse.setStatus(1);
					imageResponse.setMessage("connection registry "
							+ registryUrl + " " + e.getMessage());
					return imageResponse;
				}
				for (Repositories repositories : repositoriesList) {
					String[] tags = repositories.getTags();
					for (String tag : tags) {
						if (!(imageRegistry.existImage(registryUrl,
								repositories.getName(), tag))) {
							// 将私库信息插入到数据库中
							DockerImage image = new DockerImage();
							image.setImageUrl(registryUrl);
							image.setImageName(repositories.getName());
							image.setVersion(tag);
							image.setRegistryId(registryId);
							image.setClusterId(clusterId);
							image.setStatus(new Byte("0"));// 状态默认为0，代表有效
							image.setAutoBuild(new Byte("0"));
							image.setTitle(repositories.getName() + ":" + tag);
							// title默认的起名方法为镜像名字加对应tag名字
							image.setCategory("no define");
							image.setNote("registry Sync");
							image.setLastUpdated(new Date());
							// 设置图片 repositories.getName()
							if (repositories.getName().indexOf(BASEIMAGE) >= 0) {
								image.setImageIconUrl(SystemUtil
										.getSpringAppProperties().getProperty(
												"ku8.uploadedPicturePath")
										+ "/ku8.png");
							} else {
								image.setImageIconUrl(SystemUtil
										.getSpringAppProperties().getProperty(
												"ku8.uploadedPicturePath")
										+ "/nodef.png");
							}
							imageService.createImage(image);

						}
					}
				}
			}
			List<DockerImage> images = imageService.searchImages(key);
			imageResponse.setData(images);
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
			imageResponse.setStatus(1);
			imageResponse.setMessage("fail to search docker image");
		}

		return imageResponse;
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
	public void deleteDocker(@PathVariable("dockerId") int dockerId) {
		imageService.deleteImagesId(dockerId);
	}

	/**
	 * 新增镜像
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/dockerimg/create")
	public ImageResponse createImage(HttpServletRequest request) {
		ImageResponse imageResponse = new ImageResponse();
		DockerImage dkImg = new DockerImage();

		try {
			String[] pathUrlorSize = request.getParameter("addPathUrl").split(
					",");
			dkImg.setBuildFile(request.getParameter("addbuild_file"));
			Properties props = SystemUtil.getSpringAppProperties();
			String externalRes = props.getProperty("ku8.uploadedPicturePath");
			dkImg.setImageIconUrl(externalRes + File.separator
					+ request.getParameter("addImageUrl"));
			dkImg.setPublicImage(new Byte(request
					.getParameter("addpublicImage")));
			dkImg.setSize(Integer.parseInt(pathUrlorSize[1]) * 1024);
			dkImg.setVersionType(new Byte(request
					.getParameter("addversionType")));
			dkImg.setTitle(request.getParameter("addtitle"));
			dkImg.setVersion(request.getParameter("addversion"));
			dkImg.setImageName(request.getParameter("adddockerName"));
			dkImg.setStatus(new Byte("0"));// 状态默认为0，代表有效
			dkImg.setAutoBuild(new Byte("0"));
			dkImg.setCategory(request.getParameter("addcategory"));
			dkImg.setRegistryId(Integer.parseInt(request
					.getParameter("addpublicImage")));
			dkImg.setClusterId(0);
			// dkImg.setAutoBuildCommand(request
			// .getParameter("addauto_build_command"));
			dkImg.setLastUpdated(new Date());
			List<DockerImageImportBean> dockerImageImportBeanList = new ArrayList<DockerImageImportBean>();
			DockerImageImportBean dockerImageImportBean = new DockerImageImportBean();
			imageRegistry.getRegistry(dkImg);
			if (CommonUtil.isBlank(dkImg.getImageUrl())) {
				String warn = "warn:库表中没有私库信息，请先设置镜像私库";
				log.warn(warn);
				imageResponse.setStatus(1);
				imageResponse.setMessage("error in search docker image");
				return imageResponse;
			}
			dockerImageImportBean.setImage(dkImg);
			dockerImageImportBean.setInternalSeq(0);
			dockerImageImportBean.setSaveImageName(pathUrlorSize[0]);
			dockerImageImportBeanList.add(dockerImageImportBean);
			imageRegistry.existImage(dockerImageImportBeanList);
			request.getSession().setAttribute("dockerImageImportBeanList",
					dockerImageImportBeanList);
			if (dockerImageImportBean.isExist()) {
				imageResponse.setStatus(2);
				imageResponse.setMessage("EXIST");
				return imageResponse;
			} else {
				startImport(request, "all");
				imageResponse.setStatus(0);
				return imageResponse;
			}
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
			imageResponse.setStatus(1);
			imageResponse.setMessage("fail to create docker image");
			return imageResponse;
		}

	}

	/**
	 * 更新镜像的指定信息
	 * 
	 * @param request
	 * @param dockerId
	 * @return
	 */
	@RequestMapping(value = "/dockerimg/update")
	public ImageResponse updateImage(HttpServletRequest request,
			@RequestParam("dockerid") int dockerId) {
		ImageResponse imageResponse = new ImageResponse();
		DockerImage dkImg = new DockerImage();

		try {
			// String[] pathUrlorSize =
			// request.getParameter("addPathUrl").split(",");
			dkImg.setId(dockerId);
			dkImg.setBuildFile(request.getParameter("addbuild_file"));
			Properties props = SystemUtil.getSpringAppProperties();
			String externalRes = props.getProperty("ku8.uploadedPicturePath");
			dkImg.setImageIconUrl(externalRes + File.separator
					+ request.getParameter("addImageUrl"));
			dkImg.setPublicImage(new Byte(request
					.getParameter("addpublicImage")));
			// dkImg.setSize(Integer.parseInt(pathUrlorSize[1]) * 1024);
			dkImg.setVersionType(new Byte(request
					.getParameter("addversionType")));
			dkImg.setTitle(request.getParameter("addtitle"));
			// dkImg.setVersion(request.getParameter("addversion"));
			// dkImg.setImageName(request.getParameter("adddockerName"));
			// dkImg.setStatus(new Byte("0"));// 状态默认为0，代表有效
			dkImg.setCategory(request.getParameter("addcategory"));
			dkImg.setRegistryId(Integer.parseInt(request
					.getParameter("addpublicImage")));
			// dkImg.setClusterId(0);
			dkImg.setAutoBuildCommand(request
					.getParameter("addauto_build_command"));
			dkImg.setLastUpdated(new Date());
			imageService.updateImage(dkImg);
			imageResponse.setStatus(0);
			return imageResponse;
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
			imageResponse.setStatus(1);
			imageResponse.setMessage("fail to update docker image");
			return imageResponse;
		}
	}

	/**
	 * 列出数据库中私库地址，结果按照id倒叙，目的是列出最新被设置的私库信息
	 * 
	 * @param clusterId
	 * @return
	 */
	@RequestMapping(value = "/dockerimg/validateRgistry")
	public ImageResponse listRgistry(@RequestParam("clusterId") int clusterId) {
		ImageResponse imageResponse = new ImageResponse(0);
		List<String> list = null;
		try {
			list = DBOperator.getRegistryUrl(clusterId);
			imageResponse.setData(list);
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
			imageResponse.setStatus(1);
			imageResponse.setMessage("fail to get rgistry url");
		}
		return imageResponse;
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
	public ImageResponse addRgistry(HttpServletRequest request,
			@RequestParam("clusterId") int clusterId,
			@RequestParam("url") String url) {
		ImageResponse imageResponse = new ImageResponse(0);
		try {
			DBOperator.addRegistry(clusterId, url);
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
			imageResponse.setStatus(1);
			imageResponse.setMessage("fail to add rgistry url");
		}
		return imageResponse;
	}

	/**
	 * 解析.tar.gz文件，解压并解析文件
	 * 
	 * @param request
	 * @param clusterId
	 * @param imagePackageFile
	 * @return
	 */
	@RequestMapping(value = "/dockerimg/parsegz")
	public ImageResponse parseGzFile(HttpServletRequest request,
			@RequestParam("clusterId") int clusterId,
			@RequestParam("imagePackageFile") String imagePackageFile) {
		ImageResponse imageResponse = new ImageResponse(0);
		List<DockerImageImportBean> dockerImageImportBeanList = new ArrayList<DockerImageImportBean>();
		try {
			String[] dbArray = DBOperator.getDBInfo(clusterId);
			if (dbArray[0].isEmpty()) {
				String warn = "warn:库表中没有私库信息，请先设置镜像私库";
				log.warn(warn);
				imageResponse.setStatus(1);
				imageResponse.setMessage(warn);
				return imageResponse;
			}
			int registryId = Integer.parseInt(dbArray[0]);
			String registryUrl = dbArray[1];

			Properties props = SystemUtil.getSpringAppProperties();
			String externalRes = props.getProperty("ku8.externalRes");
			String prex = "file:";
			externalRes = externalRes.substring(prex.length());
			dockerImageImportBeanList = ImageRegistry
					.unZipAndParseKu8ImagePackage(externalRes + File.separator
							+ props.getProperty("ku8.uploadedGZFilePath")
							+ File.separator + imagePackageFile, externalRes,
							registryId, registryUrl, clusterId);
			if (dockerImageImportBeanList.size() > 0) {
				imageRegistry.existImage(dockerImageImportBeanList);
			} else {
				String erro = "json file erro, check your json file";
				log.error(erro);
				imageResponse.setStatus(1);
				imageResponse.setMessage(erro);
				return imageResponse;
			}
			request.getSession().setAttribute("dockerImageImportBeanList",
					dockerImageImportBeanList);
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
			imageResponse.setStatus(1);
			imageResponse.setMessage("fail to import images");
		}
		return imageResponse;
	}

	/**
	 * 获取session中的要处理的对象dockerImageImportBeanList
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/dockerimg/getImageImportBeanList")
	public ImageResponse getDockerImageImportBeanList(HttpServletRequest request) {
		@SuppressWarnings("unchecked")
		List<DockerImageImportBean> dockerImageImportBeanList = (List<DockerImageImportBean>) (request
				.getSession().getAttribute("dockerImageImportBeanList"));
		ImageResponse imageResponse = new ImageResponse(0, null,
				dockerImageImportBeanList);
		return imageResponse;
	}

	/**
	 * 开始导入镜像
	 * 
	 * @param request
	 * @param excuteInternalSeq
	 * @return
	 */
	@RequestMapping(value = "/dockerimg/importimg")
	public ImageResponse startImport(HttpServletRequest request,
			@RequestParam("excuteInternalSeq") String excuteInternalSeq) {
		ImageResponse imageResponse = new ImageResponse(0);
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
			e.printStackTrace();
			imageResponse.setStatus(1);
			imageResponse.setMessage("fail to import images");
			return imageResponse;
		}
		return imageResponse;
	}

	/**
	 * 查看导入线程的运行状态
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/dockerimg/importstatus")
	public ImageResponse getImagesImportStatus(HttpServletRequest request) {
		ImageResponse imageResponse = new ImageResponse(0);
		Ku8PackageImportProcess ku8PackageImportProcess = ((Ku8PackageImportProcess) request
				.getSession().getAttribute("imageimportprocess"));
		if (ku8PackageImportProcess == null) {
			String erro = "fail to import images";
			log.error(erro);
			log.error("get imageimportprocess null");
			imageResponse.setStatus(1);
			imageResponse.setMessage(erro);
			return imageResponse;
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
		}
		imageResponse.setData(importImageResult);
		return imageResponse;
	}

	// 以下三个方法为上传文件所需方法
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
		File path = new File(savePath);
		if (!path.exists()) {
			path.mkdirs();
		}
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
		File path = new File(savePath);
		if (!path.exists()) {
			path.mkdirs();
		}
		String filename = request.getParameter("name");
		File file = new File(savePath + File.separator + filename);
		File fileTmp = new File(savePath + File.separator + filename + ".tmp");

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
			inputStream.close();
			outputStream.close();
			fileTmp.renameTo(file);
		} catch (IOException ioe) {
			log.error(ioe);
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
			if (outputStream != null) {
				outputStream.close();
			}
			fileTmp.delete();
		}

		return "ok";
	}

	/**
	 * 上传镜像文件
	 * 
	 * @param request
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
		File path = new File(savePath);
		if (!path.exists()) {
			path.mkdirs();
		}
		String filename = request.getParameter("name");
		File file = new File(savePath + File.separator + filename);
		File fileTmp = new File(savePath + File.separator + filename + ".tmp");

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
			inputStream.close();
			outputStream.close();
			fileTmp.renameTo(file);
		} catch (IOException ioe) {
			log.error(ioe);
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
			if (outputStream != null) {
				outputStream.close();
			}
			fileTmp.delete();
		}

		return "ok";
	}

}
