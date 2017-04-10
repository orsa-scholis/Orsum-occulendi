package server.rebuild.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

import server.rebuild.com.CommunicationErrors;
import server.rebuild.com.CommunicationTask;
import server.rebuild.models.GameModel;
import server.rebuild.models.PlayerModel;

public class PlayerController {
	private PlayerController controller;
	private PlayerModel model;
	private ServerController server;

	public PlayerController(ServerController server) {
		this.controller = this;
		this.model = new PlayerModel();
		this.server = server;
	}

	public void start() {
		(new Thread("Player" + server.getModel().getPlayers().size() + ":mainThread") {
			@Override
			public void run() {
				System.out.println("New Player" + server.getModel().getPlayers().size() + " Thread");
				while (model.isServerRunning()) {
					if (!model.isConnected() && !model.isInGame() && !model.isPlaying()) {
						try {
							if (initalConnection()) {
								model.setConnected(true);
								// server.newPlayer();
							}
						} catch (InterruptedException e) {
							model.setServerRunning(false);
							e.printStackTrace();
						}
					}
					while (model.isConnected()) {
						if(model.isInGame() || model.isPlaying()){
							break;
						}
						CommunicationTask connected = new CommunicationTask("Wildcard");
						connected.setWildcard(true);
						model.getCommunicator().addReceivTask(connected, false);
						while (!connected.isFinished() || model.isInGame() || model.isPlaying()) {
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						String[] input = connected.getMessage().split(":");
						if (input.length > 1) {
							String msg = input[0] + ":" + input[1];
							msg = msg.replaceAll("[^0-9a-zA-Z:]*", "");
							System.out.println("Checking: '"+msg+"'");
							switch (msg) {
								case "info:requestGames":
									System.out.println("Received: info:requestGames");
									model.getCommunicator()
											.addSendTask(new CommunicationTask(server.getModel().getAllGames()));
									break;
								case "game:join":
									System.out.println("Received: game:join");
									if(input.length == 3){
										int joined = server.joinGame(input[2].replaceAll("[^0-9a-zA-Z:]*", ""), controller);
										if(joined != -1){
											System.out.println("Game joined!");
											if(joined == 2){
												model.setPlaying(true);
											}
											model.setInGame(true);
											model.getCommunicator().addSendTask(new CommunicationTask("success:joined:"+joined));
										}
										else{
											System.out.println("Join failed!");
											model.getCommunicator().sendErrorMessage(CommunicationErrors.unknownErr);
										}
									} else {
										System.out.println("No game name!");
										model.getCommunicator().sendErrorMessage(CommunicationErrors.gameFull);
									}
									break;

								case "game:finished":
									System.out.println("Received: game:finished");
									break;

								case "server:newgame":
									System.out.println("Received: server:newGame");
									if(input.length == 3){
										if(server.createGame(input[2].replaceAll("[^0-9a-zA-Z:]*", ""))){
											model.getCommunicator().addSendTask(new CommunicationTask("success:created"));
										} else {
											model.getCommunicator().sendErrorMessage(CommunicationErrors.gameExists);
										}
									}
									break;

								case "connection:disconnect":
									System.out.println("Received: connection:disconnect");
									model.setConnected(false);
									break;

								default:
									System.out.println("Received: uncertain, "+ msg);
									break;
							}
						}
					}
					while (model.isInGame()) {
						CommunicationTask ingame = new CommunicationTask("Wildcard");
						ingame.setWildcard(true);
						model.getCommunicator().addReceivTask(ingame, false);
						while (!ingame.isFinished() || model.isPlaying()) {
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						System.out.println(model.getCommunicator().decryptedMessage(ingame.getMessage()));
					}
					while (model.isPlaying()) {

					}
					if (!model.isConnected() && !model.isInGame() && !model.isPlaying()) {
						model.setServerRunning(false);
						server.newPlayer();
						try {
							model.getPlayerSocket().close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}).start();
	}

	private boolean initalConnection() throws InterruptedException {
		Socket s;
		try {
			CommunicationTask connection = new CommunicationTask("connection:connect");
			model.getCommunicator().addReceivTask(connection, false);
			s = server.getModel().getSocket().accept();
			model.setPlayerSocket(s);
			(new Thread("Player" + server.getModel().getPlayers().size() + " inputHandler") {
				@Override
				public void run() {
					try {
						inputHandler();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}).start();
			(new Thread("Player" + server.getModel().getPlayers().size() + " outputHandler") {
				@Override
				public void run() {
					try {
						outputHandler();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}).start();
			while (!connection.isFinished()) {
				Thread.sleep(500);
			}
			CommunicationTask confirm = new CommunicationTask("success:accepted");
			model.getCommunicator().addSendTask(confirm);
			while (!confirm.isFinished()) {
				Thread.sleep(500);
			}
			return true;
		} catch (IOException e) {
			model.setServerRunning(false);
		}
		return false;
	}

	private void inputHandler() throws IOException {
		model.setInput(new BufferedReader(new InputStreamReader(model.getPlayerSocket().getInputStream())));
		System.out.println("InputHandler started running on " + Thread.currentThread().getName());
		while (model.isServerRunning()) {
			if (model.getCommunicator().hasCurrentTask(true)) {
				String inline = model.getInput().readLine();
				CommunicationTask activeTask = model.getCommunicator().getCurrentTask(true);
				if (activeTask.isWildcard()) {
					activeTask.setMessage(model.getCommunicator().decryptedMessage(inline));
					activeTask.setFinished();
				} else {
					String input = model.getCommunicator().getDecryptedMessage(activeTask);
					System.out.println("Expected Message received: " + input);
					if (model.getCommunicator().doesTaskMatch(activeTask, inline)) {
						activeTask.setAttr(model.getCommunicator().getAttr(activeTask));
						activeTask.setFinished();
					} else {
						model.getCommunicator().sendErrorMessage(CommunicationErrors.unknownErr);
					}
				}
			}
		}
		System.out.println("InputHandler shuting down on " + Thread.currentThread().getName());
		model.getInput().close();
	}

	private void outputHandler() throws IOException, InterruptedException {
		model.setOutput(new PrintStream(model.getPlayerSocket().getOutputStream()));
		System.out.println("OutpuHandler started running on " + Thread.currentThread().getName());
		while (model.isServerRunning()) {
			if (model.getCommunicator().hasCurrentTask(false)) {
				CommunicationTask activeTask = model.getCommunicator().getCurrentTask(false);
				model.getOutput().println(activeTask.getMessage());
				System.out.println("Message sent: " + activeTask.getMessage() + "\nDecrypted: "
						+ model.getCommunicator().decryptedMessage(activeTask.getMessage()));
				activeTask.setFinished();
			}
		}
		System.out.println("OutpuHandler shuting down on " + Thread.currentThread().getName());
		model.getOutput().close();
	}

	public int joinGame(GameModel gm) {
		return gm.join(controller);
	}

}
