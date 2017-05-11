package client.monstergame.logic.graph;

import java.util.ArrayList;

/**
 * Eine Subklasse von Node, damit man den Player von den anderen Nodes differenzieren kann.
 * @author Lukas Bischof
 *
 */
public class Player extends Node {

	public Player(ArrayList<Connection> connections, Point point) {
		super(connections, point);
	}
	
	public Player(Point point) {
		super(point);
	}
	
	
}
