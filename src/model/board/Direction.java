package model.board;

/**
 * The direction a ship extends from its starting position
 * 
 * @author Andrew Dennison
 */
public enum Direction {
	
	/**
	 * A ship extends from a Y value to a lesser Y value
	 */
	RIGHT(0),
	
	/**
	 * A ship extends from an X value to a higher X value
	 */
	DOWN(1),
	
	/**
	 * A ship extends from a Y value to a higher Y value
	 */
	LEFT(2),
	
	/**
	 * A ship extends from an X value to a lesser X value
	 */
	UP(3);
	
	private int direction;
	private Direction(int direction) {
		this.direction = direction;
	}
	
	/**
	 * Add 90 degrees to the current direction
	 * @return	The next direction as if the current direction was rotated 90 degrees
	 */
	public Direction rotateRight() {
		int newDirection = (direction + 1) % 4;
		return values()[newDirection];
	}
	
	/**
	 * Determine whether the current direction is a vertical or horizontal direction
	 * @return	True if UP or DOWN, false if LEFT or RIGHT
	 */
	public boolean isVertical() {
		if(direction == 0 || direction == 2 ) {
			return false;
		}
		return true;
		
	}
	
	/**
	 * Get the integer representation of this direction
	 * @return	The degrees of rotation this direction represents, with 0 being right.  The degrees are divided by 90.
	 * E.g., direction RIGHT = 0 UP = 3, etc.
	 */
	public int direction() {
		return direction;
	}

	/**
	 * Get the opposite direction of this direction along the same axis (horizontal versus vertical)
	 * @return	Direction representation of a 180 degree turn
	 */
	public Direction oppositeDirection() {
		if (!isVertical()) {
			if (direction == 0) {
				return LEFT;
			}
			
			return RIGHT;
		}
		
		if (direction == 1) {
			return UP;
		}
		
		return DOWN;
	}
}
