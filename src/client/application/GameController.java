package client.application;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

import client.chat.ChatController;
import client.help.HelpType;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import client.message.ClientMessage;
import client.message.CommunicationTask;
import client.stones.StoneColor;
import client.stones.StoneColors;
import client.stones.StoneColor.ColorTypes;

public class GameController implements Initializable, Observer {
	
	@FunctionalInterface
	protected interface CompletionHandler<___> 
	{
		public void apply(
			___
			__
		);
	};
	
	@FXML
	private Canvas canvas;
	private GraphicsContext graphicsContext;
	private Controller parent;
	
	@FXML
	private Menu statusMenu;
	
	@FXML
	private Label statusLabel;
	
	@FXML
	private Menu playerColorMenu;
	
	@FXML
	private Menu opponentColorMenu;

	protected final int W_COUNT = 7;
	protected final int H_COUNT = 6;
	protected final int RADIUS = 38;
	protected final int MARGIN = 10;
	protected final int PADDING_X = RADIUS + MARGIN;
	protected final int PADDING_Y = RADIUS + MARGIN + 5;
	protected ArrayList<ArrayList<Integer>> board = new ArrayList<>(H_COUNT); // 0=empty, 1=player, 2=opponent
	protected boolean isPlayerTurn;
	
	private CommunicationTask currentTask;
	private Integer[] temporaryStone = new Integer[] { -1, -1 };
	private int mouseOverCol = -1;
	private boolean isFinished = false;
	private StoneColors colors = (StoneColors)UserDefaults.sharedDefaults().valueForKey(UserDefaults.STONE_COLORS_KEY, StoneColors.defaultColors());
	
	private ArrayList<StoneColor.ColorTypes> playerMenuIndices = new ArrayList<>();
	private ArrayList<StoneColor.ColorTypes> opponentMenuIndices = new ArrayList<>();
	
	public GameController() {
		super();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		playerMenuIndices.add(ColorTypes.BLUE);
		playerMenuIndices.add(ColorTypes.RED);
		playerMenuIndices.add(ColorTypes.GREEN);
		playerMenuIndices.add(ColorTypes.YELLOW);
		playerMenuIndices.add(ColorTypes.ORANGE);
		
		@SuppressWarnings("unchecked")
		ArrayList<ColorTypes> clone = (ArrayList<ColorTypes>)playerMenuIndices.clone();
		opponentMenuIndices = clone;
		
		for (int y = 0; y < H_COUNT; y++) {
			ArrayList<Integer> listi = new ArrayList<>(W_COUNT);
			for (int x = 0; x < W_COUNT; x++) {
				listi.add(0);
			}

			board.add(listi);
		}

		/**
		 * 
		 * Hier registriere ich den Event Handler für das MouseMoved Event.
		 * Das wird verwendet, um dem Benutzer zu zeigen, in welcher Spalte er sich 
		 * befindet, in dem es einen "Grauen Stein" anzeigt, der an der Stelle steht,
		 * an der ein richtiger Stein gesetzt werden würde.
		 * 
		 */
		canvas.setOnMouseMoved((event) -> {
			mouseMoved(event);
		});

		canvas.setOnMouseReleased((event) -> {
			mouseReleased(event);
		});

		this.graphicsContext = canvas.getGraphicsContext2D();
		draw();
	}
	
	public void viewDidAppear() {
		if (isPlayerTurn) {
			updateStatusMenu(GameStatus.PLAYER_TURN_BUT_WAITING);
		} else {
			updateStatusMenu(GameStatus.OPPONENT_TURN_BUT_WAITING);
		}
		
		updateColorMenu();
	}
	
