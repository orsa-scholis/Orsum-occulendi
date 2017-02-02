package stones;

import java.io.Serializable;

import javafx.scene.paint.Color;

public class StoneColor implements Serializable {
	private static final long serialVersionUID = 7149895565636724254L;
	private SColor color;
	private SColor lightColor;
	private ColorTypes type;
	
	public enum ColorTypes {
		RED,
		BLUE,
		GREEN,
		YELLOW,
		ORANGE;
		
		public String toString() {
			switch (this) {
			case RED:
				return "Rot";
			case BLUE:
				return "Blau";
			case GREEN:
				return "Grün";
			case YELLOW:
				return "Gelb";
			case ORANGE:
				return "Orange";
			default:
				return "unknown";
			}
		}
	};
	
	public StoneColor(ColorTypes type) {
		super();
		
		switch (type) {
		case RED:
			color = new SColor(Color.RED);
			lightColor = new SColor(new Color(0.8, 0.4, 0.4, 1.0));
			break;
			
		case BLUE:
			color = new SColor(Color.BLUE);
			lightColor = new SColor(Color.LIGHTBLUE);
			break;
			
		case GREEN:
			color = new SColor(Color.GREEN);
			lightColor = new SColor(Color.LIGHTGREEN);
			break;
			
		case YELLOW:
			color = new SColor(Color.YELLOW);
			lightColor = new SColor(Color.LIGHTYELLOW);
			break;
			
		case ORANGE:
			color = new SColor(Color.ORANGE);
			lightColor = new SColor(new Color((double)0xDF / (double)0xFF, (double)0xC5 / (double)0xFF, (double)0x50 / (double)0xFF, 1.0));
			break;
		}
		
		this.type = type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((color == null) ? 0 : color.hashCode());
		result = prime * result + ((lightColor == null) ? 0 : lightColor.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StoneColor other = (StoneColor) obj;
		if (color == null) {
			if (other.color != null)
				return false;
		} else if (!color.equals(other.color))
			return false;
		if (lightColor == null) {
			if (other.lightColor != null)
				return false;
		} else if (!lightColor.equals(other.lightColor))
			return false;
		return true;
	}

	public SColor getColor() {
		return color;
	}

	public void setColor(SColor color) {
		this.color = color;
	}

	public SColor getLightColor() {
		return lightColor;
	}

	public void setLightColor(SColor lightColor) {
		this.lightColor = lightColor;
	}

	public ColorTypes getType() {
		return type;
	}

	public void setType(ColorTypes type) {
		this.type = type;
	}
}
