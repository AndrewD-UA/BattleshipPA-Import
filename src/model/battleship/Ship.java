package model.battleship;

import java.util.Arrays;

import javafx.geometry.Point2D;
import model.board.Direction;

/**
 * A simple Ship Object.
 * 
 * This Object tracks its starting position, which direction it faces from its start,
 * and the length of the Ship.
 * 
 * Additionally, it tracks which portions of the ship are damaged.
 * 
 * Finally, it contains convenience methods to conver the Ship to an array of Point2Ds instead.
 * 
 * @author Andrew Dennison, Tom
 */
public class Ship {
	
	/**
	 * True if the index of the ship has been damaged
	 */
	private boolean[] damages;
	
	/**
	 * How far the boat reaches from the X,Y start position
	 */
	private int size;
	
	/**
	 * Which direction the ship extends from the X,Y coordinate of the start of the ship
	 */
	private Direction orientation;
	
	/**
	 * The X coordinate of the first piece of this Ship
	 */
	private int startX;
	
	/**
	 * The Y coordinate of the first piece of this Ship
	 */
	private int startY;
	
	/**
	 * True if this Ship is a submarine, false otherwise
	 */
	private boolean isSub;
	
	/**
	 * True if the ship has been fully damaged, false otherwise
	 */
	private boolean isSunk;
	
	/**
	 * True if ship will be rendered, false otherwise
	 */
	private boolean isRevealed;
	
	/**
	 * Second optional constructor, based on whichever is easier in the ShipPickScreen
	 * @param start		Coordinates of the "start" of the ship
	 * @param facing	Direction from the start to the end of the ship
	 * @param size		Length of the ship
	 * @param isSub		True if this Ship should be a submarine, otherwise false
	 */
	public Ship(Point2D start, Direction facing, int size, boolean isSub) {
		startX = (int) start.getX();
		startY = (int) start.getY();
		orientation = facing;
		damages = new boolean[size];
		
		this.size = size;
		this.isSub = isSub;
		isSunk = false;
		isRevealed = true;
	}
	
	/**
	 * Get the direction this Ship is facing
	 * 
	 * @return	The direction the ship extends from its starting position
	 */
	public Direction getFacingDirection() {
		return orientation;
	}
	
	/**
	 * Whether this ship is a submarine
	 * @return	True if the Ship is a submarine, false otherwise.
	 */
	public boolean isSub() {
		return isSub;
	}
	
	/**
	 * Set the direction this ship is facing
	 * 
	 * @param orientation	Which direction the ship extends from its starting position
	 */
	public void setFacingDirection(Direction orientation) {
		this.orientation = orientation;
	}
	
	/**
	 * Determine if the ship has had all coordinates damaged.
	 * 
	 * @return	True if the ship is sunk, false otherwise
	 */
	public boolean isSunk() {
		if (isSunk) {
			return true;
		}
		
		for (boolean isDamaged : damages) {
			if (!isDamaged) {
				return false;
			}
		}
		
		isRevealed = true;
		isSunk = true;
		return true;
	}
	
	/**
	 * Damage one square of this Ship
	 * 
	 * @param position			The distance from the hit point to the starting position of the Ship
	 * @param isPassive			True if the ship should be damaged by the evaluation of this hit, false otherwise
	 * @return					True if this hit would damage the ship, false otherwise
	 */
	public boolean hitShip(int position, boolean isPassive) {	
		if (position >= damages.length || position < 0) {
			return false;
		}
		
		if (damages[position] == true) {
			return false;
		}
		
		if (!isPassive) {
			damages[position] = true;
		}
		
		return true;
	}
	
	/**
	 * Get the [0, 0] of this Ship relative to itself
	 * @return	The X and Y coordinate on the board of the ship's 0th index
	 */
	public Point2D getStartPos() {
		return new Point2D(startX, startY);
	}
	
	/**
	 * Get the number of tiles this ship extends from its starting position.
	 * @return	The length of this Ship
	 */
	public int getLength() {
		return size;
	}
	
	/**
	 * Convert this ship to an Array of Point2Ds, instead of a starting Point2D and Orientation.
	 * @return	Every Point2D on the board which this ship occupies.
	 */
	public Point2D[] toArray() {
		Point2D[] pos = new Point2D[size];
		for (int i = 0; i < size; i++) {
			if (orientation == Direction.UP) {
				pos[i] = new Point2D(startX, startY - i);
			} else if (orientation == Direction.RIGHT) {
				pos[i] = new Point2D(startX + i, startY);
			} else if (orientation == Direction.DOWN) {
				pos[i] = new Point2D(startX, startY + i);
			} else {
				pos[i] = new Point2D(startX - i, startY);
			}
		}
		
		return pos;
	}
	
	/**
	 * Ascertain where to load the image for this ship
	 * @return	The path to the ship's image from the current working directory
	 */
	public String getImagePath() {
		if (isSunk) {
			switch(size) {
			case 2: return "/img/patrol_sunk.png";
			case 4:	return "/img/battleship_sunk.png";
			case 5:	return "/img/carrier_sunk.png";
			default: return isSub ?  "/img/submarine_sunk.png" : "/img/destroyer_sunk.png";
			}
		}
		
		switch(size) {
		case 2: return "/img/patrolTDL.png";
		case 4:	return "/img/battleTDL.png";
		case 5:	return "/img/carrTrans.png";
		default: return isSub ?  "/img/subTopDownLeft.png" : "/img/destroyerTDL.png";
		}		
	}
	
	/**
	 * Change whether the ship should be rendered or not
	 * @param status	True if this should should always be rendered, false otherwise
	 */
	public void setRevealStatus(boolean status) {
		this.isRevealed = status;
	}
	
	/**
	 * Get if this Ship is revealed
	 * @return True if this should should always be rendered, false otherwise
	 */
	public boolean isRevealed() {
		return isRevealed;
	}
	
	@Override
	public String toString() {
		return String.format("Ship of length %d at: %d, %d\nDamages: %s", size, startX, startY, Arrays.toString(damages));
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Ship)) {
			return false;
		}
		
		Ship other = (Ship) o;
		return getLength() == other.getLength() && isSub() == other.isSub();		
	}
}
