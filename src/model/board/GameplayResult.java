package model.board;

import java.time.Duration;
import java.time.LocalTime;

/**
 * A wrapper to store the results of gameplay for passing between screens
 * 
 * @author Andrew Dennison
 */
public class GameplayResult {
	
	private int hits;
	private int moves;
	private int shipsSunk;
	private long timeDuration;
	
	/**
	 * Create a new gameplay result.
	 * @param hits		Number of moves that hit a Ship.
	 * @param moves		Number of total moves made.
	 * @param startTime	Time the game began.
	 * @param endTime	Time the final move hit the final Ship
	 * @param shipsSunk	Number of ships sunk by the player
	 */
	public GameplayResult(int hits, int moves, LocalTime startTime, LocalTime endTime, int shipsSunk) {
		this.hits = hits;
		this.moves = moves;		
		this.shipsSunk = shipsSunk;
		this.timeDuration = Duration.between(startTime, endTime).toSeconds();

	}
	
	/**
	 * Get the number of moves made that hit a ship
	 * @return	Integer repr of number of hits between 0 and 17
	 */
	public int getHits() {
		return hits;
	}
	
	/**
	 * Get the number of total moves made
	 * @return	Integer repr of number of moves between 0 and 100
	 */
	public int getMoves() {
		return moves;
	}
	
	/**
	 * Get an integer representation of the percentage of hits out of 100
	 * @return	Integer from 0-100
	 */
	public int getPercentHits() {
		return (int) ((hits / moves) * 100);
	}
	
	/**
	 * Get the number of ships sunk on the board
	 * @return	Integer between 0 and 5
	 */
	public int getShipsSunk() {
		return shipsSunk;
	}
	
	/***
	 * Get the number of seconds the game took to finish
	 * @return	Number of seconds in the game
	 */
	public long getGameDuration() {
		return timeDuration;
	}
}
