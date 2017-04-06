package server.gui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Map;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import server.models.GameModel;
import server.models.PlayerModel;
import server.models.ServerModel;

public class MainServerController implements Initializable {
	@FXML
	TextField portField;
	@FXML
	TextField serverNameField;
	@FXML
	GridPane serverListView;

	private ServerModel server;
	private ServerSocket socket;
	private Boolean running;
	private Boolean isConsole;

	private SettingsController settingsC;
	private NewGameController gameC;

	private Stage settingsStage;
	private Stage addGameStage;

	private ArrayList<PlayerModel> players;
	private ArrayList<Socket> sockets;

	@FXML
	private void handleServerStarten(ActionEvent event) {
		if (!running) {
			System.out.println("Start!!");
			running = true;
			(new Thread() {
				@Override
				public void run() {
					try {
						socket = new ServerSocket(server.getPortNumber());
						while (running) {
							for (Socket s : sockets) {
								if (s == null) {
									s = socket.accept();
									BufferedReader input = new BufferedReader(
											new InputStreamReader(s.getInputStream()));
									PrintStream output = new PrintStream(s.getOutputStream());

									String inputLine = input.readLine();
									System.out.println(inputLine);
									if (inputLine.equals(null)) {
										break;
									}
									String[] splited = inputLine.split(":");

									if (splited.length == 3) {
										System.out.println(splited[2]);
										if ("connection".equals(splited[0]) && "connect".equals(splited[1])
												&& splited[2].length() > 1) {
											PlayerModel pm = new PlayerModel();
											pm.setName(splited[2]);
											pm.setAllConnections(s, server);
											players.add(pm);
											pm.setConnected(true);
											pm.start();
											output.println("success:accepted");
										} else if (splited[3].length() < 1) {
											output.println("error:Name zu kurz");
											output.close();
											input.close();
										} else {
											output.println("error:Noch nicht mit dem Server verbunden!");
											output.close();
											input.close();
										}
									} else {
										output.println("error:Unbekannte Anfrage");
										output.close();
										input.close();
									}
									break;
								}
							}
						}
					} catch (SocketException sExc) {
						running = false;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}).start();
		} else {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setHeaderText("Fehler beim Starten des Servers:");
			alert.setContentText("Der Server läuft bereits");
			alert.show();
		}
	}

	@FXML
	private void handleServerStoppen(ActionEvent event) {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (GameModel gm : server.getGames()) {
			if (gm.getPlayer1() != null) {
				gm.getPlayer1().sendMessagetoClient("connection:disconnect");
				// gm.getPlayer1().closeAllSockets();
			}
			if (gm.getPlayer2() != null) {
				gm.getPlayer2().sendMessagetoClient("connection:disconnect");
				// gm.getPlayer2().closeAllSockets();
			}
		}
		for (PlayerModel pm : players) {
			pm.sendMessagetoClient("connection:disconnect");
			if (pm.closeAllSockets()) {

			}
		}
		if ( !isConsole() )
		{
			initialize(null, null);
		}
		running = false;

	}

	@FXML
	private void handleServerNeustarten(ActionEvent event) {
		handleServerStoppen(event);
		handleServerStarten(event);
		System.out.println("Neustart!!");
	}

	@FXML
	private void handleServerEinstellungen(ActionEvent event) {
		try {

			FXMLLoader loader = new FXMLLoader(getClass().getResource("SettingsView.fxml"));
			loader.setController(settingsC);

			Pane root = loader.load();
			Scene settings = new Scene(root, 300, 300);

			settingsStage = new Stage();
			settingsStage.setScene(settings);
			settingsStage.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
				public void handle(KeyEvent kEve) {
					// System.out.println("KeeeeeyPress! " + kEve.getCode());
					if (kEve.getCode() == KeyCode.ESCAPE) {
						settingsStage.close();
					} else if (kEve.getCode() == KeyCode.ENTER) {
						settingsC.saveEvent(settingsStage);
					}
				}
			});
			settingsStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void handleSpielErstellen(ActionEvent event) {
		openNewGameStage();
	}

	@FXML
	private void handlePlusButton(ActionEvent event) {
		openNewGameStage();
	}

	private void openNewGameStage() {
		try {

			FXMLLoader loader = new FXMLLoader(getClass().getResource("NewGameView.fxml"));
			loader.setController(gameC);

			Pane root = loader.load();
			Scene addServer = new Scene(root, 300, 300);

			addGameStage = new Stage();
			addGameStage.setScene(addServer);
			addGameStage.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
				public void handle(KeyEvent kEve) {
					// System.out.println("KeeeeeyPress! " + kEve.getCode());
					if (kEve.getCode() == KeyCode.ESCAPE) {
						addGameStage.close();
					} else if (kEve.getCode() == KeyCode.ENTER) {
						gameC.saveEvent(addGameStage);
					}
				}
			});
			addGameStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		running = false;
		isConsole = false;
		server = new ServerModel(socket, this);
		players = new ArrayList<>();
		sockets = new ArrayList<>();
		setUpSockets();
		settingsC = new SettingsController(this);
		gameC = new NewGameController(this);
		gameC.setServerModel(server);
		refreshViews();

	}

	public void setUpSockets() {
		for (int i = 0; i < server.getMaxSockets(); i++) {
			Socket socket = null;
			sockets.add(socket);
		}
	}

	public void startServer() {
		handleServerStarten(null);
	}

	@SuppressWarnings("static-access")
	public void refreshViews() {
		int count = 0;

		serverListView.getChildren().clear();

		for (GameModel gm : server.getGames()) {

			GridPane root = new GridPane();
			root.setHgap(10);
			root.setVgap(10);
			root.setMinWidth(serverListView.getWidth());
			root.setPadding(new Insets(0, 10, 0, 10));
			root.getStyleClass().add("serverGrid");

			BorderPane bp = new BorderPane();

			root.add(bp, 0, 0);

			GridPane inner = new GridPane();
			GridPane right = new GridPane();

			bp.setPrefWidth(595.0);
			bp.setCenter(inner);
			bp.setRight(right);
			bp.setAlignment(right, Pos.TOP_RIGHT);

			Label gameTitel = new Label();
			gameTitel.setText(gm.getName());
			gameTitel.setFont(new Font(19));
			gameTitel.getStyleClass().add("serverTitle");

			Label player1 = new Label();
			if (gm.getPlayer1() != null) {
				player1.setText(gm.getPlayer1().getName());
			}

			Label player2 = new Label();
			if (gm.getPlayer2() != null) {
				player2.setText(gm.getPlayer2().getName());
			}

			Button closeGame = new Button();
			closeGame.setText("X");
			closeGame.setUserData(gm.getName());
			closeGame.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					System.out.println(((Button) event.getSource()).getUserData());

					GameModel tmp = null;
					for (GameModel gm : server.getGames()) {
						if (gm.getName().equals((String) ((Button) event.getSource()).getUserData())) {
							tmp = gm;
						}
					}
					if (tmp != null) {
						tmp.close(false);
						server.removeGame((String) ((Button) event.getSource()).getUserData());
					}
					refreshViews();
				}
			});

