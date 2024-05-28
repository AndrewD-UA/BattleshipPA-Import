package model.board;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import controller_view.AllProperties;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import model.battleship.Ship;
import model.battleship.ShipCollection;

/**
 * Board must be serialiable to implement saving/loading mid-game.
 * 
 * A Board can either be created with the default constructor or given a collection of Ships.
 * 
 * If the collection of Ships is passed in, as in the case of a human player, the board will
 * simply manage the collection for a BoardViewer.
 * 
 * If the default constructor is used, it will attempt to generate a new ShipCollection by placing
 * and rotating ships until open spaces are found.  This is generally used for AI boards.
 * 
 * @author Andrew Dennison
 */
public class Board implements Serializable{

	/**
	 * Generated serial ID
	 */
	private static final long serialVersionUID = -1208850620312784809L;
	
	/**
	 * Create a new board with a given collection of Ships
	 */
	ShipCollection allShips;
	
	/**
	 * Create a new list of all the moves made thus far
	 */
	ObservableList<Point2D> allMoves = FXCollections.observableArrayList();
	
	/**
	 * List to hold all the boxes that are shielded
	 */
	ArrayList<Point2D> shieldedBoxes = new ArrayList<Point2D>();
	
	/**
	 * Create a new board with a given collection of Ships
	 */
	PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	
	/**
	 * A value from 0 to 100 of the current percentage readiness of the special ability.  100 means a special ability is ready to be used.
	 */
	public int specialAbilityCounter;
	
	/**
	 * The total number of moves made on this board
	 */
	private int movesCount;
	
	/**
	 * The total number of moves resulting in a hit made on this board
	 */
	private int hitCount;
	
	/**
	 * The start time of the board
	 */
	private LocalTime startTime;
	
	/**
	 * True if this board belongs to an AI, false otherwise
	 */
	public boolean isAI;
	
	/**
	 * Timer used to increment the special ability counter
	 */
	private Timer timer;
	
	/**
	 * Create a Board with randomly generated ship positions.  This constructor
	 * is intended for usage with AI players only.
	 */
	public Board() {
		allShips = generateRandomShips();
		isAI = true;
		timer = new Timer();
	}
	
	/**
	 * Create a new board with a given collection of Ships
	 * @param ships	Collection of all Ships, with the ships already added.
	 */
	public Board(ShipCollection ships) {
		allShips = ships;
		isAI = false;
	}
	
	

	/**
	 * Make a move at a given position.
	 * 
	 * @param move 		Point2D x, y coordinates on the board to make a move
	 * @param testing	True if this is being run in a JUnit test environment, false otherwise
	 * @return -1 if the move hit a Shield, 1 if the move hit a Ship, 0 if a miss
	 */
	public int enterMove(Point2D move, boolean testing) {
		if (move.getX() < 0 || move.getX() > 9 || move.getY() < 0 || move.getY() > 9) {
			return 0;
		}
		
		movesCount++;
		// If a move has been made on the AI's board, and we still need to increment the counter
		// schedule a task to do so
		if (isAI && specialAbilityCounter < 100 && !testing) {
			specialAbilityCounter++;
			incrementSpecialAbilityCounter();
		}
				
		if(!shieldedBoxes.contains(move)) {
			if (allShips.tryMove(move, false)) {
				allMoves.add(move);
				hitCount++;

				if (allShips.isEmpty()) {
					endGame();
				}
				return 1;
			}
		} else {
			shieldedBoxes.remove(move);
			return -1;
		}
		
		return 0;
	}
	
	private void incrementSpecialAbilityCounter() {
		TimerTask t = new TimerTask() {
			@Override
			public void run() {
				if (specialAbilityCounter % 10 != 0) {
					specialAbilityCounter++;
					PropertyChangeEvent pce = new PropertyChangeEvent(this, AllProperties.UPDATE_SPECIAL.property(), specialAbilityCounter - 1, specialAbilityCounter);
					pcs.firePropertyChange(pce);
				} else {
					this.cancel();
					timer.purge();
				}
			}
		};
		
		timer.schedule(t, 0, 45);
	}

	private void endGame() {
		// Notify all listeners the game is over				
		PropertyChangeEvent pce = new PropertyChangeEvent(this, AllProperties.GAME_OVER.property(), false, true);
		pcs.firePropertyChange(pce);
		
		// Present statistics to listeners if this was a board a player was attacking
		if(isAI) {
			GameplayResult gr = new GameplayResult(hitCount, movesCount, startTime, LocalTime.now(), size());
			PropertyChangeEvent pce2 = new PropertyChangeEvent(this, AllProperties.GAME_STATS_READY.property(), null, gr);
			pcs.firePropertyChange(pce2);
		}
	}

