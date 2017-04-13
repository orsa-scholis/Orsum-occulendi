package server.controllers;

import server.models.ServerModel;

public class ServerController {
	private ServerModel model;

	public ServerController() {
		model = new ServerModel();
	}

	public void start(){
		PlayerController player = new PlayerController(this);
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
		PlayerController player = new PlayerController(this);
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
		model.getGames().add(new GameController(name));
		return true;
	}

	public void removeGame(GameController gameController) {
		if(model.getGames().contains(gameController)){
			model.getGames().remove(gameController);
		}
	}
}
