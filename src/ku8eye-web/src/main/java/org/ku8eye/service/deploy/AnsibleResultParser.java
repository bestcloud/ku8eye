package org.ku8eye.service.deploy;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnsibleResultParser {
	static Pattern IPPATTEN;

	static {
		String rexp = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";

		IPPATTEN = Pattern.compile(rexp);
	}

	private static String containsIp(String line) {
		Matcher mt = IPPATTEN.matcher(line);
		if (mt.find()) {
			return line.substring(mt.start(), mt.end() + 2);
		}
		return null;
	}

	private static String getToken(String content, String prex, char endChar) {
		try {
			return content.substring(prex.length(), content.indexOf(endChar));
		} catch (RuntimeException e) {
			e.printStackTrace();
			System.out.println(content + " prex:" + prex + " char:" + endChar);
			throw e;
		}
	}

	public static AnsibleCallResult parseResult(List<String> ansibleOut) {
		AnsibleCallResult result = new AnsibleCallResult();
		String playPrex = "PLAY [";
		String sshGatherFacts = "GATHERING FACTS ";
		String taskPrex = "TASK: [";
		String failPrex = "FATAL:";
		String taskFatalPrex = "fatal: [";
		String taskOkPrex = "ok: [";
		String taskChangedPrex = "changed: [";
		String taskfailedPrex = "failed: [";
		String taskSkipedPrex = "skipping: [";
		String playRecapPrex = "PLAY RECAP ";
		boolean groupBegin = false;
		boolean taskBegin = false;
		boolean playRecapBegin = false;
		boolean hasTaskFaild = false;
		String groupName = null;
		String taskName = null;
		String taskErrMsg = null;
		int lineIndx=0;
		for (String line : ansibleOut) {
			if(lineIndx==0&& line.startsWith("ERROR: "))
			{//param error ,so ansible exits
				String errmsg=line.substring("ERROR: ".length());
				result.setAnsibleFinished(true);
				result.setTaskResult("INIT", "init", false, errmsg);
				return result;
			}
			lineIndx++;
			if (line.startsWith(playPrex)) {
				groupName = getToken(line, playPrex, ']');
				groupBegin = true;
				playRecapBegin = false;
			}
             
			if (groupBegin) {
				if (line.startsWith(taskPrex)) {
					taskName = getToken(line, taskPrex, ']');
					taskName = taskName.substring(taskName.indexOf('|') + 1);
					taskBegin = true;
				} else if (line.startsWith(sshGatherFacts)) {
					taskName = "GATHERING FACTS";
					taskBegin = true;
				}
			}
			if (groupBegin && taskBegin) {
				if (line.startsWith(failPrex)) {
					line += "\r";
					taskErrMsg = getToken(line, failPrex, '\r');
					result.setTaskResult(groupName, taskName, false, taskErrMsg);
					hasTaskFaild = true;
				} else if (line.startsWith(taskOkPrex) || line.startsWith(taskChangedPrex)
						|| line.startsWith(taskSkipedPrex)) {
					String ip = line.substring(line.indexOf("[") + 1, line.indexOf("]"));
					String sumary = line.substring(0, line.indexOf(":"));
					result.addTaskSumary(groupName, taskName, ip, sumary);
				} else if (line.startsWith(taskFatalPrex)) {
					String ip = line.substring(line.indexOf("[") + 1, line.indexOf("]"));
					String token = "=> ";
					String sumary = line.substring(line.indexOf(token) + token.length(), line.length() );
					result.addTaskSumary(groupName, taskName, ip, sumary);
					taskErrMsg = sumary;
					result.setTaskResult(groupName, taskName, false, taskErrMsg);
					hasTaskFaild = true;
					sumary ="fatal: "+sumary;
					result.addTaskSumary(groupName, taskName, ip, sumary);
				} else if (line.startsWith(taskfailedPrex)) {
					String ip = line.substring(line.indexOf("[") + 1, line.indexOf("]"));
					String token = "=> ";
					String sumary ="failed: "+line.substring(line.indexOf(token) + token.length(), line.length() );
					result.addTaskSumary(groupName, taskName, ip, sumary);
				} else if (line.startsWith(playRecapPrex)) {
					groupBegin = false;
					taskBegin = false;
					playRecapBegin = true;
				}
			}
			if (playRecapBegin == true) {
				if (!hasTaskFaild) {
					result.markSuccess();
				}
				if (!line.startsWith(" ")) {
					String ip = containsIp(line);
					if (ip != null) {
						result.addTotalSumary(ip, line.substring(line.indexOf(":") + 1));
					}
				}
			}
		}
		return result;
	}
}
