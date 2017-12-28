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
import org.springframework.stereotype.Service;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.ContainerBuilder;
import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.ContainerPortBuilder;
//import io.fabric8.kubernetes.api.model.DoneablePod;
import io.fabric8.kubernetes.api.model.DoneableReplicationController;
//import io.fabric8.kubernetes.api.model.DoneableService;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeList;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.ReplicationController;
import io.fabric8.kubernetes.api.model.ReplicationControllerBuilder;
import io.fabric8.kubernetes.api.model.ReplicationControllerList;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.ServiceList;
import io.fabric8.kubernetes.api.model.ServicePort;
import io.fabric8.kubernetes.api.model.ServicePortBuilder;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
//import io.fabric8.kubernetes.client.Watch;
//import io.fabric8.kubernetes.client.Watcher;

//import io.fabric8.kubernetes.client.dsl.ClientMixedOperation;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
//import io.fabric8.kubernetes.client.dsl.ClientPodResource;
//import io.fabric8.kubernetes.client.dsl.ClientResource;
import io.fabric8.kubernetes.client.dsl.Resource;
//import io.fabric8.kubernetes.client.dsl.ClientRollableScallableResource;
import io.fabric8.kubernetes.client.dsl.RollableScalableResource;

//import io.fabric8.kubernetes.client.dsl.FilterWatchListDeletable;

