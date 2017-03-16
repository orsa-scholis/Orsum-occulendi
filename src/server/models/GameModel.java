package server.models;

import server.application.CommunicationTask;
import javafx.application.Platform;

public class GameModel {
	private String name;
	private PlayerModel player1 = null;
	private PlayerModel player2 = null;
	private PlayerModel playing = null;
	private BoardModel board;
	private ServerModel server;
	private boolean running;

	public GameModel(String name, ServerModel server) {
		this.name = name;
		board = new BoardModel();
		running = false;
		this.server = server;
	}

	public void start() {
		board = new BoardModel();
		(new Thread("Game:"+name) {
			@Override
			public void run() {
				System.out.println("I started to run " + name);
				while (running) {
					if (!hasWon() && !board.isTie()) {
						CommunicationTask task = new CommunicationTask(true, "game:setstone", null);
						int id = playing.newTask(task);
						String originalM = task.getMessage();
						System.out.println(playing.getName() + ", Task: " + task.getMessage());
						while (!playing.getTask(id).isFinished()) {
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						System.out.println("Original message: " + originalM + "; received message: "
								+ playing.getTask(id).getMessage() + ":" + playing.getTask(id).getParameter());
						if (originalM.equals(playing.getTask(id).getMessage())) {
							board.setStone(playerOne(), Integer.parseInt(playing.getTask(id).getParameter()));
							playing.newTask(new CommunicationTask(false, "success:set", null));

							if (hasWon() || board.isTie()) {
								player1.clearAllTasks();
								player2.clearAllTasks();

								if (hasWon()) {
									if (playing.equals(player1)) {
										playing.newTask(new CommunicationTask(false, "success:set", null));
										player2.newTask(new CommunicationTask(false, "game:setstone",
												"" + board.getLastSetRow()));
										CommunicationTask task1 = new CommunicationTask(false, "game:finished", "0");
										player1.newTask(task1);
										CommunicationTask task2 = new CommunicationTask(false, "game:finished", "1");
										player2.newTask(task2);
									} else {
										playing.newTask(new CommunicationTask(false, "success:set", null));
										player1.newTask(new CommunicationTask(false, "game:setstone",
												"" + board.getLastSetRow()));
										CommunicationTask task1 = new CommunicationTask(false, "game:finished", "1");
										player1.newTask(task1);
										CommunicationTask task2 = new CommunicationTask(false, "game:finished", "0");
										player2.newTask(task2);
									}
								} else {
									playing.newTask(new CommunicationTask(false, "success:set", null));
									CommunicationTask task1 = new CommunicationTask(false, "game:finished", "2");
									player1.newTask(task1);
									CommunicationTask task2 = new CommunicationTask(false, "game:finished", "2");
									player2.newTask(task2);
								}
								close(true);
								break;

							}
							changePlaying();
							playing.newTask(new CommunicationTask(false, "game:setstone", "" + board.getLastSetRow()));
						}
						else{
							running = false;
						}
					}
				}
			}
		}).start();
	}

	public boolean isRunning() {
		return running;
	}

	public boolean setStone(int row) {
		if (board.setStone(playerOne(), row)) {
			return true;
		}
		return false;
	}

	public boolean hasWon() {
		if (board.hasWon(playerOne())) {
			return true;
		}
		return false;
	}

	public void changePlaying() {
		if (playing.equals(player1)) {
			playing = player2;
		} else {
			playing = player1;
		}
	}

	private boolean playerOne() {
		if (!player1.equals(null)) {
			if (player1.equals(playing)) {
				return true;
			}
		}
		return false;
	}

	public boolean addPlayer(PlayerModel pm) {
		if (player1 == null) {
			player1 = pm;
			playing = pm;
			return true;
		} else if (player2 == null) {
			player2 = pm;
			running = true;
			start();
			return true;
		}
		return false;
	}

	public void close(boolean finished) {
		running = false;
		if (player1 != null) {
			if (!finished && player1.isConnected()) {
				player1.sendMessagetoClient("game:finished:4");
			}
			player1.leaveGame();
			player1 = null;
		}
		if (player2 != null) {
			if (!finished && player2.isConnected()) {
				player2.sendMessagetoClient("game:finished:4");
			}
			player2.leaveGame();
			player2 = null;
		}

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				server.getMsc().refreshViews();
			}
		});

	}

	public PlayerModel getPlaying() {
		return playing;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public PlayerModel getPlayer1() {
		return player1;
	}

	public void setPlayer1(PlayerModel player1) {
		this.player1 = player1;
	}

	public PlayerModel getPlayer2() {
		return player2;
	}

	public void setPlayer2(PlayerModel player2) {
		this.player2 = player2;
	}

	public BoardModel getBoard() {
		return board;
	}

	public void setBoard(BoardModel board) {
		this.board = board;
	}

	public ServerModel getServer() {
		return server;
	}

	public void setServer(ServerModel server) {
		this.server = server;
	}

	public boolean isTie() {
		if (board.isTie()) {
			return true;
		}
		return false;
	}

	public int getPlayerNr(PlayerModel pm) {
		if (player2 == null) {
			return 1;
		} else {
			return 2;
		}
	}

}
