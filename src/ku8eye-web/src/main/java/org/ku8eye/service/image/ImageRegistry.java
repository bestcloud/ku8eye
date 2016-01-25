package org.ku8eye.service.image;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.ku8eye.domain.DockerImage;
import org.ku8eye.service.image.util.CommonUtil;
import org.ku8eye.service.image.util.DBOperator;
import org.ku8eye.service.image.util.GZip;
import org.ku8eye.util.SystemUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerException;

@Service
public class ImageRegistry {
	private static Logger log = Logger.getLogger(ImageRegistry.class);

	DockerClient dockerClient = null;

	public ImageRegistry() throws IOException {
	}

	public void getDockerClient() throws IOException, DockerException {
		if (dockerClient == null) {
			dockerClient = new DefaultDockerClient(SystemUtil
					.getSpringAppProperties()
					.getProperty("docker.rest.api.uri"));
		}
	}

	/**
	 * Load 一个docker image到本地的docker engine里
	 * 
	 * @param registryURL
	 * @param absImageFileName
	 * @param dockerImageName
	 * @param tagName
	 * @throws InterruptedException
	 * @throws DockerException
	 * @throws IOException
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void loadDockerImage(String registryURL, String imageFile,
			String dockerImageName, String tagName) throws

	DockerException, InterruptedException, IOException {
		getDockerClient();
		// dockerClient.importImage(registryURL + "/" + dockerImageName,
		// tagName,
		// new FileInputStream(new File(imageFile)));
		dockerClient.load(registryURL + "/" + dockerImageName + ":" +

		tagName, new FileInputStream(new File(imageFile)));
	}

	/**
	 * push本地docker engine里的某个镜像到远端registryURL中
	 * 
	 * @param registryURL
	 * @param dockerImageName
	 * @param tagName
	 * @throws InterruptedException
	 * @throws DockerException
	 * @throws IOException
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void pushDockerImage(String registryURL, String dockerImageName,
			String tagName) throws DockerException,

	InterruptedException, IOException {
		getDockerClient();
		dockerClient.push(registryURL + "/" + dockerImageName + ":" +

		tagName);
	}

	/**
	 * 删除一个docker registry里的镜像
	 * 
	 * @param registryURL
	 * @param dockerImageName
	 * @param tagName
	 * @throws DockerException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void deleteDockerImage(String registryURL, String dockerImageName,
			String tagName) throws DockerException,

	InterruptedException, IOException {
		getDockerClient();
		try {
			dockerClient.removeImage(registryURL + "/" + dockerImageName

			+ ":" + tagName, true, false);
		} catch (Exception e) {
			if (e.getMessage().indexOf("NoSuchMethodError") >= 0) {
				log.warn(e);
			} else {
				throw e;
			}
		}
	}

	/**
	 * 添加一个镜像到指定的docker registry里
	 * 
	 * @param registryURL
	 * @param absImageFileName
	 * @param dockerImageName
	 * @param tagName
	 * @throws InterruptedException
	 * @throws DockerException
	 * @throws IOException
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void addDockerImage(String registryURL, String imageFile,
			String dockerImageName, String tagName) throws

	DockerException, InterruptedException, IOException {
		getDockerClient();
		this.loadDockerImage(registryURL, imageFile, dockerImageName,

		tagName);
		this.pushDockerImage(registryURL, dockerImageName, tagName);
		this.deleteDockerImage(registryURL, dockerImageName, tagName);
	}

	/**
	 * 添加一个镜像到指定的docker registry里
	 * 
	 * @param imageShell
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void existImage(List<DockerImageImportBean> dockerImageImportBeanList)
			throws Exception {
		DBOperator.exitImage(dockerImageImportBeanList);
	}

	/**
	 * 添加一个镜像到指定的docker registry里
	 * 
	 * @param imageShell
	 * @throws DockerException
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void executeInsertImage(DockerImageImportBean theImage)
			throws DockerException, InterruptedException, IOException,
			ClassNotFoundException, SQLException {
		DBOperator.executeInsertImage(theImage);
	}

	/**
	 * 更新镜像信息
	 * 
	 * @param theImage
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void executeUpdateImage(DockerImageImportBean theImage)
			throws Exception {
		DBOperator.executeUpdateImage(theImage);
	}

	/**
	 * 获取私库相关信息
	 * 
	 * @param dkImg
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void getRegistry(DockerImage dkImg) throws Exception {
		DBOperator.getRegistry(dkImg);

	}

	/**
	 * 将一个ku8iamge.gz标准压缩包解压缩到ku8_ext_filesPath目录下，并返回压缩包
	 * 
	 * 里的所有DockerImage信息， 图片路径存放在ku8_ext_filesPath 下的logo_pic
	 * 中（DockerImage里存放logo_pic/xxx.png这样的相对路径），image文件存放在
	 * 
	 * docker_images下。 供页面上导入使用，也为了 命令行未来可以并行同时导入多个包
	 * 
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * 
	 * */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public static List<DockerImageImportBean> unZipAndParseKu8ImagePackage(
			String absKu8ImagePackageFile, String ku8_ext_filesPath,
			int registryId, String registryUrl, int clusterId) throws Exception {
		if (!absKu8ImagePackageFile.endsWith(".tar.gz")) {
			String erro = "err:must .tar.gz file,then" + absKu8ImagePackageFile;
			log.error(erro);
			throw new RuntimeException(erro);
		}
		log.info("decompress file......");
		// TODO TEST
		 GZip.unTargzFile(absKu8ImagePackageFile, ku8_ext_filesPath);// 解压文件
		Properties props = SystemUtil.getSpringAppProperties();
		absKu8ImagePackageFile = absKu8ImagePackageFile.replace(".tar.gz", "");
		String name = absKu8ImagePackageFile.substring(absKu8ImagePackageFile
				.lastIndexOf(File.separator) + 1);
		String jsonFilePath = ku8_ext_filesPath + File.separator
				+ props.getProperty("ku8.uploadedGZFilePath") + File.separator
				+ name + File.separator + name + ".json";

		File file = new File(jsonFilePath);
		if (!file.exists()) {
			log.warn("json file don't exit!" + jsonFilePath);
			throw new RuntimeException("miss json file");
		}
		log.info("get json file:" + file.getAbsolutePath());

		ObjectMapper om = new ObjectMapper();
		DockerImageImportBeanArray images = om.readValue(file,
				DockerImageImportBeanArray.class);
		if (images == null || images.getImageShell() == null) {
			return null;
		}
		List<DockerImageImportBean> imageShellList = Arrays.asList(images
				.getImageShell());
		String uploadedPicturePath = ku8_ext_filesPath + File.separator
				+ props.getProperty("ku8.uploadedPicturePath");
		String uploadedImagePath = ku8_ext_filesPath + File.separator
				+ props.getProperty("ku8.uploadedDockerImagesPath");

		for (int i = 0; i < imageShellList.size(); i++) {
			DockerImageImportBean imageShell = imageShellList.get(i);
			imageShell.setInternalSeq(i);
			imageShell.setGunzipPath(ku8_ext_filesPath + File.separator
					+ props.getProperty("ku8.uploadedGZFilePath")
					+ File.separator + name);
			DockerImage dockerImage = imageShell.getImage();
			dockerImage.setRegistryId(registryId);
			dockerImage.setImageUrl(registryUrl);
			dockerImage.setClusterId(clusterId);

			if (dockerImage.getClusterId() == null) {
				dockerImage.setClusterId(1);
			}
			if (CommonUtil.isBlank(imageShell.getPath())
					|| CommonUtil.isBlank(dockerImage.getTitle())
					|| CommonUtil.isBlank(dockerImage.getImageName())
					|| CommonUtil.isBlank(dockerImage.getVersion())
					|| CommonUtil.isBlankByte(dockerImage.getVersionType())
					|| CommonUtil.isBlank(dockerImage.getCategory())
					|| dockerImage.getClusterId() == null) {
				String erro = "deal fail:json file miss value";
				log.error(erro);
				throw new RuntimeException(erro);
			}

			log.info("copy image file");
			// 复制镜像文件到指定目录
			// TODO TEST
			File imageFilefrom = new File(ku8_ext_filesPath + File.separator
					+ props.getProperty("ku8.uploadedGZFilePath")
					+ File.separator + name + File.separator
					+ imageShell.getPath() + File.separator
					+ imageShell.getSaveImageName());
			File iamgeFileto = new File(uploadedImagePath + File.separator
					+ imageShell.getSaveImageName());
			iamgeFileto.getParentFile().mkdirs();
			if (imageFilefrom.exists()) {
				FileCopyUtils.copy(imageFilefrom, iamgeFileto);
			} else {
				String erro = "miss image file:"
						+ imageFilefrom.getAbsolutePath();
				log.warn(erro);
				throw new RuntimeException(erro);
			}

			log.info("copy logo file");
			// 复制图片到指定目录
			// TODO TEST
			File picFilefrom = new File(ku8_ext_filesPath + File.separator
					+ props.getProperty("ku8.uploadedGZFilePath")
					+ File.separator + name + File.separator
					+ imageShell.getPath() + File.separator
					+ dockerImage.getImageIconUrl());
			File picFileto = new File(uploadedPicturePath + File.separator
					+ dockerImage.getImageIconUrl());
			picFileto.getParentFile().mkdirs();
			if (picFilefrom.exists()) {
				FileCopyUtils.copy(picFilefrom, picFileto);
			} else {
				log.warn("miss logo picture:" + picFilefrom.getAbsolutePath());
			}

			if (registryId == 0) {
				imageShell.getImage().setPublicImage(Byte.parseByte("1"));
			} else {
				imageShell.getImage().setPublicImage(Byte.parseByte("0"));
			}

			dockerImage.setImageIconUrl(props
					.getProperty("ku8.uploadedPicturePath")
					+ File.separator
					+ dockerImage.getImageIconUrl());
		}

		return imageShellList;
	}
}
