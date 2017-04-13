package server.models;

import java.util.ArrayList;

public class BoardModel {

	@SuppressWarnings("unchecked")
	private ArrayList<StoneModel>[] field = new ArrayList[] { new ArrayList<StoneModel>(5),
			new ArrayList<StoneModel>(5), new ArrayList<StoneModel>(5), new ArrayList<StoneModel>(5),
			new ArrayList<StoneModel>(5), new ArrayList<StoneModel>(5), new ArrayList<StoneModel>() };
	private int lastSetRow;

	public BoardModel() {
		lastSetRow = -1;
		for (ArrayList<StoneModel> listy : field) {
			for (int i = 0; i < 7; i++) {
				listy.add(new StoneModel());
			}
		}
	}

	public void printAllStones() {
		for (int i = 6; i > 0; i--) {
			for (ArrayList<StoneModel> listy : field) {
				System.out.print(listy.get(i).getState() + "|");
			}
			System.out.println("");
		}
		for (int i = 0; i < 7; i++) {
			System.out.print("--");
		}
		System.out.println("");
	}

	public boolean setStone(boolean player1, int row) {
		int emptyField = -1;
		for (int i = 6; i > 0; i--) {
			//System.out.println("" + field[row].get(i).getState());
			if (field[row].get(i).getState().equals("0")) {
				emptyField = i;
			}
		}
		if (emptyField != -1) {
			StoneModel sm = new StoneModel();
			if (player1) {
				sm.setPlayer1();
			} else {
				sm.setPlayer2();
			}
			field[row].set(emptyField, sm);
			lastSetRow = row;
			return true;
		} else {
			return false;
		}
	}

	public boolean hasWon(boolean player1) {
		String toCheck = "";
		if (player1) {
			toCheck = "1";
		} else {
			toCheck = "2";
		}
		int rowCounter = 0;
		for (ArrayList<StoneModel> listy : field) {
			for (int i = 0; i < 6; i++) {
				if (listy.get(i).getState().equals(toCheck)) {
					rowCounter++;
					if (rowCounter == 4) {
						return true;
					}
				} else {
					rowCounter = 0;
				}
			}
		}
		rowCounter = 0;
		for (int i = 0; i < 6; i++) {
			for (ArrayList<StoneModel> listy : field) {
				if (listy.get(i).getState().equals(toCheck)) {
					rowCounter++;
					if (rowCounter == 4) {
						return true;
					}
				} else {
					rowCounter = 0;
				}
			}
		}
		rowCounter = 0;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 3; j++) {
				if (toCheck.equals(field[i].get(j).getState()) && toCheck.equals(field[i + 1].get(j + 1).getState())
						&& toCheck.equals(field[i + 2].get(j + 2).getState())
						&& toCheck.equals(field[i + 3].get(j + 3).getState())) {
					return true;
				}
			}
		}
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 3; j++) {
				if (toCheck.equals(field[i].get(j+3).getState()) && toCheck.equals(field[i + 1].get(j + 2).getState())
						&& toCheck.equals(field[i + 2].get(j + 1).getState())
						&& toCheck.equals(field[i + 3].get(j).getState())) {
					return true;
				}
			}
		}
		return false;
	}

	public int getLastSetRow() {
		return lastSetRow;
	}

	public boolean isTie() {
		for (int i = 0; i < 6; i++) {
			for (ArrayList<StoneModel> listy : field) {
				if ("0".equals(listy.get(i).getState())) {
					return false;
				}
			}
		}
		return true;
	}

}
