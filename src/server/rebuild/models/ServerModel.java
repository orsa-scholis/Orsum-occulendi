package server.rebuild.models;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

import server.rebuild.models.GameModel;
import server.rebuild.controllers.PlayerController;

public class ServerModel {
	private ArrayList<PlayerController> players;
	private ArrayList<GameModel> games;
	private int portNumber;
	private int maxSockets;

	private ServerSocket socket;

	public ServerModel(){
		players = new ArrayList<>();
		games = new ArrayList<>();
		portNumber = 4560;
		try {
			socket = new ServerSocket(portNumber);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getAllGames() {
		String output = "success:requested:";

		for (GameModel g : games) {
			output += g.getName() + ",";
		}

		return output;
	}

	public ArrayList<PlayerController> getPlayers() {
		return players;
	}

	public void setPlayers(ArrayList<PlayerController> players) {
		this.players = players;
	}

	public ArrayList<GameModel> getGames() {
		return games;
	}

	public void setGames(ArrayList<GameModel> games) {
		this.games = games;
	}

	public int getPortNumber() {
		return portNumber;
	}

	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}

	public int getMaxSockets() {
		return maxSockets;
	}

	public void setMaxSockets(int maxSockets) {
		this.maxSockets = maxSockets;
	}

	public ServerSocket getSocket() {
		return socket;
	}

	public void setSocket(ServerSocket socket) {
		this.socket = socket;
	}
}
