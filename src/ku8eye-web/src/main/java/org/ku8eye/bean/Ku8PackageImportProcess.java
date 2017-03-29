package org.ku8eye.bean;

import io.ku8.docker.registry.DockerRegistry;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.ku8eye.bean.image.DockerImageImportBean;
import org.ku8eye.service.image.ImageRegistry;
import org.ku8eye.service.image.util.GZip;
import org.ku8eye.util.SystemUtil;

public class Ku8PackageImportProcess {
	private static Logger log = Logger.getLogger(Ku8PackageImportProcess.class);
	private ImageRegistry imageRegeistry;
	private Map<Integer, DockerImageImportBean> dockerImages;
	// 存放等待处理的DockerImage
	private Set<Integer> todoTasks = new HashSet<Integer>();
	private boolean success = true;
	private List<DockerImportThread> workThreads = new ArrayList<DockerImportThread>(
			8);

	public Ku8PackageImportProcess(ImageRegistry imageRegeistry,
			List<DockerImageImportBean> allImages) {
		super();
		this.imageRegeistry = imageRegeistry;
		this.dockerImages = new HashMap<Integer, DockerImageImportBean>();
		for (DockerImageImportBean img : allImages) {
			todoTasks.add(img.getInternalSeq());
			dockerImages.put(img.getInternalSeq(), img);
		}
	}

	public Collection<DockerImageImportBean> getDockerImages() {
		return dockerImages.values();
	}

	/**
	 * 启动开始执行镜像导入
	 */
	public void start() {
		for (int i = 0; i < 8; i++) {
			DockerImportThread thred = new DockerImportThread();
			thred.setDaemon(true);
			thred.start();
			workThreads.add(thred);
		}
	}

	public boolean isSuccess() {
		return success;
	}

	public boolean isFinished() {
		for (DockerImportThread tred : workThreads) {
			if (tred.isAlive()) {
				return false;
			}
		}
		return true;
	}

	class DockerImportThread extends Thread {
		DockerRegistry dockerRegistry =null;
		private void importDockerImage(DockerImageImportBean theImage)
				throws Exception {
			if ("0".equals(theImage.getExecute())) {
				if (theImage.isExist()) {
					String re = "image exist,no handle";
					log.warn(theImage.getImage().getImageUrl() + "/"
							+ theImage.getImage().getImageName() + ":"
							+ theImage.getImage().getVersion() + " " + re);
					theImage.setProgress(100);
					theImage.setProcessResult("1");
					theImage.setProcessHint(re);
				} else {
					String re = "skip image,no handle";
					log.warn(theImage.getImage().getImageUrl() + "/"
							+ theImage.getImage().getImageName() + ":"
							+ theImage.getImage().getVersion() + " " + re);
					theImage.setProgress(100);
					theImage.setProcessResult("1");
					theImage.setProcessHint(re);
				}
				return;
			}
			theImage.setProgress(40);
			log.info("push image '" + theImage.getImage().getImageUrl() + "/"
					+ theImage.getImage().getImageName() + ":"
					+ theImage.getImage().getVersion() + "'......");

			Properties props = SystemUtil.getSpringAppProperties();
			String externalRes = props.getProperty("ku8.externalRes");
			String prex = "file:";
			externalRes = externalRes.substring(prex.length());
			theImage.setGunzipPath(externalRes);
			theImage.setPath(props.getProperty("ku8.uploadedDockerImagesPath"));
			dockerRegistry = new DockerRegistry("http://"
					+ theImage.getImage().getImageUrl());
			GZip gzip = new GZip(theImage.getGunzipPath() + File.separator
					+ theImage.getPath() + File.separator
					+ theImage.getSaveImageName());
			File file = new File(theImage.getGunzipPath() + File.separator
					+ theImage.getPath() + File.separator
					+ theImage.getSaveImageName().substring(0,
							theImage.getSaveImageName().indexOf(".")));
			if(theImage.getSaveImageName().endsWith(".tar.gz")){
				gzip.unTargzFile(file.getAbsolutePath());
			}else{
				gzip.unTarFile(file.getAbsolutePath());
			}
			dockerRegistry.pushImage(file, theImage.getImage().getImageName(),
					theImage.getImage().getVersion(), true);
			FileUtils.deleteDirectory(file);

			theImage.setProgress(100);
			// save docker image to database
			log.info("insert db '" + theImage.getImage().getImageUrl() + "/"
					+ theImage.getImage().getImageName() + ":"
					+ theImage.getImage().getVersion() + "'......");
			if (theImage.isExist()) {
				imageRegeistry.executeUpdateImage(theImage);
				String re = "update success";
				log.warn(theImage.getImage().getImageUrl() + "/"
						+ theImage.getImage().getImageName() + ":"
						+ theImage.getImage().getVersion() + " " + re);
				theImage.setProcessResult("1");
				theImage.setProcessHint(re);

			} else {
				imageRegeistry.executeInsertImage(theImage);

				String re = "add success";
				log.warn(theImage.getImage().getImageUrl() + "/"
						+ theImage.getImage().getImageName() + ":"
						+ theImage.getImage().getVersion() + " " + re);
				theImage.setProcessResult("1");
				theImage.setProcessHint(re);

			}
		}

		public void run() {
			while (true) {
				DockerImageImportBean theImage = null;
				Integer theId = null;
				synchronized (todoTasks) {
					if (!todoTasks.isEmpty()) {
						theId = todoTasks.iterator().next();
						todoTasks.remove(theId);
					}
				}
				if (theId == null) {
					// no docker image
					return;
				}
				theImage = dockerImages.get(theId);
				try {
					importDockerImage(theImage);
					log.info("push '" + theImage.getImage().getImageUrl() + "/"
							+ theImage.getImage().getImageName() + ":"
							+ theImage.getImage().getVersion() + "' success!");
				} catch (Exception e) {
					success = false;
					log.error(e);
					e.printStackTrace();
					theImage.setProgress(100);
					theImage.setProcessResult("0");
					theImage.setProcessHint("err:" + e.toString());
					log.info("push '" + theImage.getImage().getImageUrl() + "/"
							+ theImage.getImage().getImageName() + ":"
							+ theImage.getImage().getVersion() + "' fail!");
				}finally{
					if(dockerRegistry!=null){
						dockerRegistry.close();
					}
				}
			}

		}
	}
}