@Service
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

	public ReplicationController buildRC(int clusterId, org.ku8eye.bean.project.Service s) {
		
		try
		{
			//Setup for Label and Selector
			Map<String, String> map = new HashMap<String, String>();
			map.put("name", s.getName());
			
			ContainerPort cport = new ContainerPortBuilder()
					.withContainerPort(s.getContainerPort())
					.withProtocol("TCP").build();
			
			Container container = new ContainerBuilder()
					.withName(s.getName())
					.withImage("IMG/URL")
					.withPorts(cport)
					.withEnv(s.getEnvVariables())
					.build();
			
			ReplicationController rc = new ReplicationControllerBuilder().withKind("ReplicationController")
					.withNewMetadata()
					.withName(s.getName()).addToLabels("name", s.getName())
					.withNamespace("default")
					.withLabels(map)
					.endMetadata()
					.withNewSpec()
					.withReplicas(s.getReplica())
					.withSelector(map)
					.withNewTemplate()
					.withNewMetadata()
					.withLabels(map)
					.endMetadata()
					.withNewSpec()
					.withContainers(container)
					.endSpec()
					.endTemplate()
					.endSpec().build();
			
			ReplicationController response_f8RC = createRC(clusterId, "default", rc);
			return response_f8RC;
		}
		catch (KubernetesClientException e)
		{
			LOGGER.error("Create RC failed, " + e);
			return null;
		}
	}
	
	public io.fabric8.kubernetes.api.model.Service buildService(int clusterId, org.ku8eye.bean.project.Service s)
	{
		try
		{
			//Setup Port
			ServicePort port = new ServicePortBuilder().withProtocol("TCP").withNewTargetPort(s.getContainerPort()).withNodePort(s.getNodePort()).withPort(s.getServicePort()).build();
			
			//Setup for Label and Selector
			Map<String, String> map = new HashMap<String, String>();
			map.put("name", s.getName());
			
			io.fabric8.kubernetes.api.model.Service f8Service = new ServiceBuilder().withKind("Service")
					.withNewMetadata()
					.withName(s.getName())
					.withNamespace("default")
					.withLabels(map)
					.endMetadata()
					.withNewSpec()
					.withPorts(port)
					.withType("NodePort")
					.withSelector(map)
					.endSpec().build();
			
			io.fabric8.kubernetes.api.model.Service response_f8Service = createService(clusterId, "default", f8Service);
			return response_f8Service;
		
		}
		catch (KubernetesClientException e)
		{
			LOGGER.error("Create service failed, " + e);
			return null;
		}
	}

	/*private ReplicationController createRC(int clusterId, String namespace, ReplicationController theRC) {
			ClientMixedOperation<
				ReplicationController, 
					ReplicationControllerList, 
						DoneableReplicationController, 
							ClientRollableScallableResource<ReplicationController, DoneableReplicationController>> replicationControllerClient 
								= getClient(clusterId).inNamespace(namespace).replicationControllers();
			return replicationControllerClient.create(theRC);
	}*/
	private ReplicationController createRC(int clusterId, String namespace, ReplicationController theRC) {
		MixedOperation<
			ReplicationController, 
				ReplicationControllerList, 
					DoneableReplicationController, 
						RollableScalableResource<ReplicationController, DoneableReplicationController>> replicationControllerClient 
							= getClient(clusterId).replicationControllers();
		return replicationControllerClient.inNamespace(namespace).create(theRC);
	}
	
	/*public boolean deleteRC(int clusterId, String namespace, String rcName) {
		ClientMixedOperation<
			ReplicationController, 
				ReplicationControllerList, 
					DoneableReplicationController, 
						ClientRollableScallableResource<ReplicationController, DoneableReplicationController>> replicationControllerClient 
							= getClient(clusterId).inNamespace(namespace).replicationControllers();
		ClientResource<
			io.fabric8.kubernetes.api.model.ReplicationController, 
				DoneableReplicationController> clientResource 
					= replicationControllerClient.withName(rcName);
		return clientResource.delete();
	}*/
	public boolean deleteRC(int clusterId, String namespace, String rcName) {
		MixedOperation<
			ReplicationController, 
				ReplicationControllerList, 
					DoneableReplicationController, 
						RollableScalableResource<ReplicationController, DoneableReplicationController>> replicationControllerClient 
							= getClient(clusterId).replicationControllers();
		Resource<
			io.fabric8.kubernetes.api.model.ReplicationController, 
				DoneableReplicationController> clientResource 
					= replicationControllerClient.inNamespace(namespace).withName(rcName);
		return clientResource.delete();
	}
	
	/*public ServiceList getServices(int clusterId, String namespace) {
		ClientMixedOperation<
			io.fabric8.kubernetes.api.model.Service, 
				ServiceList, 
					DoneableService, 
						ClientResource<io.fabric8.kubernetes.api.model.Service, DoneableService>> servicesClient 
							= getClient(clusterId).inNamespace(namespace).services();
		return servicesClient.list();
	}*/
	public ServiceList getServices(int clusterId, String namespace) {
		return getClient(clusterId).services().inNamespace(namespace).list();
	}
	
	/*public ServiceList getServicesByLabelsSelector(int clusterId, String namespace, Map<String, String> labels) {
		ClientMixedOperation<
			io.fabric8.kubernetes.api.model.Service, 
				ServiceList, 
					DoneableService, 
						ClientResource<io.fabric8.kubernetes.api.model.Service, DoneableService>> servicesClient 
							= getClient(clusterId).inNamespace(namespace).services();
		FilterWatchListDeletable<
			io.fabric8.kubernetes.api.model.Service, 
				ServiceList, 
					Boolean, 
						Watch, 
							Watcher<io.fabric8.kubernetes.api.model.Service>> filterWatchListDeletable 
								= servicesClient.withLabels(labels);
		return filterWatchListDeletable.list();
	}*/
	public ServiceList getServicesByLabelsSelector(int clusterId, String namespace, Map<String, String> labels) {
		return getClient(clusterId).services().inNamespace(namespace).withLabels(labels).list();
	}
	
	/*private io.fabric8.kubernetes.api.model.Service createService(int clusterId, String namespace, io.fabric8.kubernetes.api.model.Service service) {
		ClientMixedOperation<
			io.fabric8.kubernetes.api.model.Service, 
				ServiceList, 
					DoneableService, 
						ClientResource<io.fabric8.kubernetes.api.model.Service, DoneableService>> servicesClient 
							= getClient(clusterId).inNamespace(namespace).services();
		return servicesClient.create(service);
	}*/
	public io.fabric8.kubernetes.api.model.Service createService(int clusterId, String namespace, io.fabric8.kubernetes.api.model.Service service) {
		return getClient(clusterId).services().inNamespace(namespace).create(service);
	}
	
	/*public boolean deleteService(int clusterId, String namespace, String serviceName) {
		ClientMixedOperation<
			io.fabric8.kubernetes.api.model.Service, 
				ServiceList, 
					DoneableService, 
						ClientResource<io.fabric8.kubernetes.api.model.Service, DoneableService>> servicesClient 
							= getClient(clusterId).inNamespace(namespace).services();
		ClientResource<
			io.fabric8.kubernetes.api.model.Service, 
				DoneableService> clientResource 
					= servicesClient.withName(serviceName);
		return clientResource.delete();
	}*/
	public boolean deleteService(int clusterId, String namespace, String serviceName) {
		return getClient(clusterId).services().inNamespace(namespace).withName(serviceName).cascading(false).delete();
	}
	
	/*public io.fabric8.kubernetes.api.model.Service updateService(int clusterId, String namespace, String serviceName, 
			io.fabric8.kubernetes.api.model.Service service) {
		ClientMixedOperation<
			io.fabric8.kubernetes.api.model.Service, 
				ServiceList, 
					DoneableService, 
						ClientResource<io.fabric8.kubernetes.api.model.Service, DoneableService>> serviceClient 
							= getClient(clusterId).inNamespace(namespace).services();
		ClientResource<
			io.fabric8.kubernetes.api.model.Service, 
				DoneableService> clientResource 
					= serviceClient.withName(serviceName);
		return clientResource.update(service);
	}*/
	public io.fabric8.kubernetes.api.model.Service updateService(int clusterId, String namespace, String serviceName, 
			io.fabric8.kubernetes.api.model.Service service) {
		return getClient(clusterId).services().inNamespace(namespace).withName(serviceName).createOrReplace(service);
	}
	
	/*public io.fabric8.kubernetes.api.model.Service putService(int clusterId, String namespace, String serviceName, 
			io.fabric8.kubernetes.api.model.Service service) {
		ClientMixedOperation<
			io.fabric8.kubernetes.api.model.Service, 
				ServiceList, 
					DoneableService, 
						ClientResource<io.fabric8.kubernetes.api.model.Service, DoneableService>> serviceClient 
							= getClient(clusterId).inNamespace(namespace).services();
		ClientResource<
			io.fabric8.kubernetes.api.model.Service, 
				DoneableService> clientResource 
					= serviceClient.withName(serviceName);
		return clientResource.replace(service);
	}*/
	public io.fabric8.kubernetes.api.model.Service putService(int clusterId, String namespace, String serviceName, 
			io.fabric8.kubernetes.api.model.Service service) {
		return getClient(clusterId).services().inNamespace(namespace).withName(serviceName).replace(service);
	}
	
	/*public PodList getPodsByLabelsSelector(int clusterId, String namespace, Map<String, String> labels) {
		ClientMixedOperation<
			Pod, PodList, DoneablePod, 
				ClientPodResource<Pod,DoneablePod>> podsClient 
					= getClient(clusterId).inNamespace(namespace).pods();
		FilterWatchListDeletable<Pod, PodList, Boolean, Watch, Watcher<Pod>> filterWatchListDeletable 
			= podsClient.withLabels(labels);
		return filterWatchListDeletable.list();
	}*/
	public PodList getPodsByLabelsSelector(int clusterId, String namespace, Map<String, String> labels) {
		return getClient(clusterId).pods().inNamespace(namespace).withLabels(labels).list();
	}
	
	public io.fabric8.kubernetes.api.model.Service addLabelsService(int clusterId, String namespace, String serviceName, Map<String, String> labels) {
		return getClient(clusterId).services().inNamespace(namespace).withName(serviceName)
				.cascading(false).edit().editMetadata().addToLabels(labels).endMetadata().done();
	}
	
	public String getPodLogByName(int clusterId, String namespace, String podName, String containerName) {
		return getClient(clusterId).pods().inNamespace(namespace).withName(podName)
				.inContainer(containerName).tailingLines(80).withPrettyOutput().getLog();
	}

	public static void main(String[] args) {
		K8sAPIService service = new K8sAPIService() {
			public KubernetesClient getClient(int clusterId) {
				//Config config = new ConfigBuilder().withMasterUrl("http://10.255.242.203:1180/").withHttpProxy("http://10.1.128.200:9000").build();
				Config config = new ConfigBuilder().withMasterUrl("http://127.0.0.1:8888/").build();
				return new DefaultKubernetesClient(config);
			}
		};
		
//		ClientMixedOperation<Pod, PodList, DoneablePod, ClientPodResource<Pod,DoneablePod>> podsClient = service.getClient(2).inNamespace("default").pods();
//		FilterWatchListDeletable<Pod, PodList, Boolean, Watch, Watcher<Pod>> filterWatchListDeletable = podsClient.inAnyNamespace();
//		
//		for(Pod p : filterWatchListDeletable.list().getItems())
//		{
//			System.out.println(p.toString());
//			System.out.println("===================================================");
//		}
		
		for (String ip : service.getK8sAliveNodes(2)) {
			System.out.println(ip);
			
		}
		System.out.println("------END------");
			
			ServiceList slist = service.getServices(2,"default");
			for (io.fabric8.kubernetes.api.model.Service s : slist.getItems())
			{
				//System.out.println(s.toString());
				System.out.println(s.getMetadata().getName());
				System.out.println(s.getMetadata().getLabels());
				System.out.println("===================================================");
			}
		
	}
}
