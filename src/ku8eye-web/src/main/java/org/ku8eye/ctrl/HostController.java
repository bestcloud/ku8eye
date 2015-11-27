package org.ku8eye.ctrl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.ku8eye.bean.GridData;
import org.ku8eye.domain.Host;
import org.ku8eye.domain.User;
import org.ku8eye.service.HostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mysql.fabric.xmlrpc.base.Data;

@RestController
public class HostController {

	@Autowired
	private HostService hostService;
	int adf;

	@RequestMapping(value = "/hostlist/{zoneId}")
	public GridData getProjects(@PathVariable("zoneId") int zoneId) {
		adf = zoneId;
		GridData grid = new GridData();
		if (zoneId == 1) {
			List<Host> pros = hostService.getHostsByZoneId(zoneId);
			grid.setData(pros);
		} else if (zoneId == 2) {
			HttpServletRequest res = null;
			Object b = res.getAttribute("nodeip");
		}

		return grid;
	}

	/**
	 * check login username and password
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	@RequestMapping(value = "/hostl")
	public int checkLogin(HttpServletRequest request,
			@RequestParam("zone_id") String zone_id,
			@RequestParam("host_name") String host_name,
			@RequestParam("ip") String ip,
			@RequestParam("root_passwd") String root_passwd,
			@RequestParam("location") String location,
			@RequestParam("note") String note,
			@RequestParam("cores") String cores,
			@RequestParam("memory") String memory,
			@RequestParam("usage_flag") String usage_flag,
			@RequestParam("ssh_login") String ssh_login,
			@RequestParam("cluster_id") String cluster_id) {
		Host host = new Host();

		host.setZoneId(Integer.parseInt(zone_id));
		host.setHostName(host_name);
		host.setIp(ip);
		host.setRootPasswd(root_passwd);
		host.setLocation(location);
		host.setNote(note);
		host.setCores(new Byte(cores));
		host.setMemory(Integer.parseInt(memory));
		host.setUsageFlag(new Byte(usage_flag));
		host.setSshLogin(new Byte(ssh_login));
		host.setClusterId(Integer.parseInt(cluster_id));
		SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
    	Date curDate = new Date(System.currentTimeMillis());//获取当前时间
    	host.setLastUpdated(curDate);
		int a = hostService.setHostNode(host);
		return a;
	}
}