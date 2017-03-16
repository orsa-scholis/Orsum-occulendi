package server.models;

import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import server.gui.MainServerController;
import javafx.application.Platform;

public class ServerModel {
	private int portNumber;
	private int maxSockets;
	private ArrayList<GameModel> games;
	private ServerSocket sSocket;
	private MainServerController msc;
	private static Map<String, String> hilfeListe = new HashMap<String, String>();
	static
	{
		hilfeListe.put("?/hilfe", "Diese Hilfe ausgeben");
	}

	public ServerModel(ServerSocket socket, MainServerController msc) {
		portNumber = 4560;
		maxSockets = 20;
		games = new ArrayList<>();
		sSocket = socket;
		this.msc = msc;
	}

	public void startGame(GameModel gm) {

	}

	public String allGames() {
		String output = "success:requested:";

		for (GameModel g : games) {
			output += g.getName() + ",";
		}

		return output;
	}

	public boolean addGame(GameModel gm) {
		for (GameModel gami : games) {
			if (gami.getName().equals(gm.getName())) {
				return false;
			}
		}
		games.add(gm);
		return true;
	}

	public void removeGame(String name) {
		GameModel toDelete = null;
		for (GameModel game : games) {
			if (game.getName().equals(name)) {
				toDelete = game;

			}
		}
		if (toDelete != null) {
			if(toDelete.isRunning()){
				toDelete.close(false);
			}
			games.remove(toDelete);
			toDelete.close(false);
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					msc.refreshViews();
				}
			});
		}
	}

	public int getPortNumber() {
		return portNumber;
	}

	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}

	public ArrayList<GameModel> getGames() {
		return games;
	}

	public void setGames(ArrayList<GameModel> games) {
		this.games = games;
	}

	public ServerSocket getsSocket() {
		return sSocket;
	}

	public MainServerController getMsc() {
		return msc;
	}

	public void setMsc(MainServerController msc) {
		this.msc = msc;
	}

	public int getMaxSockets() {
		return maxSockets;
	}

	public void setMaxSockets(int maxSockets) {
		this.maxSockets = maxSockets;
	}

	public Map getHilfeListe()
	{
		return hilfeListe;
	}

}
