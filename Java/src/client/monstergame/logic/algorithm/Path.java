package client.monstergame.logic.algorithm;

import java.util.ArrayList;

import client.monstergame.logic.graph.Node;
import client.monstergame.logic.graph.Point;

public class Path {
	private ArrayList<Point> points;

	public Path(ArrayList<Node> nodes) {
		super();

		this.points = new ArrayList<>();
		for (Node node : nodes){
			points.add(node.getPoint());
		}
	}

	public ArrayList<Point> getPoints() {
		return points;
	}

	public Point getDestination() {
		return this.points.get(this.points.size() - 1);
	}

	public Point getStart() {
		return this.points.get(0);
	}

	@Override
	public String toString() {
		return "Path [points=" + points + "]";
	}
}
