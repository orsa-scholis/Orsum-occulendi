package server.rebuild.com;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import crypto.aes.CryptoEngine;

public class Communicator {
	private byte[] key = new byte[] { (byte) 0x00, (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05,
			(byte) 0x06, (byte) 0x07, (byte) 0x08, (byte) 0x09, (byte) 0x5a, (byte) 0x0b, (byte) 0x0c, (byte) 0x0d,
			(byte) 0x0e, (byte) 0x0f };
	private List<CommunicationTask> sendTasks = Collections.synchronizedList(new ArrayList<CommunicationTask>());
	private List<CommunicationTask> receivTask = Collections.synchronizedList(new ArrayList<CommunicationTask>());
	private CommunicationTask unexpected;
	private CryptoEngine crypto = new CryptoEngine(key);

	public void addReceivTask(CommunicationTask task, boolean encrypt) {
		System.out.println(Thread.currentThread().getName()+": New Receiv Task, Message: " + task.getMessage());
		task.setReceiv(true);
		task.setEncrypt(encrypt);
		//receivTask.add(task);
		synchronized (receivTask) {
			receivTask.add(task);
		}
	}

	public String getDecryptedMessage(CommunicationTask task) {
		if (task.isEncrypt()) {
			return crypto.decrypt(task.getMessage());
		} else {
			return task.getMessage();
		}
	}

	public void addSendTask(CommunicationTask task) {
		System.out.println(Thread.currentThread().getName()+": New Send Task, Message: " + task.getMessage());
		task.setMessage(crypto.encrypt(task.getMessage()));
		task.setReceiv(false);
		//sendTasks.add(task);
		synchronized (sendTasks) {
			sendTasks.add(task);
		}
	}

	public CommunicationTask getCurrentTask(boolean receiv) {
		List<CommunicationTask> list;
		if (receiv) {
			list = receivTask;
		} else {
			list = sendTasks;
		}
		if (list.size() > 0) {
			synchronized (list) {
				Iterator<CommunicationTask> i = list.iterator();
				while(i.hasNext()){
					CommunicationTask current = i.next();
					if(!current.isFinished()){
						return current;
					}
				}
			}
		}
		return null;
	}

	public boolean hasCurrentTask(boolean receiv) {
		return getCurrentTask(receiv) != null;
	}

	public String getAttr(CommunicationTask task) {
		if (getDecryptedMessage(task).split(":").length == 3) {
			return getDecryptedMessage(task).split(":")[2];
		} else {
			return null;
		}
	}

	public boolean doesTaskMatch(CommunicationTask task, String input) {
		String decryptTask = getDecryptedMessage(task);
		String[] splitT = decryptTask.split(":");
		String[] splitI = decryptedMessage(input).split(":");
		if (splitI.length > 0 && splitT.length > 0) {
			if (splitI.length == splitT.length) {
				for (int i = 0; i < 3; i++) {
					if (!splitT[i].equals(splitI[i])) {
						return false;
					}
				}
				return true;
			} else {
				if (splitI.length > splitT.length) {
					for (int i = 0; i < splitT.length; i++) {
						if (!splitT[i].equals(splitI[i])) {
							return false;
						}
					}
					return true;
				} else {
					for (int i = 0; i < splitI.length; i++) {
						if (!splitT[i].equals(splitI[i])) {
							return false;
						}
					}
				}
			}
		}
		return false;
	}

	public String decryptedMessage(String input) {
		try {
			return crypto.decrypt(input);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void sendErrorMessage(Enum<CommunicationErrors> er) {
		CommunicationTask error = new CommunicationTask(er.toString());
		addSendTask(error);
	}

	public void clearTasks(){
		synchronized (receivTask) {
			receivTask = Collections.synchronizedList(new ArrayList<CommunicationTask>());
		}
		synchronized (sendTasks) {
			sendTasks = Collections.synchronizedList(new ArrayList<CommunicationTask>());
		}
	}

	public CommunicationTask getUnexpected() {
		return unexpected;
	}

	public void setUnexpected(CommunicationTask unexpected) {
		this.unexpected = unexpected;
	}

}
