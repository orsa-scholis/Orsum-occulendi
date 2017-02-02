package message;

import java.util.ArrayList;

public class CommunicationQueue extends ArrayList<CommunicationTask> {
	
	private static final long serialVersionUID = -6894890621879980947L;
	
	public void addTask(CommunicationTask task) {
		this.add(task);
	}
	
	public void abortAllTasks() {
		for (CommunicationTask task : this) {
			task.abortTask();
		}
	}
	
	public int count() {
		return this.size();
	}
	
	public CommunicationTask currentTask() {
		if (this.size() == 0)
			return null;
		return this.get(0);
	}
	
	public void removeCurrentTask() {
		this.remove(0);
	}
}
