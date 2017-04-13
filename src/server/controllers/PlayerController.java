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
	private int playerC;

	public PlayerController(ServerController server) {
		this.controller = this;
		this.model = new PlayerModel();
		this.server = server;
	}

	public void start() {
		(new Thread("Player" + playerC + ":mainThread") {
			@Override
			public void run() {
				model.setName(Thread.currentThread().getName());
				System.out.println("New Player" + server.getModel().getPlayers().size() + " Thread");
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
							System.out.println(model.getName() + ": Checking: '" + msg + "'");
							switch (msg) {
							case "info:requestGames":
								System.out.println(model.getName() + ": Received: info:requestGames");
								model.getCommunicator()
										.addSendTask(new CommunicationTask(server.getModel().getAllGames()));
								break;
							case "game:join":
								System.out.println(model.getName() + ": Received: game:join");
								if (input.length == 3) {
									int joined = server.joinGame(input[2], controller);
									if (joined != -1) {
										System.out.println(model.getName() + ": Game joined!");
										if (joined == 2) {
											model.setPlaying(true);
										}
										model.setInGame(true);
										model.getCommunicator()
												.addSendTask(new CommunicationTask("success:joined:" + joined));
										if(joined == 2){
											model.getGame().sendFirstSet();
										}
									} else {
										System.out.println(model.getName() + ": Join failed!");
										model.getCommunicator().sendErrorMessage(CommunicationErrors.unknownErr);
									}
								} else {
									System.out.println(model.getName() + ": No game name!");
									model.getCommunicator().sendErrorMessage(CommunicationErrors.gameFull);
								}
								break;

							case "game:finished":
								System.out.println(model.getName() + ": Received: game:finished");
								break;

							case "server:newgame":
								System.out.println(model.getName() + ": Received: server:newGame");
								if (input.length == 3) {
									if (server.createGame(input[2])) {
										model.getCommunicator().addSendTask(new CommunicationTask("success:created"));
									} else {
										model.getCommunicator().sendErrorMessage(CommunicationErrors.gameExists);
									}
								}
								break;

							case "connection:disconnect":
								System.out.println(model.getName() + ": Received: connection:disconnect");
								model.setConnected(false);
								break;

							default:
								System.out.println(model.getName() + ": Received: uncertain, " + msg);
								break;
							}
						}
					}
					while (model.isInGame()) {
						if(model.isPlaying()){
							break;
						}
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
							System.out.println(model.getName() + ": Checking ingame: " + ingame.getMessage());
							String[] input = ingame.getMessage().split(":");
							String msg = input[0] + ":" + input[1];
							switch (msg) {
								case "game:setstone":
									System.out.println(model.getName() + ": Received: game:setstone");
									if (input.length == 3) {
										if (model.getGame().setStone(Integer.parseInt(input[2]))) {
											model.getCommunicator().addSendTask(new CommunicationTask("success:set"));
											model.getGame().notifyOtherPlayer(new CommunicationTask("game:setstone:"+input[2]));
										} else {

										}
									}
									break;

								case "game:finished":
									System.out.println(model.getName() + ": Received: game:finished");
									model.getGame().notifyOtherPlayer(new CommunicationTask(""));
									model.setInGame(false);
									break;

								default:
									System.out.println(model.getName() + ": Received: uncertain, " + msg);
									break;
							}
						} else {
							ingame.setFinished();
							break;
						}
					}
					while (model.isPlaying()) {
						if (!model.getGame().getGame().getBoard().isTie() && !model.getGame().getGame().hasWon()) {
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
							System.out.println(model.getName() + ": Checking playing: " + playing.getMessage());
							String[] input = playing.getMessage().split(":");
							String msg = input[0] + ":" + input[1];
							switch (msg) {
							case "game:setstone":
								System.out.println(model.getName() + ": Received: game:setstone");
								if (model.getGame().setStone(Integer.parseInt(input[2]))) {
									model.getCommunicator().addSendTask(new CommunicationTask("success:set"));
									model.getGame().notifyOtherPlayer(playing);
								} else {

								}
								break;

							default:
								System.out.println(model.getName() + ": Received: uncertain, " + msg);
								break;
							}
						} else {
							if(model.getGame().getGame().getBoard().isTie()){
								String name = model.getGame().getGame().getName();
								model.getGame().notifyAllTie();
								server.removeGame(name);
							} else {
								String name = model.getGame().getGame().getName();
								model.getGame().notifyWinnerAndLoser();
								server.removeGame(name);
							}
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
			model.setName(connection.getAttr());
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
		System.out.println("InputHandler started running on " + model.getName());
		while (model.isServerRunning()) {
			if (model.getCommunicator().hasCurrentTask(true)) {
				String inline = model.getInput().readLine();
				CommunicationTask activeTask = model.getCommunicator().getCurrentTask(true);
				if (activeTask.isWildcard()) {
					if (inline != null) {
						activeTask.setMessage(model.getCommunicator().decryptedMessage(inline));
						activeTask.setFinished();
					}
				} else {
					//String input = model.getCommunicator().getDecryptedMessage(activeTask);
					System.out.println(model.getName() + ": Expected Message received: " + inline+"\n\tDecrypted: "+model.getCommunicator().decryptedMessage(inline));
					if (model.getCommunicator().doesTaskMatch(activeTask, inline)) {
						activeTask.setMessage(inline);
						activeTask.setEncrypt(true);
						activeTask.setAttr(model.getCommunicator().getAttr(activeTask));
						activeTask.setFinished();
					} else {
						model.getCommunicator().sendErrorMessage(CommunicationErrors.unknownErr);
					}
				}
			}
		}
		System.out.println("InputHandler shuting down on " + model.getName());
		model.getInput().close();
	}

	private void outputHandler() throws IOException, InterruptedException {
		model.setOutput(new PrintStream(model.getPlayerSocket().getOutputStream()));
		System.out.println("OutpuHandler started running on " + model.getName());
		while (model.isServerRunning()) {
			if (model.getCommunicator().hasCurrentTask(false)) {
				CommunicationTask activeTask = model.getCommunicator().getCurrentTask(false);
				model.getOutput().println(activeTask.getMessage());
				System.out.println(model.getName() + ": Message sent: " + activeTask.getMessage() + "\n\tDecrypted: "
						+ model.getCommunicator().decryptedMessage(activeTask.getMessage())
						+ "\n----------------------------------------------");
				activeTask.setFinished();
			}
		}
		System.out.println("OutpuHandler shuting down on " + model.getName());
		model.getOutput().close();
	}

	public int joinGame(GameController gm) {
		model.setGame(gm);
		return gm.join(controller);
	}

	public Communicator getCom() {
		return model.getCommunicator();
	}

	public PlayerModel getModel() {
		return model;
	}

}
