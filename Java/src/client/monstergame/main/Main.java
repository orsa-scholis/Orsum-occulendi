package client.monstergame.main;

import client.monstergame.logic.*;
import client.monstergame.logic.algorithm.Path;
import client.monstergame.logic.graph.Point;

public class Main {
	public static void main(String[] args) {
		// Test-main methode
		
		int[][] field = new int[][] {
			// Bottom (y=0)
			{ 0, 0, 0, 0, 0, 0, 0, 0 },
			{ 0, 0, 0, 0, 0, 0, 0, 0 },
			{ 0, 0, 1, 0, 0, 0, 0, 0 },
			{ 0, 0, 0, 0, 0, 0, 0, 0 },
			{ 0, 0, 0, 0, 0, 0, 0, 0 },
			{ 0, 1, 1, 0, 0, 1, 0, 0 },
			{ 0, 0, 0, 0, 0, 0, 0, 0 },
			{ 0, 0, 0, 0, 0, 0, 0, 0 }
			// Top (y=7)
		};

		Game game = new Game(field);
		game.setMonsterPosition(new Point(0.9, 0.82));
		System.out.println(game.getQuadController().toStringWithIndices());
		Path path = game.getPathForMonster();
		System.out.println("Path: ");
		System.out.println(path);
		game.export("debug/export.txt");
	}
}
