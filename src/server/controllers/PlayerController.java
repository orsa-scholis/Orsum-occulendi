package server.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

import server.com.CommunicationErrors;
import server.com.CommunicationTask;
import server.com.Communicator;
import server.models.PlayerModel;

public class PlayerController {
	private PlayerController controller;
	private PlayerModel model;
	private ServerController server;

	public PlayerController(ServerController server, LogController logger) {
		this.controller = this;
		this.model = new PlayerModel(server.getModel().getLogger());
		this.server = server;
	}

	public void start() {
		int playerC = server.getModel().getGames().size();
		(new Thread("Player" + playerC + ":mainThread") {
			@Override
			public void run() {
				model.setName(Thread.currentThread().getName());
				model.getLogger().log("Player " + model.getName(), "New Player Thread", null, null);
				while (model.isServerRunning()) {
					if (!model.isConnected() && !model.isInGame() && !model.isPlaying()) {
						try {
							if (initalConnection()) {
								model.setConnected(true);
								server.newPlayer();
							}
						} catch (InterruptedException e) {
							model.setServerRunning(false);
							e.printStackTrace();
						}
					}
					while (model.isConnected()) {
						if (model.isInGame() || model.isPlaying()) {
							break;
						}
						CommunicationTask connected = new CommunicationTask("Wildcard");
						connected.setWildcard(true);
						model.getCommunicator().addReceivTask(connected, false);
						while (!connected.isFinished() && !model.isInGame() && !model.isPlaying()) {
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						String[] input = connected.getMessage().split(":");
						if (input.length > 1) {
							String msg = input[0] + ":" + input[1];
							model.getLogger().log("Player " + model.getName(), "Checking in connected", connected, msg);
							switch (msg) {
							case "chat:send":
								model.getLogger().log("Player " + model.getName(), "Received", connected, "chat:send");
								CommunicationTask received = new CommunicationTask("chat:received");
								received.setEncrypt(true);
								model.getCommunicator().addSendTask(received);
								server.notifyAllPlayer(connected);
								break;
							case "info:requestGames":
								model.getLogger().log("Player " + model.getName(), "Received", connected,
										"info:requestGames");
								CommunicationTask success = new CommunicationTask(server.getModel().getAllGames());
								success.setEncrypt(true);
								model.getCommunicator().addSendTask(success);
								break;
							case "game:join":
								model.getLogger().log("Player " + model.getName(), "Received", connected, "game:join");
								if (input.length == 3) {
									int joined = server.joinGame(input[2], controller);
									if (joined != -1) {
										model.getLogger().log("Player " + model.getName(), "Joined game", null,
												input[2]);
										if (joined == 2) {
											model.setPlaying(true);
										}
										model.setInGame(true);
										CommunicationTask success1 = new CommunicationTask("success:joined:" + joined);
										success1.setEncrypt(true);
										model.getCommunicator().addSendTask(success1);
										if (joined == 2) {
											model.getGame().sendFirstSet();
										}
									} else {
										model.getLogger().log("Player " + model.getName(), "Join failed", connected,
												null);
										model.getCommunicator().sendErrorMessage(CommunicationErrors.unknownErr);
									}
								} else {
									model.getLogger().log("Player " + model.getName(), "No game found", connected,
											input[2]);
									model.getCommunicator().sendErrorMessage(CommunicationErrors.gameFull);
								}
								break;

							case "game:finished":
								model.getLogger().log("Player " + model.getName(), "Received", connected,
										"game:finished");
								break;

							case "server:newgame":
								model.getLogger().log("Player " + model.getName(), "Received", connected,
										"server:newGame");
								if (input.length == 3) {
									if (server.createGame(input[2])) {
										CommunicationTask successG = new CommunicationTask("success:created");
										successG.setEncrypt(true);
										model.getCommunicator().addSendTask(successG);
									} else {
										model.getCommunicator().sendErrorMessage(CommunicationErrors.gameExists);
									}
								}
								break;

							case "connection:disconnect":
								model.getLogger().log("Player " + model.getName(), "Received", connected,
										"connection:disconnect");
								model.setConnected(false);
								break;

							default:
								model.getLogger().log("Player " + model.getName(), "Received uncertain", connected,
										null);
								break;
							}
						}
					}
					while (model.isInGame() && !model.isPlaying()) {
						CommunicationTask ingame = new CommunicationTask("Wildcard");
						ingame.setWildcard(true);
						model.getCommunicator().addReceivTask(ingame, false);
						while (!ingame.isFinished() && !model.isPlaying()) {
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						if (ingame.isFinished()) {
							String[] input = ingame.getMessage().split(":");
							String msg = input[0] + ":" + input[1];
							model.getLogger().log("Player " + model.getName(), "Checking in ingame", ingame, msg);
							switch (msg) {
							case "chat:send":
								model.getLogger().log("Player " + model.getName(), "Received", ingame, "chat:send");
								CommunicationTask received = new CommunicationTask("chat:received");
								received.setEncrypt(true);
								model.getCommunicator().addSendTask(received);
								model.getGame().notifyOtherPlayer(ingame);
								break;
							case "game:setstone":
								model.getLogger().log("Player " + model.getName(), "Received", ingame, "game:setstone");
								if (input.length == 3) {
									if (model.getGame().setStone(Integer.parseInt(input[2]))) {
										CommunicationTask success = new CommunicationTask("success:set");
										success.setEncrypt(true);
										model.getCommunicator().addSendTask(success);
										model.getGame()
												.notifyOtherPlayer(new CommunicationTask("game:setstone:" + input[2]));
									} else {

									}
								}
								break;

							case "game:finished":
								model.getLogger().log("Player " + model.getName(), "Received", ingame, "game:finished");
								model.getGame().notifyError();
								break;

							default:
								model.getLogger().log("Player " + model.getName(), "Received uncertain", ingame, null);
								break;
							}
						} else {
							ingame.setFinished();
							break;
						}
					}
					while (model.isPlaying()) {
						CommunicationTask playing = new CommunicationTask("Wildcard");
						playing.setWildcard(true);
						model.getCommunicator().addReceivTask(playing, false);
						while (!playing.isFinished()) {
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						String[] input = playing.getMessage().split(":");
						String msg = input[0] + ":" + input[1];
						model.getLogger().log("Player " + model.getName(), "Checking in playing", playing, msg);
						switch (msg) {
						case "chat:send":
							model.getLogger().log("Player " + model.getName(), "Received", playing, "chat:send");
							CommunicationTask received = new CommunicationTask("chat:received");
							received.setEncrypt(true);
							model.getCommunicator().addSendTask(received);
							model.getGame().notifyOtherPlayer(playing);
							break;
						case "game:setstone":
							model.getLogger().log("Player " + model.getName(), "Received", playing, "game:setstone");
							if (model.getGame().setStone(Integer.parseInt(input[2]))) {
								CommunicationTask success = new CommunicationTask("success:set");
								success.setEncrypt(true);
								model.getCommunicator().addSendTask(success);
								model.getGame().notifyOtherPlayer(playing);
								checkForWinner();
							}
							break;

						case "game:finished":
							model.getLogger().log("Player " + model.getName(), "Received", playing, "game:finished");
							model.getGame().notifyError();
							break;

						default:
							model.getLogger().log("Player " + model.getName(), "Received uncertain", playing, null);
							break;
						}
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

	protected void checkForWinner() {
		if (model.getGame().getGame().getBoard().hasWon(false) || model.getGame().getGame().getBoard().hasWon(true)) {
			model.getGame().notifyWinnerAndLoser();
		} else if (model.getGame().getGame().getBoard().isTie()) {
			model.getGame().notifyAllTie();
		}
	}

	private boolean initalConnection() throws InterruptedException {
		Socket s;
		try {
			CommunicationTask connection = new CommunicationTask("connection:connect");
			model.getCommunicator().addReceivTask(connection, false);
			s = server.getModel().getSocket().accept();
			model.setPlayerSocket(s);
			(new Thread("Player" + model.getName() + " inputHandler") {
				@Override
				public void run() {
					try {
						inputHandler();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}).start();
			(new Thread("Player" + model.getName() + " outputHandler") {
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
			model.getLogger().log("Player " + model.getName(), " change Name to", null, connection.getAttr());
			model.setName(connection.getAttr());
			CommunicationTask confirm = new CommunicationTask(
					"success:accepted:" + model.getCommunicator().exportPublikKey());
			confirm.setEncrypt(false);
			model.getCommunicator().addSendTask(confirm);
			while (!confirm.isFinished()) {
				Thread.sleep(500);
			}
			CommunicationTask keyEx = new CommunicationTask("connection:keyExchange");
			model.getCommunicator().addReceivTask(keyEx, false);
			while (!keyEx.isFinished()) {
				Thread.sleep(500);
			}
			model.getCommunicator().setCryptoKey(keyEx.getMessage().split(":")[2]);
			CommunicationTask keyExSuccess = new CommunicationTask("connection:keyExchange:success");
			keyExSuccess.setEncrypt(false);
			model.getCommunicator().addSendTask(keyExSuccess);
			return true;
		} catch (IOException e) {
			model.setServerRunning(false);
		}
		return false;
	}

	private void inputHandler() throws IOException {
		model.setInput(new BufferedReader(new InputStreamReader(model.getPlayerSocket().getInputStream())));
		model.getLogger().log("Player " + model.getName(), "InputHandler started running", null, null);
		while (model.isServerRunning()) {
			try {
				if (model.getCommunicator().hasCurrentTask(true)) {
					String inline = model.getInput().readLine();
					CommunicationTask activeTask = model.getCommunicator().getCurrentTask(true);
					if (activeTask.isWildcard()) {
						if (inline != null) {
							activeTask.setMessage(inline);
							model.getLogger().log("Player " + model.getName(), "Expected Wildcard received", activeTask,
									model.getCommunicator().decryptMessage(inline));
							activeTask.setMessage(model.getCommunicator().decryptMessage(inline));
							activeTask.setFinished();
						}
					} else {
						model.getLogger().log("Player " + model.getName(), "Expected Message received", activeTask,
							model.getCommunicator().decryptMessage(inline));
						if (model.getCommunicator().doesTaskMatch(activeTask, inline)) {
							activeTask.setMessage(inline);
							activeTask.setAttr(model.getCommunicator().getAttr(activeTask));
							activeTask.setFinished();
						} else {
							model.getCommunicator().sendErrorMessage(CommunicationErrors.unknownErr);
						}
					}
				}
			} catch (NullPointerException e) {

			}
		}
		model.getLogger().log("Player " + model.getName(), "InputHandler shuting down", null, null);
		model.getInput().close();
	}

	private void outputHandler() throws IOException, InterruptedException {
		model.setOutput(new PrintStream(model.getPlayerSocket().getOutputStream()));
		model.getLogger().log("Player " + model.getName(), "OutputHandler started running", null, null);
		while (model.isServerRunning()) {
			if (model.getCommunicator().hasCurrentTask(false)) {
				CommunicationTask activeTask = model.getCommunicator().getCurrentTask(false);
				model.getOutput().println(activeTask.getMessage());
				model.getLogger().log("Player " + model.getName(), "Message sent", activeTask,
						model.getCommunicator().getDecryptedMessage(activeTask));
				activeTask.setFinished();
			}
		}
		model.getLogger().log("Player " + model.getName(), "OutputHandler shuting down", null, null);
		model.getOutput().close();
	}

	public int joinGame(GameController gm) {
		model.setGame(gm);
		return gm.join(controller);
	}

	public void leaveGame() {
		server.removeGame(model.getGame());
		model.setGame(null);
		model.setInGame(false);
		model.setPlaying(false);
	}

	public Communicator getCom() {
		return model.getCommunicator();
	}

	public PlayerModel getModel() {
		return model;
	}

}
