package org.ku8eye.service.k8s;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;

public class K8sAPIService {

	public void checkK8sClusterStatus(String masterURL) {
		Config config = new ConfigBuilder().withMasterUrl(masterURL).build();
		KubernetesClient client = new DefaultKubernetesClient(config);
		System.out.println(client.nodes().list());
	}

	public static void main(String[] args) {
		K8sAPIService service= new K8sAPIService();
		service.checkK8sClusterStatus("http://192.168.18.133:8080");
	}
}