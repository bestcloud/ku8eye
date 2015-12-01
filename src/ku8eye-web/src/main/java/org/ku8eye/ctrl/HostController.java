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
import org.ku8eye.domain.User;
import org.ku8eye.service.HostService;
import org.ku8eye.service.deploy.Ku8ClusterDeployService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mysql.fabric.xmlrpc.base.Data;

@RestController
public class HostController {

	@Autowired
	private HostService hostService;
	private Ku8ClusterDeployService deployService;
	ModelMap model;
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
	public List<InstallNode> getProjects(@PathVariable("IdString") String zoneId) {
		
		List<Host> pros = hostService.getHostsByZoneString(zoneId);
		
		
		Ku8ClusterTemplate temp = new Ku8ClusterTemplate();
		List<InstallNode> nodes = new LinkedList<InstallNode>();
		
		InstallNode node = new InstallNode();
		node.setDefautNode(true);
		node.setHostId(1);
		node.setHostName("sdfsdfsdf");
		node.setIp("1.2.2.2");
		node.getNodeRoleParams().put(Ku8ClusterTemplate.NODE_ROLE_MASTER, initInstallParameter());
		temp.getNodes().add(node);
		
		InstallNode node1 = new InstallNode();
		node1.setDefautNode(true);
		node1.setHostId(1);
		node1.setHostName("sdfsdfsdf");
		node1.setIp("1.2.2.2");
		node1.getNodeRoleParams().put(Ku8ClusterTemplate.NODE_ROLE_MASTER, initInstallParameter());
		temp.getNodes().add(node1);
		
		temp.addNewNode("kube-master");
		
		nodes.add(node);
		nodes.add(node1);
		
		return nodes;
	}

	
//	@RequestMapping(value = "/hostlist/{IdString}")
//	public Ku8ClusterTemplate getProjects(@PathVariable("IdString") String zoneId) {
//		GridData grid = new GridData();
//		log.info("要发送的id的String为"+zoneId);
//		List<Host> pros = hostService.getHostsByZoneString(zoneId);
//
//		
//		Ku8ClusterTemplate temp = new Ku8ClusterTemplate();
//
//		InstallNode node = new InstallNode();
//		node.setDefautNode(true);
//		node.setHostId(1);
//		node.setHostName("sdfsdfsdf");
//		node.setIp("1.2.2.2");
//		node.getNodeRoleParams().put(Ku8ClusterTemplate.NODE_ROLE_MASTER, initInstallParameter());
//		temp.getNodes().add(node);
//		
//		temp.addNewNode("kube-master");
//		log.info("添加节点完成");
//		
//		//temp=temp.clone();
//		
//		return temp;
//	}
	
	

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