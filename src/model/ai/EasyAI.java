package model.ai;

import javafx.geometry.Point2D;

/**
 * This AI will pick moves at random across the entire board, with no pattern.
 *
 * @author Tom Giallanza
 */
public class EasyAI extends BattleshipAI {
		
	/**
	 * Create a new easy AI
	 */
	public EasyAI() {
		super();
	}

	@Override
	public Point2D nextMove() {
		Point2D move = new Point2D(r.nextInt(BOARD_SIZE),  r.nextInt(BOARD_SIZE));
		return move;
	}	
}
