package client.monstergame.logic.algorithm;

import client.monstergame.logic.graph.Node;

/**
 *
 * Diese Klasse ist eine Hilfsklasse der Dijkstra-Klasse.
 * Da die bereits vorhandene Node-Klasse weniger Informationen speichern kann, als Dijkstra benötigt, füllt diese Klasse die fehlenden Informationen ein.
 * Dies ist insbesondere die hier als "vorhergehende" Bezeichnete Node, die Länge und den Flag "checked".
 * Da diese nur Packetintern verwendet wird, ist es auch nur packetintern verfügbar.
 *
 * Node me : Dies ist die eigentliche Node.
 * float length : Die kürzeste berechnete Länge, Standartwert 10000, damit die berechneten Längen kürzer sind.
 * boolean checked : Dieser boolean wird dazu verwendete "fertige" Nodes, also solche, die sicher keine kürzere Länge mehr erhalten.
 * Node previous : Dies ist die vorhergehende Node, dies ist dafür da, dass man am Schluss des Berechnes den idealen Pfad auslesen kann.
 *
 */

class DijkstraNode {
	private Node me;
	private float length;
	private boolean checked;
	private Node previous;

	/**
	 * DijkstraNode-Konstruktor
	 * @param me: Dies ist die eigentliche Node.
	 */
	public DijkstraNode(Node me) {
		this.me = me;
		this.length = 10000;
		this.checked = false;
		this.previous = null;
	}

	public float getLength() {
		return length;
	}

	public void setLength(float length) {
		this.length = length;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked() {
		this.checked = true;
	}

	public Node getPrevious() {
		return previous;
	}

	public void setPrevious(Node previous) {
		this.previous = previous;
	}

	public Node getMe() {
		return me;
	}

}
