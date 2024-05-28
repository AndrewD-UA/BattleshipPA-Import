package controller_view.screens;

import javafx.geometry.Insets;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

/**
 * A collection of Strings to apply as CSS styling to objects
 * 
 * @author Adam Fehse
 */
public class CSS {
	
	/**
	 * Default styling for the ship picker buttons
	 */
	public static final String SHIP_PICKER_DEFAULT = "-fx-background-color: #6495ED; " + "-fx-font-size: 14px; "
			+ "-fx-text-fill: linear-gradient(to bottom, #FFFFFF, #C0C0C0); " + "-fx-padding: 8px 12px; "
			+ "-fx-background-radius: 10; " + "-fx-border-radius: 10;";
	
	/**
	 * Default styling for ship picker buttons when hovered over
	 */
	public static final String SHIP_PICKER_ENTERED = "-fx-background-color: #FFA500; "
			+ "-fx-effect: dropshadow(gaussian, rgba(255,255,255,0.7), 10, 0, 0, 0);";
	
	/**
	 * Reset styling for ship picker buttons when not selected or hovered over
	 */
	public static final String SHIP_PICKER_EXITED = "-fx-background-color: #6495ED; " + "-fx-font-size: 14px; "
			+ "-fx-text-fill: linear-gradient(to bottom, #FFFFFF, #C0C0C0); " + "-fx-padding: 8px 12px; "
			+ "-fx-background-radius: 10; " + "-fx-border-radius: 10;";
	
	/**
	 * Styling for ship picker buttons when selected
	 */
	public static final String SHIP_PICKER_SELECTED = "-fx-effect: dropshadow(three-pass-box, rgba(255, 165, 0, 0.8), 10, 0.0, 0, 1); "
			+ "-fx-text-fill: black; " + "-fx-background-color: transparent;";
	
	/**
	 * Default styling for the next button on the ShipPickerScreen
	 */
	public static final String NEXT_DEFAULT = "-fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.6), 5, 0.0, 0, 1); "
			+ "-fx-text-fill: white; " + "-fx-background-color: transparent;";
		
	/**
	 * Styling whil next button on the ShipPickerScreen is clicked
	 */
	public static final String NEXT_ENTERED = "-fx-effect: dropshadow(three-pass-box, rgba(255, 165, 0, 0.8), 10, 0.0, 0, 1); "
			+ "-fx-text-fill: black; " + "-fx-background-color: transparent;";
	
	/**
	 * Styling for powerups on the GamePlayScreen
	 */
	public static final String SPECIAL_BUTTON = "-fx-effect: dropshadow(three-pass-box, rgba(255, 165, 0, 0.8), 10, 0.0, 0, 1); "
			+ "-fx-text-fill: black; " + "-fx-background-color: transparent;";
	
	/**
	 * Styling for the "End game" button
	 */
	public static final String END_BUTTON_SELECTED = "-fx-background-color: #FF0000;" + "-fx-text-fill: white;";
	
	/**
	 * Styling for the title on the StartScreen
	 */
	public static final String MAIN_SCREEN_TITLE = "-fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.6), 5, 0.0, 0, 1); " + "-fx-text-fill: white;";
	
	/**
	 * Styling for the buttons on the StartScreen
	 */
	public static final String MAIN_SCREEN_BUTTON_DEFAULT = "-fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.6), 5, 0.0, 0, 1); "
			+ "-fx-text-fill: white; " + "-fx-background-color: transparent;";
	
	/**
	 * Styling for the buttons on the StartScreen when clicked
	 */
	public static final String MAIN_SCREEN_BUTTON_SELECTED = "-fx-effect: dropshadow(three-pass-box, rgba(255, 165, 0, 0.8), 10, 0.0, 0, 1); "
			+ "-fx-text-fill: black; " + "-fx-background-color: transparent;";
		
	/**
	 * A soft white plain background
	 */
	public static final BackgroundFill TOGGLE_BUTTON_BACKGROUND = new BackgroundFill(Color.WHITESMOKE, new CornerRadii(5), Insets.EMPTY);
}
