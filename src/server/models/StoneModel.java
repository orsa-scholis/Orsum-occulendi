package models;

public class StoneModel{
	private int state; //0 = not set, 1 = player1, 2 = player2
	
	public StoneModel() {
		state = 0;
	}
	
	public boolean setPlayer1(){
		if(state == 0){
			state = 1;
			return true;
		}
		else{
			return false;
		}
	}
	
	public boolean setPlayer2(){
		if(state == 0){
			state = 2;
			return true;
		}
		else{
			return false;
		}
	}
	
	public String getState(){
		return ""+state;
	}
}
