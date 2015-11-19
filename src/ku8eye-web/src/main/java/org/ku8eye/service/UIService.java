package org.ku8eye.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.ku8eye.bean.ui.Menu;
import org.ku8eye.domain.Host;
import org.ku8eye.domain.Ku8Cluster;
import org.ku8eye.domain.Ku8Project;
import org.ku8eye.domain.User;
import org.ku8eye.domain.Zone;
import org.ku8eye.mapping.HostMapper;
import org.ku8eye.mapping.Ku8ClusterMapper;
import org.ku8eye.mapping.Ku8ProjectMapper;
import org.ku8eye.mapping.ZoneMapper;
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
	private ZoneMapper zoneDao;
	@Autowired
	private Ku8ClusterMapper K8ClusterDao;
	@Autowired
	private HostMapper hostDao;

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
		Menu firstMenu4 = new Menu("projects", "My projects", "dddddd.do", MENU_TYPE_PROJECT_GROUP);
		menus.add(firstMenu4);
		List<Ku8Project> allProjects = getMyProjects(curUser);
		for (Ku8Project proj : allProjects) {
			Menu firstMenu4Sub = new Menu("prj" + proj.getId(), proj.getName(), "project_main.html",
					MENU_TYPE_PROJECT_NODE);
			firstMenu4.getSubMenus().add(firstMenu4Sub);

		}
		List<Zone> allZones = getAllZones(curUser);
		Map<Integer, List<Ku8Cluster>> allClsters = getAllClusters(curUser);
		Map<Integer, List<Host>> allHosts = getAllHosts(curUser);
		for (Zone zone : allZones) {
			// zone menu
			Menu firstMenus1 = new Menu("zone" + zone.getId(), zone.getName(), "zone_main.html", MENU_TYPE_ZONE);
			// 菜单1 第二级 submenu
			Menu firstMenuSub1 = new Menu("1_1", "K8s Cluster", "", MENU_TYPE_CLUSTER_GROUP);
			firstMenus1.getSubMenus().add(firstMenuSub1);
			// 菜单1 第三级 菜单即第二级的子菜单
			List<Ku8Cluster> zoneClusters = allClsters.get(zone.getId());
			if (zoneClusters != null) {
				for (Ku8Cluster cls : zoneClusters) {
					Menu firstMenuSsuba = new Menu("cls" + cls.getId(), cls.getName(), "cluster_main.html",
							MENU_TYPE_CLUSTER_NODE);
					firstMenuSub1.getSubMenus().add(firstMenuSsuba);
				}

			}
			// host pool sub menu
			Menu firstMenuSub2 = new Menu("1_2", "Host Pool", "", MENU_TYPE_HOST_GROUP);
			firstMenus1.getSubMenus().add(firstMenuSub2);
			menus.add(firstMenus1);

			List<Host> zoneHosts = allHosts.get(zone.getId());
			if (zoneHosts != null) {
				for (Host host : zoneHosts) {
					Menu firstMenuSub2_1 = new Menu("host" + host.getId(), host.getHostName(), "host_main.html",
							MENU_TYPE_HOST_NODE);
					firstMenuSub2.getSubMenus().add(firstMenuSub2_1);
				}
			}
		}
		return menus;

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

	private List<Zone> getAllZones(User curUser) {
		return zoneDao.selectAll();
	}

	private Map<Integer, List<Ku8Cluster>> getAllClusters(User curUser) {
		List<Ku8Cluster> allClusters = K8ClusterDao.selectAll();
		Map<Integer, List<Ku8Cluster>> result = new HashMap<Integer, List<Ku8Cluster>>();
		for (Ku8Cluster cls : allClusters) {
			List<Ku8Cluster> list = result.get(cls.getZoneId());
			if (list == null) {
				list = new LinkedList<Ku8Cluster>();
				result.put(cls.getZoneId(), list);

			}
			list.add(cls);
		}
		return result;
	}

	private List<Ku8Project> getMyProjects(User curUser) {
		return ku8ProjectDao.selectAll();
	}
}
