package server.rebuild.com;

public class CommunicationTask {
	private boolean finished = false;
	private boolean receiv = false;
	private String message;




	public void setFinished() {
		finished = true;
	}

	public boolean isFinished() {
		return finished;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}


}
