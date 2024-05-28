package model.ai;

import java.util.ArrayList;

import javafx.geometry.Point2D;
import model.board.Board;
import model.board.Direction;

/**
 * This AI will fire randomly until it hits a ship. Then, it will fire at
 * surrounding squares until the ship is sunk.
 * 
 * @author Tom Giallanza, Andrew Dennison
 */
public class HardAI extends BattleshipAI {
	
	private boolean lastMoveHit = false;
	
	/**
	 * True if the AI has found an unsunk ship, false otherwise
	 */
	protected boolean isHunting = false;
	
	private boolean axisLocked;
	
	private Point2D shipOrigin;
	private Point2D lastMove;
	
	private Direction moveDir;
	
	/**
	 * The board this AI is targeting
	 */
	protected Board targetedBoard;
	
	private ArrayList<Point2D> currentTarget;
	private ArrayList<Direction> prohibitedDirections;

	/**
	 * Create a new HardAI
	 * @param b	The player's board, which this AI will target
	 */
	public HardAI(Board b) {
		super();
		targetedBoard = b;
		moveDir = Direction.UP;
		axisLocked = false;
		currentTarget = new ArrayList<Point2D>();
		prohibitedDirections = new ArrayList<Direction>();
	}

	@Override
	public void registerHit(Point2D hitPos, int hit) {
		lastMoveHit = hit == 1;
		lastMove = hitPos;
		
		// If we sank the last ship
		if (shipsLeft != targetedBoard.size()) {
			shipOrigin = null;
			isHunting = false;
			lastMoveHit = false;
			currentTarget.clear();
			prohibitedDirections.clear();
			axisLocked = false;
			shipsLeft = targetedBoard.size();
		}
		
		// If we hit a shield, add the move back to the pool of valid moves
		if (hit == -1) {
			movesLeft.add(hitPos);
		}
		
		// If the last move was a hit, add it to the list of points we've targeted on the current ship
		if (lastMoveHit) {
			currentTarget.add(hitPos);
		}		
	}

	@Override
	public Point2D nextMove() {
		Point2D move;
		
		if (!lastMoveHit && !isHunting) {
			move = guessRandomly();
		} else {
			move = huntShip();
		}
		
		movesLeft.remove(move);
		return move;
	}
	
	/**
	 * Randomly choose a point from the remaining points.  This function does not remove the move from the movesLeft list.
	 * @return	x, y coordinates of a random non-hit location on the board.
	 */
	protected Point2D guessRandomly() {
		return movesLeft.get(r.nextInt(movesLeft.size()));
	}

	/**
	 * Assuming the currently targeted ship is on the same axis (horizontal and vertical) as the current direction,
	 * attempt to locate an open square on the axis centered on the starting position
	 * @param start	The "starting position" we are centering our hits around.  This is the last known affirmative hit
	 * @param dir	The direction containing the axis to look
	 * @return		An open point along the axis, unless the axis is full, then a random point
	 */
	private Point2D findNextOpenSquareOnAxis(Point2D start, Direction dir) {
		Point2D nextMove = start;
		int count = 0;
		
		// Look for the ship on the current axis
		while (!movesLeft.contains(nextMove)) {
			if (dir == Direction.UP) {
				nextMove = nextMove.subtract(0, 1);
				// Out of bounds check, if so restart at the beginning and look the other way
				if (nextMove.getY() < 0) {
					dir = dir.oppositeDirection();
					nextMove = start;
				}
			}
			
			if (dir == Direction.DOWN) {
				nextMove = nextMove.add(0, 1);
				if (nextMove.getY() > 9) {
					dir = dir.oppositeDirection();
					nextMove = start;
				}
			}
			
			if (dir == Direction.RIGHT) {
				nextMove = nextMove.add(1, 0);
				if (nextMove.getX() > 9) {
					dir = dir.oppositeDirection();
					nextMove = start;
				}
			}
			
			if (dir == Direction.LEFT) {
				nextMove = nextMove.subtract(1, 0);
				if (nextMove.getX() < 0) {
					dir = dir.oppositeDirection();
					nextMove = start;
				}
			}
			
			count++;
			if (count > 8) {
				// All square on the axis are taken
				axisLocked = false;
				return guessRandomly();
			}
		}
		
		return nextMove;
	}

	private Point2D huntShip() {
		if (isHunting) {
			// We did hit a ship, but the last shot was a miss
			if (!lastMoveHit) {
				// If we have multiple hits on a "ship", and we went off it somehow,
				// restart from the ship's origin and look in another direction
				// This happens when two ships are stacked parallel and one tile apart
				// on the perpendicular axis
				if (currentTarget.size() > 1) {
					prohibitedDirections.add(moveDir);
					lastMove = shipOrigin;
					currentTarget.clear();
					currentTarget.add(shipOrigin);
					axisLocked = false;
				}
				
				if (axisLocked) {
					moveDir = moveDir.oppositeDirection();
				}
				
				// In both cases, keep rotating
				return getOpenRotations();
			}
						
			// If we have two hits, figure out which axis we are hitting along
			if (!axisLocked && currentTarget.size() > 1) {				
				lastMove = shipOrigin;
				if (currentTarget.get(0).getX() == currentTarget.get(1).getX()) {
					moveDir = Direction.UP;
				} else {
					moveDir = Direction.RIGHT;
				}
				
				axisLocked = true;
			}
			
			
			// We hit a ship, but it hasn't been sunk yet,
			// continue firing in the same direction
			// If we're going to run off the board, reset our direction to the other way
			return findNextOpenSquareOnAxis(lastMove, moveDir);

		} 

		// We just found a new ship, so we need to find the ship's axis
		if (lastMoveHit) {
			shipOrigin = lastMove;
			isHunting = true;
			return getOpenRotations();
		}
		
		// We didn't find a new ship, so we look for another one
		return guessRandomly();
	}
	
	/**
	 * Explore the possible squares that are withing one tile in a cardinal direction
	 * of the last move
	 * @return	x, y coordinates of next move
	 */
	private Point2D getOpenRotations() {
		int rotations = 0;
		
		// If we know the axis of the ship we are targeting, hit the axis
		if (axisLocked) {
			return findNextOpenSquareOnAxis(lastMove, moveDir);
		}
		
		// Rotate until we find an open square
		while (rotations < 4) {
			moveDir = moveDir.rotateRight();
			if (prohibitedDirections.contains(moveDir)) {
				rotations++;
				continue;
			}
			
			Point2D nextMove = findNextOpenSquareOnAxis(lastMove, moveDir);
			if(movesLeft.contains(nextMove)) {
				return nextMove;
			}
			rotations++;
		}
		
		// We don't have any unused rotations nearby, so we messed up somewhere
		// Go back to random hits
		// this code is *never* reached in tests, but we do have to have a return here
		isHunting = false;
		return guessRandomly();
	}
}