package client.stones;

import java.io.Serializable;

import javafx.scene.paint.Color;

public class SColor implements Serializable {
	private static final long serialVersionUID = -8153154859754288849L;
	private double blue;
	private double red;
	private double green;
	private double alpha;
	
	public SColor(Color color) {
		this.red = color.getRed();
		this.green = color.getGreen();
		this.blue = color.getBlue();
		this.alpha = color.getOpacity();
	}
	
	public Color val() {
		return new Color(red, green, blue, alpha);
	}
	
	public void setVal(Color color) {
		this.red = color.getRed();
		this.green = color.getGreen();
		this.blue = color.getBlue();
		this.alpha = color.getOpacity();
	}

	public double getBlue() {
		return blue;
	}

	public void setBlue(double blue) {
		this.blue = blue;
	}

	public double getRed() {
		return red;
	}

	public void setRed(double red) {
		this.red = red;
	}

	public double getGreen() {
		return green;
	}

	public void setGreen(double green) {
		this.green = green;
	}

	public double getAlpha() {
		return alpha;
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}
}
