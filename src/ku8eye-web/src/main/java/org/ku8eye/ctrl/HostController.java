package org.ku8eye.ctrl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.ku8eye.bean.GridData;
import org.ku8eye.bean.deploy.InstallNode;
import org.ku8eye.bean.deploy.InstallParam;
import org.ku8eye.bean.deploy.Ku8ClusterTemplate;
import org.ku8eye.domain.Host;
import org.ku8eye.service.HostService;
import org.ku8eye.service.deploy.Ku8ClusterDeployService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

@RestController
@SessionAttributes("ku8template")
public class HostController {

	@Autowired
	private HostService hostService;
	private Ku8ClusterDeployService deployService;
	private Logger log = Logger.getLogger(this.toString());

	@RequestMapping(value = "/hostlist/{zoneId}")
	public GridData getProjects(@PathVariable("zoneId") int zoneId) {
		GridData grid = new GridData();
		List<Host> pros = hostService.getHostsByZoneId(zoneId);
		grid.setData(pros);
		return grid;
	}

	/**
	 * add host
	 * 
	 */
	@RequestMapping(value = "/host/addhost")
	public int checkLogin(HttpServletRequest request, @RequestParam("hostname") String hostname,
			@RequestParam("ip") String ip, @RequestParam("rootpass") String rootpass,
			@RequestParam("cores") String cores,@RequestParam("memory") String memory,
			@RequestParam("location") String location) {
		
		Host host = new Host();
		host.setZoneId(1);
		host.setHostName(hostname);
		host.setIp(ip);
		host.setRootPasswd(rootpass);
		host.setLocation(location);
		host.setCores(new Byte(cores));
		host.setMemory(Integer.parseInt(memory));
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		host.setLastUpdated(curDate);
		int hostresult = hostService.setHostNode(host);
		
//		host.setNote(note);
//		host.setUsageFlag(new Byte(usage_flag));
//		host.setSshLogin(new Byte(ssh_login));
//		host.setClusterId(Integer.parseInt(cluster_id));
		
		return hostresult;
	}

}