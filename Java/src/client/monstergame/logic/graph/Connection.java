package client.monstergame.logic.graph;

import java.lang.ref.WeakReference;

/**
 * Diese Klasse repräsentiert eine Verbindung zwischen zwei Nodes (Oder auch im mathematischen Sinne eine Kante)
 * @author Lukas Bischof
 *
 */
public class Connection {
	/**
	 * Die erste Node
	 */
	private WeakReference<Node> start;
	
	/**
	 * Die zweite Node
	 */
	private WeakReference<Node> end;
	
	/**
	 * Die Länge der Verbindung, bzw. die Länge der Strecke zwischen den beiden Punkten
	 */
	private float length;
	
	/**
	 * Eine interne Variable, um zu erkennen, ob die Länge neu berechnet werden muss
	 */
	private boolean lengthDirty;
	
	/**
	 * Erzeugt eine neue Verbindung
	 * @param start	Die Node 1
	 * @param end	Die Node 2
	 */
	public Connection(Node start, Node end) {
		super();
		setStart(start);
		setEnd(end);
	}
	
	/**
	 * Gibt die erste Node zurück
	 * @return Die erste Node
	 */
	public Node getStart() {
		return start.get();
	}
	
	public void setStart(Node start) {
		this.start = new WeakReference<Node>(start);
		this.lengthDirty = true;
	}
	
	public Node getEnd() {
		return end.get();
	}
	
	public void setEnd(Node end) {
		this.end = new WeakReference<>(end);
		this.lengthDirty = true;
	}
	
	/**
	 * Gibt den "Gegenspieler" zurück. Wenn "thisNode" also der ersten Node entspricht, wird die zweite Node zurückgegeben.
	 * Wenn sie der zweiten Node entspricht, wird die erste Node zurückgegeben.
	 * @param thisNode	Die Node, für welche den Gegenspieler gefunden werden soll
	 * @return Den Gegenspieler zu "thisNode"
	 */
	public Node getCounterpart(Node thisNode) {
		if (thisNode.equals(start.get())) {
			return end.get();
		} else if (thisNode.equals(end.get())) {
			return start.get();
		} else {
			return null;
		}
	}
	
	/**
	 * Berechnet automatisch die Länge der Strecke zwischen den beiden Nodes
	 * @return Die Länge
	 */
	public float getLength() {
		if (lengthDirty) {
			Point p1 = getStart().getPoint();
			Point p2 = getEnd().getPoint();
			
			length = (float)Math.sqrt(Math.pow((p1.getX() - p2.getX()), 2.0) + Math.pow((p1.getY() - p2.getY()), 2.0));
		}
		
		return length;
	}
}
