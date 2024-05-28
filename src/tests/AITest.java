package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;

import javafx.geometry.Point2D;
import model.ai.BattleshipAI;
import model.ai.EasyAI;
import model.ai.ExtremeAI;
import model.ai.HardAI;
import model.ai.ModerateAI;
import model.board.Board;


public class AITest {
	
	int numGames = 100;

	@Test
	public void testEasyAI() {
		EasyAI ai = new EasyAI();
		
		assertEquals(true, ai.nextMove().getX() < 10);
		assertEquals(true, ai.nextMove().getX() >= 0);
		assertEquals(true, ai.nextMove().getY() < 10);
		assertEquals(true, ai.nextMove().getY() >= 0);
	}
	
	@Test
	public void testModerateAI() {
		ModerateAI ai = new ModerateAI();
		for (int i = 0; i < 100; i++) {
			Point2D move = ai.nextMove();
			ai.registerHit(move, 0);
			//assertEquals(1, Collections.frequency(ai.getSelected(), move));
		}
	}
	
	@Test
	public void testHardBetterThanEasy() {
		int hiWins = simulateGames("Easy", "Hard", numGames);
		assertEquals(true, hiWins > (numGames / 2));
	}
	
	@Test
	public void testHardBetterThanModerate() {		
		int hiWins = simulateGames("Moderate", "Hard", numGames);
		assertEquals(true, hiWins > (numGames / 2));
	}
	
	@Test
	public void testExtremeBetterThanAll() {
		int hiWins = simulateGames("Easy", "Extreme", numGames);
		System.out.println(hiWins);
		assertEquals(true, hiWins > (numGames / 2));
		
		hiWins = simulateGames("Moderate", "Extreme", numGames);		
		assertEquals(true, hiWins > (numGames / 2));
		
		hiWins = simulateGames("Hard", "Extreme", numGames);		
		assertEquals(true, hiWins > (numGames / 2));
	}
	
	@Test
	public void testModerateBetterThanEasy() {
		int hiWins = simulateGames("Easy", "Moderate", numGames);
		
		assertEquals(true, hiWins / numGames > 0.5); 
	}
	
	
	private int simulateGames(String firstAI, String secondAI, int numGames) {
		Board loBoard = new Board(); 	
		Board hiBoard = new Board();
		
		BattleshipAI aiOne = generateAI(hiBoard, firstAI);
		BattleshipAI aiTwo = generateAI(loBoard, secondAI);
		
		int hiWins = 0;
		int totalPlays = 0;
		
		for (; totalPlays < numGames; totalPlays++) {
			loBoard.startStats();
			hiBoard.startStats();
			while (!loBoard.isEmpty() && !hiBoard.isEmpty()) {
				makeOneMove(hiBoard, aiOne);
				makeOneMove(loBoard, aiTwo);
			}
			
			if (loBoard.isEmpty()) {
				hiWins++;
			}
			
			loBoard = new Board();
			hiBoard = new Board();
			aiOne = generateAI(hiBoard, firstAI);
			aiTwo = generateAI(loBoard, secondAI);
		}
		
		return hiWins;
	}
	
	private BattleshipAI generateAI(Board board, String ai) {
		if (ai.equals("Easy")) {
			return new EasyAI();
		}
		
		if (ai.equals("Moderate")) {
			return new ModerateAI();
		}
		
		if (ai.equals("Hard")) {
			return new HardAI(board);
		}
		
		return new ExtremeAI(board);
	}

	private void makeOneMove(Board b, BattleshipAI ai) {
		Point2D next = ai.nextMove();
		int result = b.enterMove(next, true);
		ai.registerHit(next, result);
	}

	public void testAll(int games) {
		numGames = games;
		
		testEasyAI();
		testModerateAI();
		
		testModerateBetterThanEasy();
		testHardBetterThanEasy();
		testHardBetterThanModerate();
		testExtremeBetterThanAll();
	}
}
