package org.ku8eye.service.deploy;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class AnsibleCallResult {
	private Map<String, Map<String, AnsibleTaskResult>> hostTaskResultMap = new HashMap<String, Map<String, AnsibleTaskResult>>();
	private Map<String, String> nodeTotalSumaryMap = new LinkedHashMap<String, String>();

	public void addTaskSumary(String groupName, String taskName, String node, String summary) {

		findTaskResult(groupName, taskName).andNodeSumary(node, summary);
	}

	public void addTotalSumary(String node, String summary) {
		nodeTotalSumaryMap.put(node, summary);

	}

	public Map<String, String> getNodeTotalSumaryMap() {
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
		System.out.println("group:" + groupName + ",task:" + taskName + " " + success);
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

	public void printInfo() {
		System.out.println("Ansible job " + (this.isSuccess() ? " SUCCESS " : " FAILED "));
		for (Map.Entry<String, Map<String, AnsibleTaskResult>> groupTasks : hostTaskResultMap.entrySet()) {
			System.out.println("group " + groupTasks.getKey());
			for (AnsibleTaskResult taskResult : groupTasks.getValue().values()) {
				System.out.println("   task " + taskResult.getTaskName()
						+ (taskResult.isSuccess() ? " SUCCESS " :(taskResult.getFailMsg()==null)?"": " FAILED :" +taskResult.getFailMsg() ));
				for (Map.Entry<String, String> taskSumerys : taskResult.getNodeSumarys().entrySet())

				{
					System.out.println("       node  " + taskSumerys.getKey() + " result:" + taskSumerys.getValue());
				}
			}
		}
		for (Map.Entry<String, String> taskSumerys : nodeTotalSumaryMap.entrySet())

		{
			System.out.println("node  " + taskSumerys.getKey() + " sumary:" + taskSumerys.getValue());
		}
	}

	public void markSuccess() {
		for (Map<String, AnsibleTaskResult> groupTasks : hostTaskResultMap.values()) {
			for (AnsibleTaskResult taskResult : groupTasks.values()) {
				taskResult.setResult(true, null);
			}
		}

	}
}
