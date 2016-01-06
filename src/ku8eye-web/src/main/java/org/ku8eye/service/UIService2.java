package org.ku8eye.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.ku8eye.bean.ui.Menu;
import org.ku8eye.domain.Host;
import org.ku8eye.domain.Ku8Project;
import org.ku8eye.domain.Ku8ResPartion;
import org.ku8eye.domain.User;
import org.ku8eye.mapping.HostMapper;
import org.ku8eye.mapping.Ku8ProjectMapper;
import org.ku8eye.mapping.Ku8ResPartionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UIService2 {
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
		Menu dashboard = new Menu("dashboard", "Dashboard 集群监控", "", MENU_TYPE_PROJECT_GROUP);
		menus.add(dashboard);
		
		Menu dashboard_hosts = new Menu("dashboard_host", "Hosts 主机", "", MENU_TYPE_PROJECT_NODE);
		dashboard.getSubMenus().add(dashboard_hosts);
		
		Menu dashboard_services = new Menu("dashboard_services", "Services 服务", "", MENU_TYPE_PROJECT_NODE);
		dashboard.getSubMenus().add(dashboard_services);
		
		//Docker Registry
		Menu docker = new Menu("docker", "Docker 私库", "", MENU_TYPE_PROJECT_GROUP);
		menus.add(docker);
		
		Menu docker_list = new Menu("docker_list", "List", "application_docker.html", MENU_TYPE_PROJECT_NODE);
		docker.getSubMenus().add(docker_list);
		
		//Applications
		Menu application = new Menu("application", "My Apps 我的应用", "", MENU_TYPE_PROJECT_GROUP);
		menus.add(application);

		Menu application_list = new Menu("application_list", "List", "application_main.html", MENU_TYPE_PROJECT_NODE);
		application.getSubMenus().add(application_list);
		
		Menu application_report = new Menu("application_report", "Report", "application_report.html", MENU_TYPE_PROJECT_NODE);
		application.getSubMenus().add(application_report);

		//Public Services
		Menu public_services = new Menu("public_services", "Public Services 公共服务", "", MENU_TYPE_PROJECT_GROUP);
		menus.add(public_services);
		
		//Resources
		Menu resources = new Menu("resources", "Resources 资源管理", "", MENU_TYPE_ZONE);
		menus.add(resources);
		
		Menu resource_part = new Menu("resource_part", "Resource Part 资源分区", "", MENU_TYPE_CLUSTER_GROUP);
		resources.getSubMenus().add(resource_part);
		
		Menu resource_list = new Menu("respartion_main", "List ", "respartion_main.html", MENU_TYPE_CLUSTER_NODE);
		resource_part.getSubMenus().add(resource_list);
		
		Menu resource_report = new Menu("respartion_report", "Report ", "respartion_report.html", MENU_TYPE_CLUSTER_NODE);
		resource_part.getSubMenus().add(resource_report);

		// Host Pool
		Menu host_pool = new Menu("host_pool", "Host Pool 主机池", "", MENU_TYPE_HOST_GROUP);
		resources.getSubMenus().add(host_pool);
		
		Menu host_pool_list = new Menu("host_pool_list", "List ", "host_main.html", MENU_TYPE_HOST_NODE);
		host_pool.getSubMenus().add(host_pool_list);
		
		Menu host_pool_report = new Menu("host-report", "Report ", "host_report.html", MENU_TYPE_HOST_NODE);
		host_pool.getSubMenus().add(host_pool_report);
		
		// Cluster Install
		Menu cluster_install = new Menu("cluster_install", "Cluster 集群安装", "cluster_main.html", MENU_TYPE_PROJECT_GROUP);
		resources.getSubMenus().add(cluster_install);

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
