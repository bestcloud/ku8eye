package org.ku8eye.service.deploy;

import java.util.HashMap;
import java.util.Map;

public class AnsibleTaskResult {
	private final String taskName;
	private boolean success;
	private String failMsg;
	// key是节点IP，value是总结
	private Map<String, String> nodeSumarys = new HashMap<String, String>();

	public AnsibleTaskResult(String taskName) {
		super();
		this.taskName = taskName;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setResult(boolean success,String errMgs) {
		this.success = success;
		this.failMsg=errMgs;
	}

	public String getFailMsg() {
		return failMsg;
	}

	public void setFailMsg(String failMsg) {
		this.failMsg = failMsg;
	}

	public String getTaskName() {
		return taskName;
	}

	public Map<String, String> getNodeSumarys() {
		return nodeSumarys;
	}

	public void andNodeSumary(String node, String summary) {
		nodeSumarys.put(node, summary);
	}
}
