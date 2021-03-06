package client.application;

import java.io.IOException;
import java.net.URL;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

import client.chat.ChatController;
import client.connectToServer.ConnectToServerController;
import client.help.HelpController;
import client.help.HelpType;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import client.message.ClientMessage;
import client.message.CommunicationTask;
import client.message.ServerMessage;
import crypto.CryptoEngine;

/**
 * Kontroller des Hauptmenus
 * @author Lukas
 *
 */
public class Controller implements Initializable, ClientDelegate {
	@FXML
	private ListView<String> listView;

	@FXML
	private Button startButton;
	
	@FXML
	private Button promptNewGameButton;

	@FXML
	private MenuItem refreshMenuItem;
	
	@FXML
	private MenuItem connectOrDisconnectMenuItem;

	private Client client;
	private Runnable connectToServerCallback;
	private Stage connectToServerStage;
	private HelpController helpController;
	private Stage helpStage;
	private ConnectToServerController connectToServerController;
	private GameController gameController;
	private ChatController chatController;

	public Controller() {
		super();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		refreshMenuItem.setDisable(true);
		lockUI();
	}

	/**
	 * Diese Funktion disabled alle UI Elemente
	 */
	public void lockUI() {
		ObservableList<String> value = FXCollections.observableArrayList("Nicht verbunden");

		listView.setItems(value);
		startButton.setDisable(true);
		promptNewGameButton.setDisable(true);
	}

	public void unlockUI() {
		startButton.setDisable(false);
	}

	@FXML
	public void presentHowToConnectWindow(Event e) {
		presentHelpWindow(e, HelpType.HOW_TO_CONNECT);
	}

	@FXML
	public void presentWhatIsAPortWindow(Event e) {
		presentHelpWindow(e, HelpType.WHAT_IS_A_PORT);
	}

	@FXML
	public void presentAbout(Event e) {
		presentHelpWindow(e, HelpType.ABOUT);
	}
	
	@FXML
	public void presentWhatsMyIP(Event e) {
		presentHelpWindow(e, HelpType.WHAT_IS_MY_IP);
	}

