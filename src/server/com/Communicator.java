package server.com;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import crypto.CryptoEngine;
import crypto.CryptoEngineEnvType;

public class Communicator {
	private List<CommunicationTask> sendTasks = Collections.synchronizedList(new ArrayList<CommunicationTask>());
	private List<CommunicationTask> receivTask = Collections.synchronizedList(new ArrayList<CommunicationTask>());
	private CryptoEngine crypto = new CryptoEngine(CryptoEngineEnvType.server);

	public String exportPublikKey() throws IOException{
		return crypto.exportPublicKey();
	}

	public void setCryptoKey(String keyAsString){
		byte[] key = crypto.rsaDecrypt(keyAsString, crypto.getKeyPair().getPrivate());
		crypto.setKey(key);
	}

	public void addReceivTask(CommunicationTask task, boolean encrypt) {
		System.out.println(Thread.currentThread().getName()+": New Receiv Task, Message: " + task.getMessage());
		task.setReceiv(true);
		task.setEncrypt(encrypt);
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
		if(task.isEncrypt()){
			task.setMessage(crypto.encrypt(task.getMessage()));
		}
		task.setReceiv(false);
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
		String[] splitI = decryptMessage(input).split(":");
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

	public String decryptMessage(String input) {
		try {
			return crypto.decrypt(input);
		} catch (Exception e) {
			return input;
		}
	}

	public void sendErrorMessage(Enum<CommunicationErrors> er) {
		CommunicationTask error = new CommunicationTask(er.toString());
		addSendTask(error);
	}

	public void clearTasks(){
		synchronized (receivTask) {
			Iterator<CommunicationTask> i = receivTask.iterator();
			while(i.hasNext()){
				CommunicationTask current = i.next();
				if(current.isReceiv()){
					current.setMessage("null:null");
				}
				current.setFinished();
			}
		}
		synchronized (sendTasks) {
			Iterator<CommunicationTask> i = sendTasks.iterator();
			while(i.hasNext()){
				CommunicationTask current = i.next();
				current.setFinished();
			}
		}
	}
}
