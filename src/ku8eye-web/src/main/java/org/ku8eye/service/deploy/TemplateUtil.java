package org.ku8eye.service.deploy;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.ClasspathResourceLoader;

public class TemplateUtil {

	public static void createAnsibleFiles() throws IOException
	{
		ClasspathResourceLoader resourceLoader = new ClasspathResourceLoader();
		Configuration cfg = Configuration.defaultConfiguration();
		GroupTemplate gt = new GroupTemplate(resourceLoader, cfg);
		Template t = gt.getTemplate("/templates/ansible-host.beetl");
		List<String> hosts=new LinkedList<String>();
		hosts.add("192.168.0.1");
		hosts.add("192.168.0.2");
		t.binding("nodes", hosts);
		String str = t.render();
		System.out.println(str);
	}
	public static void main(String[] args) throws IOException
	{
		createAnsibleFiles();
	}
}
