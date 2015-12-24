package org.ku8eye.bean.project;

public class Images {
	String name;
	String version;
	String registry;
	String imageName;
	String command;
	String imagePullPolicy;
	String quotas_limits;
	String quotas_cpu;
	String quotas_memory;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getRegistry() {
		return registry;
	}

	public void setRegistry(String registry) {
		this.registry = registry;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getQuotas_limits() {
		return quotas_limits;
	}

	public void setQuotas_limits(String quotas_limits) {
		this.quotas_limits = quotas_limits;
	}

	public String getQuotas_cpu() {
		return quotas_cpu;
	}

	public void setQuotas_cpu(String quotas_cpu) {
		this.quotas_cpu = quotas_cpu;
	}

	public String getQuotas_memory() {
		return quotas_memory;
	}

	public void setQuotas_memory(String quotas_memory) {
		this.quotas_memory = quotas_memory;
	}
}
