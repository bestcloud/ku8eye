package io.ku8.docker.registry;

import java.util.Map;

import org.codehaus.jackson.annotate.JsonProperty;


public class V1JsonObj {
	private String id;
	private String parent;
	private String created;
	private String container;
	private String docker_version;
	private Map<String, Object> config;
	private Map<String, Object> container_config;
	private String architecture;
	private String os;
	private String comment;
	@JsonProperty(value="Size")
	private int size;
	private String author;
	private String parent_id;//++
	private String layer_id;//++

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public String getContainer() {
		return container;
	}

	public Map<String, Object> getContainer_config() {
		return container_config;
	}

	public void setContainer_config(Map<String, Object> container_config) {
		this.container_config = container_config;
	}

	public void setContainer(String container) {
		this.container = container;
	}

	public String getDocker_version() {
		return docker_version;
	}

	public void setDocker_version(String docker_version) {
		this.docker_version = docker_version;
	}

	public Map<String, Object> getConfig() {
		return config;
	}

	public void setConfig(Map<String, Object> config) {
		this.config = config;
	}

	public String getArchitecture() {
		return architecture;
	}

	public void setArchitecture(String architecture) {
		this.architecture = architecture;
	}

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getParent_id() {
		return parent_id;
	}

	public void setParent_id(String parent_id) {
		this.parent_id = parent_id;
	}

	public String getLayer_id() {
		return layer_id;
	}

	public void setLayer_id(String layer_id) {
		this.layer_id = layer_id;
	}

}
