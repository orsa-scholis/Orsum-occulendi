package message;

import java.util.ArrayList;

public class ClientMessage extends Message {
	public ClientMessage(String domain, String command, ArrayList<String> arguments) {
		this.setDomain(domain);
		this.setCommand(command);
		this.setArguments(arguments);
	}
	
	public ClientMessage(String domain, String command, String arg1) {
		ArrayList<String> arguments = new ArrayList<>();
		arguments.add(arg1);
		
		this.setDomain(domain);
		this.setCommand(command);
		this.setArguments(arguments);
	}
	
	public ClientMessage(String domain, String command, String arg1, String arg2) {
		ArrayList<String> arguments = new ArrayList<>();
		arguments.add(arg1);
		arguments.add(arg2);
		
		this.setDomain(domain);
		this.setCommand(command);
		this.setArguments(arguments);
	}
}
