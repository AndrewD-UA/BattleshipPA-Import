package controller_view.screens;

import java.beans.PropertyChangeEvent;

import controller_view.AllProperties;
import controller_view.SwitchableScreen;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

/**
 * The initial screen displayed to the user when the application is launched.
 * 
 * @author Andrew Dennison and Adam Fehse
 */
public class StartScreen extends SwitchableScreen {

	private GridPane allButtons = new GridPane();

	private Button startButton = new Button("Start game");
	private Button optionsButton = new Button("Options");
	private Label title = new Label("BATTLESHIP");

	private static final Insets TITLE_MARGIN = new Insets(40, 0, 0, 0);
	
	private static final String START_MUSIC = "/audio/startScreen.mp3";

	/**
	 * Create a new starting screen.
	 * 
	 * @param root   BorderPane which contains this StartScreen
	 * @param width  Width of the main window.
	 * @param height Height of the main window.
	 */
	public StartScreen(BorderPane root, double width, double height) {
		super(root, width, height, "/img/jersey.jpg");

		initStartScreen();
		initHandlers();
		playSound(START_MUSIC, true);		
	}

	/**
	 * Layout the contents of the starting screen
	 */
	private void initStartScreen() {
		startButton.setFont(BUTTON_FONT);
		optionsButton.setFont(BUTTON_FONT);
		
		startButton.setPrefWidth(getWidth() / 2);
		optionsButton.setPrefWidth(getWidth() / 2);

		allButtons.setAlignment(Pos.CENTER);
		allButtons.setVgap(100);

		allButtons.add(startButton, 0, 0);
		allButtons.add(optionsButton, 0, 1);

		title.setFont(TITLE_FONT);

		BorderPane.setMargin(title, TITLE_MARGIN);
		BorderPane.setAlignment(title, Pos.CENTER);
		window.setTop(title);
		window.setCenter(allButtons);

		// title
		title.setStyle(CSS.MAIN_SCREEN_TITLE);

		// startButton
		startButton.setStyle(CSS.MAIN_SCREEN_BUTTON_DEFAULT);
		startButton.setOnMouseEntered(e -> {
			startButton.setStyle(CSS.MAIN_SCREEN_BUTTON_SELECTED);
		});
		startButton.setOnMouseExited(e -> {
			startButton.setStyle(CSS.MAIN_SCREEN_BUTTON_DEFAULT);
		});

		// optionsButton
		optionsButton.setStyle(CSS.MAIN_SCREEN_BUTTON_DEFAULT);
		optionsButton.setOnMouseEntered(e -> {
			optionsButton.setStyle(CSS.MAIN_SCREEN_BUTTON_SELECTED);
		});
		optionsButton.setOnMouseExited(e -> {
			optionsButton.setStyle(CSS.MAIN_SCREEN_BUTTON_DEFAULT);
		});
	}

	/**
	 * Register handlers for all buttons on the start screen
	 */
	private void initHandlers() {
		// Notify the main window the user wants to start a new game
		startButton.setOnAction((event) -> {
			playSound(BUTTON_CLICK_SOUND, false);
			PropertyChangeEvent startGame = new PropertyChangeEvent(this, AllProperties.SWITCH_SCREEN.property(), AllProperties.BACK_TO_MENU, AllProperties.START_GAME);
			fireEvent(startGame);
		});

		// Notify the main window the user wants to change options
		optionsButton.setOnAction((event) -> {
			playSound(BUTTON_CLICK_SOUND, false);
			PropertyChangeEvent openOptions = new PropertyChangeEvent(this, AllProperties.SWITCH_SCREEN.property(), AllProperties.BACK_TO_MENU,
					AllProperties.OPEN_OPTIONS);
			fireEvent(openOptions);
		});
	}
}