	/**
	 * Add a shield at given position
	 * 
	 * @param pos Point2D x, y coordinates on the board to place shield
	 * @return -1 if the shield is on a ship, 0 if false otherwise
	 */
	public int placeShield(Point2D pos) {
		if (allShips.tryMove(pos, true) && !shieldedBoxes.contains(pos)) {
			shieldedBoxes.add(pos);
			return -1;
		}
		else {
			return 0;
		}
	}
	
	/**
	 * Determine whether this board has any valid targets left.
	 * @return	True if all ships are sunk, false otherwise
	 */
	public boolean isEmpty() {
		return allShips.isEmpty();
	}
	
	/**
	 * Get the logical size of the underlying ShipCollection
	 * @return	Number of ships left on the board
	 */
	public int size() {
		return allShips.size();
	}
	
	/**
	 * Generate a collection of ships in random positions and directions
	 * @return	The collection containing five randomly generated ships
	 */
	private ShipCollection generateRandomShips() {
		ShipCollection sc = new ShipCollection();
		Random rand = new Random();
		int shipsAdded = 0;
		
		for (int lengthOfShip : ShipCollection.SHIP_LENGTHS) {
			while(true) {
				// Generate a new Random point
				Point2D currPoint = new Point2D(rand.nextInt(10), rand.nextInt(10));
				
				// Make sure the start point has no conflict with existing ships
				boolean conflict = false;
				for (Ship s : sc) {
					if (ShipCollection.tryLengthOfShip(currPoint, s) != -1) {
						conflict = true;
						break;
					}
				}
								
				// If we detected a conflict, generate a new point
				if (conflict) {
					continue;
				}
				
				Ship newShip = new Ship(currPoint, Direction.LEFT, lengthOfShip, shipsAdded == 2);

				
				// Rotate the ship until we find an open spot
				for (int i = 0; i < 4; i++) {
					conflict = false;
					newShip.setFacingDirection(newShip.getFacingDirection().rotateRight());
					
					// If the ship runs off the board, skip to the next rotation
					conflict = !checkShipPlacementOnBoard(newShip);					
					if (conflict) continue;
					
					// If there's a conflict between this ship and any other ship on the board,
					// skip to the next rotation
					for (Ship s : sc) {
						conflict = checkShipsConflict(newShip, s);
						if (conflict) break;
					}	
					
					// If we didn't find any issues with this ship placement, break out.
					if (!conflict) break;					
				}
				
				// If there was no conflict with the ships startPos and Direction, add it
				if (!conflict) {
					sc.addShip(newShip);
					shipsAdded++;
					break;
				}
			}
		}
		
		return sc;
	}
	
