package server.controllers;

import server.com.CommunicationTask;

public class LogController {
	private boolean loggingon = false;

	public LogController(boolean logging) {
		this.loggingon = logging;
	}

	/**
	 * This logs to the console in Format <--|Who: model.name,What: @param what,
	 * Message from Task: @param task.message, Message: @param message|-->
	 *
	 * @param what
	 * @param task
	 * @param message
	 */
	public void log(String who, String what, CommunicationTask task, String message) {
		if (loggingon) {
			String log = "<--|Who: " + who + ", What: " + what;
			if (task != null) {
				log += ", Message from Task: " + task.getMessage();
			}
			if (null != message) {
				log += " Message: " + message;
			}
			log += "|-->";
			System.out.println(log);
		}
	}
}
