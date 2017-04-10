package server.rebuild.controllers;

import server.rebuild.models.GameModel;
import server.rebuild.models.PlayerModel;
import server.rebuild.models.ServerModel;

public class ServerController {
	private ServerModel model;

	public ServerController() {
		model = new ServerModel();
	}

	public void start(){
		PlayerController player = new PlayerController(this);
		player.start();
		model.getPlayers().add(player);
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
		for (GameModel gm : model.getGames()) {
			if(gm.getName().equals(string)){
				if(gm.canJoin()){
					return controller.joinGame(gm);
				}
			}
		}
		return -1;
	}

	public boolean createGame(String name) {
		for (GameModel gm : model.getGames()) {
			if(gm.getName().equals(name)){
				return false;
			}
		}
		model.getGames().add(new GameModel(name));
		return true;
	}
}
