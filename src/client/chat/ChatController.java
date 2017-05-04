package client.chat;

import java.net.URL;
import java.util.ResourceBundle;

import client.application.Client;
import client.message.ClientMessage;
import client.message.CommunicationTask;
import client.message.ServerMessage;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class ChatController implements Initializable {

	@FXML
	private ListView<String> listView;

	@FXML
	private TextField chatTextField;
	
	private ObservableList<String> messages;
	private Client client;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
		messages = FXCollections.observableArrayList("Huhu");
		listView.setItems(messages);
	}

	@FXML
	private void sendButtonPressed() {
		String text = getChatTextField().getText();
		
		client.enqueueTask(new CommunicationTask(new ClientMessage("chat", "send", text), (success, msg) -> {
			if (success) {
				Platform.runLater(() -> {
					listView.getItems().add(client.getUserID() + ": " + text);
				});
			}
		}));
		
		getChatTextField().setText("");
	}
	
	public void didReceiveChatServerMessage(ServerMessage msg) {
		System.out.println("Chat message: " + msg);
		Platform.runLater(() -> {
			listView.getItems().add(msg.getCommand() + ": " + msg.getArguments().get(0));
		});
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

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}
}
