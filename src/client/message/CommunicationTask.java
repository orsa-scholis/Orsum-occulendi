package client.message;

import java.util.HashMap;
import java.util.Observable;

public class CommunicationTask extends Observable {
	
	@FunctionalInterface
	public interface ResponseRunnable<A, B> {
		public void apply(A a, B b);
	}
	
	private ClientMessage message;
	private boolean sent;
	private boolean completed;
	private ServerMessage response;
	private HashMap<String, Object> userInfo = new HashMap<>();
	private ResponseRunnable<Boolean, ServerMessage> completedRunnable;
	
	public CommunicationTask(ClientMessage message) {
		this.message = message;
	}
	
	public CommunicationTask(ClientMessage message, HashMap<String, java.lang.Object> userInfo) {
		this.message = message;
		this.userInfo = userInfo;
	}
	
	public CommunicationTask(ClientMessage message, ResponseRunnable<Boolean, ServerMessage> completedHandler) {
		this.message = message;
		this.completedRunnable = completedHandler;
	}
	
	public CommunicationTask(ClientMessage message, HashMap<String, java.lang.Object> userInfo, ResponseRunnable<Boolean, ServerMessage> completedHandler) {
		this.message = message;
		this.userInfo = userInfo;
		this.completedRunnable = completedHandler;
	}
	
	// RESERVED METHODS
	private void completedCall(boolean success) {
		if (completedRunnable != null)
			completedRunnable.apply(success, this.response);
	}
	
	public void didSendClientMessage() {
		this.sent = true;
		
		if (this.countObservers() > 0) {
			this.setChanged();
			this.notifyObservers();
		}
	}
	
	public void didReceiveAnswer(ServerMessage answer) {
		this.response = answer;
		this.completed = true;
		
		completedCall(true);
	}
	
	public void abortTask() {
		this.sent = true;
		this.completed = true;
		
		completedCall(false);
	}

	// GETTERS/SETTERS
	public ClientMessage getMessage() {
		return message;
	}

	public void setMessage(ClientMessage message) {
		this.message = message;
	}

	public boolean isSent() {
		return sent;
	}

	public void setSent(boolean sent) {
		this.sent = sent;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public ServerMessage getResponse() {
		return response;
	}

	public void setResponse(ServerMessage response) {
		this.response = response;
	}

	public HashMap<String, Object> getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(HashMap<String, Object> userInfo) {
		this.userInfo = userInfo;
	}

	public void setCompletedRunnable(ResponseRunnable<Boolean, ServerMessage> completedRunnable) {
		this.completedRunnable = completedRunnable;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("CommunicationTask <");
		builder.append("message = " + message + ", ");
		if (sent) {
			builder.append("task sent, ");
		} 
		
		if (completed) {
			builder.append("completed task, ");
		}
		
		if (response != null) {
			builder.append("response = " + response + ", ");
		}
		
		if (userInfo.size() > 0) {
			builder.append("userInfo = " + userInfo + ">");
		} else {
			builder.append("no UserInfo>");
		}
		
		return builder.toString();
	}
}
