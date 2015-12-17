package org.ku8eye;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.ku8eye.ctrl.deploy.DemoDataUtil;
import org.ku8eye.service.deploy.AnsibleCallResult;
import org.ku8eye.service.deploy.AnsibleNodeSum;
import org.ku8eye.service.deploy.AnsibleResultParser;
import org.ku8eye.service.deploy.AnsibleTaskResult;

public class TestAnsibleResultParser {
	
	
	@Test
	public void testAllInOneParseTest1() throws UnsupportedEncodingException {
		InputStream inStream = this.getClass().getClassLoader().getResourceAsStream("ansbile-result1.txt");
		List<String> fileLines = DemoDataUtil.readFromStream(inStream);
		AnsibleCallResult result = AnsibleResultParser.parseResult(fileLines);
		System.out.println("____________________________Report______________________________");
		result.printInfo();
		Assert.assertEquals(result.isSuccess(),true);
		Map<String, Map<String, AnsibleTaskResult>>  hostResultMap=result.getHostTaskResultMap();
		Assert.assertEquals(hostResultMap.size(),4);
		Assert.assertEquals(result.getNodeTotalSumaryMap().get("192.168.18.133"),new AnsibleNodeSum(107,92,0,0));
	}

	@Test
	public void testAllInOneParseTest2() throws UnsupportedEncodingException {
		InputStream inStream = this.getClass().getClassLoader().getResourceAsStream("ansible-result2.txt");
		List<String> fileLines =  DemoDataUtil.readFromStream(inStream);
		AnsibleCallResult result = AnsibleResultParser.parseResult(fileLines);
		System.out.println("____________________________Report______________________________");
		System.out.println(result.printInfo());
		Assert.assertEquals(result.isSuccess(),false);
		Map<String, Map<String, AnsibleTaskResult>>  hostResultMap=result.getHostTaskResultMap();
		Assert.assertEquals(hostResultMap.size(),1);
	}
	
	@Test
	public void testAllInOneParseTest3() throws UnsupportedEncodingException {
		InputStream inStream = this.getClass().getClassLoader().getResourceAsStream("ansible-result3.txt");
		List<String> fileLines =  DemoDataUtil.readFromStream(inStream);
		AnsibleCallResult result = AnsibleResultParser.parseResult(fileLines);
		System.out.println("____________________________Report______________________________");
		System.out.println(result.printInfo());
		Assert.assertEquals(result.isSuccess(),false);
		Map<String, Map<String, AnsibleTaskResult>>  hostResultMap=result.getHostTaskResultMap();
		Assert.assertEquals(hostResultMap.size(),1);
	}
	@Test
	public void testAllInOneParseTest4() throws UnsupportedEncodingException {
		InputStream inStream = this.getClass().getClassLoader().getResourceAsStream("ansible-result4.txt");
		List<String> fileLines =  DemoDataUtil.readFromStream(inStream);
		AnsibleCallResult result = AnsibleResultParser.parseResult(fileLines);
		System.out.println("____________________________Report______________________________");
		System.out.println(result.printInfo());
		Assert.assertEquals(result.isSuccess(),false);
		Map<String, Map<String, AnsibleTaskResult>>  hostResultMap=result.getHostTaskResultMap();
		Assert.assertEquals(hostResultMap.size(),1);
	}
}
