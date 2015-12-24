package org.ku8eye.bean.project;

import java.util.ArrayList;
import java.util.List;

public class Service {
	private String name;
	private String describe;
	private String tag;
	private String replica;
	private String version;
	private List<Images> image = new ArrayList<Images>();
	private List<Port> ports = new ArrayList<Port>();

	public void addPort(Port p) {
		ports.add(p);
	}

	public List<Port> getPorts() {
		return ports;
	}

	public void setPorts(List<Port> ports) {
		this.ports = ports;
	}

	public void addImage(Images i) {
		image.add(i);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getReplica() {
		return replica;
	}

	public void setReplica(String replica) {
		this.replica = replica;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public List<Images> getImage() {
		return image;
	}

	public void setImage(List<Images> image) {
		this.image = image;
	}
}
