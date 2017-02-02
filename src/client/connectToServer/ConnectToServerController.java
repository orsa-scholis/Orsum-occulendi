package connectToServer;

import java.net.URL;
import java.util.ResourceBundle;

import application.UserDefaults;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ConnectToServerController implements Initializable {
	
	@FunctionalInterface
	public interface CompletionRunnable<A> {
		public void apply(A a);
	}
	
	@FXML
	protected TextField ipAdressTextField;
	
	@FXML
	protected TextField portTextField;
	
	@FXML
	protected TextField nameTextField;
	
	@FXML
	protected Label statusLabel;
	
	private CompletionRunnable<Runnable> completedRunnable;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
		//System.out.println(UserDefaults.sharedDefaults().valueForKey(UserDefaults.PORT_KEY));
		ipAdressTextField.setText((String)UserDefaults.sharedDefaults().valueForKey(UserDefaults.ADRESS_KEY, ""));
		portTextField.setText(""+UserDefaults.sharedDefaults().valueForKey(UserDefaults.PORT_KEY, ""));
		nameTextField.setText("" + UserDefaults.sharedDefaults().valueForKey(UserDefaults.NAME_KEY, ""));
	}
	
	private boolean validateIP(String ip) {
		String[] components = ip.split("\\.");
		if (components.length != 4) {
			return false;
		}
		
		for (int i = 0; i < components.length; i++) {
			String component = components[i];
			try {
				int temp = Integer.parseInt(component);
				
				if (temp < 0 || temp > 255) {
					return false;
				}
			} catch (NumberFormatException formatException) {
				return false;
			}
		}
		
		return true;
	}
	
	private ConnectToServerControllerErrors validateInput() {
		try {
			int port = Integer.parseInt(portTextField.getText());
			if (port < 0) {
				return ConnectToServerControllerErrors.PORT_TEXT_FILD;
			}
			
			return validateIP(ipAdressTextField.getText()) ? ConnectToServerControllerErrors.NO_ERR : ConnectToServerControllerErrors.IP_TEXT_FIELD;
		} catch (NumberFormatException formatException) {
			return ConnectToServerControllerErrors.PORT_TEXT_FILD;
		}
	}
	
	@FXML
	protected void connectButtonPressed(Event event) {
		ConnectToServerControllerErrors error = validateInput();
		
		if (error == ConnectToServerControllerErrors.NO_ERR) {			
			UserDefaults.sharedDefaults().setKey(UserDefaults.ADRESS_KEY, ipAdressTextField.getText());
			UserDefaults.sharedDefaults().setKey(UserDefaults.PORT_KEY, Integer.parseInt(portTextField.getText()));
			UserDefaults.sharedDefaults().setKey(UserDefaults.NAME_KEY, nameTextField.getText());
		    
			if (completedRunnable != null) {
				completedRunnable.apply(() -> {
					System.out.println("Close: " + event);
					close(event);
				});
			}
			
		} else if (error == ConnectToServerControllerErrors.IP_TEXT_FIELD) {
			statusLabel.setText("IP address is invalid");
		} else if (error == ConnectToServerControllerErrors.PORT_TEXT_FILD) {
			statusLabel.setText("Port is invalid");
		}
	}
	
	public void close(Event event) {
		Stage stage = (Stage)((Node)(event.getSource())).getScene().getWindow();
		if (stage == null)
			return;
		stage.close();
	}

	public CompletionRunnable<Runnable> getCompletedRunnable() {
		return completedRunnable;
	}

	public void setCompletedRunnable(CompletionRunnable<Runnable> completedRunnable) {
		this.completedRunnable = completedRunnable;
	}

	public TextField getIpAdressTextField() {
		return ipAdressTextField;
	}

	public TextField getPortTextField() {
		return portTextField;
	}

	public Label getStatusLabel() {
		return statusLabel;
	}

	public void setStatusLabel(Label statusLabel) {
		this.statusLabel = statusLabel;
	}

	public TextField getNameTextField() {
		return nameTextField;
	}

	public void setNameTextField(TextField nameTextField) {
		this.nameTextField = nameTextField;
	}
}
