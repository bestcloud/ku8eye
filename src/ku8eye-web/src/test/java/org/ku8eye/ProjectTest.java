package org.ku8eye;

import org.junit.Test;
import org.ku8eye.bean.project.Images;
import org.ku8eye.bean.project.Port;
import org.ku8eye.bean.project.Project;
import org.ku8eye.bean.project.Service;
import org.ku8eye.util.JSONUtil;

public class ProjectTest
{
	@Test
	public void creatProJson()
	{
		try
		{
			Project p = new Project();
			p.setAuthor("author");
			p.setNotes("note");
			p.setK8sVersion("1");
			p.setProjectName("project name");
			p.setVersion("1");

			Service s = new Service();
			s.setDescribe("describe");
			s.setName("service name");
			s.setReplica("12");
			s.setTag("tag");
			s.setVersion("0.1");

			Images i = new Images();
			i.setCommand("bin");
			i.setImageName("imageName");
			i.setName("name");
			i.setQuotas_cpu("quotas_cpu");
			i.setQuotas_limits("100");
			i.setQuotas_memory("1230mb");
			i.setRegistry("d:/registry");
			i.setVersion("0.1");

			Port p1 = new Port();
			p1.setContainerPort("8080");
			p1.setNodePort("8080");
			p1.setServicePort("8080");

			s.addImage(i);
			s.addPort(p1);

			p.addService(s);

			String val = JSONUtil.getJSONString(p);
			System.out.println(val);

			System.out.println("-----------------------------");

			Project p2 = Project.getFromJSON(val);
			System.out.println(p2);

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
