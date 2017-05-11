package client.monstergame.logic.graph;

import java.util.ArrayList;

/**
 * Eine Node in dem Graphen
 * @author Lukas Bischof
 *
 */
public class Node {
	private ArrayList<Connection> connections = new ArrayList<>();
	private Point point;
	private final int id;
	private static int currentIdCounter = 0;
	
	public Node(Point point) {
		super();
		
		this.point = point;
		this.id = Node.currentIdCounter++;
	}
	
	public Node(ArrayList<Connection> connections, Point point) {
		super();
		this.connections = connections;
		this.point = point;
		this.id = Node.currentIdCounter++;
	}
	
	/**
	 * Verbindet diese Node mit einer anderen
	 * @param nodeToConnect	Die Node, mit welcher diese Node verbunden werden soll
	 */
	public void connectTo(Node nodeToConnect) {
		if (nodeToConnect.equals(this)) {
			System.out.println("(Node) Cannot connect to itself");
			return;
		}
		
		Connection connection = new Connection(this, nodeToConnect);
		this.connections.add(connection);
		nodeToConnect.getConnections().add(connection);
	}
	
	public ArrayList<Connection> getConnections() {
		return connections;
	}
	
	public void setConnections(ArrayList<Connection> connections) {
		this.connections = connections;
	}
	
	public Point getPoint() {
		return point;
	}
	
	public void setPoint(Point point) {
		this.point = point;
	}

	@Override
	public String toString() {
		return "Node [point=" + point + "]";
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		@SuppressWarnings("unchecked")
		ArrayList<Connection> clone2 = (ArrayList<Connection>)this.connections.clone();
		Node clone = new Node(clone2, new Point(this.point.getX(), this.point.getY()));
		return clone;
	}

	/* 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((point == null) ? 0 : point.hashCode());
		return result;
	}

	/* 
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
		
		Node other = (Node)obj;
		if (id != other.id) {
			return false;
		}
		
		if (point == null) {
			if (other.point != null) {
				return false;
			}
		} else if (!point.equals(other.point)) {
			return false;
		}
		
		return true;
	}
}
