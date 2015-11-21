package org.ku8eye.ctrl;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.ku8eye.bean.ui.Menu;
import org.ku8eye.service.UIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * resetFul菜单，可以放到系统的Controller中
 * 
 * @author jackChen
 *
 */
@RestController
public class MenuController {
	@Autowired
	private UIService uiService;

	/**
	 * reset获取菜单的对象
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/menus", method = RequestMethod.GET)
	public List<Menu> getUserMenus(HttpServletRequest request) {
		return uiService.generateMenus(null);
	}

}
