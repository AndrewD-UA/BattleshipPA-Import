package tests;

import org.junit.Test;

public class AllTests {
	
	private int numTests = 100;
	
	@Test
	public void testShip() {
		ShipTest ship = new ShipTest();
		ship.testAll();
	}
	
	@Test
	public void testBoard() {
		BoardTest board = new BoardTest();
		board.testAll();
	}
	
	@Test
	public void testAI() {
		AITest ai = new AITest();
		ai.testAll(numTests);
	}
}
