package server.controllers;

import server.com.CommunicationTask;
import server.models.GameModel;

public class GameController {
	private GameModel game;

	public GameController(String name){
		game = new GameModel(name);
	}

	public boolean canJoin() {
		if (game.getPlayerTwo() == null) {
			return true;
		}
		return false;
	}

	public int join(PlayerController controller) {
		if (game.getPlayerOne() == null) {
			game.setPlayerOne(controller);
			return 1;
		} else {
			controller.getModel().setInGame(true);
			controller.getModel().setPlaying(true);
			game.setPlayerTwo(controller);
			return 2;
		}
	}

	public void notifyOtherPlayer(CommunicationTask communicationTask) {
		if (game.getPlayerOne() != null && game.getPlayerTwo() != null) {
			if (game.getPlayerFlag()) {
				System.out.println(Thread.currentThread().getName()+" sending '"+communicationTask.getMessage()+"' to playerTwo");
				communicationTask.setUnfinished();
				game.getPlayerTwo().getCom().addSendTask(communicationTask);
			} else {
				System.out.println(Thread.currentThread().getName()+" sending '"+communicationTask.getMessage()+"' to playerOne");
				communicationTask.setUnfinished();
				game.getPlayerOne().getCom().addSendTask(communicationTask);
			}
		} else {
			game.setFirstSet(communicationTask);
		}
	}

	public void sendFirstSet(){
		if(game.getFirstSet() != null){
			game.getPlayerTwo().getCom().addSendTask(game.getFirstSet());
		}
	}

	public GameModel getGame() {
		return game;
	}

	public boolean setStone(int parseInt) {
		return game.setStone(parseInt);
	}

	public void notifyWinnerAndLoser() {
		if(!game.isFinished()){
			game.setFinished(true);
			game.getPlayerOne().getCom().clearTasks();
			game.getPlayerTwo().getCom().clearTasks();
			CommunicationTask winner = new CommunicationTask("game:finished:0");
			CommunicationTask loser = new CommunicationTask("game:finished:1");
			if(!game.getPlayerFlag()){
				game.getPlayerOne().getCom().addSendTask(winner);
				game.getPlayerTwo().getCom().addSendTask(loser);
			} else {
				game.getPlayerOne().getCom().addSendTask(loser);
				game.getPlayerTwo().getCom().addSendTask(winner);
			}
			cleanAndDestroy();
		}
	}

	public void notifyAllTie() {
		if(!game.isFinished()){
			game.setFinished(true);
			game.getPlayerOne().getCom().clearTasks();
			game.getPlayerTwo().getCom().clearTasks();
			CommunicationTask tie = new CommunicationTask("game:finished:2");
			game.getPlayerOne().getCom().addSendTask(tie);
			game.getPlayerTwo().getCom().addSendTask(tie);
			cleanAndDestroy();
		}

	}

	private void cleanAndDestroy(){
		game.getPlayerOne().getModel().setGame(null);
		game.getPlayerTwo().getModel().setGame(null);
		game.setPlayerOne(null);
		game.setPlayerTwo(null);
	}

}