	private void updateColorMenu() {
		playerColorMenu.getItems().removeAll(playerColorMenu.getItems());
		opponentColorMenu.getItems().removeAll(opponentColorMenu.getItems());
		
		for (int i = 0; i < playerMenuIndices.size(); i++) {
			ColorTypes type = playerMenuIndices.get(i);
			RadioMenuItem menuItem = new RadioMenuItem(type.toString());
			menuItem.setSelected(colors.playerColor().getType() == type);
			menuItem.setOnAction(e -> didClickPlayerMenuItem(e));
			menuItem.setUserData(type);
			menuItem.setDisable(colors.opponentColor().getType() == type);
			playerColorMenu.getItems().add(menuItem);
		}
		
		for (int i = 0; i < opponentMenuIndices.size(); i++) {
			ColorTypes type = opponentMenuIndices.get(i);
			RadioMenuItem menuItem = new RadioMenuItem(type.toString());
			menuItem.setSelected(colors.opponentColor().getType() == type);
			menuItem.setOnAction(e -> didClickOpponentMenuItem(e));
			menuItem.setUserData(type);
			menuItem.setDisable(colors.playerColor().getType() == type);
			opponentColorMenu.getItems().add(menuItem);
		}
	}
	
	public void didClickPlayerMenuItem(ActionEvent e) {
		ColorTypes type = (ColorTypes)((RadioMenuItem)e.getSource()).getUserData();

		colors.setPlayerColor(new StoneColor(type));
		updateColorMenu();
		draw();
		UserDefaults.sharedDefaults().setKey(UserDefaults.STONE_COLORS_KEY, colors);
	}
	
	public void didClickOpponentMenuItem(ActionEvent e) {
		ColorTypes type = (ColorTypes)((RadioMenuItem)e.getSource()).getUserData();

		colors.setOpponentColor(new StoneColor(type));
		updateColorMenu();
		draw();
		UserDefaults.sharedDefaults().setKey(UserDefaults.STONE_COLORS_KEY, colors);
	}

	@FXML
	public void quitGame(Event e) {
		this.parent.getClient().enqueueTask(new CommunicationTask(new ClientMessage("game", "finished", ""), ((success, response) -> {
			if (success && response.getDomain().equals("success")) {
				((Stage)statusLabel.getScene().getWindow()).close();
			}
		})));
	}

	@FXML
	public void showHow4WinsWorksHelpWindow(Event e) {
		getParent().presentHelpWindow(e, HelpType.HOW_TO_PLAY_4_WINS);
	}

	public void draw() {
		graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

		drawGrid();
		drawStones();
	}

	private void drawGrid() {
		for (int y = 0; y < H_COUNT; y++) {
			for (int x = 0; x < W_COUNT; x++) {
				double posX = (x + 1) * MARGIN + 2 * RADIUS * x + PADDING_X;
				double posY = (y + 1) * MARGIN + 2 * RADIUS * y + PADDING_Y;

				if (isFinished) {
					graphicsContext.setStroke(Color.LIGHTGRAY);
				} else {
					graphicsContext.setStroke(Color.BLACK);
				}
				
				graphicsContext.beginPath();
				graphicsContext.arc(posX, posY, RADIUS, RADIUS, 0, 360);
				graphicsContext.closePath();
				graphicsContext.stroke();
			}
		}
	}

	private void drawStones() {
		boolean didDrawMouseOverCol = mouseOverCol < 0;
		for (int y = H_COUNT-1; y >= 0; y--) {
			for (int x = 0; x < W_COUNT; x++) {
				int stone = board.get(y).get(x);
				if (stone == 0) {
					// empty
					
					if (!didDrawMouseOverCol && x == mouseOverCol && isPlayerTurn && !isFinished) {
						Color color = Color.LIGHTGRAY;
						
						double posX = (x + 1) * MARGIN + 2 * RADIUS * x + PADDING_X;
						double posY = (y + 1) * MARGIN + 2 * RADIUS * y + PADDING_Y;
						
						graphicsContext.beginPath();
						graphicsContext.setFill(color);
						graphicsContext.arc(posX, posY, RADIUS-1, RADIUS-1, 0, 360);
						graphicsContext.closePath();
						graphicsContext.fill();
						
						didDrawMouseOverCol = true;
					}
					
					continue;
				} else if (stone == 1) {
					// player
					
					Color color = colors.playerColor().getColor().val();
					if ((x == temporaryStone[0] && y == temporaryStone[1]) || isFinished) {
						color = colors.playerColor().getLightColor().val();
					}
					
					double posX = (x + 1) * MARGIN + 2 * RADIUS * x + PADDING_X;
					double posY = (y + 1) * MARGIN + 2 * RADIUS * y + PADDING_Y;

					graphicsContext.beginPath();
					graphicsContext.setFill(color);
					graphicsContext.arc(posX, posY, RADIUS-1, RADIUS-1, 0, 360);
					graphicsContext.closePath();
					graphicsContext.fill();
				} else {
					// opponent

					double posX = (x + 1) * MARGIN + 2 * RADIUS * x + PADDING_X;
					double posY = (y + 1) * MARGIN + 2 * RADIUS * y + PADDING_Y;

					Color color;
					
					if (isFinished) {
						color = colors.opponentColor().getLightColor().val();
					} else {
						color = colors.opponentColor().getColor().val();
					}
					
					graphicsContext.beginPath();
					graphicsContext.setFill(color);
					graphicsContext.arc(posX, posY, RADIUS-1, RADIUS-1, 0, 360);
					graphicsContext.closePath();
					graphicsContext.fill();
				}
			}
		}
	}

