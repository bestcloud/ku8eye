package org.ku8eye.service;

import java.util.List;

import org.ku8eye.bean.service.Pod;
import org.ku8eye.bean.service.ServiceAndRC;
import org.springframework.stereotype.Service;

@Service
public class ServiceAndRcService
{
	public List<ServiceAndRC> getAllServiceAndRc()
	{
		return null;// TODO 从kubernets中获取
	}
	public List<Pod> getAllPodsBySelector(String selector)
	{
		return null;// TODO 从kubernets中获取
	}
	public String dealServiceAndRc(String name)
	{
		return null;// TODO 从kubernets中获取
	}
	public String updateServiceAndRc()
	{
		return null;// TODO 从kubernets中获取
	}
	public String createServiceAndRc()
	{
		return null;// TODO 从kubernets中获取
	}
}
