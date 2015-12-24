package org.ku8eye.service.k8s;

import java.util.LinkedList;
import java.util.List;

import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeList;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;

public class K8sAPIService {

	
	public List<String> getK8sAliveNodes(String masterURL) {
		List<String> nodeIps = new LinkedList<String>();
		Config config = new ConfigBuilder().withMasterUrl(masterURL).build();
		KubernetesClient client = new DefaultKubernetesClient(config);
		NodeList nodeList = client.nodes().list();
		for (Node node : nodeList.getItems()) {
			System.out.println(node.getMetadata().getName());
			nodeIps.add(node.getSpec().toString());
		}
		return nodeIps;

	}

	private  KubernetesClient getClient(int clusterId)
	{
		return null;
	}
	public Pod buildPod() {
		// Pod newPod=new
		// PodBuilder().withNewMetadata().withName("nginx-controller").addToLabels("server",
		// "nginx").endMetadata()
		// .withNewSpec().addToContainers(xxxx)
		return null;

	}

	public void createPod(String namespace, Pod thePod) {

	}

	public static void main(String[] args) {
		K8sAPIService service = new K8sAPIService();
		for (String ip : service.getK8sAliveNodes("http://192.168.18.133:8080")) {
			System.out.println(ip);
		}
	}
}