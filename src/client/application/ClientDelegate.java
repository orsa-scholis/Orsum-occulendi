package application;

import message.ServerMessage;

public interface ClientDelegate {
	public void clientDidNotConnect(Client client);
	public void clientDidSuccessfullyConnect(Client client);
	public void clientDidDisconnect(Client client);
	public void clientDidReceiveException(Throwable e);
	public void clientDidReceiveStandaloneServerMessage(ServerMessage message);
}
