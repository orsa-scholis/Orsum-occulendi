package server.rebuild.controllers;

import javafx.print.PageLayout;
import server.rebuild.com.CommunicationTask;
import server.rebuild.models.GameModel;

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
			if(game.getPlayerOne().equals(controller)){
				System.out.println("WHAAAAAAAT THE HELL!!??");
			}
			controller.getModel().setInGame(true);
			controller.getModel().setPlaying(true);
			game.setPlayerTwo(controller);
			sendFirstSet();
			return 2;
		}
	}

	public void notifyOtherPlayer(CommunicationTask communicationTask) {
		if (game.getPlayerOne() != null && game.getPlayerTwo() != null) {
			if (game.getPlayerFlag()) {
				System.out.println(Thread.currentThread().getName()+" sending '"+communicationTask.getMessage()+"' to playerTwo");
				game.getPlayerTwo().getCom().addSendTask(communicationTask);
				//System.out.println(playerTwo.getCom().getCurrentTask(false).getMessage());
			} else {
				System.out.println(Thread.currentThread().getName()+" sending '"+communicationTask.getMessage()+"' to playerOne");
				game.getPlayerOne().getCom().addSendTask(communicationTask);
				//System.out.println(playerOne.getCom().getCurrentTask(false).getMessage());
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
		CommunicationTask winner = new CommunicationTask("game:finished:0");
		CommunicationTask loser = new CommunicationTask("game:finished:1");
		if(game.getPlayerFlag()){
			game.getPlayerOne().getCom().addSendTask(winner);
			game.getPlayerTwo().getCom().addSendTask(loser);
		} else {
			game.getPlayerOne().getCom().addSendTask(loser);
			game.getPlayerTwo().getCom().addSendTask(winner);
		}
	}

	public void notifyAllTie() {
		CommunicationTask tie = new CommunicationTask("game:finished:2");
		game.getPlayerOne().getCom().addSendTask(tie);
		game.getPlayerTwo().getCom().addSendTask(tie);

	}

}
