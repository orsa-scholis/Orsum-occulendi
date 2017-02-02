package message;

import java.util.ArrayList;

public abstract class Message {
	private String domain;
	private String command;
	private ArrayList<String> arguments = new ArrayList<>();
	
	public String construct() {
		String ret = domain + ":" + command;
		
		if (arguments.size() > 0) {
			for (String arg : arguments) {
				ret += ":" + arg;
			}
		}
		
		return ret;
	}
	
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	public ArrayList<String> getArguments() {
		return arguments;
	}
	public void setArguments(ArrayList<String> arguments) {
		this.arguments = arguments;
	}
	
	@Override
	public String toString() {
		return this.construct();
	}
}
