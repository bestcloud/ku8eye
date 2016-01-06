package org.ku8eye.service.image;

import org.ku8eye.domain.DockerImage;

public class ImageShell {
	private String path;
	private String saveImageName;
	private DockerImage image;
	private String registryUrl;
	
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
	public String getRegistryUrl() {
		return registryUrl;
	}
	public void setRegistryUrl(String registryUrl) {
		this.registryUrl = registryUrl;
	}
}
