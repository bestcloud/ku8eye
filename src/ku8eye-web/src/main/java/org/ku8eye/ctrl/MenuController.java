package org.ku8eye.ctrl;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.ku8eye.domain.Menu;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**
 * resetFul菜单，可以放到系统的Controller中
 * @author jackChen
 *
 */
@RestController
public class MenuController {
	
	/**
	 * reset获取菜单的对象
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/menus", method = RequestMethod.GET)
	public List<Menu> getUserMenus(HttpServletRequest request) {
		List<Menu> menus = generateMenus();//获取默认的菜单数据
		return menus;
	}
	
	/**
	 * 模拟菜单数据
	 * @return
	 */
	private List<Menu> generateMenus(){
		List<Menu> menus =new ArrayList<Menu>();
		//菜单1
		Menu firstMenus1 = new Menu("1","Zone1","");
		//菜单1 第二级 submenu
		Menu firstMenuSub1 = new Menu("1_1","K8s Cluster","");
		//菜单1 第三级 菜单即第二级的子菜单
		Menu firstMenuSsuba = new Menu("1_1_1","ClusterA","");
		firstMenuSub1.getSubMenus().add(firstMenuSsuba);
		Menu firstMenuSsubb = new Menu("1_1_1","ClusterB","");
		firstMenuSub1.getSubMenus().add(firstMenuSsubb);
		firstMenus1.getSubMenus().add(firstMenuSub1);
		Menu firstMenuSub2 = new Menu("1_2","Host Pool","");
		firstMenus1.getSubMenus().add(firstMenuSub2);
		menus.add(firstMenus1);
		//菜单2
		Menu firstMenus2 = new Menu("2","Zone2","");
		menus.add(firstMenus2);
		//菜单3
		Menu firstMenus3 = new Menu("3","Zone3","");
		menus.add(firstMenus3);
		//菜单4
		Menu firstMenu4 = new Menu("4","My project","");
		Menu firstMenu4Sub = new Menu("4_1","xxxProject","");
		firstMenu4.getSubMenus().add(firstMenu4Sub);
		menus.add(firstMenu4);
		return menus;
	}
}
