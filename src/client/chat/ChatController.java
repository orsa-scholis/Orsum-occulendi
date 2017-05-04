package client.chat;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class ChatController implements Initializable {

	@FXML
	private ListView<String> listView;

	@FXML
	private TextField chatTextField;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub

	}

	@FXML
	private void sendButtonPressed() {
		System.out.println("press");

	}

	public ListView<String> getListView() {
		return listView;
	}

	public void setListView(ListView<String> listView) {
		this.listView = listView;
	}

	public TextField getChatTextField() {
		return chatTextField;
	}

	public void setChatTextField(TextField chatTextField) {
		this.chatTextField = chatTextField;
	}

}
