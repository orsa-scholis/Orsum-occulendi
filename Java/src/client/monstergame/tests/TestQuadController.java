package client.monstergame.tests;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import client.monstergame.logic.Game;
import client.monstergame.logic.algorithm.PathFinder;
import client.monstergame.logic.graph.*;

public class TestQuadController {
	
	private Game game;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		int[][] field = new int[][] {
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
		
		game = new Game(field);
		System.out.println(game.getQuadController().toStringWithIndices());
	}

	@After
	public void tearDown() throws Exception {
	
	}
	
	@Test
	public void testNodesGeneration() {
		int[][] field = new int[][] {
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
		
		Game game = new Game(field);
		
		PathFinder pFinder = new PathFinder(game.getQuadController().getGraphNodesWithObstacles(), game.getQuadController());
		String desc = pFinder.getGraph().toString(game.getQuadController().getDWidth());
		
		String expected = "nodes: {\n1.7999999970197678	0.7999999970197678\n3.2000000029802322	0.7999999970197678\n"
						+"1.7999999970197678	3.2000000029802322\n4.200000002980232	3.2000000029802322\n"
						+"4.200000002980232	1.7999999970197678\n0.7999999970197678	6.200000002980232\n0.7999999970197678	4.799999997019768\n"
						+"3.2000000029802322	6.200000002980232\n3.2000000029802322	4.799999997019768\n"
						+"4.799999997019768	6.200000002980232\n6.200000002980232	6.200000002980232\n"
						+"4.799999997019768	4.799999997019768\n6.200000002980232	4.799999997019768\n}";
		
		assertNotEquals(expected, desc);
	}

	@Test
	public void testLineTestForObstacles() {
		Point[] point1s = new Point[] {
				new Point(1.0/8.0, 4.0/8.0),
				new Point(1.0/8.0, 3.0/8.0),
				new Point(1.0/8.0, 2.5/8.0),
				new Point(1.0/8.0, 3.5/8.0),
				new Point(4.0/8.0, 4.0/8.0),
				new Point(4.0/8.0, 4.0/8.0),
				new Point(0.0/8.0, 0.0/8.0),
				new Point(0.0/8.0, 0.0/8.0),
				new Point(1.0/8.0, 7.0/8.0),
				new Point(0.09999999962747097, 0.5999999996274689)
		};
		
		Point[] point2s = new Point[] {
				new Point(5.0/8.0, 3.0/8.0),
				new Point(4.0/8.0, 1.0/8.0),
				new Point(3.0/8.0, 2.5/8.0),
				new Point(3.0/8.0, 3.5/8.0),
				new Point(4.0/8.0, 6.0/8.0),
				new Point(1.0/8.0, 7.0/8.0),
				new Point(2.0/8.0, 7.0/8.0),
				new Point(1.0/8.0, 7.0/8.0),
				new Point(0.0/8.0, 0.0/8.0),
				new Point(0.4000000000000002, 0.7900000000000005)
		};
		
		boolean[] solutions = new boolean[] {
				false, true, true, false, false, true, true, false, false, true
		};
		
		for (int i = 0; i < point1s.length; i++) {
			Point point1 = point1s[i];
			Point point2 = point2s[i];
			boolean solution = solutions[i];
			boolean hasObsti = game.getQuadController().testLineForObstacles(point1, point2);
			
			if (hasObsti != solution) {
				fail("Function returned wrong value for line " + i + ".");
			}
		}
	}

}
