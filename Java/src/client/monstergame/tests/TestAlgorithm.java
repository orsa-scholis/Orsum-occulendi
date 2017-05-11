package client.monstergame.tests;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import client.monstergame.logic.Game;
import client.monstergame.logic.algorithm.Path;
import client.monstergame.logic.graph.Connection;
import client.monstergame.logic.graph.Graph;
import client.monstergame.logic.graph.Node;
import client.monstergame.logic.graph.Point;

public class TestAlgorithm {

	private int[][] field = new int[][] {
		// Bottom
		{ 0, 0, 0, 0, 0, 0, 0, 0 },
		{ 0, 0, 0, 0, 0, 0, 0, 0 },
		{ 0, 0, 1, 0, 0, 0, 0, 0 },
		{ 0, 0, 0, 0, 0, 0, 0, 0 },
		{ 0, 0, 0, 0, 0, 0, 0, 0 },
		{ 0, 1, 1, 0, 0, 1, 0, 0 },
		{ 0, 0, 0, 0, 0, 0, 0, 0 },
		{ 0, 0, 0, 0, 0, 0, 0, 0 }
		// Top
	};

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGraphClone() {
		Game game = new Game(field);
		game.setMonsterPosition(new Point(0.9, 0.82));
		game.getPathForMonster();

		try {
			Graph copy = (Graph)game.getPathFinder().getGraph().clone();
			Graph original = game.getPathFinder().getGraph();

			for (int i = 0; i < original.getNodes().size(); ++i) {
				Node originalNode = original.getNodes().get(i);
				Node clonedNode = copy.getNodes().get(i);

				if (!originalNode.getPoint().equals(clonedNode.getPoint())) {
					fail("Points missmatch");
				}

				for (Connection connection : originalNode.getConnections()) {
					Node originalCounterpart = connection.getCounterpart(originalNode);

					boolean hasConnection = false;
					for (Connection clonedConnection : clonedNode.getConnections()) {
						if (clonedConnection.getCounterpart(clonedNode).getPoint().equals(originalCounterpart.getPoint())) {
							hasConnection = true;
							break;
						}
					}

					if (!hasConnection) {
						fail("The node " + originalNode + " has no connection to the node " + originalCounterpart + " in the clone");
					}
				}
			}
		} catch (Exception t) {
			t.printStackTrace();
			fail("An exception was thrown");
		}
	}

	@Test
	public void testPerformance() {
		long start = System.currentTimeMillis();

		Game game = new Game(field);
		game.setMonsterPosition(new Point(0.9, 0.82));
		Path path = game.getPathForMonster();
		if (null == path) {
			fail("Can't measure performance. Missing returned path");
		}

		long end = System.currentTimeMillis();
		long elapsedTime = end - start;
	    System.out.println("Performance of algorithm: " + elapsedTime + "ms elapsed");
	}

	@Test
	public void testPerformancePerCalculation() {
		Game game = new Game(field);
		game.setMonsterPosition(new Point(0.9, 0.82));

		long avg = 0;
		int itr = 100;
		for (int i = 0; i < itr; i++) {
			long start = System.currentTimeMillis();

			Path path = game.getPathForMonster();
			if (null == path) {
				fail("Can't measure performance. Missing returned path");
			}

			long end = System.currentTimeMillis();
			long elapsedTime = end - start;
		    avg += elapsedTime;
		}

		System.out.println("Average time elapsed per calculation: " + ((double)avg / (double)itr) + " ms.");
	}

}
