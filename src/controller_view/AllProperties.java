package controller_view;

/**
 * Standardizes the list of properties that can be fired to notify the main window of changes.
 * 
 * @author Andrew Dennison
 */
public enum AllProperties {
	/**
	 * Used to indicate the main screen should swap out a screen
	 */
	SWITCH_SCREEN("goto_next"),
	
	/**
	 * Go to the HelpScreen
	 */
	HELP_SCREEN("help_screen"),
	
	
	/**
	 * Go to the ShipPickerScreen
	 */
	START_GAME("start_game"),
	
	/**
	 * Go to the options screen
	 */
	OPEN_OPTIONS("goto_options"),
	
	/**
	 * Go to StartScreen
	 */
	BACK_TO_MENU("back_to_main"),
	
	/**
	 * Toggle if fullscreen is currently active
	 */
	SET_FULLSCREEN("set_fullscreen"),
	
	/**
	 * Go to the stats screen
	 */
	GAME_OVER("game_over"),
	
	/**
	 * Go to the GamePlayScreen
	 */
	DISPLAY_BOARD("display_board"),
	
	/**
	 * Used when a board is clicked
	 */
	BOARD_CLICK("board_clicked"),
	
	/**
	 * Used when a board has been loaded
	 */
	HUMAN_BOARD_READY("human_board_ready"),
	
	/**
	 * Used when the AI's board has been loaded
	 */
	AI_BOARD_READY("ai_board_ready"),
	
	/**
	 * Used to indicate the statistics from the last played game are ready to be read
	 */
	GAME_STATS_READY("game_stats_ready"),
	
	/**
	 * Used to reset all boards
	 */
	RESET_GAME("reset_game"),
	
	/**
	 * Used to notify listeners a change has been made to the special ability counter
	 */
	UPDATE_SPECIAL("update_special_text"),
	
	/**
	 * Used to notify a Screen that a Node has been moused over
	 */
	MOUSE_OVER_EVENT("mouse_over_event");
	
	private final String property;
	
	/**
	 * Bind a property to a given name for that property
	 * @param propertyName	The name of the property
	 */
	AllProperties(String propertyName) {
		property = propertyName;
	}
	
	/**
	 * Get a String representation of this Property
	 * @return	This Property as a String
	 */
	public String property() { return property; }
}
