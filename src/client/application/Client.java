package client.application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javafx.application.Platform;
import client.message.ClientMessage;
import client.message.CommunicationQueue;
import client.message.CommunicationTask;
import client.message.ServerMessage;
import crypto.CryptoEngine;
import crypto.CryptoEngineEnvType;

public class Client extends Thread {
	private String ip;
	private int port;
	private Socket socket;
	private PrintWriter output;
	private BufferedReader input;
	private String userID;
	private CommunicationQueue queue = new CommunicationQueue();
	private ClientDelegate delegate;
	private boolean connected;
	private CryptoEngine cryptoEngine;

	public Client(String ip, int port, String userID) {
		super();
		
		this.ip = ip;
		this.port = port;
		this.userID = userID;
		this.cryptoEngine = new CryptoEngine(CryptoEngineEnvType.client);
	}

	@Override
	public void run() {
	//public static void main(String[] args) {
		try {
			socket = new Socket(this.ip, this.port);
			output = new PrintWriter(socket.getOutputStream(), true);
			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			connected = true;
			if (delegate != null) {
				Platform.runLater(() -> {
					delegate.clientDidSuccessfullyConnect(this);
				});
			}

			String serverMessage = null;
			while (socket.isConnected() && ((serverMessage = input.readLine()) != null)) {
				if (queue.size() > 0) {
					CommunicationTask task = queue.currentTask();

					try {
						System.out.println("received enc: " + serverMessage);
						String decryptedServerMessage = cryptoEngine.decrypt(serverMessage);
						System.out.println("decrypted: " + decryptedServerMessage);
						if (decryptedServerMessage == null) {
							throw new NullPointerException("Couldn't encrypt and send");
						}
						
						ServerMessage response = new ServerMessage(decryptedServerMessage);
						task.didReceiveAnswer(response);
						queue.removeCurrentTask();

						System.out.println(task.toString());

						if (queue.size() > 0) {
							task = queue.currentTask();
							String plainText = task.getMessage().construct();
							String encrypted = cryptoEngine.encrypt(plainText);
							
							if (encrypted == null) {
								throw new NullPointerException("Couldn't encrypt and send");
							}
							
							output.println(encrypted);
							task.didSendClientMessage();
							System.out.println("Sent Task " + task);
						} else {

						}
					} catch (Throwable e) {
						System.err.println("DEBUG INFO: ");
						System.err.println("Client class exception at receiving message");
						System.err.println("Received Message: "+ serverMessage);
						System.err.println("Current task: " + task);
						if (delegate != null) {
							delegate.clientDidReceiveException(e);
						} else {
							e.printStackTrace();
						}
					}

				} else {
					try {
						System.out.println("received enc: " + serverMessage);
						String decryptedServerMessage = cryptoEngine.decrypt(serverMessage);
						System.out.println("decrypted: " + decryptedServerMessage);
						ServerMessage message = new ServerMessage(decryptedServerMessage);

						if (this.delegate != null) {
							Platform.runLater(() -> {
								this.delegate.clientDidReceiveStandaloneServerMessage(message);
							});
						}
					}  catch (Throwable e) {
						// ServerMessage construction exception

						e.printStackTrace();
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			System.out.println("test delegate");
			if (null != delegate) {
				System.out.println("call delegate");
				Platform.runLater(() -> {
					delegate.clientDidNotConnect(this);
				});
			}
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void enqueueTask(CommunicationTask task) {
		queue.addTask(task);
		
		if (queue.count() == 1) {
			String plain = task.getMessage().construct();
			System.out.println("sending message " + plain);
			String encrypted = cryptoEngine.encrypt(plain);
			System.out.println("decrypted message to send " + cryptoEngine.decrypt(encrypted));
			
			if (encrypted == null) {
				throw new NullPointerException("Couldn't encrypt and send");
			}
			
			output.println(encrypted);
			task.didSendClientMessage();
			System.out.println("Sent Task " + task);
		}
	}

	public void connect() {
		this.start();
	}

	public void disconnect() {
		Client client = this;
		Runnable runnable = (() -> {
			if (client.socket == null || client.socket.isClosed())
				return;
			
			try {
				client.socket.shutdownInput();
				client.socket.shutdownOutput();
				client.socket.close();
			} catch (Exception e) {
				if (client.delegate != null) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							client.delegate.clientDidReceiveException(e);
						}
					});
				} else {
					e.printStackTrace();
				}
			} finally {
				if (client.delegate != null) {
					Platform.runLater(new Runnable() {
						public void run() {
							client.delegate.clientDidDisconnect(client);
						}
					});
				}
			}
		});
		
		enqueueTask(new CommunicationTask(new ClientMessage("connection", "disconnect", userID), (success, response) -> {
			if (!success || !response.getCommand().equals("success")) {
				System.err.println("Error when trying to shutdown connection: "+response+"\n==>FORCE SHUTDOWN!");
			}
			
			runnable.run();
		}));
		
		setTimeout(runnable, 1000 * 5);
	}
	
	private void setTimeout(Runnable runnable, int delay){
	    autoDispatchingThread(() -> {
	        try {
	            Thread.sleep(delay);
	            runnable.run();
	        }
	        catch (Exception e){
	            System.err.println(e);
	        }
	    });
	}
	
	private void autoDispatchingThread(Runnable runnable) {
		new Thread(runnable).start();
	}



	// GETTERS/SETTERS
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public ClientDelegate getDelegate() {
		return delegate;
	}

	public void setDelegate(ClientDelegate delegate) {
		this.delegate = delegate;
	}

	public boolean isConnected() {
		return connected;
	}
}
