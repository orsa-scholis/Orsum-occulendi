package server.rebuild;

import server.rebuild.controllers.ServerController;

public class Main {
	private ServerController server;

	public Main() {
		this.server = new ServerController();
	}

	public static void main(String[] args) {
		Main main = new Main();
	}
}
