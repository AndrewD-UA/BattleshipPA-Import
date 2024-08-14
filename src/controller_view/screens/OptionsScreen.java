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
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
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
	
	private Button helpButton = new Button("Help Screen");	
	private Button menuButton = new Button("Quit Game");
	private Button fullscreenButton = new Button("Fullscreen");
	private Button backButton = new Button("Exit Options");
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

	private Node[][] options = { { fullscreenButton, helpButton }, { soundSlider, menuButton } // Replace Button with Slider
	};


	private static final Insets TEST_MARGIN = new Insets(0, 0, 40, 0);
	
	/**
	 * The font applied to all text on the options screen
	 */
	public static final Font OPTIONS_FONT = Font.font("Arial", FontWeight.BOLD, 40);

	private static final int OPTIONS_HGAP = 30;
	private static final int OPTIONS_VGAP = 100;

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
		soundSlider.maxHeightProperty().bind(fullscreenButton.heightProperty());
		soundSlider.minHeightProperty().bind(fullscreenButton.heightProperty());
		soundSlider.maxWidthProperty().bind(fullscreenButton.widthProperty());
		soundSlider.minWidthProperty().bind(fullscreenButton.widthProperty());
		Tooltip tooltip = new Tooltip("Volume");
		soundSlider.setTooltip(tooltip);
	}
	
	/**
	 * Completes the initialization of the allOptions GridPane.
	 * 
	 * After this method finishes, all contained Nodes will be positioned and
	 * initialized.
	 */
	private void initOptionsGridPane() {
	    allOptions = new GridPane();
	    
	    // All other buttons will be bound to this width
	    fullscreenButton.setMaxWidth(350);
	    fullscreenButton.setMinWidth(350);
	    
	    // Add all buttons to the GridPane
	    for (int column = 0; column < options.length; column++) {
	        for (int row = 0; row < options[column].length; row++) {
	            Node current = options[column][row];
	            if (current instanceof Button) {
	            	Button currButton = (Button) current;
	                currButton.setFont(OPTIONS_FONT);
	                currButton.prefWidthProperty().bind(fullscreenButton.widthProperty());
	            }
	            
                current.setStyle(CSS.SHIP_PICKER_ENTERED);
	            // Add the button to the appropriate column and row
	            allOptions.add(current, column, row);
	        }
	    }

	    BorderPane.setAlignment(backButton, Pos.TOP_CENTER);
	    BorderPane.setMargin(backButton, TEST_MARGIN);
	    
	    backButton.setFont(OPTIONS_FONT);
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
		BorderPane.setMargin(info, new Insets(10, 0, 0, 0));
		info.setFont(OPTIONS_FONT);
		info.setStyle(CSS.SHIP_PICKER_ENTERED);
		info.setBorder(new Border(new BorderStroke(Color.TRANSPARENT, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(10))));
		
		return info;		
	}

	/**
	 * Create and register all handlers for interaction.
	 * 
	 * Adds functionality to all options and buttons.
	 */
	private void initHandlers() {
		registerButtonAction(menuButton, AllProperties.SWITCH_SCREEN, AllProperties.RESET_GAME, AllProperties.BACK_TO_MENU);
		registerButtonAction(backButton, AllProperties.SWITCH_SCREEN, null, returnScene);
		registerButtonAction(helpButton, AllProperties.SWITCH_SCREEN, null, AllProperties.HELP_SCREEN);
		
		//Fullscreen button requires custom logic to swap 
		fullscreenButton.setOnAction((e)->{
			playSound(BUTTON_CLICK_SOUND, false);
			PropertyChangeEvent pce = new PropertyChangeEvent(this, AllProperties.SET_FULLSCREEN.property(), null, null);
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
	
	private void registerButtonAction(Button button, AllProperties event, Object oldVal, Object newVal) {
		button.setOnAction((e)->{
			playSound(BUTTON_CLICK_SOUND, false);
			PropertyChangeEvent pce = new PropertyChangeEvent(this, event.property(), oldVal, newVal);
		    fireEvent(pce);
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
