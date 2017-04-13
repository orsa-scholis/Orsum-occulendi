package server.models;

import java.io.BufferedReader;
import java.io.PrintStream;
import java.net.Socket;

import server.com.Communicator;
import server.controllers.GameController;

public class PlayerModel {
	private String name;

	private Socket playerSocket;
	private BufferedReader input;
	private PrintStream output;

	private boolean serverRunning = true;
	private boolean connected = false;
	private boolean inGame = false;
	private boolean playing = false;

	private Communicator communicator;
	private GameController game;

	public PlayerModel(){
		this.playerSocket = new Socket();
		this.communicator = new Communicator();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Socket getPlayerSocket() {
		return playerSocket;
	}

	public void setPlayerSocket(Socket playerSocket) {
		this.playerSocket = playerSocket;
	}

	public BufferedReader getInput() {
		return input;
	}

	public void setInput(BufferedReader input) {
		this.input = input;
	}

	public PrintStream getOutput() {
		return output;
	}

	public void setOutput(PrintStream output) {
		this.output = output;
	}

	public boolean isServerRunning() {
		return serverRunning;
	}

	public void setServerRunning(boolean serverRunning) {
		this.serverRunning = serverRunning;
	}

	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	public boolean isInGame() {
		return inGame;
	}

	public void setInGame(boolean inGame) {
		this.inGame = inGame;
	}

	public boolean isPlaying() {
		return playing;
	}

	public void setPlaying(boolean playing) {
		this.playing = playing;
	}

	public Communicator getCommunicator() {
		return communicator;
	}

	public void setCommunicator(Communicator communicator) {
		this.communicator = communicator;
	}

	public GameController getGame(){
		return game;
	}

	public void setGame(GameController gm) {
		this.game = gm;
	}

}
