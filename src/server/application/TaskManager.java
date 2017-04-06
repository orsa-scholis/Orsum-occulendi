package server.application;

import java.util.ArrayList;
import crypto.aes.CryptoEngine;

public class TaskManager {
	private ArrayList<CommunicationTask> tasks;

	public TaskManager() {
		tasks = new ArrayList<>();
	}

	public void addTask(CommunicationTask task){
		tasks.add(task);

	}

	public CommunicationTask getCurrentTask(){
		for(CommunicationTask taski : tasks){
			if(!taski.isFinished()){
				return taski;
			}
		}
		return null;
	}

	public void currentTaskFinished(){
		tasks.get(0).setFinished(true);
	}

	public void removeTask(int id){
		tasks.remove(id);
	}

	public boolean hasUnfinishedTask(){
		if(tasks.size() > 0){
			for(CommunicationTask taski : tasks){
				if(!taski.isFinished()){
					return true;
				}
			}
		}
		return false;
	}

	public CommunicationTask getTask(int id) {
		if(tasks.size() > id){
			return tasks.get(id);
		}
		return null;
	}

	public int getTaskID(CommunicationTask task) {
		return tasks.indexOf(task);

	}

	public int getSize() {
		return tasks.size();
	}

	public void clearAllTasks() {
		for(CommunicationTask task : tasks){
			task.setFinished(true);
		}

	}

}
