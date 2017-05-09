package server;

import server.controllers.GameController;
import server.controllers.ServerController;

public class Main {
	private ServerController server;

	public void start(boolean test, boolean log){
		this.server = new ServerController(log);
		if(test){
			server.getModel().getGames().add(new GameController("Test", server.getModel().getLogger()));
		}
		server.start();
	}

	public static void main(String[] args) {
		boolean test = false;
		boolean log = false;
		if(args.length > 0){
			for(int i = 0; i < args.length; i++){
				if(args[i].equals("-t")){
					test = true;
				} else if (args[i].equals("-l")) {
					log = true;
				}
			}
		}
		Main main = new Main();
		main.start(test, log);
	}
}
