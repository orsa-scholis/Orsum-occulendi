package client.monstergame.logic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import client.monstergame.logic.algorithm.Path;
import client.monstergame.logic.algorithm.PathFinder;
import client.monstergame.logic.graph.Connection;
import client.monstergame.logic.graph.Monster;
import client.monstergame.logic.graph.Node;
import client.monstergame.logic.graph.Player;
import client.monstergame.logic.graph.Point;

/**
 * Diese Klasse repräsentert das Spiel an sich un bietet wichtige Funktionen für die Interaktion zwischen Algorithmus/Backend
 * und Presentation/View/Frontend
 * @author Lukas Bischof
 *
 */
public class Game {
	private QuadController quadController;
	private PathFinder pathFinder;
	private Player player;
	private Monster monster;

	/**
	 * Game constructor
	 * @param field	Represents the field. A 0 represents an empty space, while 1 represents an obstacle.
	 * The origin ((0, 0)-Index) is in the bottom left corner
	 */
	public Game(int[][] field) {
		super();

		int width = field[0].length;
		int height = field.length;
		this.quadController = new QuadController(width, height);
		this.player = new Player(new Point(0.01, 0.01));
		this.monster = new Monster(new Point(0.99, 0.99));

		for (Quad quad : quadController.getQuads()) {
			int isObstacle = field[quad.getIndex().getY()][quad.getIndex().getX()];
			quad.setObstacle(isObstacle == 1);
		}

		setup();
	}

	protected void setup() {
		pathFinder = new PathFinder(quadController.getGraphNodesWithObstacles(), quadController);
	}

	/**
	 * Gibt den idealen Pfad zum Abschuss auf den Player von dem Monster
	 * @return Den Pfad
	 */
	public Path getPathForMonster() {
		return pathFinder.getBestPathToShootForMonster(monster, player, quadController);
	}

	/**
	 * Mit dieser Methode kann man ein export file zum debuggen erstellen
	 * @param path
	 */
	public void export(String path) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(new File(path)));
			writer.write(getExportStr());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private String getExportStr() {
		String returnString = "{\"size\":"+quadController.getWidth()+",\"nodes\":[";

		ArrayList<Connection> connections = new ArrayList<>();
		ArrayList<Node> nodes = new ArrayList<>();
		nodes.addAll(pathFinder.getGraph().getNodes());
		nodes.add(monster);
		nodes.add(player);
		for (Node node : nodes) {
			returnString += "[" + node.getPoint().getX() + "," + node.getPoint().getY() + "],";

			for (Connection connection : node.getConnections()) {
				if (!connections.contains(connection)) {
					connections.add(connection);
				}
			}
		}
		returnString = returnString.substring(0, returnString.length() - 1); // remove last comma
		returnString += "],\"lines\":[";

		for (Connection connection : connections) {
			returnString += "[[" + connection.getStart().getPoint().getX() + "," + connection.getStart().getPoint().getY()
						 + "],[" + connection.getEnd().getPoint().getX() + "," + connection.getEnd().getPoint().getY() + "]],";
		}
		returnString = returnString.substring(0, returnString.length() - 1) + "],"; // remove last comma

		returnString += "\"obstacles\":[";
		for (Quad obstacle : quadController.getObstacles()) {
			returnString += "[" + obstacle.getIndex().getX() + "," + obstacle.getIndex().getY() + "],";
		}
		returnString = returnString.substring(0, returnString.length() - 1) + "]"; // remove last comma
		
		returnString += ",\"player\":[" + player.getPoint().getX() + "," + player.getPoint().getY() + "]";
		returnString += ",\"monster\":[" + monster.getPoint().getX() + "," + monster.getPoint().getY() + "]";

		returnString += "}";
		return returnString;
	}

	public void setMonsterPosition(Point newPoint) {
		monster.setPoint(newPoint);
	}

	public void setPlayerPosition(Point newPoint) {
		player.setPoint(newPoint);
	}

	public QuadController getQuadController() {
		return quadController;
	}

	public Player getPlayer() {
		return player;
	}

	public Monster getMonster() {
		return monster;
	}

	/**
	 * @return the pathFinder
	 */
	public PathFinder getPathFinder() {
		return pathFinder;
	}
}
