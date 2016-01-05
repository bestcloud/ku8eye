package org.ku8eye.ctrl.com;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.ku8eye.Constants;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.bind.annotation.RequestMapping;
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
	private String uploadedDockerImagesPath;
	private String externalRes;
	private String uploadedPicturePath;

	public void setUploadedPicturePath(String uploadedPicturePath) {
		this.uploadedPicturePath = uploadedPicturePath;

	}

	public void setExternalRes(String externalRes) {
		this.externalRes = externalRes;
		makeSureDirs(externalRes);
	}

	public void setUploadedDockerImagesPath(String uploadedImagesPath) {
		this.uploadedDockerImagesPath = uploadedImagesPath;

	}

	private void makeSureDirs(String dir) {
		File rootFile = new File(dir);
		if (!rootFile.exists()) {
			rootFile.mkdirs();
		}
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/extresources/listlogosurl")
	public List<String> getLogoPicUrls() {
		String[] extNames = { ".png", ".jpg", ".bmp", ".jpeg", ".gif" };
		File rootFile = new File(externalRes, uploadedPicturePath);
		List<File> matchedFiles = filerFiles(rootFile, extNames);
		List<String> fileUrls = new ArrayList<String>(matchedFiles.size());
		for (File file : matchedFiles) {
			fileUrls.add(Constants.EXTERNAL_URL_ROOT + "/" + uploadedPicturePath + "/" + file.getName());
		}
		return fileUrls;
	}

	private List<File> filerFiles(File dir, String[] extNames) {
		List<File> results = new LinkedList<File>();
		File[] files = dir.listFiles();

		for (File file : files) {
			String fileName = file.getName();
			if (file.isFile()) {
				boolean matched = false;
				for (String ext : extNames) {
					if (fileName.endsWith(ext)) {
						matched = true;
						break;
					}
				}
				if (matched) {
					results.add(file);

				}

			}
		}
		return results;
	}

	/**
	 * 
	 * @return map of file name and size
	 */
	@RequestMapping(value = "/extresources/listuploadedimages")
	public Map<String, Integer> getUploadedImagesList() {
		String[] extNames = { ".gz", ".tar" };
		Map<String, Integer> resultFiles = new LinkedHashMap<String, Integer>();
		File rootFile = new File(externalRes, uploadedDockerImagesPath);
		List<File> matchedFiles = filerFiles(rootFile, extNames);
		for (File file : matchedFiles) {
			String fileName = file.getName();
			resultFiles.put(fileName, (int) (file.length() / 1024 / 1024));
		}
		return resultFiles;
	}

}
