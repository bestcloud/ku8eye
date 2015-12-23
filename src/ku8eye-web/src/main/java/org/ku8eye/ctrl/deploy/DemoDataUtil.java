package org.ku8eye.ctrl.deploy;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.LinkedList;
import java.util.List;

import org.ku8eye.service.deploy.AnsibleCallResult;
import org.ku8eye.service.deploy.AnsibleResultParser;

public class DemoDataUtil {

	private static int calledTimes=0;
	public static List<String> getFakeAnsibleOutput() {
		InputStream inStream = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("org/ku8eye/ctrl/deploy/ansbile-result1.txt");
		return readFromStream(inStream);
	}

	
	public static AnsibleCallResult getFakeAnsibleResult()
	{
		InputStream inStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("org/ku8eye/ctrl/deploy/ansible-result2.txt");
		List<String> fileLines = readFromStream(inStream);
		AnsibleCallResult result = AnsibleResultParser.parseResult(fileLines);
		calledTimes++;
		if(calledTimes++>5)
		{
			result.setAnsibleFinished(true);
			calledTimes=0;
		}
		return result;
	}
	public static List<String> readFromStream(InputStream inStream) {
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
}