	private void mouseMoved(MouseEvent e) {
		int x = (int)e.getX();
		int column = Math.max((int)((x - PADDING_X / 2) / ((2 * RADIUS + MARGIN))), 0);
		
		if (column != mouseOverCol) {
			mouseOverCol = column;
			draw();
		}
	}

	private void mouseReleased(MouseEvent e) {
		if (!isPlayerTurn)
			return;
		
		int x = (int)e.getSceneX();

		int column = Math.max((int)((x - PADDING_X / 2) / ((2 * RADIUS + MARGIN))), 0);
		
		isPlayerTurn = false;
		setStone(column, false, (didSet) -> {
			if (didSet) {
				resetTempStone();
				draw();
				updateStatusMenu(GameStatus.OPPONENT_TURN);
			} else {
				System.err.println("Couldn't set stone ==> reset");
				
				this.board.get(temporaryStone[1]).set(temporaryStone[0], 0);
				resetTempStone();
				draw();
				updateStatusMenu(GameStatus.ERROR);
			}
		});
	}
	
	public void didReceiveGameFinishedMessage(GameResult result) {
		String text = "";
		if (result == GameResult.PLAYER_WON) {
			text = "Du hast gewonnen!";
			statusLabel.setTextFill(Color.DARKGREEN);
		} else if (result == GameResult.OPPONENT_WON) {
			text = "Du hast verloren :(";
			statusLabel.setTextFill(Color.DARKRED);
		} else if (result == GameResult.TIE) {
			text = "Unentschieden";
		} else if (result == GameResult.ERROR) {
			text = "Fehler?";
		}
		
		isFinished = true;
		statusLabel.setText(text);
		statusLabel.setVisible(true);
		updateStatusMenu(GameStatus.END);
		draw();
	}

	public boolean setStone(int column, boolean isOpponent) {
		return setStone(column, isOpponent, null);
	}
	
	public boolean setStone(int column, boolean isOpponent, CompletionHandler<Boolean> completionHandler) {
		for (int y = H_COUNT-1; y >= 0; y--) {
			if (board.get(y).get(column) == 0) {
				if (!isOpponent) {
					currentTask = new CommunicationTask(new ClientMessage("game", "setstone", ""+column), (success, response) -> {
						synchronized (this.currentTask) {
							if (this.currentTask != null)
								this.currentTask.deleteObserver(this);
						}
						
						Platform.runLater(new Runnable() { @Override
							public void run() {
								if (success && response.getDomain().equals("success")) {
									if (completionHandler != null)
										completionHandler.apply(true);
								} else if (success && response.getDomain().equals("game") && response.getCommand().equals("finished")) {
									didReceiveGameFinishedMessage(GameResult.fromInt(Integer.parseInt(response.getArguments().get(0))));
									if (completionHandler != null)
										completionHandler.apply(false);
								} else {
									System.out.println("Can't set stone: " + response);
									if (completionHandler != null)
										completionHandler.apply(false);
								}
							}
						});
					});
					
					HashMap<String, Object> userInfo = new HashMap<>();
					userInfo.put("row", new Integer(y));
					userInfo.put("col", new Integer(column));
					
					currentTask.setUserInfo(userInfo);
					currentTask.addObserver(this);

					this.parent.getClient().enqueueTask(currentTask);
					
					updateStatusMenu(GameStatus.SENDING);
				} else {
					updateStatusMenu(GameStatus.PLAYER_TURN);
					board.get(y).set(column, 2);
					isPlayerTurn = true;
					if (null != completionHandler)
						completionHandler.apply(true);
				}

				return true;
			}
		}

		return false;
	}
	
