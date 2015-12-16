package org.ku8eye.service.deploy;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * used for external process call support asyncall
 * 
 * @author wuzhih
 *
 */
public class ProcessCaller {
	// save process's output streams
	private CopyOnWriteArrayList<String> outputs = new CopyOnWriteArrayList<String>();
	private volatile boolean finished = true;
	private volatile boolean normalExit = false;
	private Logger LOGGER = LoggerFactory.getLogger(ProcessCaller.class);
	// if not normal exit ,then set error message
	private volatile String errorMsg;
	private volatile Process curProcess;

	public void call(String workDir, String... execArgs) throws Exception {
		finished = false;
		ProcessBuilder pb = new ProcessBuilder(execArgs);
		pb.directory(new File(workDir));
		System.out.println("working dir is " + workDir + " args " + Arrays.toString(execArgs));
		pb.redirectErrorStream(true);

		curProcess = pb.start();

		final BufferedReader reader = new BufferedReader(new InputStreamReader(curProcess.getInputStream()));
		try {
			String line;
			while ((line = reader.readLine()) != null) {
				outputs.add(line);
			}
		} catch (IOException e) {
			finished = true;
			normalExit = false;
			errorMsg = e.toString();
			LOGGER.warn("failed to read output from process", e);
		} finally {
			reader.close();

		}
		curProcess.waitFor();
		int exit = curProcess.exitValue();
		if (exit != 0) {
			normalExit = false;
			errorMsg = "return exit code :" + exit;
		} else {
			normalExit = true;
		}
		finished = true;
	}

	public CopyOnWriteArrayList<String> getOutputs() {
		return outputs;
	}

	public boolean isFinished() {
		return finished;
	}

	public void shutdownCaller() {
		Process process = curProcess;
		if (process != null && process.isAlive()) {
			process.destroyForcibly();
		}
	}

	public boolean isNormalExit() {
		return normalExit;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void asnyCall(final String workDir, final String... execArgs) {
		finished = false;
		Thread processThread = new Thread() {
			@Override
			public void run() {
				try {
					call(workDir, execArgs);

				} catch (Exception e) {
					normalExit = false;
					errorMsg = e.toString();
					finished = true;
				} finally {
					finished = true;
				}
			};

		};
		processThread.setDaemon(true);
		processThread.start();
	}

	public void waitFinish(int timeOutSeconds) {
		long timeOutMillis = System.currentTimeMillis() + timeOutSeconds * 1000;
		while (System.currentTimeMillis() < timeOutMillis && !finished) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {

			}  
		}
		if (curProcess != null && curProcess.isAlive()) {
			curProcess.destroyForcibly();

		}
	}

	public void reset() {
		this.finished = false;
		errorMsg = null;
		this.normalExit = true;
		this.outputs.clear();
		this.curProcess = null;

	}

	@Override
	public String toString() {
		return "ProcessCaller [outputs=" + outputs + ", finished=" + finished + ", normalExit=" + normalExit
				+ ", errorMsg=" + errorMsg + ", curProcess=" + curProcess + "]";
	}

}
