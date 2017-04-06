package server.rebuild.com;

import java.util.ArrayList;

import crypto.aes.CryptoEngine;

public class Communicator {
	private byte[] key = new byte[] {
			(byte)0x00, (byte)0x01, (byte)0x02, (byte)0x03,
		    (byte)0x04, (byte)0x05, (byte)0x06, (byte)0x07,
		    (byte)0x08, (byte)0x09, (byte)0x5a, (byte)0x0b,
		    (byte)0x0c, (byte)0x0d, (byte)0x0e, (byte)0x0f
	};
	private ArrayList<CommunicationTask> tasks = new ArrayList<>();
	private CryptoEngine crypto = new CryptoEngine(key);

	public void addReceivTask(CommunicationTask task){
		tasks.add(task);
	}

	public String getDecryptedMessage(CommunicationTask task){
		return crypto.decrypt(task.getMessage());
	}

	public void addSendTask(CommunicationTask task){
		task.setMessage(crypto.encrypt(task.getMessage()));
		tasks.add(task);
	}

	public CommunicationTask getCurrentTask(){
		if(tasks.size()>0){
			for(CommunicationTask taski : tasks){
				if(taski.isFinished()){
					return taski;
				}
			}
		}
		return null;
	}

	public void currentTaskFinished(){
		if(tasks.size()>0){
			tasks.get(0).setFinished();
		}
	}

}
