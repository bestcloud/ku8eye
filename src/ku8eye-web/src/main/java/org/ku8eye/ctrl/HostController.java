package org.ku8eye.ctrl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.ku8eye.bean.GridData;
import org.ku8eye.bean.deploy.InstallNode;
import org.ku8eye.bean.deploy.InstallParam;
import org.ku8eye.bean.deploy.Ku8ClusterTemplate;
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
	private Logger log = Logger.getLogger(this.toString());
	
	@RequestMapping(value = "/addlist/{zoneId}")
	public GridData getProjects(@PathVariable("zoneId") int zoneId) {
		GridData grid = new GridData();
		List<Host> pros = hostService.getHostsByZoneId(zoneId);
		log.info("进入了！！！");
		grid.setData(pros);
		return grid;
	}
	
	
	@RequestMapping(value = "/hostlist/{IdString}")
	public GridData getProjects(@PathVariable("IdString") String zoneId) {
		GridData grid = new GridData();
		log.info("要发送的id的String为"+zoneId);
		List<Host> pros = hostService.getHostsByZoneString(zoneId);
		log.info("要发送的id的String为"+pros);
		log.info("ddddddddddddddddddddddddddddd");
		Ku8ClusterTemplate templates=new Ku8ClusterTemplate();
		templates.addNewNode("docker-registry");
		
		
		Ku8ClusterTemplate temp = new Ku8ClusterTemplate();
		temp.setId(1);
		temp.setName("Distribute Cluster");
		temp.setTemplateType(1);
		temp.setDescribe("Distribute server");
		temp.setMinNodes(3);
		temp.setMaxNodes(20);

		InstallNode etcd_node = new InstallNode();
		etcd_node.setDefautNode(true);
		etcd_node.setHostId(2);
		etcd_node.setHostName("Etcd");
		etcd_node.setIp("192.168.1.2");
		etcd_node.getNodeRoleParams().put(Ku8ClusterTemplate.NODE_ROLE_ETCD, initInstallParameter());
		
		temp.getNodes().add(etcd_node);
		
		grid.setData(pros);		
		return grid;
	}

	private List<InstallParam> initInstallParameter() {
		List<InstallParam> list = new ArrayList<InstallParam>();
		list.add(new InstallParam("ansible_ssh_user", "root", "login uername"));
		list.add(new InstallParam("ansible_ssh_pass", "root", "login pass"));
		return list;
	}


	/**
	 * add host
	 * 
	 * 
	 *
	 *
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