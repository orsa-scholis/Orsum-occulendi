package server.models;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import server.application.CommunicationTask;
import server.application.TaskManager;
import javafx.application.Platform;
import server.models.PlayerModel;

public class PlayerModel {
	private String name;
	private Socket playerSocket;
	private BufferedReader input;
	private PrintStream output;
	private GameModel gm;
	private ServerModel sm;
	private final PlayerModel pm;
	private boolean connected;
	private TaskManager tasker;
	private boolean inputListener;
	private ArrayList<String> inputs;

	public void start() {
		(new Thread(name+":mainThread") {
			@Override
			public void run() {
				try {
					input = new BufferedReader(new InputStreamReader(playerSocket.getInputStream()));
					output = new PrintStream(playerSocket.getOutputStream());

					while (connected) {
						inputListener = true;
						inputListener();
						while (gm == null) {
							Thread.sleep(200);
						}
						String inline = null;
						inputListener = true;
						inputListener();
						while (gm != null || tasker.hasUnfinishedTask()) {
							Thread.sleep(10);
							if (tasker.hasUnfinishedTask()) {
								CommunicationTask current = tasker.getCurrentTask();
								System.out.println("I've got a task " + name + " " + current.isReceive() + " "
										+ current.getMessage() + " " + current.getParameter());

								if (current.isReceive()) {
									while (inputs.size() == 0) {
										Thread.sleep(100);
									}
									inline = inputs.get(0);
									String[] split2 = inline.split(":");

									if (current.getMessage().equals(split2[0] + ":" + split2[1])) {
										current.setParameter(split2[2]);
										current.setFinished(true);
									} else {
										current.setMessage(split2[0] + ":" + split2[1]);
										current.setFinished(true);
									}
									System.out.println(name + " received Client Message: " + current.getMessage() + ":"
											+ current.getParameter());
									inline = null;
									inputs.remove(0);

								} else {
									output.println(current.getMessage() + ":" + current.getParameter());
									current.setFinished(true);
								}
							}
						}
						// inputListener = false;
					}
					output.close();
					input.close();
					playerSocket.close();
				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void inputListener() {
		(new Thread(name+":listener") {
			@Override
			public void run() {
				String inline = null;
				while (inputListener) {
					try {
						if ((inline = input.readLine()) != null) {
							String[] split = inline.split(":");
							System.out.println(name + " New Client Message: " + inline);
							if ((split[0] + ":" + split[1]).equals("game:finished") && gm != null) {
								inline = null;
								gm.close(false);
							} else if ((split[0] + ":" + split[1]).equals("info:requestGames") && gm == null) {
								output.println(sm.allGames());
							} else if ((split[0] + ":" + split[1]).equals("game:join") && gm == null) {
								boolean joined = false;
								for (GameModel game : sm.getGames()) {
									if (game.getName().equals(split[2])) {
										if (joinGame(game)) {
											int playerNr = game.getPlayerNr(pm);
											output.println("success:joined:" + playerNr);
											joined = true;
											if (!sm.getMsc().isConsole()) {
												refreshViews();
											}
										} else {
											output.println("error:full");
										}
									}
								}
								if (!joined) {
									output.println("error:unknown error");
								}
							} else if ((split[0] + ":" + split[1]).equals("server:newgame") && gm == null) {
								if (split[2].length() > 2) {
									GameModel tmp = new GameModel(split[2], sm);
									if (sm.addGame(tmp)) {
										if (!sm.getMsc().isConsole()) {
											refreshViews();
										}
										output.println("success:created");
									} else {
										output.println(
												"error:Ein Spiel mit dem selben Namen existier bereits, bitte w�hle einene anderen Namen!");
									}
								} else {
									output.println("error:Name zu kurz");
								}
							} else if ((split[0] + ":" + split[1]).equals("connection:disconnect") && gm == null) {
								gm.close(false);
								output.println("success:bye");
								input.close();
								output.close();
								playerSocket.close();
							} else {
								inputs.add(inline);
							}

						}
					} catch (SocketException | NullPointerException e) {
						inputListener = false;
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	public CommunicationTask getCurrentTask() {
		return tasker.getCurrentTask();
	}

	public int newTask(CommunicationTask tsk) {
		tasker.addTask(tsk);
		return tasker.getTaskID(tsk);
	}

	public PlayerModel() {
		name = null;
		gm = null;
		playerSocket = null;
		pm = this;
		tasker = new TaskManager();
		inputListener = false;
		inputs = new ArrayList<>();
	}

	public void setAllConnections(Socket socket, ServerModel s) {
		playerSocket = socket;
		sm = s;
	}

	public void sendMessagetoClient(String message) {
		output.println(message);
	}

	private void refreshViews() {
		if (!sm.getMsc().isConsole()) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {

					sm.getMsc().refreshViews();

				}
			});
		}
	}

	public boolean joinGame(GameModel game) {
		if (game.addPlayer(pm)) {
			gm = game;
			return true;
		}
		return false;
	}

	public void leaveGame() {
		gm = null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	public CommunicationTask getTask(int id) {
		return tasker.getTask(id);
	}

	public void clearAllTasks() {
		tasker.clearAllTasks();
		inputs.clear();
	}

	public boolean isConnected() {
		return connected;
	}

	public boolean closeAllSockets() {
		try {
			input.close();
			output.close();
			playerSocket.close();
			return true;
		} catch (IOException e) {
			return false;
		}
	}

}
