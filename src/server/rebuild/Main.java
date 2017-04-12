package server.rebuild;

import server.rebuild.controllers.GameController;
import server.rebuild.controllers.ServerController;

public class Main {
	private ServerController server;

	public Main() {
		this.server = new ServerController();
		server.getModel().getGames().add(new GameController("Test"));
	}

	public void start(){
		System.out.println("Server is starting....");
		System.out.println("-------------------------------------------------------");
		server.start();
	}

	public static void main(String[] args) {
		Main main = new Main();
		main.start();
	}
}
