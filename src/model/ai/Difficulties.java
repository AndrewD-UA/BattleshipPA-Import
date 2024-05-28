package model.ai;

/**
 * A list of all difficulties to make code decisions regarding the difficulty easy to read
 * 
 * @author Andrew Dennison
 */
public enum Difficulties {
	/**
	 * Easy difficulty
	 */
	EASY("Easy"),
	
	/**
	 * Medium difficulty
	 */
	MEDIUM("Medium"),
	
	/**
	 * Hard difficulty
	 */
	HARD("Hard"),
	
	/**
	 * Extreme difficulty
	 */
	EXTREME("Extreme");
	
	private String name;
	
	private Difficulties(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
