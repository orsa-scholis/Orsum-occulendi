package client.application;

import client.message.ServerMessage;

/**
 * Ein Interface, welches eine Klasse zu dem Korrespondent der Client-Klasse erhebt
 * @author Lukas
 *
 */
public interface ClientDelegate {
	public void clientDidNotConnect(Client client);
	public void clientDidSuccessfullyConnect(Client client);
	public void clientDidDisconnect(Client client);
	public void clientDidReceiveException(Throwable e);
	public void clientDidReceiveStandaloneServerMessage(ServerMessage message);
}
