$(function(){
	initUserMenu();
})

function initUserMenu(){
	 $.ajax({url: '/menus', success: function(menu){
	    setMenu($('.sidebar-menu'),menu);
     }});
}

function setMenu(menuid,menus){
	var menuhtm = [];
	$.each(menus,function(n,menudata) {
		 menuhtm.push("<li id=\"menu");
		 menuhtm.push(menudata.menuId);
		 menuhtm.push("\" class=\"treeview\">");
		 menuhtm.push("<a href=\"#\">");
		 menuhtm.push("<i class=\"fa fa-television\"></i> <span>");
		 menuhtm.push(menudata.menuName);
		 if(menudata.subMenus.length>0){
			 menuhtm.push("</span><i class=\"fa fa-angle-left pull-right\"></i></a>")
			 menuhtm.push(setSecondLevelMenu(menudata.subMenus));
		 }else{
			 menuhtm.push("</a>")
		 }
		 menuhtm.push("</li>");
	})
	menuid.append('' + menuhtm.join('') + '');
}

function setSecondLevelMenu(menus){
	var menuhtm = [];
	menuhtm.push("<ul class=\"treeview-menu\">");
	$.each(menus,function(n,menudata) {
		 menuhtm.push("<li><a href=\"#\"><i class=\"fa fa-circle-o\"></i>"+menudata.menuName);
		 if(menudata.subMenus.length>0){
			 menuhtm.push("<i class=\"fa fa-angle-left pull-right\"></i></a>")
			 menuhtm.push(setThirdLevelMenu(menudata.subMenus));
		 }else{
			 menuhtm.push("</a>")
		 }
		 menuhtm.push("</li>");
	})
	menuhtm.push("</ul>");
	return '' + menuhtm.join('') + '';
}

function setThirdLevelMenu(menus){
	var menuhtm = [];
	menuhtm.push("<ul class=\"treeview-menu\">");
	$.each(menus,function(n,menudata) {
		 menuhtm.push("<li><a href=\"#\"><i class=\"fa fa-circle-o\"></i>"+menudata.menuName+"</a>");
	})
	menuhtm.push("</ul>");
	return '' + menuhtm.join('') + '';
}