package client.monstergame.logic.graph;

import java.util.ArrayList;

/**
 * Eine Subklasse von Node, damit man das Monster von den anderen Nodes differenzieren kann.
 * @author Lukas Bischof
 *
 */
public class Monster extends Node {

	// Methoden für den Graph
	public Monster(ArrayList<Connection> connections, Point point) {
		super(connections, point);
	}
	
	public Monster(Point point) {
		super(point);
	}
}
