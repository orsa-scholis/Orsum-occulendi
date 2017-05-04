package server.controllers;

import server.com.CommunicationTask;
import server.models.ServerModel;

public class ServerController {
	private ServerModel model;

	public ServerController(boolean logging) {
		model = new ServerModel(logging);
	}

	public void start(){
		model.getLogger().log("Server", "Starting up", null, null);
		PlayerController player = new PlayerController(this, model.getLogger());
		model.getPlayers().add(player);
		player.start();
	}

	public ServerModel getModel() {
		return model;
	}

	public void setModel(ServerModel model) {
		this.model = model;
	}

	public void newPlayer() {
		PlayerController player = new PlayerController(this, model.getLogger());
		player.start();
		model.getPlayers().add(player);
	}

	public int joinGame(String string, PlayerController controller) {
		for (GameController gm : model.getGames()) {
			if(gm.getGame().getName().equals(string)){
				if(gm.canJoin()){
					return controller.joinGame(gm);
				}
			}
		}
		return -1;
	}

	public boolean createGame(String name) {
		for (GameController gm : model.getGames()) {
			if(gm.getGame().getName().equals(name)){
				return false;
			}
		}
		model.getGames().add(new GameController(name, model.getLogger()));
		return true;
	}

	public void removeGame(GameController gameController) {
		if(model.getGames().contains(gameController)){
			model.getGames().remove(gameController);
		}
	}

	public void notifyAllPlayer(CommunicationTask ct){
		for (PlayerController pl : model.getPlayers()){
			if(pl.getModel().isConnected() && !pl.getModel().isInGame()){
				pl.getCom().addSendTask(ct);
			}
		}
	}
}
