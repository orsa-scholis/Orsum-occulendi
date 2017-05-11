package client.monstergame.logic;

import java.util.ArrayList;

import client.monstergame.logic.graph.Node;
import client.monstergame.logic.graph.Point;

final class Constants {
	public static float OBSTACLE_CORNER_NODE_PADDING = 0.2f;
}

/**
 * Diese Klasse repräsentiert das Spielfeld
 * @author lukasbischof
 *
 */
public class QuadController {
	private ArrayList<Quad> quads = new ArrayList<>();
	private int width;
	private int height;
	
	/**
	 * Constructor for the class QuadController
	 * @param width	The width of the field. It currently must be equal to the height to result in a square field
	 * @param height	The height of the field. It currently must be equal to the width to result in a square field
	 */
	public QuadController(int width, int height) {
		super();
		
		this.width = width;
		this.height = height;
		
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				quads.add(new Quad(new Index2D(j, i))); // x; y
			}
		}
	}
	
	/**
	 * Testet, ob zwischen dem start und dem endpunkt ein Hindernis liegt
	 * @param start Der Startpunkt
	 * @param end Der Endpunkt
	 * @return Ein bool ob ein Hindernis dazwischen ist.
	 */
	public boolean testLineForObstacles(Point start, Point end) {
		if (start.getX() <= 0 || end.getX() <= 0 ||
			start.getX() >= 1 || end.getX() >= 1 ||
			start.getY() <= 0 || end.getY() <= 0 ||
			start.getY() >= 1 || end.getY() >= 1) {
			return true;
		}
		
		Index2D startIndex = indexForPoint(start);
		Index2D endIndex = indexForPoint(end);
		boolean hasObstacle = false;
		
		if (startIndex.getY() == endIndex.getY()) {
			// gleiche Zeile
			
			int yIndex = (startIndex.getY() * width); // Der Index des ersten Quads der Zeile
			for (int i = startIndex.getX(); i <= endIndex.getX(); i++) {
				if (quads.get(yIndex + i).isObstacle()) {
					hasObstacle = true;
					break;
				}
			}
		} else if (startIndex.getX() == endIndex.getX()) {
			// gleiche Spalte
			
			for (int i = startIndex.getY(); i <= endIndex.getY(); i++) {
				if (quadAtIndex(new Index2D(startIndex.getX(), i)).isObstacle()) {
					hasObstacle = true;
					break;
				}
			}
		} else {
			// Querlinie
			
			// Immer bei dem tieferen X-Wert anfangen
			Index2D oldStartIndex = startIndex;
			startIndex = startIndex.getX() < endIndex.getX() ? startIndex : endIndex;
			endIndex = oldStartIndex.getX() > endIndex.getX() ? oldStartIndex : endIndex;
			
			LinearFunction function = new LinearFunction(
					new Point(start.getX() * width, start.getY() * height), 
					new Point(end.getX() * width, end.getY() * height)
			);

			for (int x = startIndex.getX(); x <= endIndex.getX(); x++) {
				int entryY = (int)Math.floor(function.getY((float)x));
				int exitY = (int)Math.floor(function.getY((float)(x + 1)));
				
				int add;
				if (function.getM() > 0) {
					// entryY < exitY
					
					add = 1;
					
					// Falls wir noch in der gleichen Zeile sind, in der sich der Start-Punkt befindet, ist der "entry" kleiner als die Y-Komponente
					// des Punkt selbst. 
					if (entryY < startIndex.getY()) {
						entryY = startIndex.getY();
					}
					
					// ditto
					if (exitY > endIndex.getY()) {
						exitY = endIndex.getY();
					}
				} else {
					// exitY < entryY
					
					add = -1;
					
					// Falls wir noch in der gleichen Zeile sind, in der sich der Start-Punkt befindet, ist der "entry" kleiner als die Y-Komponente
					// des Punkt selbst. 
					if (entryY > startIndex.getY()) {
						entryY = startIndex.getY();
					}
					
					// ditto
					if (exitY < endIndex.getY()) {
						exitY = endIndex.getY();
					}
				}
				
				for (int j = entryY; (add == 1 ? j <= exitY : j >= exitY); j += add) {
					Quad quad = quadAtIndex(new Index2D(x, j));
					if (quad.isObstacle()) {
						hasObstacle = true;
						break;
					}
				}
				
				// exit outer loop
				if (hasObstacle)
					break;
			}
		}
		
		return hasObstacle;
	}
	
	public ArrayList<Node> getGraphNodesWithObstacles() {
		ArrayList<Node> nodes = new ArrayList<>();
		float relativePadding = (1.0f / width) * Constants.OBSTACLE_CORNER_NODE_PADDING;
		for (Quad obstacle : getObstacles()) {
			Index2D quadIndex = obstacle.getIndex();
			
			Index2D[] indices = new Index2D[] {
					new Index2D(quadIndex.getX() - 1, quadIndex.getY() + 1), // Top left
					new Index2D(quadIndex.getX() + 1, quadIndex.getY() + 1), // Top right
					new Index2D(quadIndex.getX() - 1, quadIndex.getY() - 1), // Bottom left
					new Index2D(quadIndex.getX() + 1, quadIndex.getY() - 1)  // Bottom right
			};
			
			// Bottom left origin point
			Point relativeCoords = quadIndex.toPoint(getDWidth());
			double relativeQuadSize = 1.0 / getDWidth();
			Point[] points = new Point[] {
					new Point(relativeCoords.getX() - relativePadding, relativeCoords.getY() + relativeQuadSize + relativePadding), // Top Left
					new Point(relativeCoords.getX() + relativeQuadSize + relativePadding, relativeCoords.getY() + relativeQuadSize + relativePadding), // Top right
					new Point(relativeCoords.getX() - relativePadding, relativeCoords.getY() - relativePadding), // Bottom left
					new Point(relativeCoords.getX() + relativeQuadSize + relativePadding, relativeCoords.getY() - relativePadding) // Bottom right
			};
			
			for (int i = 0; i < indices.length; i++) {
				Index2D index = indices[i];
				Point point = points[i];
				
				// Out of boundaries of the field 
				if (index.getX() < 0 || index.getY() < 0 ||
					index.getX() >= width || index.getY() >= height) {
					
					break;
				}
				
				// Get quads, which are vertical or horizontal neighbors to the current obstacle
				Quad neighborQuad1 = quadAtIndex(new Index2D(quadIndex.getX(), index.getY()));
				Quad neighborQuad2 = quadAtIndex(new Index2D(index.getX(), quadIndex.getY()));
				
				// Out of field bounds. Simulate obstacles
				if (neighborQuad1 == null) {
					neighborQuad1 = new Quad(null, true);
				}
				
				if (neighborQuad2 == null) {
					neighborQuad2 = new Quad(null, true);
				}
				
				Quad edgeQuad = quadAtIndex(index);
				if (edgeQuad.isObstacle() || neighborQuad1.isObstacle() || neighborQuad2.isObstacle()) {
					continue;
				}
				
				Node node = new Node(point);
				nodes.add(node);
			}
		}
		
		return nodes;
	}
	
	
	/* *** GETTERS *** */
	public Index2D indexForPoint(Point point) {
		return point.toIndex2d(width);
	}
	
	public Quad quadAtIndex(Index2D index) {
		if (index.getX() < 0 || index.getY() < 0 || index.getX() >= width || index.getY() >= height) {
			return null;
		}
		
		return quads.get((width * index.getY()) + index.getX());
	}
	
	public ArrayList<Quad> getObstacles() {
		ArrayList<Quad> obstacles = new ArrayList<>();
		quads.stream().forEach((quad) -> {
			if (quad.isObstacle()) {
				obstacles.add(quad);
			}
		});
		
		return obstacles;
	}

	public ArrayList<Quad> getQuads() {
		return quads;
	}

	public int getWidth() {
		return width;
	}
	
	public double getDWidth() {
		return (double)width;
	}

	public int getHeight() {
		return height;
	}
	
	public double getDHeight() {
		return (double)height;
	}
	
	@Override
	public String toString() {
		String retString = "QuadController, field:";
		int row = 0;
		for (int y = height - 1; y >= 0; y--) {
			for (int x = 0; x < width; x++) {
				Quad quad = quadAtIndex(new Index2D(x, y));
				if (quad.getIndex().getY() != row) {
					retString += "\n|";
					row = quad.getIndex().getY();
				}
				
				retString += quad.isObstacle() ? "x|" : " |";
			}
		}
		
		return retString;
	}
	
	public String toStringWithIndices() {
		String retString = "QuadController, field:";
		int row = 0;
		for (int y = height - 1; y >= 0; y--) {
			for (int x = 0; x < width; x++) {
				Quad quad = quadAtIndex(new Index2D(x, y));
				if (quad.getIndex().getY() != row) {
					retString += "\n"+y+" |";
					row = quad.getIndex().getY();
				}
				
				retString += quad.isObstacle() ? "x|" : " |";
			}
		}
		
		retString += "\n   ";
		for (int x = 0; x < width; x++) {
			retString += x + " ";
		}
		
		return retString + "\n";
	}
}
