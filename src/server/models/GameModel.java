package server.models;

import server.com.CommunicationTask;
import server.controllers.LogController;
import server.controllers.PlayerController;

public class GameModel {
	private String name;
	private PlayerController playerOne = null;
	private PlayerController playerTwo = null;
	private BoardModel board;
	private boolean playerFlag = false; //TRUE = 1, FALSE = 2, START as FALSE because setStone()
	private boolean finished = false;
	private CommunicationTask firstSet = null;
	private LogController logger = null;

	public GameModel(String name, LogController logger) {
		this.name = name;
		this.board = new BoardModel();
	}

	public boolean setStone(int row) {
		playerFlag = !playerFlag;
		return board.setStone(playerFlag, row);
	}

	public String getName() {
		return name;
	}

	public BoardModel getBoard(){
		return board;
	}

	public boolean hasWon() {
		return board.hasWon(!playerFlag);
	}

	public PlayerController getPlayerOne() {
		return playerOne;
	}

	public void setPlayerOne(PlayerController playerOne) {
		this.playerOne = playerOne;
	}

	public PlayerController getPlayerTwo() {
		return playerTwo;
	}

	public void setPlayerTwo(PlayerController playerTwo) {
		this.playerTwo = playerTwo;
	}

	public CommunicationTask getFirstSet() {
		return firstSet;
	}

	public void setFirstSet(CommunicationTask firstSet) {
		this.firstSet = firstSet;
	}

	public boolean getPlayerFlag() {
		return playerFlag;
	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	public LogController getLogger(){
		return logger;
	}
}
