package client.monstergame.logic.algorithm;

import java.rmi.UnexpectedException;
import java.util.ArrayList;

import client.monstergame.logic.LinearFunction;
import client.monstergame.logic.QuadController;
import client.monstergame.logic.graph.Connection;
import client.monstergame.logic.graph.Graph;
import client.monstergame.logic.graph.Monster;
import client.monstergame.logic.graph.Node;
import client.monstergame.logic.graph.Player;
import client.monstergame.logic.graph.Point;

/**
 * Diese Klasse ist dazu da, den besten Pfad für ein Monster zum Player zu erhalten
 * @author Lukas Bischof
 *
 */
public class PathFinder {
	private Graph graph;
	private QuadController quadController;

	/**
	 * Der designierte Konstruktor
	 * @param nodes	Alle Nodes, die dazu verwendet werden können, um den Graphen zu generieren.
	 * @param quadController	Der QuadController
	 */
	public PathFinder(ArrayList<Node> nodes, QuadController quadController) {
		super();
		this.graph = new Graph(null, nodes);
		this.quadController = quadController;

		connectNodes();
	}

	/**
	 * Private Methode um die einzelnen Nodes miteinander zu verbinden (Kanten erstellen)
	 */
	private void connectNodes() {
		for (int i = 0; i < this.graph.getNodes().size(); i++) {
			Node currentNode = this.graph.getNodes().get(i);
			for (int j = i + 1; j < this.graph.getNodes().size(); j++) {
				Node nodeToConnect = this.graph.getNodes().get(j);

				Point p1 = currentNode.getPoint();
				Point p2 = nodeToConnect.getPoint();

				if (!quadController.testLineForObstacles(p1, p2)) {
					currentNode.connectTo(nodeToConnect);
				}
			}
		}
	}
	
	/**
	 * Fügt den Spieler und ein Monster in den Graphen ein.
	 * @param player	Der Spieler
	 * @param monster	Das Monster
	 * @param quadController	Der QuadController für das vorliegende Feld
	 * @param graph	Der Graph, in den der Spieler / das Monster integriert werden.
	 */
	private void integratePlayerAndMonsterIntoGraph(Player player, Monster monster, QuadController quadController, Graph graph) {
		graph.setRoot(monster);
		graph.setEnd(player);

		Point monsterPoint = monster.getPoint();
		Point playerPoint = player.getPoint();
		for (Node node : graph.getNodes()) {
			if (!quadController.testLineForObstacles(monsterPoint, node.getPoint())) {
				monster.connectTo(node);
			}

			if (!quadController.testLineForObstacles(playerPoint, node.getPoint())) {
				player.connectTo(node);
			}
		}
	}

	/**
	 * Diese Methode sucht nach weiteren Möglichkeiten, die das Monster brauchen kann, um zum Spieler zu gelangen,
	 * indem es Geraden an die Player-Nodes anlegt und diese mit rechtwinkligen Geraden zu den anderen Nodes verbindet, 
	 * um dann den Schnittpunkt hineinzufügen.
	 * @param player	Der Spieler
	 * @param monster	Das Monster
	 * @param quadController	Der QuadController für das vorliegende Feld 
	 * @param graph	Der momentane Graph
	 * @throws UnexpectedException
	 */
	private void findAdditionalGraphNodesWithPerpendicularLineAtPointOfInterception(Player player, Monster monster, QuadController quadController, Graph graph) throws UnexpectedException {
		Object clone = player.getConnections().clone();
		if (!(clone instanceof ArrayList<?>)) {
			throw new UnexpectedException("Clone of ArrayList is not an Array");
		}

		@SuppressWarnings("unchecked")
		ArrayList<Connection> playerConnections = (ArrayList<Connection>)clone;

		Point playerPosition = player.getPoint();
		for (Connection connection : playerConnections) {
			Point connectionNodePosition = connection.getEnd().getPoint();

			LinearFunction function = new LinearFunction(playerPosition, connectionNodePosition);
			LinearFunction perpendicularFunction = function.getPerpendicularFunction(monster.getPoint());
			Point interception = function.getInterceptionPoint(perpendicularFunction);

			if (interception.getX() < 0 || interception.getX() > 1 ||
				interception.getY() < 0 || interception.getY() > 1) {
				continue;
			}

			//System.out.println("player: " + playerPosition + ", inter: aaa" + interception);
			if (!quadController.testLineForObstacles(playerPosition, interception)) {
				Node node = new Node(interception);
				node.connectTo(connection.getEnd());
				graph.addNode(node);

				if (!quadController.testLineForObstacles(interception, monster.getPoint())) {
					node.connectTo(monster);
					// Hier müsste man eigentlich auch die anderen Nodes mit der neuen Verbinden,
					// doch für Performance überspringen wir dies (noch)
				} else {
					for (Node graphNode : graph.getNodes()) {
						if (graphNode.equals(node)) {
							continue;
						}

						if (!quadController.testLineForObstacles(graphNode.getPoint(), node.getPoint())) {
							node.connectTo(graphNode);
						}
					}
				}
			}
		}
	}

	/**
	 * Gibt den besten Pfad für das Monster um auf den Player zu schiessen zurück
	 * @param monster	Das Monster, für das der Pfad generiert wird
	 * @param player	Der Spieler
	 * @param quadController	Der QuadController des Feldes
	 * @return Den besten Pfad für das Monster
	 */
	public Path getBestPathToShootForMonster(Monster monster, Player player, QuadController quadController) {
		try {
			Graph graph = this.graph.clone();
			Monster monsterClone = new Monster((Point)monster.getPoint().clone());
			Player playerClone = new Player((Point)player.getPoint().clone());
			integratePlayerAndMonsterIntoGraph(playerClone, monsterClone, quadController, graph);
			try {
				findAdditionalGraphNodesWithPerpendicularLineAtPointOfInterception(playerClone, monsterClone, quadController, graph);
			} catch (UnexpectedException e) {
				e.printStackTrace();
				return null;
			}

			Dijkstra dijkstra = new Dijkstra(graph);
			return dijkstra.getShortestWaysPath();
		} catch (CloneNotSupportedException e1) {
			e1.printStackTrace();

			return null;
		}
	}

	/**
	 * Gibt den Graphen zurück
	 * @return	Den Graphen
	 */
	public Graph getGraph() {
		return graph;
	}
}