			inner.add(gameTitel, 0, 0);
			inner.add(player1, 0, 1);
			inner.add(player2, 0, 2);

			right.add(closeGame, 0, 0);

			serverListView.add(root, 0, count);
			count++;
		}

	}

	public ServerModel getServer() {
		return server;
	}

	public ArrayList<Socket> getSockets() {
		return sockets;
	}

	public void setSockets(ArrayList<Socket> sockets) {
		this.sockets = sockets;
	}

	public void stopServer() {
		handleServerStoppen(null);

	}

	public void initializeConsole() {
		running = false;
		isConsole = true;
		server = new ServerModel(socket, this);
		players = new ArrayList<>();
		sockets = new ArrayList<>();
		setUpSockets();
		settingsC = new SettingsController(this);
		gameC = new NewGameController(this);
		gameC.setServerModel(server);
		consoleRun();
	}

	private void consoleRun() {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		boolean running = true;
		System.out.println("Willkommen beim Konsolen-Server für Vier-Gewinnt");
		while (running) {
			System.out.println("Was willst du tun? (? oder hilfe für eine Hilfe)");
			try {
				String in = br.readLine();
				switch (in) {
					case "?":
					case "hilfe":
						printConsoleHelp();
						break;

					case "start":
						startServer();
						break;
					case "stop":
						stopServer();
						break;

					case "spieleAnzeigen":
						for (GameModel game : getServer().getGames()) {
							System.out.print(game.getName());
							if (game.getPlayer1() != null) {
								System.out.println(", p1: " + game.getPlayer1().getName());
							}
							if (game.getPlayer2() != null) {
								System.out.println(", p2: " + game.getPlayer2().getName());
							}
							System.out.println("");
						}
						break;

					case "spielHinzufuegen":
						System.out.println("Bitte den Spielname eingeben:");
						in = br.readLine();

						if (in.isEmpty()) {
							System.out.println("Der Name kann nicht leer sein!");
						} else {
							if (getServer().addGame(new GameModel(in, getServer()))) {
								System.out.println("Neues Spiel erfolgreich hinzugefügt!");
							} else {
								System.out.println("ERROR: Der Name ist wahrscheinlich bereits in Verwendung!");
							}
						}

						break;

					case "herunterfahren":
						running = false;
						break;

					default:
						System.out.println("Unbekannter Befehl!");

						break;
				}

			} catch (IOException e) {
				e.printStackTrace();
				running = false;
			}
		}

	}

	private void printConsoleHelp() {

	}

	public boolean isConsole() {
		return isConsole;
	}

}
