package org.ku8eye.bean.image;

import org.ku8eye.domain.DockerImage;

public class DockerImageImportBean {
	/**
	 * 标志镜像是否要上传，0不上传，1上传，空则是没定义
	 */
	private String execute="0";
	/**
	 * 标志镜像是否已经存在
	 */
	private boolean exist;
	/**
	 * 任务编号
	 */
	private Integer internalSeq;
	/**
	 * 处理进度
	 */
	private Integer progress = 0;
	/**
	 * 处理结果描述
	 */
	private String processHint;
	/**
	 * 处理结果描述0失败，1成功，
	 */
	private String processResult = "1";
	/**
	 * 解压后的文件夹目录（带压缩文件名的那一层）
	 */
	private String gunzipPath;
	/**
	 * 子文件目录名字（如jre8）
	 */
	private String path;
	/**
	 * 保存的镜像名字
	 */
	private String saveImageName;
	/**
	 * 镜像对象，数据库对象
	 */
	private DockerImage image;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public DockerImage getImage() {
		return image;
	}

	public void setImage(DockerImage image) {
		this.image = image;
	}

	public String getSaveImageName() {
		return saveImageName;
	}

	public void setSaveImageName(String saveImageName) {
		this.saveImageName = saveImageName;
	}

	public String getGunzipPath() {
		return gunzipPath;
	}

	public void setGunzipPath(String gunzipPath) {
		this.gunzipPath = gunzipPath;
	}

	public Integer getInternalSeq() {
		return internalSeq;
	}

	public void setInternalSeq(Integer internalSeq) {
		this.internalSeq = internalSeq;
	}

	public Integer getProgress() {
		return progress;
	}

	public void setProgress(Integer progress) {
		this.progress = progress;
	}

	public String getProcessHint() {
		return processHint;
	}

	public void setProcessHint(String processHint) {
		this.processHint = processHint;
	}

	public boolean isExist() {
		return exist;
	}

	public void setExist(boolean exist) {
		this.exist = exist;
	}

	public String getExecute() {
		return execute;
	}

	public void setExecute(String execute) {
		this.execute = execute;
	}

	public String getProcessResult() {
		return processResult;
	}

	public void setProcessResult(String processResult) {
		this.processResult = processResult;
	}
}
