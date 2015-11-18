package org.ku8eye.domain;

import java.util.ArrayList;
import java.util.List;

public class Menu {
	
	private String menuId;
	
	private String menuName;
	
	private String menuUrl;
	
	private List<Menu> subMenus;
	
	public Menu() {
		super();
	}

	public Menu(String menuId, String menuName, String menuUrl) {
		super();
		this.menuId = menuId;
		this.menuName = menuName;
		this.menuUrl = menuUrl;
		this.subMenus = new ArrayList();
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
	
}
