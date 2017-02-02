package gui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.GameModel;
import models.ServerModel;

public class NewGameController implements Initializable {
	@FXML
	TextField serverNameField;
	@FXML
	Label errorOut;

	private ServerModel sm = null;
	private MainServerController msc;

	public NewGameController(MainServerController mainServerController) {
		this.msc = mainServerController;
	}

	@FXML
	private void handleNewServerSubmitButton(ActionEvent event) {
		saveEvent(((Stage) ((Node) (event.getSource())).getScene().getWindow()));
	}
	
	public void saveEvent(Stage stg){
		boolean exists = false;
		if (serverNameField.getText().length() > 2) {
			for (GameModel g : msc.getServer().getGames()) {
				if (g.getName().equals(serverNameField.getText())) {
					exists = true;
					break;
				}
			}
			if (!exists) {
				GameModel gm = new GameModel(serverNameField.getText(), sm);

				Platform.runLater(new Runnable() {
					@Override
					public void run() {

						sm.addGame(gm);
						msc.refreshViews();
					}
				});
				stg.close();
			}
			else{
				errorOut.setText("Name existiert bereits!");
			}

		} else {
			errorOut.setText("Name zu kurz!");
		}

		System.out.println("Submit!");
	}
	

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		serverNameField.setText("");
		// setUpViews();

	}

	public void setServerModel(ServerModel sm) {
		this.sm = sm;
	}

	public void setMainServerController(MainServerController msc) {
		this.msc = msc;
	}
}
