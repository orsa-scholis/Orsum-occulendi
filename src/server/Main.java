package server;

import server.controllers.GameController;
import server.controllers.ServerController;

public class Main {
	private ServerController server;

	public Main(boolean test) {
		this.server = new ServerController(test);
		if(test){
			server.getModel().getGames().add(new GameController("Test", server.getModel().getLogger()));
		}
	}

	public void start(){
		server.start();
	}

	public static void main(String[] args) {
		boolean test = false;
		if(args.length > 0){
			for(int i = 0; i < args.length; i++){
				if(args[i].equals("-t")){
					test = true;
				}
			}
		}
		Main main = new Main(test);
		main.start();
	}
}
