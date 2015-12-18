package org.ku8eye.bean.deploy;

import java.util.Map;

public class InstallOutputBean {

	private final Map<String, InstallStepOutInfo> stepResults;
	private final boolean finished;
	private final boolean success;

	public InstallOutputBean(Map<String, InstallStepOutInfo> stepResults, boolean finished, boolean success) {
		super();
		this.stepResults = stepResults;
		this.finished = finished;
		this.success = success;
	}

	public Map<String, InstallStepOutInfo> getStepResults() {
		return stepResults;
	}

	public boolean isFinished() {
		return finished;
	}

	public boolean isSuccess() {
		return success;
	}

	@Override
	public String toString() {

		return "InstallOutputBean [stepResults=" + stepResults + ", finished=" + finished + ", success=" + success
				+ "]";
	}

}
