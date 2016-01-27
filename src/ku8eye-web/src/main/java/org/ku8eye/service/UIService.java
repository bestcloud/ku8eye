package org.ku8eye.service;

import io.fabric8.kubernetes.api.model.ServiceList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.ku8eye.bean.ui.Menu;
import org.ku8eye.domain.Host;
import org.ku8eye.domain.Ku8Project;
import org.ku8eye.domain.Ku8ResPartion;
import org.ku8eye.domain.User;
import org.ku8eye.mapping.HostMapper;
import org.ku8eye.mapping.Ku8ProjectMapper;
import org.ku8eye.mapping.Ku8ResPartionMapper;
import org.ku8eye.service.k8s.K8sAPIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UIService {
	private Logger log = Logger.getLogger(this.toString());
	
	private static final String MENU_TYPE_ZONE = "1";

	private static final String MENU_TYPE_CLUSTER_GROUP = "2";

	private static final String MENU_TYPE_CLUSTER_NODE = "3";

	private static final String MENU_TYPE_HOST_GROUP = "4";

	private static final String MENU_TYPE_HOST_NODE = "5";

	private static final String MENU_TYPE_PROJECT_GROUP = "6";

	private static final String MENU_TYPE_PROJECT_NODE = "7";
	@Autowired
	private Ku8ProjectMapper ku8ProjectDao;
	@Autowired
	private HostMapper hostDao;

	@Autowired
	private Ku8ResPartionMapper Ku8ResPartionDao;
	
	@Autowired
	private K8sAPIService k8sAPIService;

	/**
	 * fetch current user's menu
	 * 
	 * @param curUser
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<Menu> generateMenus(User curUser) {
		List<Menu> menus = new ArrayList<Menu>();
		
		//Dashboard
		Menu dashboard = new Menu("dashboard", "集群监控 ", "", MENU_TYPE_HOST_GROUP);
		Menu dashboard_hosts = new Menu("dashboard_host", "主机视图", "host_usage.html", MENU_TYPE_HOST_NODE);
		dashboard.getSubMenus().add(dashboard_hosts);

		Menu dashboard_services = new Menu("dashboard_services", "服务视图", "", MENU_TYPE_HOST_NODE);
		dashboard.getSubMenus().add(dashboard_services);
		try {
			Menu childMenu;
			ServiceList services = k8sAPIService.getServices(0, "default");
			List<io.fabric8.kubernetes.api.model.Service> serviceItems = services.getItems();
			if(serviceItems != null){
				int order = 1;
				for(io.fabric8.kubernetes.api.model.Service serviceItem : serviceItems){
					if(serviceItem.getMetadata() != null && !"kubernetes".equals(serviceItem.getMetadata().getName())){
						childMenu = new Menu("2-" + order, serviceItem.getMetadata().getName(), "single_service_usage.html", MENU_TYPE_CLUSTER_NODE);
						order++;
						dashboard_services.getSubMenus().add(childMenu);
					}
				}
			}
		} catch (Exception e) {
			log.error("get service menu error: " + e.getMessage());
		}
		
		//Applications
		Menu application = new Menu("application", "应用管理", "application_main.html", MENU_TYPE_PROJECT_GROUP);
		Menu application_list = new Menu("application_list", "我的应用", "application_main.html", MENU_TYPE_PROJECT_NODE);
		Menu docker_list = new Menu("docker_list", "私库镜像", "application_docker.html", MENU_TYPE_PROJECT_NODE);
		Menu public_services_list = new Menu("application_list", "公共服务", "service.html", MENU_TYPE_PROJECT_NODE);
		application.getSubMenus().add(docker_list);
		application.getSubMenus().add(application_list);		
		application.getSubMenus().add(public_services_list);	
		
		//Resources
		Menu resources = new Menu("resources", "资源管理 ", "", MENU_TYPE_ZONE);
		Menu resource_part = new Menu("resource_part", "资源分区", "respartion_main.html", MENU_TYPE_CLUSTER_GROUP);
		Menu host_pool = new Menu("host_pool", "主机池", "host_main.html", MENU_TYPE_HOST_GROUP);
		Menu cluster_install = new Menu("cluster_install", "集群安装", "cluster_main.html", MENU_TYPE_PROJECT_GROUP);
		resources.getSubMenus().add(host_pool);
		resources.getSubMenus().add(resource_part);
		resources.getSubMenus().add(cluster_install);
		
		menus.add(application);
		menus.add(resources);
		menus.add(dashboard);
		return menus;

	}

	private List<Ku8ResPartion> getAllResPartions(int clusterId) {
		List<Ku8ResPartion> allPartions = Ku8ResPartionDao.selectAll();
		List<Ku8ResPartion> result = new LinkedList<Ku8ResPartion>();
		for (Ku8ResPartion resPt : allPartions) {
			if (resPt.getClusterId() == clusterId) {
				result.add(resPt);
			}
		}
		return result;
	}

	private Map<Integer, List<Host>> getAllHosts(User curUser) {
		List<Host> allClusters = hostDao.selectAll();
		Map<Integer, List<Host>> result = new HashMap<Integer, List<Host>>();
		for (Host host : allClusters) {
			List<Host> list = result.get(host.getZoneId());
			if (list == null) {
				list = new LinkedList<Host>();
				result.put(host.getZoneId(), list);

			}
			list.add(host);
		}
		return result;
	}

	private List<Ku8Project> getMyProjects(User curUser) {
		return ku8ProjectDao.selectAll();
	}
}
