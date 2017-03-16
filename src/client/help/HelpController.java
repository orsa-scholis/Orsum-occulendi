package client.help;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class HelpController implements Initializable {

	@FXML
	private TextArea content;

	@FXML
	private Label title;

	private HelpType type;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	public void display() {
		switch (type) {
		case HOW_TO_CONNECT:
			content.setText("Verbinden mit dem Server\n\n\n"+
							"Damit sie sich mit einem Server verbinden k�nnen, gehen Sie unter dem Menu \"Server\" auf \"Verbinden\""+
							"Dort sollten Sie die IP Adresse des Servers eingeben. Diese k�nnen Sie herausfinden, indem Sie"+
							"auf dem Server in die Netzwerkeinstellungen (Netzwerk- und Freigabecenter) gehen"+
							"und dort auf ihre LAN-Verbindung klicken. In dem Pop-Up k�nnen Sie nun auf \"Details\"gehen und dort steht die IPv4 Adresse. "+
							"Den Port m�ssen Sie nicht unbedingt �ndern. "+
							"Das \"Name\"-Textfeld ist f�r den Namen des Client. Den k�nnen Sie frei w�hlen.");

			title.setText("Wie verbinde ich mich?");
			break;

			case WHAT_IS_A_PORT:
				content.setText("Der Port ist quasi der Eingang f�r die ganze Kommunikation zwischen Client und Server. "+
								"Er kann eigentlich frei gew�hlt werden"+
								"Wichtig ist eigentlich nur, dass beim Client genau der gleiche Port wie beim Server ausgew�hlt werden muss.");
				title.setText("Was ist ein Port?");
				break;

			case HOW_TO_PLAY_4_WINS:
				content.setText("Vier Gewinnt besteht aus einem Brett, in das man Jetons hineinwerfen kann. "+
								"Diese stapeln sich dann auf. Ziel ist es, wer als Erstes vier in einer Reihe hat. "+
								"Dazu kann die Reihe entweder horizontal, vertikal oder schr�g sein. "+
								"\n"+
								"Mehr Informationen finden Sie auf https://de.wikipedia.org/wiki/Vier_gewinnt");
				title.setText("Wie spielt man Vier Gewinnt?");
				break;
				
			case WHAT_IS_MY_IP:
				try {
					content.setText("Deine IP Adresse ist " + InetAddress.getLocalHost().getHostAddress() + ". Die IP des Servers wird bis auf einige letzte Ziffern gleich sein, "+
									"da sich beide im gleichen Subnetz befinden müssen. Es sei denn du willst lokal spielen. "+
									"Dann müssen beide die Adresse 127.0.0.1 aufweisen.");
				} catch (UnknownHostException e) {
					content.setText("Deine IP Adresse konnte nicht identifiziert werden");
					e.printStackTrace();
				} finally {
					title.setText("Was ist meine IP Adresse?");
				}
				
				break;

			case ABOUT:
				content.setText("Schulprojekt von Philipp Fehr (Server) und Lukas Bischof (Client)");
				title.setText("About: Vier Gewinnt");
				break;
		}
	}

	public TextArea getContent() {
		return content;
	}

	public void setContent(TextArea content) {
		this.content = content;
	}

	public HelpType getType() {
		return type;
	}

	public void setType(HelpType type) {
		this.type = type;
	}

	public Label getTitle() {
		return title;
	}

	public void setTitle(Label title) {
		this.title = title;
	}
}
