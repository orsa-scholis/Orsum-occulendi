package application;

public class CommunicationTask {
	private boolean finished;
	private boolean receive;
	private String message;
	private String parameter;
	
	public CommunicationTask(boolean rece, String message, String params) {
		receive = rece;
		this.message = message;
		parameter = params;
		finished = false;
	}

	
	
	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}
	
	public boolean isReceive() {
		return receive;
	}

	public void setReceive(boolean receive) {
		this.receive = receive;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}
	
	
	
}
