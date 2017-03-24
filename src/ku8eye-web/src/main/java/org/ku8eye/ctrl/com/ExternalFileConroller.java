package org.ku8eye.ctrl.com;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.ku8eye.Constants;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
			fileUrls.add(Constants.EXTERNAL_URL_ROOT + "/"
					+ uploadedPicturePath + "/" + file.getName());
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
		String[] extNames = { ".img", ".tar", ".tar.gz" };
		Map<String, Integer> resultFiles = new LinkedHashMap<String, Integer>();
		File rootFile = new File(externalRes, uploadedDockerImagesPath);
		List<File> matchedFiles = filerFiles(rootFile, extNames);
		for (File file : matchedFiles) {
			String fileName = file.getName();
			int size = ((int) (file.length() / 1024 / 1024));
			size = size == 0 ? 1 : size;
			resultFiles.put(fileName, size);
		}
		return resultFiles;
	}

	/**
	 * 
	 * @return map of file name and size
	 */
	@RequestMapping(value = "/extresources/listuploadedGzs")
	public Map<String, Integer> getUploadedGzList() {
		String[] extNames = { ".tar.gz" };
		Map<String, Integer> resultFiles = new LinkedHashMap<String, Integer>();
		File rootFile = new File(externalRes, uploadedGZFilePath);
		List<File> matchedFiles = filerFiles(rootFile, extNames);
		for (File file : matchedFiles) {
			String fileName = file.getName();
			resultFiles.put(fileName, (int) (file.length() / 1024 / 1024));
		}
		return resultFiles;
	}

	private List<FileDetail> listFiles(File dir) {
		List<FileDetail> results = new LinkedList<FileDetail>();
		File[] files = dir.listFiles();

		for (File file : files) {
			FileDetail fileDetail = new FileDetail();
			fileDetail.setName(file.getName());
			fileDetail.setSize(file.length());
			fileDetail.setDate(new SimpleDateFormat("yyyy/MM/dd HH:mm")
					.format(new Date(file.lastModified())));
			if (file.isFile()) {
				long size = file.length() / 1024;
				size = size == 0 ? 1 : size;
				fileDetail.setSize(size);// KB
				String fileName = file.getName();
				String pic = "";
				String tmp = fileName.substring(fileName.lastIndexOf(".") + 1);
				switch (tmp) {
				case "gz":
					pic = "gz.png";
					break;
				case "img":
					pic = "img.png";
					break;
				case "png":
					pic = "pic.png";
					break;
				case "jpg":
					pic = "pic.png";
					break;
				default:
					pic = "other.png";
					break;
				}
				fileDetail.setPic(pic);
				fileDetail.setType("file");
			} else {
				fileDetail.setType("path");
				fileDetail.setPic("file.png");
			}
			results.add(fileDetail);
		}
		return results;
	}

	/**
	 * 列出指定目录下的文件和目录
	 * 
	 * @param path
	 * @return
	 */
	@RequestMapping(value = "/extresources/listfiles")
	public ReturnObject getFilesList(@RequestParam("path") String path) {
		File listPath = new File(path);

		ReturnObject re = new ReturnObject();
		re.setErr("0");
		if (listPath.isFile()) {
			String erro = path + " is a file";
			LOGGER.error(erro);
			re.setErr("1");
			re.setMsg(erro);
			return re;
		}
		if (!listPath.exists()) {
			String erro = path + " no exist";
			LOGGER.error(erro);
			re.setErr("1");
			re.setMsg(erro);
			return re;
		}
		List<FileDetail> reFiles = listFiles(listPath);
		re.setResult(reFiles);
		return re;
	}

	/**
	 * 列出指定目录下的文件和目录
	 * 
	 * @param path
	 * @return
	 */
	@RequestMapping(value = "/extresources/deleteFileOrPath")
	public ReturnObject deletePathOrFile(
			@RequestParam("pathOrFile") String pathOrFile) {
		File delete = new File(pathOrFile);

		ReturnObject re = new ReturnObject();
		re.setErr("0");
		if (!delete.exists()) {
			String erro = pathOrFile + " no exist";
			LOGGER.error(erro);
			re.setErr("1");
			re.setMsg(erro);
			return re;
		}
		try {
			if (delete.isDirectory()) {
				FileUtils.deleteDirectory(delete);
			} else {
				delete.delete();
			}
			re.setMsg("deleted");
		} catch (IOException e) {
			LOGGER.error(e);
			e.printStackTrace();
			re.setErr("1");
			re.setMsg("fail to delete " + pathOrFile);
		}
		return re;
	}

	/**
	 * 上传镜像文件
	 * 
	 * @param request
	 * @param files
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/extresources/upload-file")
	public String uploadFile(HttpServletRequest request) throws IOException {
		String filename = request.getParameter("name");
		String path = request.getParameter("path");
		File savePath = new File(path);
		if (!savePath.exists()) {
			savePath.mkdirs();
		}
		File file = new File(path + File.separator + filename);
		File fileTmp = new File(path + File.separator + filename + ".tmp");
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
			LOGGER.error(ioe);
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
