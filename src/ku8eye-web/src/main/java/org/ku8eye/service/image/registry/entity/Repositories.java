package org.ku8eye.service.image.registry.entity;


public class Repositories {
	private String name;
	private String[] tags;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String[] getTags() {
		return tags;
	}
	public void setTags(String[] tags) {
		this.tags = tags;
	}

}
