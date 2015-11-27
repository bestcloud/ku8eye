package org.ku8eye.service.deploy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * used for external process call
 * support asyncall
 * 
 * @author wuzhih
 *
 */
public class ProcessCaller {
	// save process's output streams
	private CopyOnWriteArrayList<String> outputs = new CopyOnWriteArrayList<String>();
	private volatile boolean finished;
	private volatile boolean normalExit = false;
	private Logger LOGGER = LoggerFactory.getLogger(ProcessCaller.class);
	// if not normal exit ,then set error message
	private volatile String errorMsg;
	private Thread processThread;

	public void call(String... execArgs) throws Exception {
		ProcessBuilder pb = new ProcessBuilder(execArgs);
		pb.redirectErrorStream(true);
		Process process = pb.start();
		final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
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
			IOUtils.closeQuietly(reader);
		}
		process.waitFor();
		int exit = process.exitValue();
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

	public boolean isNormalExit() {
		return normalExit;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void asnyCall(final String... execArgs) {
		processThread = new Thread() {
			public void run() {
				try {
					call(execArgs);

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

	public Thread getProcessThread() {
		return processThread;
	}

}
