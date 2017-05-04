package client.message;

import java.util.ArrayList;

import org.w3c.dom.ranges.RangeException;

/**
 * Repräsentert eine erhaltene Nachricht des Servers
 * @author Lukas
 *
 */
public class ServerMessage extends Message {
	
	/**
	 * Diese Methode konstruiert eine ServerMessage und validiert diese
	 * @param msg	Die "rohe" Nachricht
	 * @throws Throwable	Falls die Nachricht nicht so formatiert ist, wie sie sollte
	 */
	public ServerMessage(String msg) throws Throwable {
		if (msg.length() <= 0) {
			throw new Exception("msg argument is empty");
		}
		
		String[] chunks = msg.split(":");
		
		if (chunks.length < 1) {
			throw new RangeException((short)1, "Must have more than 1 component(s)");
		}
		
		this.setDomain(chunks[0]);
		
		if (chunks.length >= 2) {
			this.setCommand(chunks[1]);
			
			ArrayList<String> args = new ArrayList<>();
			if (chunks.length > 2) {
				for (int i = 2; i < chunks.length; ++i) {
					args.add(chunks[i]);
				}
			}
			
			this.setArguments(args);
			
			if (!checkServerMessage()) 
				throw new Exception("Params are invalid");
		}
	}
	
	/**
	 * Kontrolliert, ob die Domäne existiert und ob der Command valide ist
	 * @return
	 */
	protected boolean checkServerMessage() {
		boolean valid = this.getDomain().length() > 0 	&&
						this.getCommand().length() > 0 	&&
						this.getArguments() != null;
						
		switch (this.getDomain()) {
			case "connection":
				valid = valid && (this.getCommand().equals("disconnect") || this.getCommand().equals("keyExchange")); // Server kann die Verbindung nur trennen
			break;
			
			case "game":
				valid = valid &&    this.getCommand().equals("setstone") // Der Gegner hat einen Stein gesetzt
					    		 || this.getCommand().equals("finished"); // Jemand hat gewonnen/unentschieden
			break;
			
			case "chat":
			break;
			
			case "success": // Kontextbezogene Nachrichten
			case "error":
				valid = valid && true;
			break;
			
			case "info":
			case "server":
			default:
				valid = false; // Server schickt keine Nachrichten mit der Server-Methode oder es gibt keine Info-Methode des Servers oder unbekannt
			break;
		}
		
		return valid;
	}
}
