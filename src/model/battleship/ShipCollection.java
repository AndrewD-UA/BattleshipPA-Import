package model.battleship;

import java.util.Iterator;

import javafx.geometry.Point2D;
import model.board.Direction;

/**
 * A collection of Ships.
 * 
 * A ShipCollection is responsible for holding five Ships for a board, as well
 * as updating individual Ships in the collection in response to the board's
 * actions.
 * 
 * @author Andrew Dennison, Tom
 */
public class ShipCollection implements Iterable<Ship> {

	/**
	 * Internal storage of all Ships.
	 */
	private Ship[] allShips;

	/**
	 * The number of ships that have not been sunk in this collection.
	 */
	private int shipsRemaining;

	/**
	 * The unchanging length of the five kinds of ships on the board.
	 */
	public static final int[] SHIP_LENGTHS = new int[] { 2, 3, 3, 4, 5 };

	/**
	 * Create a new collection with null ships in it.
	 * 
	 * It is expected five ships will be added after this constructor is called
	 */
	public ShipCollection() {
		allShips = new Ship[5];
		shipsRemaining = 0;
	}

	/**
	 * Create a new collection of ships in the following order: ships[0] : patrol
	 * boat ships[1] : submarine ships[2] : destroyer ships[3] : battleship ships[4]
	 * : carrier
	 * 
	 * @param ships An array of ships to be stored in this collection
	 */
	public ShipCollection(Ship[] ships) {
		allShips = ships;
		shipsRemaining = ships.length;
	}

	/**
	 * Add a ship to the next open position in this collection.
	 * 
	 * If there are already five Ships, do nothing.
	 * 
	 * @param s Ship to add to the collection
	 * @return True if the Ship was added, otherwise false
	 */
	public boolean addShip(Ship s) {
		if (shipsRemaining >= 5) {
			return false;
		}

		allShips[shipsRemaining] = s;
		shipsRemaining++;
		return true;
	}
	
	/**
	 * Remove a ship from the collection
	 * @param newShip	The ship to remove
	 * @return			True if the ship was removed, false if it could not be found
	 */
	public boolean removeShip(Ship newShip) {
		for (int i = 0; i < allShips.length; i++) {
			if (allShips[i]!= null && allShips[i].equals(newShip)) {
				allShips[i] = null;
				shipsRemaining--;
				return true;
			}
		}
		
		return false;
	}

	/**
	 * Get a ship within this collection
	 * 
	 * @param i The index of the Ship within the collection
	 * @return Reference to the Ship
	 */
	public Ship getShip(int i) {
		return allShips[i];
	}

	/**
	 * Get the number of un-damaged ships
	 * 
	 * @return The number of un-damaged ships
	 */
	public int size() {
		return shipsRemaining;
	}

	/**
	 * Determine whether all ships are fully damaged
	 * 
	 * @return True if all ships are fully damaged, false if any ship has not been
	 *         fully damaged
	 */
	public boolean isEmpty() {
		return shipsRemaining == 0;
	}

	/**
	 * Given an X and Y coordinate, check if that is a hit on any ship or a miss
	 * 
	 * @param hitPoint The X and Y coordinates of the point to hit
	 * @param isPassive True if this move should not leave lasting damage (update a Ship's damages), false otherwise
	 * @return True if the move hit a Ship, false otherwise.
	 */
	public boolean tryMove(Point2D hitPoint, boolean isPassive) {
		Iterator<Ship> iter = iterator();

		while (iter.hasNext()) {
			Ship s = iter.next();
			if (s.isSunk()) {
				continue;
			}
			
			int hitPos = tryLengthOfShip(hitPoint, s);
			if (hitPos != -1) {
				s.hitShip(hitPos, isPassive);
				if (s.isSunk()) {
					shipsRemaining--;
				}
				return true;
			}
		}

		return false;
	}

	/**
	 * Given a position on the board, check if that position falls into any Ship in
	 * the collection.
	 * 
	 * @param hitPos Position on the board being targeted.
	 * @param s      The Ship that is being checked for a hit.
	 * @return -1 if the ship was not hit, otherwise the index of the hit on the
	 *         Ship.
	 */
	public static int tryLengthOfShip(Point2D hitPos, Ship s) {
		Direction facing = s.getFacingDirection();
		int length = s.getLength();
		Point2D start = s.getStartPos();

		if (facing.isVertical() && hitPos.getX() != start.getX() || 
			!facing.isVertical() && hitPos.getY() != start.getY()) {
			return -1;
		}
		
		if (facing.equals(Direction.LEFT)) {
			if (hitPos.getX() <= start.getX() && hitPos.getX() > start.getX() - length) {
				return (int) (start.getX() - hitPos.getX());
			}
		} else if (facing.equals(Direction.UP)) {
			if (hitPos.getY() <= start.getY() && hitPos.getY() > start.getY() - length) {
				return (int) (start.getY() - hitPos.getY());
			}
		} else if (facing.equals(Direction.RIGHT)) {
			if (hitPos.getX() >= start.getX() && hitPos.getX() < start.getX() + length) {
				return (int) (hitPos.getX() - start.getX());
			}
		} else if (facing.equals(Direction.DOWN)){
			if (hitPos.getY() >= start.getY() && hitPos.getY() < start.getY() + length) {
				return (int) (hitPos.getY() - start.getY());
			}
		}
		
		return -1;
	}

	@Override
	public Iterator<Ship> iterator() {
		return new ShipsIterator();
	}

	/**
	 * A simple custom iterator to iterate over the array of all ships.
	 */
	private class ShipsIterator implements Iterator<Ship> {

		/**
		 * The index of allShips that the iterator is currently accessing
		 */
		private byte index = 0;

		@Override
		public boolean hasNext() {
			return index < allShips.length && allShips[index] != null;
		}

		@Override
		public Ship next() {
			Ship s = allShips[index];
			index++;
			return s;
		}

	}

	@Override
	public String toString() {
		String result = "";

		for (Ship s : allShips) {
			if (s == null) {
				continue;
			}
			result += String.format("%s\t", s.toString());
		}

		return result + "\n";

	}
}
