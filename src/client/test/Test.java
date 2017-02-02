package test;

import static org.junit.Assert.*;

import org.junit.Before;

import message.ClientMessage;
import message.ServerMessage;

public class Test {

	@Before
	public void setUp() throws Exception {
	}

	@org.junit.Test
	public void testServerMessage() {
		try {
			ServerMessage message = new ServerMessage("connection:disconnect");
			
			if (! (message.getCommand().equals("disconnect") && message.getDomain().equals("connection"))) {
				fail("Wrong init");
			}
		} catch (Throwable e) {
			fail("Shouldn't assert");
		}
		
		try {
			ServerMessage message = new ServerMessage("game:setstone:2");
			if (! (message.getCommand().equals("setstone") && message.getDomain().equals("game") && message.getArguments().get(0).equals("2"))) {
				fail("Wrong init");
			}
		} catch (Throwable e) {
			fail("Shouldn't assert");
		}
	}
	
	@org.junit.Test(expected = Exception.class)
	public void testServerAssertMessagesOnEmpty() throws Throwable {
		new ServerMessage("");
	}
	
	@org.junit.Test(expected = Exception.class)
	public void testServerAssertMessagesOnTooFewArgs() throws Throwable {
		new ServerMessage("phillip");
	}
	
	@org.junit.Test()
	public void testClientMessage() {
		String domain = "connection";
		String command = "connect";
		String args = "test";
		
		ClientMessage message = new ClientMessage(domain, command, args);
		if (message.getDomain().equals(domain) && message.getCommand().equals(command) && message.getArguments().get(0).equals(args)) {
			// good
		} else {
			fail("Can't construct CLient message");
		}
	}
}
