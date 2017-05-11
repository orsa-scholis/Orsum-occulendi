package client.monstergame.logic;

/**
 * Repräsentiert ein Quadrat auf dem Spielfeld. Dies kann entweder ein freies Feld oder ein Hindernis sein.
 * @author lukasbischof
 *
 */
public class Quad {
	private Index2D index;
	private boolean isObstacle;
	
	public Quad(Index2D index, boolean isObstacle) {
		this.index = index;
		this.isObstacle = isObstacle;
	}
	
	public Quad(Index2D index) {
		this.index = index;
		this.isObstacle = false;
	}

	public Index2D getIndex() {
		return index;
	}

	public void setIndex(Index2D index) {
		this.index = index;
	}

	public boolean isObstacle() {
		return isObstacle;
	}

	public void setObstacle(boolean isObstacle) {
		this.isObstacle = isObstacle;
	}
}
 