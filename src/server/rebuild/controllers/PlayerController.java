package server.rebuild.controllers;

import server.rebuild.models.PlayerModel;

public class PlayerController {
	private PlayerModel model;

	public PlayerController(PlayerModel model){
		this.model = model;
	}

	public void start(){
		(new Thread(model.getName()+":mainThread") {
			@Override
			public void run() {
				while(model.isServerRunning()){
					while(!model.isConnected() && !model.isInGame() && !model.isPlaying()){
						model.getPlayerSocket();
					}
					while(model.isConnected()){

					}
					while(model.isInGame()){

					}
					while(model.isPlaying()){

					}

				}
			}
		}).start();
	}

}
