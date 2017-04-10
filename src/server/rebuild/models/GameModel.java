package server.rebuild.models;

import server.rebuild.controllers.PlayerController;

public class GameModel {
	private String name;
	private PlayerController playing;
	private PlayerController notPlaying;
	private BoardModel board;

	public GameModel(String name) {
		this.name = name;
	}

	public boolean start(){
		if(playing != null && notPlaying != null){
			(new Thread("Game "+name+": ") {
				@Override
				public void run() {

				}
			}).start();
		}
		return false;
	}

	public BoardModel getBoard() {
		return board;
	}

	public void setBoard(BoardModel board) {
		this.board = board;
	}

	public String getName() {
		return name;
	}

	public boolean canJoin(){
		if(playing == null || notPlaying == null){
			return true;
		}
		return false;
	}

	public int join(PlayerController controller) {
		if(playing == null){
			playing = controller;
			return 1;
		}
		else{
			notPlaying = controller;
			return 2;
		}
	}
}
