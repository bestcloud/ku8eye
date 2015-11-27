package org.ku8eye.service.deploy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.el.lang.FunctionMapperImpl.Function;
import org.beetl.core.Configuration;
import org.beetl.core.Context;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.ClasspathResourceLoader;
import org.ku8eye.bean.deploy.InstallNode;
import org.ku8eye.bean.deploy.InstallParam;
import org.ku8eye.bean.deploy.Ku8ClusterTemplate;
import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "deploy.ansible.root")
public class TemplateUtil {
private String home;

	
	
	public static void createAnsibleFiles() throws IOException {
		ClasspathResourceLoader resourceLoader = new ClasspathResourceLoader();
		Configuration cfg = Configuration.defaultConfiguration();
		GroupTemplate gt = new GroupTemplate(resourceLoader, cfg);
		// Template t = gt.getTemplate("/templates/ansible-host.beetl");

		List<Ku8ClusterTemplate> tmpList = Ku8ClusterDeployService
				.getAllTemplates();
		if (tmpList.size() < 0) {
			return;
		}

		for (Ku8ClusterTemplate tmp : tmpList) {
			String str = getAllGlobleParameterFile(
					tmp.getGlobParameterByRole(Ku8ClusterTemplate.DEFAULT_GLOBAL),
					gt);
//			System.out.println(str);
//			System.out.println("===========>>");
			 str = getDockerRegistryParameterFile(
			 tmp.getGlobParameterByRole(Ku8ClusterTemplate.NODE_ROLE_REGISTRY),
			 gt);
//			 System.out.println(str);
//			 System.out.println("===========>>");
			 str = getEtcdParameterFile(
			 tmp.getGlobParameterByRole(Ku8ClusterTemplate.NODE_ROLE_ETCD),
			 gt);
//			 System.out.println(str);
//			 System.out.println("===========>>");
			 str = getKuberMasterParameterFile(
			 tmp.getGlobParameterByRole(Ku8ClusterTemplate.NODE_ROLE_MASTER),
			 gt);
//			 System.out.println(str);
//			 System.out.println("===========>>");
			 str = getKuberNodeParameterFile(
			 tmp.getGlobParameterByRole(Ku8ClusterTemplate.NODE_ROLE_NODE),
			 gt);
//			 System.out.println(str);
//			 System.out.println("===========>>");

			str = getHostsParameterFile(tmp.getNodes(), gt);
			 System.out.println(str);
			 System.out.println("===========>>");

			
		}
	}

	static String getAllGlobleParameterFile(List<InstallParam> l,
			GroupTemplate gt) {
		Template t = gt.getTemplate("/templates/all.yml");
		for (InstallParam para : l) {
			t.binding(para.getName(), para.getValue());
		}
		return t.render();
	}

	static String getDockerRegistryParameterFile(List<InstallParam> l,
			GroupTemplate gt) {
		Template t = gt.getTemplate("/templates/docker_registry.yml");
		for (InstallParam para : l) {
			t.binding(para.getName(), para.getValue());
		}
		return t.render();
	}

	static String getEtcdParameterFile(List<InstallParam> l, GroupTemplate gt) {
		Template t = gt.getTemplate("/templates/etcd.yml");
		for (InstallParam para : l) {
			t.binding(para.getName(), para.getValue());
		}
		return t.render();
	}

	static String getKuberMasterParameterFile(List<InstallParam> l,
			GroupTemplate gt) {
		Template t = gt.getTemplate("/templates/kuber_master.yml");
		for (InstallParam para : l) {
			t.binding(para.getName(), para.getValue());
		}
		return t.render();
	}

	static String getKuberNodeParameterFile(List<InstallParam> l,
			GroupTemplate gt) {
		Template t = gt.getTemplate("/templates/kuber_node.yml");
		for (InstallParam para : l) {
			t.binding(para.getName(), para.getValue());
		}
		return t.render();
	}

	static String getHostsParameterFile(List<InstallNode> l, GroupTemplate gt) {

		Template t = gt.getTemplate("/templates/hosts");
		HashMap<String, List<InstallParam>> registry_list = new HashMap<String, List<InstallParam>>();
		HashMap<String, List<InstallParam>> etde_list = new HashMap<String, List<InstallParam>>();
		HashMap<String, List<InstallParam>> master_list = new HashMap<String, List<InstallParam>>();
		HashMap<String, List<InstallParam>> nodes_list = new HashMap<String, List<InstallParam>>();
//		gt.registerFormat("ShowParameter",new showParameter());
		
		for (InstallNode node : l) {

			Map<String, List<InstallParam>> hm = node.getNodeRoleParams();


			if (hm.get(Ku8ClusterTemplate.NODE_ROLE_ETCD) != null) {
				etde_list.put(node.getIp(),
						hm.get(Ku8ClusterTemplate.NODE_ROLE_ETCD));
			}
			if (hm.get(Ku8ClusterTemplate.NODE_ROLE_MASTER) != null) {
				master_list.put(node.getIp(),
						hm.get(Ku8ClusterTemplate.NODE_ROLE_MASTER));

			}
			if (hm.get(Ku8ClusterTemplate.NODE_ROLE_REGISTRY) != null) {
				registry_list.put(node.getIp(),
						hm.get(Ku8ClusterTemplate.NODE_ROLE_REGISTRY));

			}
			if (hm.get(Ku8ClusterTemplate.NODE_ROLE_NODE) != null) {
				nodes_list.put(node.getIp(),
						hm.get(Ku8ClusterTemplate.NODE_ROLE_NODE));
			}
		}
		t.binding("master", master_list);
		t.binding("etcd", etde_list);
		t.binding("registry", registry_list);
		t.binding("node", nodes_list);
		return t.render();
	}

	public static void main(String[] args) throws IOException {
		createAnsibleFiles();
	}
}
//class showParameter implements org.beetl.core.Format
//{
//	@Override
//	public Object format(Object paramObject, String paramString) {
// 
//		String result="";
//		for(InstallParam para:(List<InstallParam>)paramObject)
//		{
//			result=" "+para.getName()+"="+para.getValue();	
//		}
//		return result;
//	}
//  
//
//}
