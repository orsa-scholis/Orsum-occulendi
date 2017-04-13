package server;

import server.controllers.GameController;
import server.controllers.ServerController;

public class Main {
	private ServerController server;

	public Main(boolean test) {
		this.server = new ServerController();
		if(test){
			server.getModel().getGames().add(new GameController("Test"));
		}
	}

	public void start(){
		System.out.println("Server is starting....");
		System.out.println("-------------------------------------------------------");
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
