package org.ku8eye.ctrl.com;

import java.io.File;

import org.apache.log4j.Logger;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.bind.annotation.RestController;

/**
 * 获取外部资源文件，如图片列表，Docker镜像等
 * 
 * @author wuzhih
 *
 */
@RestController
@ConfigurationProperties(prefix = "ku8")
public class ExternalFileConroller {
	private Logger LOGGER = Logger.getLogger(ExternalFileConroller.class);
	private String uploadedDockerImagesPath;
	private String uploadedGZFilePath;
	private String externalRes;
	private String uploadedPicturePath;

	public void setUploadedPicturePath(String uploadedPicturePath) {
		this.uploadedPicturePath = uploadedPicturePath;

	}

	public void setExternalRes(String externalRes) {
		String prex = "file:";
		if (!externalRes.startsWith(prex)) {
			throw new java.lang.RuntimeException(
					"invalid externalRes properties " + externalRes);
		}
		externalRes = externalRes.substring(prex.length());
		this.externalRes = externalRes;
		makeSureDirs(externalRes);
	}

	public void setUploadedDockerImagesPath(String uploadedImagesPath) {
		this.uploadedDockerImagesPath = uploadedImagesPath;

	}

	public void setUploadedGZFilePath(String uploadedGZFilePath) {
		this.uploadedGZFilePath = uploadedGZFilePath;

	}

	private void makeSureDirs(String dir) {
		File rootFile = new File(dir);
		LOGGER.info("make sure dirs exist " + rootFile.getAbsolutePath());
		if (!rootFile.exists()) {
			rootFile.mkdirs();
		}
	}

}
