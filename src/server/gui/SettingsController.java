package gui;


import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SettingsController implements Initializable {
	@FXML
	TextField portField;
	@FXML
	TextField maxPlayerField;
	@FXML
	Label errorOut;
	private int portNumber;
	private int maxPlayer;
	private MainServerController msc;

	@FXML
	private void handleServerSaveButton(ActionEvent event){
		saveEvent(((Stage)((Node) (event.getSource())).getScene().getWindow()));
	}

	public SettingsController(MainServerController msc){
		this.msc = msc;
	}

	public void saveEvent(Stage stg){
		boolean verified = false;
		if(portField.getText().length() == 4 && portField.getText().matches("\\d+$")){
			msc.getServer().setPortNumber(Integer.parseInt(portField.getText()));
			System.out.println(msc.getServer().getPortNumber());
			verified = true;
		}
		else{
			errorOut.setText("Ungültiger Port!");
			verified = false;
		}
		if(maxPlayerField.getText().length() > 0 && maxPlayerField.getText().matches("\\d*")){
			if(Integer.parseInt(maxPlayerField.getText()) > 1 && Integer.parseInt(maxPlayerField.getText()) < 60){
				msc.getServer().setMaxSockets(Integer.parseInt(maxPlayerField.getText()));
				verified = true;
			}
			else{
				verified = false;
				errorOut.setText(errorOut.getText() + "\nUngültige maximal Spieleranzahl!");
			}

		}
		else{
			verified = false;
			errorOut.setText(errorOut.getText() + "\nUngültige maximal Spieleranzahl!");
		}

		if(verified){
			stg.close();
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		portNumber = msc.getServer().getPortNumber();
		maxPlayer = msc.getServer().getMaxSockets();

		portField.setText("" + portNumber);
		maxPlayerField.setText("" + maxPlayer);
		// setUpViews();

	}

	public void setMainServerController(MainServerController msc){
		this.msc = msc;
	}

}
