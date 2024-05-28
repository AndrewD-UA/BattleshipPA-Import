package controller_view.screens;

import java.beans.PropertyChangeEvent;

import controller_view.AllProperties;
import controller_view.SwitchableScreen;
import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;
import model.board.GameplayResult;

/**
 * The screen to be displayed when the game ends.
 * 
 * Background image credit to Sebastian Arie Voortman on pexels.com * 
 * https://www.pexels.com/photo/body-of-water-during-golden-hour-189349/
 * 
 * @author Adam Fehse, Andrew Dennison
 */

public class GameOverScreen extends SwitchableScreen {
	
	private Text hitStats;
	private Text ratioStats;
	private Text sunkStats;
	private Text gameTimeStats;
	
	/**
	 * Create a new statistics screen
	 * @param root		The root of the screen
	 * @param width		The width of the screen in pixels
	 * @param height	The height of the screen in pixels
	 */
	
	/**
	 * Back to main menu on Game Over Screen
	 */
	private Button mainMenu = new Button("Back to Main Menu");
	/**
	 * Smaller than the title font, but larger than any individual elements should be
	 */
	public static final Font BUTTON_FONT = Font.font("Arial", FontWeight.BOLD, 40);

	/**
	 * Make a new GameOverScreen
	 * @param root		Parent root of this screen
	 * @param width		Width in pixels
	 * @param height	Height in pixel
	 */
	public GameOverScreen(BorderPane root, double width, double height) {
		super(root, width, height, "/img/sunset.jpg");
		
		initButton();
		initGameOverScreen();
		initHandlers();
	}
	
	private void initButton() {
		//Back to menu button
		mainMenu.setStyle(CSS.MAIN_SCREEN_BUTTON_DEFAULT);
		mainMenu.setOnMouseEntered(e -> {
			mainMenu.setStyle(CSS.MAIN_SCREEN_BUTTON_SELECTED);
		});
		mainMenu.setOnMouseExited(e -> {
			mainMenu.setStyle(CSS.MAIN_SCREEN_BUTTON_DEFAULT);
		});
		mainMenu.setFont(BUTTON_FONT);

	};
	
	private void initGameOverScreen() {
		// Vbox container for stats labels  and end game text with a border
		VBox statsContainer = new VBox();
		statsContainer.setAlignment(Pos.CENTER);
		statsContainer.setSpacing(20);
		statsContainer.setPadding(new Insets(50));

		// game over text and Main Menu Button
		Text gameOverText = new Text("Game Over");
		gameOverText.setFont(Font.font("Arial", FontWeight.BOLD, 36));
		gameOverText.setFill(Color.WHITE);
		
		

		// game stats
		hitStats = createStatText("Hits: ___"); 
		ratioStats = createStatText("Hit Ratio: ___%");
		sunkStats = createStatText("Ships Sunk: _");
		gameTimeStats = createStatText("Game Time: 00:00");

		statsContainer.getChildren().addAll(gameOverText, hitStats, ratioStats, sunkStats, gameTimeStats, mainMenu);

		window.setCenter(statsContainer);
		
		//transition effect
		FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), statsContainer);
		fadeIn.setFromValue(0);
		fadeIn.setToValue(1);
		fadeIn.play();
	}
	
	
	/**
	 * Applying styling to a Text field
	 * @param text	String to format into a Text field
	 * @return		Reference to the created Text field
	 */
	private Text createStatText(String text) {
		Text statText = new Text(text);
		statText.setFont(Font.font("Arial", FontWeight.NORMAL, 24));
		statText.setFill(Color.WHITE);
		return statText;
	}
	
	/**
	 * Register handlers for all buttons on the start screen
	 */
	private void initHandlers() {
		// Notify the main window the user wants to change options
		mainMenu.setOnAction((event) -> {
			playSound(BUTTON_CLICK_SOUND, false); // Play sound for startButton click
			PropertyChangeEvent pce = new PropertyChangeEvent(this, AllProperties.SWITCH_SCREEN.property(), 
															AllProperties.RESET_GAME, AllProperties.BACK_TO_MENU);
			fireEvent(pce);
		});
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(AllProperties.GAME_STATS_READY.property())) {
			loadStatistics((GameplayResult) evt.getNewValue());
		}
	}

	private void loadStatistics(GameplayResult results) {
		String moves = String.format("Hits: %d", results.getHits());
		hitStats.setText(moves);
		
		String percentHits = String.format("Hit Ratio: %d", results.getPercentHits());
		ratioStats.setText(percentHits + "%");
		
		String sunkShips = String.format("Ships Sunk: %d", results.getShipsSunk());
		sunkStats.setText(sunkShips);
		
		long duration = results.getGameDuration();
		String gameDuration = String.format("Game Time: %d minutes, %d seconds", (int) (duration / 60), (int) (duration % 60));
		gameTimeStats.setText(gameDuration);
		
	}
}
