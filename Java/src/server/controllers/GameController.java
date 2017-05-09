package server.controllers;

import server.com.CommunicationTask;
import server.models.GameModel;

public class GameController {
	private GameModel game;

	public GameController(String name, LogController logger){
		game = new GameModel(name, logger);
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
			game.getPlayerOne().getModel().setPlaying(true);
			controller.getModel().setInGame(true);
			controller.getModel().setPlaying(true);
			game.setPlayerTwo(controller);
			return 2;
		}
	}

	public void notifyOtherPlayer(CommunicationTask communicationTask) {
		if (game.getPlayerOne() != null && game.getPlayerTwo() != null) {
			if (game.getPlayerFlag()) {
				game.getLogger().log("Game "+game.getName(), "sending Message to PlayerTwo", communicationTask, null);
				communicationTask.setUnfinished();
				communicationTask.setEncrypt(true);
				game.getPlayerTwo().getCom().addSendTask(communicationTask);
			} else {
				game.getLogger().log("Game "+game.getName(), "sending Message to PlayerOne", communicationTask, null);
				communicationTask.setUnfinished();
				communicationTask.setEncrypt(true);
				game.getPlayerOne().getCom().addSendTask(communicationTask);
			}
		} else {
			game.setFirstSet(communicationTask);
		}
	}

	public void notifyOtherPlayer(CommunicationTask communicationTask, PlayerController player) {
		if (game.getPlayerOne() != null && game.getPlayerTwo() != null) {
			if (game.getPlayerOne().equals(player)) {
				game.getLogger().log("Game "+game.getName(), "sending Message to PlayerTwo", communicationTask, null);
				communicationTask.setUnfinished();
				communicationTask.setEncrypt(true);
				game.getPlayerTwo().getCom().addSendTask(communicationTask);
			} else {
				game.getLogger().log("Game "+game.getName(), "sending Message to PlayerOne", communicationTask, null);
				communicationTask.setUnfinished();
				communicationTask.setEncrypt(true);
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
			winner.setEncrypt(true);
			CommunicationTask loser = new CommunicationTask("game:finished:1");
			loser.setEncrypt(true);
			if(game.getPlayerFlag()){
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
			tie.setEncrypt(true);
			game.getPlayerOne().getCom().addSendTask(tie);
			game.getPlayerTwo().getCom().addSendTask(tie);
			cleanAndDestroy();
		}

	}

	public void notifyError(){
		CommunicationTask err = new CommunicationTask("game:finished:4");
		err.setEncrypt(true);
		game.getPlayerOne().getCom().clearTasks();
		game.getPlayerOne().getCom().addSendTask(err);
		if(game.getPlayerTwo() != null){
			game.getPlayerTwo().getCom().clearTasks();
			game.getPlayerTwo().getCom().addSendTask(err);
			cleanAndDestroy();
		} else {
			game.getPlayerOne().getModel().setInGame(false);
			game.setPlayerOne(null);
		}
	}

	private void cleanAndDestroy(){
		game.getPlayerOne().leaveGame();
		game.getPlayerTwo().leaveGame();
		game.setPlayerOne(null);
		game.setPlayerTwo(null);
	}

}
