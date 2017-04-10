package server.rebuild.com;

public class CommunicationTask {
	private boolean finished = false;
	private boolean receiv = false;
	private boolean encrypt = false;
	private boolean wildcard = false;
	private String message;
	private String attr;

	public CommunicationTask(String message) {
		this.message = message;
	}

	public boolean isEncrypt() {
		return encrypt;
	}

	public void setEncrypt(boolean encrypt) {
		this.encrypt = encrypt;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	public boolean isReceiv() {
		return receiv;
	}

	public void setReceiv(boolean receiv) {
		this.receiv = receiv;
	}

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

	public String getAttr() {
		return attr;
	}

	public void setAttr(String attr) {
		this.attr = attr;
	}

	public boolean isWildcard() {
		return wildcard;
	}

	public void setWildcard(boolean wildcard){
		this.wildcard = wildcard;
	}
}
