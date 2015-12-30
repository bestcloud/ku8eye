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

/**
 * used for ui menu
 * 
 * @author wuzhih
 *
 */
@Service
public class UIService {
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
		// projects menu
		Menu firstMenu4 = new Menu("projects", "My Applications", "", MENU_TYPE_PROJECT_GROUP);
		menus.add(firstMenu4);

		Menu childMenu = new Menu("project-docker", "docker ", "project_docker.html", MENU_TYPE_PROJECT_NODE);
		firstMenu4.getSubMenus().add(childMenu);
		
		childMenu = new Menu("project-list", "List ", "project_main.html", MENU_TYPE_PROJECT_NODE);
		childMenu = new Menu("project-list", "List ", "application_main.html", MENU_TYPE_PROJECT_NODE);

		firstMenu4.getSubMenus().add(childMenu);

		childMenu = new Menu("project-report", "Report ", "application_report.html", MENU_TYPE_PROJECT_NODE);
		firstMenu4.getSubMenus().add(childMenu);
		Map<Integer, List<Host>> allHosts = getAllHosts(curUser);
		int zoneId = 1;
		// cluster menu
		Menu firstMenus1 = new Menu("clsdef", "K8s Cluster", "", MENU_TYPE_ZONE);
		// 菜单1 第二级 submenu
		Menu firstMenuSub1 = new Menu("1_1", "Resource Partions", "", MENU_TYPE_CLUSTER_GROUP);
		firstMenus1.getSubMenus().add(firstMenuSub1);
		// 菜单1 第三级 菜单即第二级的子菜单
		childMenu = new Menu("respartion_main", "List ", "respartion_main.html", MENU_TYPE_CLUSTER_NODE);
		firstMenuSub1.getSubMenus().add(childMenu);
		childMenu = new Menu("respartion_report", "Report ", "respartion_report.html", MENU_TYPE_CLUSTER_NODE);
		firstMenuSub1.getSubMenus().add(childMenu);

		// host pool sub menu
		Menu firstMenuSub2 = new Menu("hostp1", "Host Pool", "", MENU_TYPE_HOST_GROUP);
		firstMenus1.getSubMenus().add(firstMenuSub2);
		menus.add(firstMenus1);
		childMenu = new Menu("host-list", "List ", "host_main.html", MENU_TYPE_HOST_NODE);
		firstMenuSub2.getSubMenus().add(childMenu);
		
		childMenu = new Menu("host-report", "Report ", "host_report.html", MENU_TYPE_HOST_NODE);
		firstMenuSub2.getSubMenus().add(childMenu);
		// cluster info menu
		Menu firstMenuSub3 = new Menu("cls_inf", "Cluster Inf", "cluster_main.html", MENU_TYPE_PROJECT_GROUP);
		firstMenus1.getSubMenus().add(firstMenuSub3);

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
