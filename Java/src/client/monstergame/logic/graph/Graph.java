package client.monstergame.logic.graph;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Diese Klasse repräsentiert den Graphen
 * @author lukasbischof
 *
 */
public class Graph {
	private Node root;
	private Node end;
	private ArrayList<Node> nodes;

	public Graph() {
		super();
		this.nodes = new ArrayList<>();
	}

	/**
	 * Erstellt einen Graphen mit einer Root-Node, sowie mit anderen, verschiedenen Nodes
	 * @param root	Die Root-Node
	 * @param nodes	Die anderen Nodes
	 */
	public Graph(Node root, ArrayList<Node> nodes) {
		super();
		this.root = root;
		this.nodes = nodes;
	}

	public void addNode(Node node) {
		this.nodes.add(node);
	}

	public void removeNode(Node node) {
		this.nodes.remove(node);
	}

	public Node getRoot() {
		return root;
	}

	public void setRoot(Node root) {
		this.root = root;
	}

	public ArrayList<Node> getNodes() {
		return nodes;
	}

	public void setNodes(ArrayList<Node> nodes) {
		this.nodes = nodes;
	}

	public Node getEnd() {
		return end;
	}

	public void setEnd(Node end) {
		this.end = end;
	}

	/**
	 * Exportiert den Graphen in ein JSON-Format
	 * @param fieldSize	Die Grösse des Feldes
	 * @return Den Export-String
	 */
	public String toString(double fieldSize) {
		String returnString = "nodes: {";
		for (Iterator<Node> iterator = nodes.iterator(); iterator.hasNext();) {
			Node node = (Node)iterator.next();

			double x = node.getPoint().getX() * fieldSize;
			double y = node.getPoint().getY() * fieldSize;

			returnString += "\n" + x + "\t" + y;
		}

		returnString += "\n}";
		return returnString;
	}

	public Graph clone() throws CloneNotSupportedException {
		Graph graph = new Graph();

		for (Node node : this.nodes) {
			Node newNode = new Node(node.getPoint());
			graph.getNodes().add(newNode);
		}

		graph.setRoot(this.root != null ? new Node(this.root.getPoint()) : null);
		graph.setEnd(this.end != null ? new Node(this.end.getPoint()) : null);

		// Alle Nodes neu verbinden
		for (int i = 0; i < this.nodes.size(); i++) {
			Node oldNode = nodes.get(i);
			Node newNode = graph.getNodes().get(i);

			for (Connection connection : oldNode.getConnections()) {
				Node oldCounterpart = connection.getCounterpart(oldNode);
				int oldCounterpartNodeIndex = nodes.indexOf(oldCounterpart);
				if (oldCounterpartNodeIndex >= 0) {
					// In array

					newNode.connectTo(graph.getNodes().get(oldCounterpartNodeIndex));
				} else {
					// Not in array

					if (oldCounterpart.equals(this.root)) {
						newNode.connectTo(graph.getRoot());
					} else if (oldCounterpart.equals(this.end)) {
						newNode.connectTo(graph.getEnd());
					} else {
						System.err.println("Node connected to a node outside of graph");
					}
				}
			}
		}

		if (this.root != null && this.end != null) {
			for (Connection rootConnection : this.getRoot().getConnections()) {
				if (rootConnection.getCounterpart(root).equals(this.end)) {
					graph.getRoot().connectTo(graph.getEnd());
				}
			}
		}

		return graph;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((end == null) ? 0 : end.hashCode());
		result = prime * result + ((nodes == null) ? 0 : nodes.hashCode());
		result = prime * result + ((root == null) ? 0 : root.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Graph other = (Graph) obj;
		if (end == null) {
			if (other.end != null) {
				return false;
			}
		} else if (!end.equals(other.end)) {
			return false;
		}
		if (nodes == null) {
			if (other.nodes != null) {
				return false;
			}
		} else if (!nodes.equals(other.nodes)) {
			return false;
		}
		if (root == null) {
			if (other.root != null) {
				return false;
			}
		} else if (!root.equals(other.root)) {
			return false;
		}
		return true;
	}

	public String uniqueExportString() {
		String output = "<Graph, nodes: [";
		for (Node node : nodes) {
			output += "\n" + node + ", connections: ";

			for (Connection connection : node.getConnections()) {
				output += "\n\t" + connection.getCounterpart(node) + ", ";
			}

			output += "],";
		}

		output += "\nroot: " + root + ", ";
		output += "\nend: " + end + ">";

		return output;
	}
}
