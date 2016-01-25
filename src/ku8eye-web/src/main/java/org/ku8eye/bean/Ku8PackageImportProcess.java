package org.ku8eye.bean;

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
import org.ku8eye.service.image.DockerImageImportBean;
import org.ku8eye.service.image.ImageRegistry;
import org.ku8eye.service.image.util.CommonUtil;
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
			theImage.setProgress(10);
			log.info("load image '" + theImage.getImage().getImageUrl() + "/"
					+ theImage.getImage().getImageName() + ":"
					+ theImage.getImage().getVersion() + "'......");

			if (CommonUtil.isBlank(theImage.getGunzipPath())) {
				Properties props = SystemUtil.getSpringAppProperties();
				String externalRes = props.getProperty("ku8.externalRes");
				String prex = "file:";
				externalRes = externalRes.substring(prex.length());
				theImage.setGunzipPath(externalRes);
				theImage.setPath(props
						.getProperty("ku8.uploadedDockerImagesPath"));
			}
			imageRegeistry.loadDockerImage(
					theImage.getImage().getImageUrl(),
					theImage.getGunzipPath() + File.separator
							+ theImage.getPath() + File.separator
							+ theImage.getSaveImageName(), theImage.getImage()
							.getImageName(), theImage.getImage().getVersion());
			theImage.setProgress(40);
			log.info("push image '" + theImage.getImage().getImageUrl() + "/"
					+ theImage.getImage().getImageName() + ":"
					+ theImage.getImage().getVersion() + "'......");
			imageRegeistry.pushDockerImage(theImage.getImage().getImageUrl(),
					theImage.getImage().getImageName(), theImage.getImage()
							.getVersion());
			theImage.setProgress(90);
			imageRegeistry.deleteDockerImage(theImage.getImage().getImageUrl(),
					theImage.getImage().getImageName(), theImage.getImage()
			.getVersion());
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
					theImage.setProgress(100);
					theImage.setProcessResult("0");
					theImage.setProcessHint("err:" + e.toString());
					log.info("push '" + theImage.getImage().getImageUrl() + "/"
							+ theImage.getImage().getImageName() + ":"
							+ theImage.getImage().getVersion() + "' fail!");
				}
			}

		}
	}
}
