package client.monstergame.logic.algorithm;

import java.util.ArrayList;
import java.util.Collections;

import client.monstergame.logic.algorithm.DijkstraNode;
import client.monstergame.logic.graph.Connection;
import client.monstergame.logic.graph.Graph;
import client.monstergame.logic.graph.Node;

/**
*
* Diese Klasse implementiert den Dijkstra-Algorithmus.
*
* Dazu ben�tig sie die start-Node, die end-Node, eine Liste aller Nodes (allNodes) und eine Liste aller DijkstraNodes (diese wird generiert
*
*/
public class Dijkstra {
	private Node start;
	private Node end;
	private ArrayList<Node> allNodes;
	private ArrayList<DijkstraNode> allNodesAsDN;

	public Dijkstra(Graph graph) {
		this.start = graph.getRoot();
		this.end = graph.getEnd();

		allNodes = graph.getNodes();
		//allNodes = (ArrayList<Node>) graph.getNodes().clone();
		allNodes.add(start);
		allNodes.add(end);
		allNodesAsDN = new ArrayList<>();
		for (Node node : allNodes) {
			DijkstraNode dnode = new DijkstraNode(node);
			if (node == start) {
				dnode.setLength(0);
			}
			allNodesAsDN.add(dnode);
		}
	}

/**
 *
 * Implementation von Dijkstra.
 *
 * @return Path Der k�rzeste errechnete Pfad.
 */
	public Path getShortestWaysPath() {
		DijkstraNode activeNode = findNodeWithShortestLength();
		while (activeNode.getMe() != end) {
			activeNode = findNodeWithShortestLength();
			activeNode.setChecked();
			for (Connection conn : activeNode.getMe().getConnections()) {
				if (!getDnodeFromNode(getTargetNode(conn, activeNode.getMe())).isChecked()) {
					if (getDnodeFromNode(getTargetNode(conn, activeNode.getMe())).getLength() > activeNode.getLength()
							+ conn.getLength()) {
						getDnodeFromNode(getTargetNode(conn, activeNode.getMe()))
								.setLength(activeNode.getLength() + conn.getLength());
						getDnodeFromNode(getTargetNode(conn, activeNode.getMe())).setPrevious(activeNode.getMe());
					}
				}
			}
		}

		return getPathAsArrayList();
	}

	/**
	 *
	 * Sucht in der ArrayList allNodesAsDN nach dem Knoten mit der k�rzesten Distanz, der noch nicht abgearbeitet wurde.
	 *
	 * @return DijkstraNode
	 */
	private DijkstraNode findNodeWithShortestLength() {
		DijkstraNode shortest = new DijkstraNode(new Node(null));

		for (DijkstraNode dnode : allNodesAsDN) {
			if (!dnode.isChecked()) {
				if (dnode.getLength() < shortest.getLength()) {
					shortest = dnode;
				}
			}
		}
		return shortest;
	}

	/**
	 *
	 * Gibt die DijkstraNode zur�ck, welche die node als me gespeichert hat.
	 *
	 * @param node Eine Node aus der allNodes ArrayList.
	 * @return DijkstraNode Die DijkstraNode, die die node als me gespeichert hat.
	 */
	private DijkstraNode getDnodeFromNode(Node node) {
		try{
		if (allNodesAsDN.get(allNodes.indexOf(node)).getMe() == node) {
			return allNodesAsDN.get(allNodes.indexOf(node));
		}
		for (DijkstraNode dnode : allNodesAsDN) {
			if (dnode.getMe() == node) {
				return dnode;
			}
		}
		}catch (ArrayIndexOutOfBoundsException e){
			System.out.println(node.toString());
			e.printStackTrace();
		}
		return null;
	}

	/**
	 *
	 * Gibt die gegen�berliegende Node einer Connection zur�ck.
	 *
	 * @param conn Die Connection aus der man die gegen�berliegende sucht.
	 * @param node Die Node die man bereits kennt.
	 * @return Die Node die auf der anderen Seite der Connection liegt.
	 */
	private Node getTargetNode(Connection conn, Node node) {
		if (conn.getEnd() == node) {
			return conn.getStart();
		}
		return conn.getEnd();
	}

	/**
	 *
	 * Gibt den berechneten Pfad als ArrayList zur�ck.
	 *
	 * @return Path Der errechnete Pfad.
	 */
	private Path getPathAsArrayList() {
		ArrayList<Node> out = new ArrayList<>();
		DijkstraNode activeNode = getDnodeFromNode(end);
		out.add(activeNode.getMe());

		while (activeNode.getMe() != start) {
			out.add(activeNode.getPrevious());
			activeNode = getDnodeFromNode(activeNode.getPrevious());
		}

		Collections.reverse(out);
		return new Path(out);
	}

}