	/**
	 * Öffnet ein Fenster mit einer Hilfe
	 * @param e	Das Event
	 * @param type	Die Hilfe, die angezeigt werden soll
	 */
	public void presentHelpWindow(Event e, HelpType type) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("helpWindow.fxml"));
			AnchorPane root = (AnchorPane)loader.load();

			helpController = loader.getController();
			helpController.setType(type);
			helpStage = new Stage();
			helpStage.setResizable(false);
			helpStage.setTitle("Vier Gewinnt Hilfe");
			helpStage.setScene(new Scene(root));
			helpStage.show();

			helpController.display();
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	@FXML
	public void connectOrDisconnectMenuItemPressed(Event e) {
		if (this.client == null || !this.client.isConnected()) {
			presentConnectToServerWindow(e);
		} else {
			disconnectFromServer(e);
		}
	}
	
	public void disconnectFromServer(Event e) {
		this.client.disconnect();
	}
	
	/**
	 * Zeigt den Connect-Dialog an
	 * @param e	Das Event
	 */
	public void presentConnectToServerWindow(Event e) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("connectToServer.fxml"));
			GridPane root = (GridPane)loader.load();
			connectToServerController = loader.getController();

			connectToServerController.setCompletedRunnable((run) -> {
				/*String ip = connectToServerController.getIpAdressTextField().getText();
				String port = connectToServerController.getPortTextField().getText();*/
				String ip = (String) UserDefaults.sharedDefaults().valueForKey(UserDefaults.ADRESS_KEY);
				int port = (int) UserDefaults.sharedDefaults().valueForKey(UserDefaults.PORT_KEY);
				String userID = (String)UserDefaults.sharedDefaults().valueForKey(UserDefaults.NAME_KEY);

				this.client = new Client(ip, port, userID);
				this.client.setDelegate(this);
				this.client.connect();

				connectToServerCallback = run;
			});

            connectToServerStage = new Stage();
            connectToServerStage.setResizable(false);
            connectToServerStage.setTitle("Connect to server");
            connectToServerStage.setScene(new Scene(root));
            connectToServerStage.show();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	@FXML
	public void presentNewGameProposalWindow(Event e) {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setHeaderText("Neuer Spielvorschalg");
		dialog.setContentText("Wie soll das Spiel heissen?");
		
		Optional<String> gameNameResult = dialog.showAndWait();
		gameNameResult.ifPresent(result -> {
			if (!gameNameResult.get().isEmpty()) {
				this.client.enqueueTask(new CommunicationTask(new ClientMessage("server", "newgame", gameNameResult.get()), (success, response) -> {
					Platform.runLater(() -> {
						if (success && response.getDomain().equals("success") && response.getCommand().equals("created")) {
							refreshGameList(e);
							System.out.println("Game erstellt");
						} else {
							Alert alert = new Alert(AlertType.ERROR);
							alert.setHeaderText("Das Spiel konnte nicht erstellt werden");
							alert.setContentText(response.toString());
							alert.show();
						}
					});
				}));
			}
		});
	}

	/**
	 * Startet das Game und öffnet das Spielfenster
	 * @param e	Das Event
	 */
	@FXML
	public void startGame(Event e) {
		final String game = listView.getSelectionModel().getSelectedItem();

		startButton.setDisable(true);
		client.enqueueTask(new CommunicationTask(new ClientMessage("game", "join", game), (success, response) -> {
			if (success && response.getDomain().equals("success") && response.getCommand().equals("joined")) {
				Platform.runLater(() -> {
					try {
						ArrayList<String> args = response.getArguments();
						String arg1 = args.get(0);
						boolean isFirst = Integer.parseInt(arg1) == 1;
						System.out.println("arg1: " + arg1);
						startButton.setDisable(false);

						// load game fxml
						FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));
						AnchorPane root = (AnchorPane)loader.load();
						gameController = loader.getController();
						gameController.setParent(this);
						gameController.setPlayerTurn(isFirst);

			            Stage stage = new Stage();

			            // Handle window closing (finish game)
			            stage.setOnCloseRequest((closeEvent) -> {
			            	if (!gameController.isFinished()) {
			            		CommunicationTask disconnectTask = new CommunicationTask(new ClientMessage("game", "finished", game), (disconnectedSuccessfully, disconnectResponse) -> {
				            		Platform.runLater(() -> {
				            			if (disconnectedSuccessfully) {

					            		} else {
					            			Alert alert = new Alert(AlertType.ERROR, "Can't disconnect from game. ServerMessage: " + disconnectResponse);
					            			alert.show();
					            		}
				            		});
				            	});

				            	client.enqueueTask(disconnectTask);
			            	}
			            });

			            stage.setResizable(false);
			            stage.setTitle("4 Gewinnt - " + client.getUserID());
			            stage.setScene(new Scene(root));
			            stage.show();
			            gameController.viewDidAppear();
					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (NumberFormatException e2) {
						e2.printStackTrace();
					}
				});
			} else {
				startButton.setDisable(false);
			}
		}));
	}

	@FXML
	public void refreshGameList(Event e) {
		if (this.client.isConnected()) {
			this.client.enqueueTask(new CommunicationTask(new ClientMessage("info", "requestGames", new ArrayList<>()), (success, response) -> {
				if (success && response.getDomain().equals("success")) {
					handleGameRequestResponse(success, response);
				}
			}));
		}
	}

	// *** Client delegate ***
	@Override
	public void clientDidNotConnect(Client client) {
		// Der Client konnte sich nicht mit dem Server verbinden
		// =>	Fehlermeldung präsentieren
		
		connectToServerController.getStatusLabel().setText("Can't connect");
		connectToServerController.getStatusLabel().setTextFill(Color.RED);

		(new Thread() {

			@Override
			public void run() {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				Platform.runLater(new Runnable() {
					public void run() {
						connectToServerCallback.run();
					}
				});
			};
		}).start();
	}

	@Override
	public void clientDidSuccessfullyConnect(Client client) {
		// Der Client hat sich erfolgreich mit dem Server verbunden
		// =>	Hauptmenu aktivieren und aktualisieren
		
		refreshMenuItem.setDisable(false);
		promptNewGameButton.setDisable(false);

		connectToServerController.getStatusLabel().setText("Erfolgreich mit Server verbunden");
		connectToServerController.getStatusLabel().setTextFill(Color.GREEN);
		
		connectOrDisconnectMenuItem.setText("Verbindung trennen");

		Controller tCon = this;
		(new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						connectToServerCallback.run();

						// Verbindungsaufbau starten
						CommunicationTask task = new CommunicationTask(new ClientMessage("connection", "connect", client.getUserID()));
						task.setCompletedRunnable((success, message) -> {
							Platform.runLater(() -> {
								if (message.getDomain().equals("success") && success) {
									CryptoEngine engine = client.getCryptoEngine();
									String serverPublicKey = message.getArguments().get(0);
									
									// Schlüsselaustausch
									try {
										PublicKey publicKey = engine.publicKeyFromString(serverPublicKey);
										String encrypted = engine.rsaEncrypt(engine.getKey(), publicKey);
										
										CommunicationTask responseTask = new CommunicationTask(new ClientMessage("connection", "keyExchange", encrypted));
										responseTask.setCompletedRunnable((successful, msg) -> {
											Platform.runLater(() -> {
												listView.setItems(FXCollections.observableArrayList("Synchronisation..."));

												// Aktuelle Spiele vom Server holen
												CommunicationTask gamesRequestTask = new CommunicationTask(new ClientMessage("info", "requestGames", new ArrayList<>()));
												gamesRequestTask.setCompletedRunnable((successFullyRequestedGames, requestGamesMessage) -> {
													handleGameRequestResponse(successFullyRequestedGames, requestGamesMessage);
												});
												client.enqueueTask(gamesRequestTask);
												
												// Das Chatfenster öffnen, sobald eine stablie Verbindung zum Server herrscht
												try {
													FXMLLoader chatloader = new FXMLLoader(getClass().getClassLoader().getResource("chat.fxml"));
													BorderPane chatroot = (BorderPane)chatloader.load();
													chatController = chatloader.getController();
													chatController.setClient(tCon.client);
													Stage stage = new Stage();
													stage.setResizable(false);
											        stage.setTitle("Chat");
											        stage.setScene(new Scene(chatroot));
											        stage.setResizable(true);
											        stage.show();
												} catch (Exception exci) {
													exci.printStackTrace();
												}
											});
										});
										
										responseTask.setEncrypt(false);
										client.enqueueTask(responseTask);
									} catch (Exception e) {
										client.abortConnection();
										e.printStackTrace();
									}
								} else {
									ObservableList<String> value = FXCollections.observableArrayList("Ein Fehler ist aufgetreten", "Nachricht: " + message);
									listView.setItems(value);
								}
							});
						});
						
						task.setEncrypt(false);
						client.enqueueTask(task);
					}
				});
			};
		}).start();
	}

	/**
	 * Der Client hat sich vom Server getrennt
	 */
	@Override
	public void clientDidDisconnect(Client client) {
		refreshMenuItem.setDisable(true);
		lockUI();
		listView.setItems(FXCollections.observableArrayList("Nicht verbunden"));
		
		connectOrDisconnectMenuItem.setText("Verbinden");
		this.client = null;
	}

	/**
	 * Zeigt die aktuellen Spiele in der Tabelle an
	 * @param successFullyRequestedGames	Ob die Spiele erfolgreich geladen werden konnten
	 * @param requestGamesMessage	Die Servernachricht
	 */
	private void handleGameRequestResponse(boolean successFullyRequestedGames, ServerMessage requestGamesMessage) {
		Platform.runLater(() -> {
			if (successFullyRequestedGames && requestGamesMessage.getDomain().equals("success")) {
				ArrayList<String> args = requestGamesMessage.getArguments();
				ArrayList<String> availableGames = new ArrayList<>();

				if (args.size() > 0) {
					String arg1 = args.get(0);
					String[] components = arg1.split(",");

					for (int i = 0; i < components.length; ++i) {
						availableGames.add(components[i]);
					}
				}

				if (availableGames.size() <= 0) {
					listView.setItems(FXCollections.observableArrayList("Keine Spiele vorhanden"));
				} else {
					unlockUI();
					listView.setItems(FXCollections.observableArrayList(availableGames));
				}
			} else {
				ObservableList<String> value = FXCollections.observableArrayList(
						"Ein Fehler ist aufgetreten bei der Synchronisation",
						"Nachricht: " + requestGamesMessage
				);
				listView.setItems(value);
			}
		});
	}

	@Override
	public void clientDidReceiveException(Throwable e) {
		e.printStackTrace(); // Todo: Bessere Fehlerbehandlung
	}

	/**
	 * Diese Methode wird aufgerufen, wenn der Server eine Nachricht geschickt hat, ohne dass der Client vorher eine Nachricht an den Server gerichtet hat
	 */
	@Override
	public void clientDidReceiveStandaloneServerMessage(ServerMessage message) {
		System.out.println("Did receive standalone message");
		System.out.println(message.toString());

		// Eine Game-Relevante Nachricht ist eingetroffen
		if (message.getDomain().equals("game")) {
			try {
				if (message.getCommand().equals("setstone")) {
					ArrayList<String> args = message.getArguments();

					if (args.size() < 1) {
						System.err.println("The server sent a illigeal message: The arguments are empty. Message: " + message);
						return;
					}

					String arg1 = args.get(0);
					int col = Integer.parseInt(arg1);

					gameController.setStone(col, true);
					gameController.draw();
				} else if (message.getCommand().equals("finished")) {
					ArrayList<String> args = message.getArguments();
					
					if (args.size() < 1) {
						System.err.println("The server sent a illigeal message: The arguments are empty. Message: " + message);
						return;
					}
					
					String arg1 = args.get(0);
					int type = Integer.parseInt(arg1);
					GameResult result = GameResult.fromInt(type);
					
					gameController.didReceiveGameFinishedMessage(result);
				}
			} catch (NumberFormatException e) {
				System.err.println("Can't process server message " + message + ": Can't parse argument as integer");
				e.printStackTrace();
			}
		} else if (message.getDomain().equals("chat")) {
			// Eine chat-relevante Nachricht ist eingroffen
			chatController.didReceiveChatServerMessage(message);
		}
	}
	
	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public Button getStartButton() {
		return startButton;
	}

	public void setStartButton(Button startButton) {
		this.startButton = startButton;
	}

	public Button getPromptNewGameButton() {
		return promptNewGameButton;
	}

	public void setPromptNewGameButton(Button promptNewGameButton) {
		this.promptNewGameButton = promptNewGameButton;
	}
	
	public MenuItem getConnectOrDisconnectMenuItem() {
		return connectOrDisconnectMenuItem;
	}

	public void setConnectOrDisconnectMenuItem(MenuItem connectOrDisconnectMenuItem) {
		this.connectOrDisconnectMenuItem = connectOrDisconnectMenuItem;
	}
}
