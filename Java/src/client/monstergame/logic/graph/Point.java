package client.monstergame.logic.graph;

import client.monstergame.logic.Index2D;

/**
 * 
 * @author lukas bischof
 * @class Point
 * @description Die Klasse Point repräsentiert einen Punkt auf dem Feld. Damit er auf jede grösse des Fensters skalierbar ist,
 * wurde x auf 0-1 und y auf 0-1 "beschränkt".
 */
public class Point {
	private double x;
	private double y;


	public Point(double x, double y) {
		super();
		this.x = x;
		this.y = y;
	}


	public double getX() {
		return x;
	}


	public float getFX(){
		return (float) x;
	}

	public void setX(double x) {
		this.x = x;
	}


	public double getY() {
		return y;
	}

	public float getFY(){
		return (float) y;
	}

	public void setY(double y) {
		this.y = y;
	}

	/**
	 * Konvertierung zu einem zweidimensionalem Index
	 * @param fieldSize Die grösse des Feldes in anzahl Blocks pro Zeile/Spalte
	 * @return Der index
	 */
	public Index2D toIndex2d(double fieldSize) {
		return new Index2D((int)Math.floor(x * fieldSize), (int)Math.floor(y * fieldSize));
	}


	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

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
		Point other = (Point) obj;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x)) {
			return false;
		}
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y)) {
			return false;
		}
		return true;
	}
	
	@Override
	public Object clone() {
		return new Point(this.x, this.y);
	}
}
