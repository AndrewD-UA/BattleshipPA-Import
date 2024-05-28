package model.ai;

import javafx.geometry.Point2D;
import model.board.Board;

/**
 * An AI which performs exactly like the HardAI, but occasionally gets to cheat
 * 
 * @author Andrew Dennison
 */
public class ExtremeAI extends HardAI{
	
	/**
	 * Create a new Extreme AI
	 * @param b	The player's board, which this AI will target
	 */
	public ExtremeAI(Board b) {
		super(b);
	}
	
	@Override
	public Point2D nextMove() {
		if (targetedBoard.moveCount() % 6 == 0 && !isHunting) {
			Point2D nextMove = targetedBoard.cheatMove();
			movesLeft.remove(nextMove);
			return nextMove;
		}
		
		return super.nextMove();
	}
	
}
