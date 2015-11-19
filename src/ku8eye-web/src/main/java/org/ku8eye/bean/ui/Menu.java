package org.ku8eye.bean.ui;

import java.util.ArrayList;
import java.util.List;
/**
 * used for ui menu
 * @author wuzhih
 *
 */
public class Menu {

	private String menuId;

	private String menuName;

	private String menuUrl;

	private String menuType;

	private List<Menu> subMenus;

	public Menu() {
		super();
	}

	public Menu(String menuId, String menuName, String menuUrl, String menuType) {
		super();
		this.menuId = menuId;
		this.menuName = menuName;
		this.menuUrl = menuUrl;
		this.menuType = menuType;
		this.subMenus = new ArrayList<Menu>();
	}

	public List<Menu> getSubMenus() {
		return subMenus;
	}

	public void setSubMenus(List<Menu> subMenus) {
		this.subMenus = subMenus;
	}

	public String getMenuId() {
		return menuId;
	}

	public void setMenuId(String menuId) {
		this.menuId = menuId;
	}

	public String getMenuName() {
		return menuName;
	}

	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}

	public String getMenuUrl() {
		return menuUrl;
	}

	public void setMenuUrl(String menuUrl) {
		this.menuUrl = menuUrl;
	}

	public String getMenuType() {
		return menuType;
	}

	public void setMenuType(String menuType) {
		this.menuType = menuType;
	}

}
