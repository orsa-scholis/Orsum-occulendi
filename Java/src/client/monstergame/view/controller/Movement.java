package client.monstergame.view.controller;

import client.monstergame.logic.QuadController;
import client.monstergame.logic.graph.Node;
import client.monstergame.logic.graph.Point;
import client.monstergame.main.Driver;

/**
*
* Diese Klasse k�mmert sich um die Bewegung des Monsters und des Spielers.
* Hierbei werden die Datens�tze des jeweiligen angepasst und im n�chsten Rendervorgang neu gerendert.
*
*/
public class Movement {

	public static int UP = 0;
	public static int DOWN = 2;
	public static int RIGHT = 1;
	public static int LEFT = 3;

	private Driver driver;

	public Movement(Driver driver) {
		this.driver = driver;
	}

	public boolean move(Node nd, int direction) {
		//System.out.println("-------------Move----------------");
		double posX = nd.getPoint().getX();
		double posY = nd.getPoint().getY();
		switch (direction) {
		case 0:
			if (isThereNoObstacle(posX, posY + 0.01)) {
				if ((posY += 0.01) > 0.99) {
					posY = 0.99;
				}
			}
			break;
		case 1:
			if (isThereNoObstacle(posX + 0.01, posY)) {
				if ((posX += 0.01) > 0.99) {
					posX = 0.99;
				}
			}
			break;
		case 2:
			if (isThereNoObstacle(posX, posY - 0.01)) {
				if ((posY -= 0.01) < 0) {
					posY = 0;
				}
			}
			break;
		case 3:
			if (isThereNoObstacle(posX - 0.01, posY)) {
				if ((posX -= 0.01) < 0) {
					posX = 0;
				}
			}
			break;
		default:
			return false;
		}
		nd.setPoint(new Point(posX, posY));

		return true;
	}

	/**
	 *
	 * �berpr�ft ob an dem angegebenen Punkt ein Hinderniss ist.
	 *
	 * @param x Die x-Koordinate, des �berpr�ften Punktes
	 * @param y Die y-Koordinate, des �berpr�ften Punktes
	 * @return Gibt zur�ck ob, an dem angegebene Punkt ein Hinderniss ist.
	 */
	public boolean isThereNoObstacle(double x, double y) {
		QuadController c = driver.getGame().getQuadController();
		try {
			if (c.quadAtIndex(c.indexForPoint(new Point(x, y))).isObstacle()) {
				return false;
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}

		return true;
	}

	/**
	 * Bewegt die Node an den neuen Punkt.
	 *
	 * @param nd Die Node die bewegt werden soll.
	 * @param point Der Punkt an den die Node bewegt werden soll.
	 */
	public void moveTo(Node nd, Point point) {
		nd.setPoint(point);
	}

}
