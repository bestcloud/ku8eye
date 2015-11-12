package org.ku8eye.bean;

/**
 * all supported ku8 service
 * 
 * @author wuzhih
 *
 */
public class Ku8ServiceBean {
	private final int serviceType;
	private final String serviceName;
	public static final int SERVICE_STATUS_OK=1;
	public static final int SERVICE_STATUS_ERR=-1;
	public static final int KU8_SERVICE_ETCD = 1;
	public static final int KU8_SERVICE_API_SERVICE = 2;
	public static final int KU8_SERVICE_KUBELE = 3;
	public static final int KU8_SERVICE_DOCKER_REGISTRY = 4;
	public static final Ku8ServiceBean ETCD_SERVICE = new Ku8ServiceBean(KU8_SERVICE_ETCD, "Etcd Service");
	public static final Ku8ServiceBean KU8_API_SERVICE = new Ku8ServiceBean(KU8_SERVICE_API_SERVICE, "K8s API Service");
	public static final Ku8ServiceBean KU8_KUBELETE_SERVICE = new Ku8ServiceBean(KU8_SERVICE_KUBELE,
			"K8s Kubelet Service");
	public static final Ku8ServiceBean KU8_DOCKER_REGISTRY_SERVICE = new Ku8ServiceBean(KU8_SERVICE_DOCKER_REGISTRY,
			"Docker Registry Service");

	public Ku8ServiceBean(int serviceType, String serviceName) {
		super();
		this.serviceType = serviceType;
		this.serviceName = serviceName;
	}

	public int getServiceType() {
		return serviceType;
	}

	public String getServiceName() {
		return serviceName;
	}

	@Override
	public String toString() {
		return "Ku8ServiceBean [serviceType=" + serviceType + ", serviceName=" + serviceName + "]";
	}

}
