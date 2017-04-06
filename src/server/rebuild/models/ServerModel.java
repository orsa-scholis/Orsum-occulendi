package server.rebuild.models;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

import server.models.GameModel;
import server.rebuild.controllers.PlayerController;

public class ServerModel {
	private ArrayList<PlayerController> players;
	private ArrayList<GameModel> games;
	private int portNumber;
	private int maxSockets;

	private ServerSocket socket;

	public ServerModel(){
		try {
			socket = new ServerSocket(portNumber);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}



}
