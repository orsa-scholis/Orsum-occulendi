package client.stones;

import java.io.Serializable;

import client.stones.StoneColor.ColorTypes;

public class StoneColors implements Serializable {
	private static final long serialVersionUID = -6953702430262874256L;
	private StoneColor playerColor;
	private StoneColor opponentColor;
	
	public static StoneColors defaultColors() {
		StoneColors colors = new StoneColors();
		colors.setOpponentColor(new StoneColor(ColorTypes.RED));
		colors.setPlayerColor(new StoneColor(ColorTypes.BLUE));
		
		return colors;
	}

	public StoneColor playerColor() {
		return playerColor;
	}

	public void setPlayerColor(StoneColor playerColor) {
		this.playerColor = playerColor;
	}

	public StoneColor opponentColor() {
		return opponentColor;
	}

	public void setOpponentColor(StoneColor opponentColor) {
		this.opponentColor = opponentColor;
	}
}
