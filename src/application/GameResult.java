package application;

public enum GameResult {
	PLAYER_WON, OPPONENT_WON, TIE, ERROR;
	
	public static GameResult fromInt(int re) {
		switch (re) {
		case 0:
			return GameResult.PLAYER_WON;

		case 1:
			return OPPONENT_WON;
			
		case 2:
			return GameResult.TIE;
			
		default:
			return GameResult.ERROR;
		}
	}
}