	/**
	 * 
	 * Hier ist die Update-Methode:
	 * Sie wird aufgerufen, wenn der aktuelle CommunicationTask gesendet wurde.
	 * => Einen temporären Stein setzen, bis der Erfolg vom Server bestätigt wird.
	 * 
	 */
	@Override
	public void update(Observable o, Object arg) {
		if (o instanceof CommunicationTask) {
			// sent task to server
			
			CommunicationTask task = (CommunicationTask)o;
			
			if (task.getUserInfo().size() <= 0) {
				System.err.println("GameController: Sent a CommunicationTask without a userInfo!");
				System.exit(-1);
			} else if (!task.getUserInfo().containsKey("row") || !task.getUserInfo().containsKey("col")) {
				System.err.println("GameController: UserInfo lack row or col param: " + task);
				System.exit(-1);
			}
			
			int row = (Integer)task.getUserInfo().get("row");
			int col = (Integer)task.getUserInfo().get("col");
			
			this.board.get(row).set(col, 1); // 1 = player
			temporaryStone = tuple(col, row);
			draw();
		}
	}

	public Canvas getCanvas() {
		return canvas;
	}

	public void setCanvas(Canvas canvas) {
		this.canvas = canvas;
	}

	public Controller getParent() {
		return parent;
	}

	public void setParent(Controller parent) {
		this.parent = parent;
	}
	
	public Menu getStatusMenu() {
		return statusMenu;
	}

	public void setStatusMenu(Menu statusMenu) {
		this.statusMenu = statusMenu;
	}

	public Label getStatusLabel() {
		return statusLabel;
	}

	public void setStatusLabel(Label statusLabel) {
		this.statusLabel = statusLabel;
	}

	public boolean isPlayerTurn() {
		return isPlayerTurn;
	}

	public void setPlayerTurn(boolean isPlayerTurn) {
		this.isPlayerTurn = isPlayerTurn;
	}

	public Menu getPlayerColorMenu() {
		return playerColorMenu;
	}

	public void setPlayerColorMenu(Menu playerColorMenu) {
		this.playerColorMenu = playerColorMenu;
	}

	public Menu getOpponentColorMenu() {
		return opponentColorMenu;
	}

	public void setOpponentColorMenu(Menu opponentColorMenu) {
		this.opponentColorMenu = opponentColorMenu;
	}

	private Integer[] tuple(int a, int b) {
		return new Integer[] { a, b };
	}
	
	private void resetTempStone() {
		synchronized (this.temporaryStone) {
			this.temporaryStone = new Integer[] { -1, -1 };
		}
	}
	
	public boolean isFinished() {
		return isFinished;
	}

	private void updateStatusMenu(GameStatus status) {
		String mString = "";
		
		switch (status) {
			case PLAYER_TURN:
			case PLAYER_TURN_BUT_WAITING:
				mString = "Du bist am Zug";
				break;
			
			case OPPONENT_TURN:
				mString = "Gegner is am ziehen";
				break;
				
			case OPPONENT_TURN_BUT_WAITING:
				mString = "Warte auf Erstzug des Gegners";
				break;
				
			case SENDING:
				mString = "senden...";
				break;
				
			case END:
				mString = "Spiel beendet";
				break;
			
			default:
			case ERROR:
				mString = "Fehler";
			break;
		}
		
		statusMenu.setText(mString);
	}
}
