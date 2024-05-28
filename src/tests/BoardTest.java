package tests;

import static org.junit.Assert.assertEquals;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.junit.Test;

import controller_view.AllProperties;
import javafx.geometry.Point2D;
import model.battleship.Ship;
import model.battleship.ShipCollection;
import model.board.Board;
import model.board.Direction;
import model.board.GameplayResult;

public class BoardTest implements PropertyChangeListener{
	
	private Board playerBoard;
	private Board aiBoard;
	
	Ship[] ships = new Ship[] {
    		new Ship(new Point2D(0, 0), Direction.DOWN, 2, false),
    		new Ship(new Point2D(1, 0), Direction.DOWN, 3, false),
    		new Ship(new Point2D(2, 0), Direction.DOWN, 3, true),
    		new Ship(new Point2D(3, 0), Direction.DOWN, 4, false),
    		new Ship(new Point2D(4, 0), Direction.DOWN, 5, false)
    };
	
	public void testAll() {
		testInitBoards();
		testBoardBuilding();
	}
	
	@Test
	public void testInitBoards() {
		playerBoard = new Board(new ShipCollection(ships));
		aiBoard = new Board();
		
		assertEquals(5, playerBoard.size());
		
		System.out.println(aiBoard.toString());
		
		playerBoard.addListener(this);
		aiBoard.addListener(this);
		
		aiBoard.startStats();
		
		makeMovesOnHuman();
		assertEquals(0, playerBoard.size());
		assertEquals(0, playerBoard.getCollection().size());
		makeMovesOnAI();
	}
	
	@Test
	public void testBoardBuilding() {
		Board b = new Board(new ShipCollection());
		for (Ship s : ships) {
			assertEquals(true, b.addShipToBoard(s));
		}
		
		assertEquals(false, b.addShipToBoard(new Ship(new Point2D(0, 0), Direction.UP, 3, false)));
	}
	
	public void makeMovesOnHuman() {
		playerBoard.enterMove(new Point2D(0, 0), true);
		playerBoard.enterMove(new Point2D(0, 1), true);
		playerBoard.enterMove(new Point2D(1, 0), true);
		playerBoard.enterMove(new Point2D(1, 1), true);
		playerBoard.enterMove(new Point2D(1, 2), true);
		playerBoard.enterMove(new Point2D(2, 0), true);
		playerBoard.enterMove(new Point2D(2, 1), true);
		playerBoard.enterMove(new Point2D(2, 2), true);
		playerBoard.enterMove(new Point2D(3, 0), true);
		playerBoard.enterMove(new Point2D(3, 1), true);
		playerBoard.enterMove(new Point2D(3, 2), true);
		playerBoard.enterMove(new Point2D(3, 3), true);
		playerBoard.enterMove(new Point2D(4, 0), true);
		playerBoard.enterMove(new Point2D(4, 1), true);
		playerBoard.enterMove(new Point2D(4, 2), true);
		playerBoard.enterMove(new Point2D(4, 3), true);
		playerBoard.enterMove(new Point2D(4, 4), true);
	}

	public void makeMovesOnAI() {
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				aiBoard.enterMove(new Point2D(i, j), true);
			}
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(AllProperties.GAME_OVER.property())) {
			assertEquals((boolean) evt.getNewValue(), true);
		}
		
		if (evt.getPropertyName().equals(AllProperties.GAME_STATS_READY.property())) {
			GameplayResult gr = (GameplayResult) evt.getNewValue();
			System.out.println(gr.getGameDuration());
			System.out.println(gr.getHits());
			System.out.println(gr.getMoves());
			System.out.println(gr.getShipsSunk());

		}
	}
}
