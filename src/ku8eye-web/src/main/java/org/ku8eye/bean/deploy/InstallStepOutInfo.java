package org.ku8eye.bean.deploy;

import java.util.List;

import org.ku8eye.service.deploy.AnsibleCallResult;

public class InstallStepOutInfo {
	private final String stepName;
	private final boolean finished;
	private boolean success;

	private final AnsibleCallResult ansibleResult;
	private final List<String> ansibleRowDatas;

	public InstallStepOutInfo(String stepName, boolean finished, AnsibleCallResult ansibleResult,
			List<String> ansibleRowDatas) {
		super();
		this.stepName = stepName;
		this.finished = finished;
		this.success = ansibleResult.isSuccess();
		this.ansibleResult = ansibleResult;
		this.ansibleRowDatas = ansibleRowDatas;
	}

	public AnsibleCallResult fetchAnsibleCallResult()
	{
		return this.ansibleResult;
	}
	public String getStepName() {
		return stepName;
	}

	public boolean isFinished() {
		return finished;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public List<String> getAnsibleRowDatas() {
		return ansibleRowDatas;
	}

	public List<String> getAnsibleOutSummary() {
		return ansibleResult.toSimpleInfo();
	}

}
