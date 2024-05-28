package controller_view.screens;

import java.beans.PropertyChangeEvent;

import controller_view.AllProperties;
import controller_view.PlayBattleship;
import controller_view.SwitchableScreen;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * A simple screen to provide the user a GUI for switching options.
 * 
 * Credit to PeakPX for background wallpaper:
 * https://www.peakpx.com/en/hd-wallpaper-desktop-kljcw
 * 
 * @author Andrew Dennison, Adam Fehse
 */
public class OptionsScreen extends SwitchableScreen {


	/**
	 * Container for all available options in the center of the options screen
	 */
	private GridPane allOptions;
	
	private Button helpButton = new Button("Go To Help Screen");
	
	private Button backButton = new Button("Quit to Main Menu");
	private Button fullscreenButton = new Button("Fullscreen: off");
	private Slider soundSlider = new Slider(0, 100, 50);

	/**
	 * A 2D array of all options, such as buttons and sliders, present in the center
	 * of the options screen.
	 * 
	 * options[0] represents the left side of the screen, and options[1] represents
	 * the right side.
	 * 
	 * The first element of each array is the top of the respective column. E.g.,
	 * options[0][0] represents the top-left most option.
	 * 
	 * This array is of type Node so non-button elements can be added.
	 */

	private Node[][] options = { { fullscreenButton }, { soundSlider } // Replace Button with Slider
	};


	private static final Insets TEST_MARGIN = new Insets(0, 0, 40, 0);
	
	/**
	 * The font applied to all text on the options screen
	 */
	public static final Font OPTIONS_FONT = Font.font("Arial", FontWeight.BOLD, 40);

	private static final int OPTIONS_HGAP = 30;
	private static final int OPTIONS_VGAP = 100;

	private boolean fullscreen = false;
	private AllProperties returnScene = AllProperties.BACK_TO_MENU;

	/**
	 * Create a new OptionsScreen scene.
	 * 
	 * @param root   The BorderPane which will contain this Screen
	 * @param width  The width of the screen
	 * @param height The height of the screen
	 */
	public OptionsScreen(BorderPane root, double width, double height) {
		super(root, width, height, "/img/bo.jpg");

		initSlider();
		initOptionsGridPane();
		Label info = initLabel();
		initHandlers();
		
		window.setTop(info);
		window.setCenter(allOptions);
		window.setBottom(backButton);		
	}
	
	private void initSlider() {
		soundSlider.setShowTickLabels(true);
		soundSlider.setShowTickMarks(true);
		soundSlider.setMajorTickUnit(20);
		soundSlider.setMinorTickCount(5);
		soundSlider.setBlockIncrement(10);
		soundSlider.setStyle(CSS.SHIP_PICKER_ENTERED);
		soundSlider.maxHeightProperty().bind(fullscreenButton.heightProperty());
		soundSlider.minHeightProperty().bind(fullscreenButton.heightProperty());
		soundSlider.maxWidthProperty().bind(fullscreenButton.widthProperty());
		soundSlider.minWidthProperty().bind(fullscreenButton.widthProperty());
	}
	
	/**
	 * Completes the initialization of the allOptions GridPane.
	 * 
	 * After this method finishes, all contained Nodes will be positioned and
	 * initialized.
	 */
	private void initOptionsGridPane() {
	    allOptions = new GridPane();

	    // Add all buttons to the GridPane
	    for (int column = 0; column < options.length; column++) {
	        for (int row = 0; row < options[column].length; row++) {
	            Node current = options[column][row];
	            if (current instanceof Button) {
	                ((Button) current).setFont(OPTIONS_FONT);
	            }

	            // Add the button to the appropriate column and row
	            allOptions.add(current, column, row);
	        }
	    }
	    
	    // Adding help screen button 
	    allOptions.add(helpButton, 3, 0); 

	    fullscreenButton.setStyle(CSS.SHIP_PICKER_ENTERED);

	    // Initialize the "back to main menu" button
	    BorderPane.setAlignment(backButton, Pos.TOP_CENTER);
	    BorderPane.setMargin(backButton, TEST_MARGIN);

	    backButton.setFont(BUTTON_FONT);
	    backButton.setStyle(CSS.SHIP_PICKER_ENTERED);

	    // Generic options for the GridPane container
	    allOptions.setAlignment(Pos.CENTER);
	    allOptions.setVgap(OPTIONS_VGAP);
	    allOptions.setHgap(OPTIONS_HGAP);
	}


	/**
	 * Align the three major elements - top (label), middle (options grid), and
	 * bottom (return to menu)
	 */
	private Label initLabel() {
		Label info = new Label("OPTIONS");
		BorderPane.setAlignment(info, Pos.CENTER);
		info.setFont(OPTIONS_FONT);
		info.setStyle(CSS.SHIP_PICKER_ENTERED);
		
		return info;		
	}

	/**
	 * Create and register all handlers for interaction.
	 * 
	 * Adds functionality to all options and buttons.
	 */
	private void initHandlers() {
		// Notify the main window the user wants to go back to the menu
		backButton.setOnAction((event) -> {
			playSound(BUTTON_CLICK_SOUND, false);
			PropertyChangeEvent pce = new PropertyChangeEvent(this, AllProperties.SWITCH_SCREEN.property(), AllProperties.RESET_GAME, AllProperties.BACK_TO_MENU);
			fireEvent(pce);
		});

		// If the fullscreen button is clicked, swap the value of fullscreen for text
		// display
		// Then, notify the main window fullscreen is/not desired
		fullscreenButton.setOnAction((event) -> {
			playSound(BUTTON_CLICK_SOUND, false);
			fullscreen = !fullscreen;
			fullscreenButton.setText(String.format("Fullscreen: %s", fullscreen ? "on" : "off"));
			PropertyChangeEvent pce = new PropertyChangeEvent(this, AllProperties.SET_FULLSCREEN.property(),
					!fullscreen, fullscreen);
			fireEvent(pce);
		});
		
		helpButton.setOnAction((event) -> {
		    playSound(BUTTON_CLICK_SOUND, false);
		    PropertyChangeEvent pce = new PropertyChangeEvent(this, AllProperties.SWITCH_SCREEN.property(),
		            null, AllProperties.HELP_SCREEN);
		    fireEvent(pce);
		});

		
		getRoot().setOnKeyReleased((event)->{
			if (event.getCode() == KeyCode.ESCAPE) {
				fireEvent(new PropertyChangeEvent(this, AllProperties.SWITCH_SCREEN.property(), null, returnScene));
			}
		});
		
		soundSlider.setOnMouseReleased((e)->{
			double newLevel = soundSlider.getValue() / 100;
			PlayBattleship.getInstance().setSoundLevel(newLevel);
		});
	}

	/**
	 * Set the screen to return to from the options screen
	 * @param returnScene	The next scene to go to when the escape key is hit
	 */
	public void setReturn(AllProperties returnScene) {
		this.returnScene  = returnScene;
	}
}
