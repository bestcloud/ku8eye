package org.ku8eye.service.deploy;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class AnsibleCallResult {
	private Map<String, Map<String, AnsibleTaskResult>> hostTaskResultMap = new HashMap<String, Map<String, AnsibleTaskResult>>();
	private Map<String, AnsibleNodeSum> nodeTotalSumaryMap = new LinkedHashMap<String, AnsibleNodeSum>();
	private boolean ansibleFinished = false;
	private String stepName;

	public void addTaskSumary(String groupName, String taskName, String node, String summary) {

		findTaskResult(groupName, taskName).andNodeSumary(node, summary);
	}

	/**
	 * 是否ansible结果完成，如果否，表示还没结束，当前是部分解析结果
	 * 
	 * @return
	 */
	public boolean isAnsibleFinished() {
		return ansibleFinished;
	}

	public void setAnsibleFinished(boolean ansibleFinished) {
		this.ansibleFinished = ansibleFinished;
	}

	public String getStepName() {
		return stepName;
	}

	public void setStepName(String stepName) {
		this.stepName = stepName;
	}

	private Map<String, String> findFailedTasks(String nodeIP) {
		Map<String, String> faildTasks = new LinkedHashMap<String, String>();
		for (Map<String, AnsibleTaskResult> groupTasks : hostTaskResultMap.values()) {
			for (AnsibleTaskResult resut : groupTasks.values()) {
				for (Map.Entry<String, String> ipSumEnry : resut.getNodeSumarys().entrySet()) {
					String sumary = ipSumEnry.getValue();
					if (ipSumEnry.getKey().equals(nodeIP)
							&& (sumary.startsWith("failed:") || sumary.startsWith("fatal:"))) {
						// faildTasks
						faildTasks.put(resut.getTaskName(), sumary);
					}
				}
			}
		}
		return faildTasks;
	}

	public void addTotalSumary(String node, String summary) {
		String[] items = summary.split("\\s");
		int ok = 0;
		int changed = 0;
		int unreachable = 0;
		int failed = 0;
		for (String item : items) {
			if (item.startsWith("ok")) {
				ok = getValue(item);
			} else if (item.startsWith("changed")) {
				changed = getValue(item);
			} else if (item.startsWith("unreachable")) {
				unreachable = getValue(item);
			} else if (item.startsWith("failed")) {
				failed = getValue(item);
			}
		}
		AnsibleNodeSum sum = new AnsibleNodeSum(ok, changed, unreachable, failed);
		if (failed > 0 || unreachable > 0) {
			sum.setFailedMsgs(this.findFailedTasks(node));
		}
		nodeTotalSumaryMap.put(node, sum);

	}

	private static int getValue(String item) {
		return Integer.parseInt(item.substring(item.indexOf("=") + 1));
	}

	public Map<String, AnsibleNodeSum> getNodeTotalSumaryMap() {
		return nodeTotalSumaryMap;
	}

	/**
	 * key is ansible group name value is Map<String, AnsibleTaskResult>,map's
	 * key is task name ,
	 * 
	 * @return
	 */
	public Map<String, Map<String, AnsibleTaskResult>> getHostTaskResultMap() {
		return hostTaskResultMap;
	}

	private AnsibleTaskResult findTaskResult(String groupName, String taskName) {
		Map<String, AnsibleTaskResult> taskResulMap = hostTaskResultMap.get(groupName);
		if (taskResulMap == null) {
			taskResulMap = new HashMap<String, AnsibleTaskResult>();
			hostTaskResultMap.put(groupName, taskResulMap);
		}
		AnsibleTaskResult tastResult = taskResulMap.get(taskName);
		if (tastResult == null) {
			tastResult = new AnsibleTaskResult(taskName);
			taskResulMap.put(taskName, tastResult);
		}
		return tastResult;
	}

	public void setTaskResult(String groupName, String taskName, boolean success, String errmsg) {
		findTaskResult(groupName, taskName).setResult(success, errmsg);

	}

	public boolean isSuccess() {
		for (Map<String, AnsibleTaskResult> groupTasks : hostTaskResultMap.values()) {

			for (AnsibleTaskResult taskResult : groupTasks.values()) {
				if (!taskResult.isSuccess()) {
					return false;
				}
			}
		}
		return true;
	}

	public String printInfo() {
		StringBuilder sb = new StringBuilder();
		sb.append("Ansible job " + (this.isSuccess() ? " SUCCESS " : " FAILED \r\n"));
		for (Map.Entry<String, Map<String, AnsibleTaskResult>> groupTasks : hostTaskResultMap.entrySet()) {
			System.out.println("group " + groupTasks.getKey());
			for (AnsibleTaskResult taskResult : groupTasks.getValue().values()) {
				sb.append("   task " + taskResult.getTaskName()
						+ (taskResult.isSuccess() ? " SUCCESS "
								: (taskResult.getFailMsg() == null) ? "" : " FAILED :" + taskResult.getFailMsg()))
						.append("\r\n");
				for (Map.Entry<String, String> taskSumerys : taskResult.getNodeSumarys().entrySet())

				{
					sb.append("       node  " + taskSumerys.getKey() + " result:" + taskSumerys.getValue())
							.append("\r\n");
				}
			}
		}
		for (Map.Entry<String, AnsibleNodeSum> taskSumerys : nodeTotalSumaryMap.entrySet())

		{
			sb.append("node  " + taskSumerys.getKey() + " sumary:" + taskSumerys.getValue()).append("\r\n");
		}
		return sb.toString();
	}

	public void markSuccess() {
		for (Map<String, AnsibleTaskResult> groupTasks : hostTaskResultMap.values()) {
			for (AnsibleTaskResult taskResult : groupTasks.values()) {
				taskResult.setResult(true, null);
			}
		}

	}
}
