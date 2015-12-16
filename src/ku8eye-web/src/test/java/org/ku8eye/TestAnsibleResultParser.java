package org.ku8eye;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.ku8eye.service.deploy.AnsibleCallResult;
import org.ku8eye.service.deploy.AnsibleResultParser;
import org.ku8eye.service.deploy.AnsibleTaskResult;

public class TestAnsibleResultParser {
	private static List<String> readFromStream(InputStream inStream) {
		List<String> fileLines = new LinkedList<String>();
		LineNumberReader reader = null;
		try {
			reader = new LineNumberReader(new InputStreamReader(inStream, "utf-8"));
			String line = null;
			while ((line = reader.readLine()) != null) {
				fileLines.add(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
		}
		return fileLines;
	}
	
	@Test
	public void testAllInOneParseTest1() throws UnsupportedEncodingException {
		InputStream inStream = this.getClass().getClassLoader().getResourceAsStream("ansbile-result1.txt");
		List<String> fileLines = readFromStream(inStream);
		AnsibleCallResult result = AnsibleResultParser.parseResult(fileLines);
		System.out.println("____________________________Report______________________________");
		result.printInfo();
		Assert.assertEquals(result.isSuccess(),true);
		Map<String, Map<String, AnsibleTaskResult>>  hostResultMap=result.getHostTaskResultMap();
		Assert.assertEquals(hostResultMap.size(),4);
	}

	@Test
	public void testAllInOneParseTest2() throws UnsupportedEncodingException {
		InputStream inStream = this.getClass().getClassLoader().getResourceAsStream("ansible-result2.txt");
		List<String> fileLines = readFromStream(inStream);
		AnsibleCallResult result = AnsibleResultParser.parseResult(fileLines);
		System.out.println("____________________________Report______________________________");
		result.printInfo();
		Assert.assertEquals(result.isSuccess(),false);
		Map<String, Map<String, AnsibleTaskResult>>  hostResultMap=result.getHostTaskResultMap();
		Assert.assertEquals(hostResultMap.size(),1);
	}
	
	@Test
	public void testAllInOneParseTest3() throws UnsupportedEncodingException {
		InputStream inStream = this.getClass().getClassLoader().getResourceAsStream("ansible-result3.txt");
		List<String> fileLines = readFromStream(inStream);
		AnsibleCallResult result = AnsibleResultParser.parseResult(fileLines);
		System.out.println("____________________________Report______________________________");
		result.printInfo();
		Assert.assertEquals(result.isSuccess(),false);
		Map<String, Map<String, AnsibleTaskResult>>  hostResultMap=result.getHostTaskResultMap();
		Assert.assertEquals(hostResultMap.size(),1);
	}
	@Test
	public void testAllInOneParseTest4() throws UnsupportedEncodingException {
		InputStream inStream = this.getClass().getClassLoader().getResourceAsStream("ansible-result4.txt");
		List<String> fileLines = readFromStream(inStream);
		AnsibleCallResult result = AnsibleResultParser.parseResult(fileLines);
		System.out.println("____________________________Report______________________________");
		result.printInfo();
		Assert.assertEquals(result.isSuccess(),false);
		Map<String, Map<String, AnsibleTaskResult>>  hostResultMap=result.getHostTaskResultMap();
		Assert.assertEquals(hostResultMap.size(),1);
	}
}
