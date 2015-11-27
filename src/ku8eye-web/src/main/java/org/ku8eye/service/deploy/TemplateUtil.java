package org.ku8eye.service.deploy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.ClasspathResourceLoader;
import org.ku8eye.bean.deploy.InstallNode;
import org.ku8eye.bean.deploy.InstallParam;
import org.ku8eye.bean.deploy.Ku8ClusterTemplate;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
@Component
@ConfigurationProperties(prefix = "deploy.ansible.root")
public class TemplateUtil {

	private String tmpFileYML;
	private String hostsFile;
	

	public void setTmpFileYML(String tmpFileStr) {
		this.tmpFileYML = tmpFileStr;
	}

	public void setHostsFile(String hostsFile) {
		this.hostsFile = hostsFile;
	}



	public void createAnsibleFiles(Ku8ClusterTemplate tmp) throws Exception {
		ClasspathResourceLoader resourceLoader = new ClasspathResourceLoader();
		Configuration cfg = Configuration.defaultConfiguration();
		GroupTemplate gt = new GroupTemplate(resourceLoader, cfg);

		// creatYml file
		String[] files = tmpFileYML.split(",");
		for (String fileline : files) {
			String[] fpara = fileline.split(":");
			creatParameterFile(tmp.getGlobParameterByRole(fpara[0]), gt,
					fpara[1], fpara[2]);
		}
		// creat hosts file
		String[] hostsLine = hostsFile.split(":");
		creatHostsParameterFile(tmp.getNodes(), gt, hostsLine[0], hostsLine[1]);
		// creat password file

	

	}

	void creatParameterFile(List<InstallParam> l, GroupTemplate gt,
			String temlate, String outFile) throws Exception {

		Template t = gt.getTemplate(temlate);
		for (InstallParam para : l) {
			t.binding(para.getName(), para.getValue());
		}

		writeFile(t.render(), outFile);
	}

	void creatHostsParameterFile(List<InstallNode> l, GroupTemplate gt,
			String temlate, String outFile) throws Exception {

		Template t = gt.getTemplate(temlate);
		HashMap<String, List<InstallParam>> registry_list = new HashMap<String, List<InstallParam>>();
		HashMap<String, List<InstallParam>> etde_list = new HashMap<String, List<InstallParam>>();
		HashMap<String, List<InstallParam>> master_list = new HashMap<String, List<InstallParam>>();
		HashMap<String, List<InstallParam>> nodes_list = new HashMap<String, List<InstallParam>>();
		// gt.registerFormat("ShowParameter",new showParameter());

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

		writeFile(t.render(), outFile);
	}

	private void writeFile(String fileInfo, String outFile) throws Exception {
		FileOutputStream out = null;
		try {

			File file = new File(outFile);
			if (!file.exists())
				file.createNewFile();
			out = new FileOutputStream(file, false);
			out.write(fileInfo.getBytes("utf-8"));

		} finally {
			if (out != null)
				out.close();
		}

	}

	public static void main(String[] args) throws IOException {
		// createAnsibleFiles();
	}
}
// class showParameter implements org.beetl.core.Format
// {
// @Override
// public Object format(Object paramObject, String paramString) {
//
// String result="";
// for(InstallParam para:(List<InstallParam>)paramObject)
// {
// result=" "+para.getName()+"="+para.getValue();
// }
// return result;
// }
//
//
// }
