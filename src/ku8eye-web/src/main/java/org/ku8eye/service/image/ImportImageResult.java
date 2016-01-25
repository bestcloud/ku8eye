package org.ku8eye.service.image;

import java.util.Collection;

public class ImportImageResult {
	/**
	 * 处理结束
	 */
	private boolean finish;
	/**
	 * 处理成功。总体的
	 */
	private boolean success;
	/**
	 * 每个任务的具体对象，包含处理属性和数据库属性
	 */
	private Collection<DockerImageImportBean> dockerImages;

	public boolean isFinish() {
		return finish;
	}

	public void setFinish(boolean finish) {
		this.finish = finish;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public Collection<DockerImageImportBean> getDockerImages() {
		return dockerImages;
	}

	public void setDockerImages(Collection<DockerImageImportBean> dockerImages) {
		this.dockerImages = dockerImages;
	}
}
