package model.ai;

import java.util.ArrayList;
import java.util.Random;

import javafx.geometry.Point2D;

/**
 * Template for all AI Classes
 * 
 * @author Andrew Dennison
 */
public abstract class BattleshipAI {
	
	/**
	 * The list of all remaining moves
	 */
	protected ArrayList<Point2D> movesLeft;

	/**
	 * The total number of ships left
	 */
	protected int shipsLeft;
	
	/**
	 * The fixed size of the board
	 */
	public static final int BOARD_SIZE = 10;
	
	/**
	 * The random instance available to all children
	 */
	protected Random r;
	
	/**
	 * Create a new BattleshipAI with a blank slate of empty moves
	 */
	public BattleshipAI() {
		r = new Random();
		movesLeft = new ArrayList<Point2D>();
		for (int i = 0; i < BOARD_SIZE; i++) {
			for (int j = 0; j < BOARD_SIZE; j++) {
				movesLeft.add(new Point2D(i, j));
			}
		}
		
		shipsLeft = 5;
	}
	
	/**
	 * Determine the X and Y coordinates of the next move
	 * @return	Point2D containing the next move the AI would like to make
	 */
	public abstract Point2D nextMove();

	/**
	 * Action to conduct after a move
	 * @param hitPos	Point2D of X,Y location of the move
	 * @param hit		Whether the move hit a ship or not
	 */
	public void registerHit(Point2D hitPos, int hit) {
	}
	
	/**
	 * Get the selected points queued by an AI
	 * @return	ArrayList of Point2D X,Y coordinates of where the AI is targeting
	 */
	public ArrayList<Point2D> getSelected() {
		return null;
	}
}