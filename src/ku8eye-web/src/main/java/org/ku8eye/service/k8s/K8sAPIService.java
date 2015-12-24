package org.ku8eye.service.k8s;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeList;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.ReplicationController;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;

public class K8sAPIService {
	private Logger LOGGER = Logger.getLogger(K8sAPIService.class);
	@Autowired
	private SqlSessionFactoryBean sqlSessionFactory;
	private volatile Map<Integer, KubernetesClient> cachedK8sClient = new HashMap<Integer, KubernetesClient>();

	public void setSqlSessionFactory(SqlSessionFactoryBean sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
	}

	public List<String> getK8sAliveNodes(int clusterId) {
		List<String> nodeIps = new LinkedList<String>();
		KubernetesClient client = getClient(clusterId);
		NodeList nodeList = client.nodes().list();
		for (Node node : nodeList.getItems()) {
			System.out.println(node.getMetadata().getName());
			nodeIps.add(node.getSpec().toString());
		}
		return nodeIps;

	}

	public KubernetesClient getClient(int clusterId) {
		KubernetesClient client = cachedK8sClient.get(clusterId);
		if (client == null) {
			String masterURL = fetchMasterURLFromDB(clusterId);
			if (masterURL != null) {
				Config config = new ConfigBuilder().withMasterUrl(masterURL).build();
				client = new DefaultKubernetesClient(config);
				cachedK8sClient.put(clusterId, client);
			}

		}
		if (client == null) {
			throw new java.lang.RuntimeException("can't create KubernetesClient for cluster " + clusterId);
		} else {
			return client;
		}
	}

	private String fetchMasterURLFromDB(int clusterId) {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.getObject().openSession(true);
			Statement stmt = session.getConnection().createStatement();
			ResultSet rs = stmt
					.executeQuery("select SERVICE_URL from ku8s_srv_endpoint where CLUSTER_ID =" + clusterId);
			if (rs.next()) {
				return rs.getString(1);
			}
		} catch (Exception e) {
			LOGGER.warn("sql exe err :" + e);
		} finally {
			if (session != null) {
				session.close();
			}
		}
		LOGGER.warn("cant' find master url for cluster " + clusterId);
		return null;
	}

	public Pod buildPod() {
		// Pod newPod=new
		// PodBuilder().withNewMetadata().withName("nginx-controller").addToLabels("server",
		// "nginx").endMetadata()
		// .withNewSpec().addToContainers(xxxx)
		return null;

	}

	public void createPod(int clusterId, String namespace, Pod thePod) {

	}

	public ReplicationController buildRC() {
		// Pod newPod=new
		// PodBuilder().withNewMetadata().withName("nginx-controller").addToLabels("server",
		// "nginx").endMetadata()
		// .withNewSpec().addToContainers(xxxx)
		return null;

	}

	public void createRC(int clusterId, String namespace, ReplicationController theRC) {

	}

	public static void main(String[] args) {
		K8sAPIService service = new K8sAPIService() {
			public KubernetesClient getClient(int clusterId) {
				Config config = new ConfigBuilder().withMasterUrl("http://192.168.18.133:8080").build();
				return new DefaultKubernetesClient(config);
			}
		};
		for (String ip : service.getK8sAliveNodes(1)) {
			System.out.println(ip);
		}
	}
}