	/**
	 * Check if two ships on the board conflict in their positioning
	 * 
	 * @param ship1	The first Ship to check for conflicts
	 * @param ship2	The second Ship to check for conflicts
	 * @return		True if the ships conflict, false if they do not
	 */
	private boolean checkShipsConflict(Ship ship1, Ship ship2) {
		Point2D[] firstShipPos = ship1.toArray();
		
		for (Point2D pos : firstShipPos) {
			if (ShipCollection.tryLengthOfShip(pos, ship2) != -1){
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Check that all squares a Ship is in are valid squares on the board
	 * @param s	Ship to check
	 * @return	false if the ship does not run off the board
	 */
	private boolean checkShipPlacementOnBoard(Ship s) {
		double startX = s.getStartPos().getX();
		double startY = s.getStartPos().getY();
				
		switch(s.getFacingDirection()) {
			case UP: return startY - s.getLength() >= 0;
			case RIGHT: return startX + s.getLength() < 10;
			case DOWN: return startY + s.getLength() < 10;
			default: return startX - s.getLength() >= 0;
		}		
	}
	
	/**
	 * Get the collection of Ships backing this board, primarily for the BoardViewer
	 * @return	ShipCollection representation of the ships on the Board
	 */
	public ShipCollection getCollection() {
		return allShips;
	}
	
	/**
	 * Add a Ship to the board, for use in building the board at ShipPickerScreen
	 * @param newShip	Ship to add to the Board
	 * @return 			True if the ship is added to the board, false if not
	 */
	public boolean addShipToBoard(Ship newShip) {
		if (allShips.size() == 5 || newShip == null) {
			return false;
		}
		
		if (!checkShipPlacementOnBoard(newShip)) {
			return false;
		}
		
		for (Ship currentShip : allShips) {
			if(checkShipsConflict(currentShip, newShip)){
				return false;
			}
		}
		
		return allShips.addShip(newShip);
	}
	
	/**
	 * Add a listener to this Board.
	 * 
	 * @param pcl	Object implementing PropertyChangeListener
	 */
	public void addListener(PropertyChangeListener pcl) {
		pcs.addPropertyChangeListener(pcl);
	}
	
	@Override
	public String toString() {
		String result = "";
		for (Ship s : allShips) {
			result += String.format("%s\n", s.toString());
		}
		
		return result;
	}
	
	/**
	 * Called when the game begins.  All stats are reset and game begins from nothing
	 */
	public void startStats() {
		startTime = LocalTime.now();
		movesCount = 0;
		hitCount = 0;
		
		specialAbilityCounter = 0;
	}
	
	/**
	 * Get the number of moves made on this Board
	 * @return	The total of all moves, regardless of hit/miss
	 */
	public int moveCount() {
		return movesCount;
	}

	/**
	 * Cheat and get a valid move from the board directly
	 * @return	X, Y coordinates of a square that has not been hit
	 */
	public Point2D cheatMove() {
		for (Ship s : allShips) {
			if (!s.isSunk()) {
				Point2D hitPos = s.getStartPos();
				int i = 0;
				
				while(i < s.getLength() - 1) {	
					if (s.hitShip(i, true)) {
						break;
					}
					
					i++;
				}
				
				boolean negative = s.getFacingDirection() == Direction.UP ||
						s.getFacingDirection() == Direction.LEFT;

				boolean alongXAxis = !s.getFacingDirection().isVertical();
								
				if (alongXAxis) {
					return negative ? hitPos.subtract(i, 0) : hitPos.add(i, 0);
				}
				
				return negative ? hitPos.subtract(0, i) : hitPos.add(0, i);
			}
		}
		
		// How are we looking for cheat moves if all ships are sunk?
		return new Point2D(0, 0);
	}
	
	/**
	 * Hides ships from being rendered
	 * @param status	True if all ships should be rendered, false otherwise
	 */
	public void setShipRendering(boolean status) {	
		for (Ship s : allShips) {
			s.setRevealStatus(status);
		}
	}
	
	/**
	 * Will reveal one ship
	 * @param toReveal	The ship to be set to revealed
	 */
	public void revealOneShip(Ship toReveal) {	
		for (Ship s : allShips) {
			if (s.equals(toReveal)) {
				s.setRevealStatus(true);
			}
		}
	}
	
	/**
	 * Nuke a 3x3 square on the board
	 * @param mid	The middle point of the 3x3 square to be hit
	 * @return		Whether each square was a hit or a miss
	 */
	private int[] nuke(Point2D mid) {
		int[] result = new int[9];
		
		Point2D startPoint = new Point2D(mid.getX() - 1, mid.getY() - 1);
		
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				result[i * 3 + j] = enterMove(startPoint.add(i, j), false);
			}
		}	
		
		return result;
	}
	
	/**
	 * Hit a 1x5 vertical square on the board
	 * @param mid	The middle of the 1x5 square
	 * @return		Whether each square was a hit or a miss
	 */
	private int[] strafingRun(Point2D mid) {
		int[] result = new int[5];
		Point2D startPoint = new Point2D(mid.getX(), mid.getY() - 2);
		
		for (int i = 0; i < 5; i++) {
			result[i] = enterMove(startPoint.add(0, i), false);
		}
		
		return result;
	}
	
	/**
	 * Reveals one enemy ship	
	 * @return	True if a ship was revealed, false otherwise
	 */
	private boolean reveal(Point2D pos) {
		for (Ship s : allShips) {
			if (s != null && !s.isRevealed() && !s.isSunk()) {
				s.setRevealStatus(true);
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Determine if a shield move should be allowed right now
	 * @return	True if the special ability counter is at 100, false otherwise
	 */
	public boolean canMakeShieldMove() {
		return specialAbilityCounter >= 100;
	}
	/**
	 * Attempt to make a special move on this board
	 * @param move			A string representation of the type of move requested	
	 * @param playerMove	The x and y coordinates of the center of the requested move
	 * @return				false if The result of the hits
	 */
	public int[] attemptSpecialMove(String move, Point2D playerMove) {
		if (specialAbilityCounter < 100) {
			return new int[0];
		}
		
		int[] result = new int[0];		
		
		// Shield and second chance operate independently of the Board		
		switch(move) {
		case "nuke":
			result = nuke(playerMove);			break;
		case "strafing run":
			result = strafingRun(playerMove);	break;
		case "reveal":
			if (reveal(playerMove)) result = new int[1];			break;			
		}
		
		specialAbilityCounter = 0;
		pcs.firePropertyChange(AllProperties.UPDATE_SPECIAL.property(), 100, 0);
		
		return result;
	}

	/**
	 * Remove a ship from this collection
	 * @param s		Ship to remove
	 * @return		True if removed, false if not
	 */
	public boolean remove(Ship s) {
		return allShips.removeShip(s);
	}
}
