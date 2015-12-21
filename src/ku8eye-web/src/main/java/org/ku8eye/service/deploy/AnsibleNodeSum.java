package org.ku8eye.service.deploy;

import java.util.Map;

public class AnsibleNodeSum {
	private final int ok;
	private final int changed;
	private final int unreachable;
	private final int failed;
	private Map<String, String> failedMsgs = null;

	public AnsibleNodeSum(int ok, int changed, int unreachable, int failed) {
		super();
		this.ok = ok;
		this.changed = changed;
		this.unreachable = unreachable;
		this.failed = failed;
	}

	public int getOk() {
		return ok;
	}

	public Map<String, String> getFailedMsgs() {
		return failedMsgs;
	}

	public String getFailedPrintMsg() {
		StringBuilder sb = new StringBuilder();
		if (failedMsgs != null && !failedMsgs.isEmpty()) {
			for (Map.Entry<String, String> entry : failedMsgs.entrySet()) {
				sb.append("<BR>").append(entry.getValue()).append(":").append(entry.getValue()).append("<BR>");
			}
		}
		return sb.toString();
	}

	public void setFailedMsgs(Map<String, String> failedMsgs) {
		this.failedMsgs = failedMsgs;
	}

	public int getChanged() {
		return changed;
	}

	public int getUnreachable() {
		return unreachable;
	}

	public int getFailed() {
		return failed;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + changed;
		result = prime * result + failed;
		result = prime * result + ok;
		result = prime * result + unreachable;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AnsibleNodeSum other = (AnsibleNodeSum) obj;
		if (changed != other.changed)
			return false;
		if (failed != other.failed)
			return false;
		if (ok != other.ok)
			return false;
		if (unreachable != other.unreachable)
			return false;
		return true;
	}

	public boolean isSuccess() {
		return failed == 0 && unreachable == 0;
	}

	@Override
	public String toString() {
		String str = isSuccess() ? "SUCESS"
				: "FAILED" + " [ok=" + ok + ", changed=" + changed + ", unreachable=" + unreachable + ", failed="
						+ failed + "]";
		if (failedMsgs != null) {
			for (Map.Entry<String, String> entry : failedMsgs.entrySet()) {
				str += " TASK:" + entry.getKey() + " " + entry.getValue() + "\r\n";
			}
		}
		return str;
	}

}
