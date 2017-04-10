package server.rebuild;

import server.rebuild.models.GameModel;
import server.rebuild.controllers.ServerController;

public class Main {
	private ServerController server;

	public Main() {
		this.server = new ServerController();
		server.getModel().getGames().add(new GameModel("Test"));
		System.out.println(server.getModel().getAllGames());
		server.start();
	}

	public static void main(String[] args) {
		Main main = new Main();
	}
}
