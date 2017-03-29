package io.ku8.docker.registry;

import java.util.List;

public class ImageManifest {
	private String name;
	private String tag;
	private String architecture = "amd64";
	private FsLayer[] fsLayers;
	private V1LayerJson[] history;
	private int schemaVersion = 1;
	private List<JWSSignature> signatures;
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getArchitecture() {
		return architecture;
	}

	public void setArchitecture(String architecture) {
		this.architecture = architecture;
	}

	

	public FsLayer[] getFsLayers() {
		return fsLayers;
	}

	public void setFsLayers(FsLayer[] fsLayers) {
		this.fsLayers = fsLayers;
	}

	public V1LayerJson[] getHistory() {
		return history;
	}

	public void setHistory(V1LayerJson[] history) {
		this.history = history;
	}

	public int getSchemaVersion() {
		return schemaVersion;
	}

	public void setSchemaVersion(int schemaVersion) {
		this.schemaVersion = schemaVersion;
	}

	public List<JWSSignature> getSignatures() {
		return signatures;
	}

	public void setSignatures(List<JWSSignature> signatures) {
		this.signatures = signatures;
	}

	
}
