package controller_view.screens;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import controller_view.AllProperties;
import controller_view.PlayBattleship;
import controller_view.SwitchableScreen;
import controller_view.board.BoardContainer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import model.ai.Difficulties;
import model.battleship.Ship;
import model.battleship.ShipCollection;
import model.board.Board;
import model.board.Direction;

/**
 * The screen used to select the difficulty and place ships onto the board
 * 
 * @author Andrew Dennison
 */
public class ShipPickScreen extends SwitchableScreen implements PropertyChangeListener {

	// The various sub-components within the ShipPickScreen
	private GridPane difficultyButtons;
	private GridPane shipButtons;
	private BoardContainer boardView;
	
	private Label instructions;

	private int prefButtonWidth;

	private static final Insets DIFFICULTY_INSETS = new Insets(0, 0, 0, 20);
	private static final Insets SHIPS_INSETS = new Insets(0, 20, 0, 0);
	private static final Insets INSTRUCTIONS_INSETS = new Insets(10, 0, 0, 0);
	protected static final Font INSTRUCTIONS_FONT = Font.font("Arial", FontWeight.BOLD, 30);

	
	private Direction currentDirection = Direction.RIGHT;
	private ToggleGroup currentShipSelector;
	private ToggleGroup currentDifficultySelector;

	/**
	 * Create a new ShipPickerScreen
	 * 
	 * @param root   The root of this screen
	 * @param width  The width in pixels of this screen
	 * @param height The height in pixels of this screen
	 */
	public ShipPickScreen(BorderPane root, double width, double height) {
		super(root, width, height, "/img/west.jpg");

		initBoardPicker();
		initDifficultySelector();
		initShipSelector();
		initDisplay();
		initEvents();
		initEventHandlers();
	}
	
	private void initEventHandlers() {
		getRoot().setOnKeyReleased((e)->{
			if (e.getCode() == KeyCode.ESCAPE) {
				fireEvent(new PropertyChangeEvent(this, AllProperties.SWITCH_SCREEN.property(), AllProperties.START_GAME, AllProperties.OPEN_OPTIONS));
			}
		});
	}

	/**
	 * Create the remainder of the display.
	 * 
	 * The remainder encompasses the button to begin at the bottom, as well as the
	 * instructions.
	 */
	private void initDisplay() {
		Button next = new Button("I'm ready to fight!");

		// startButton
		next.setStyle(CSS.NEXT_DEFAULT);
		next.setOnMouseEntered(e -> {
			next.setStyle(CSS.NEXT_ENTERED);
		});
		next.setOnMouseExited(e -> {
			next.setStyle(CSS.NEXT_DEFAULT);
		});

		next.setOnAction((event) -> {
			playSound(BUTTON_CLICK_SOUND, false);
			if (boardView.isBoardFull() && currentDifficultySelector.getSelectedToggle() != null) {
				startGame();
			}
			if (boardView.isBoardFull() == false || currentDifficultySelector.getSelectedToggle() == null) {
				highlightInstructions();
			}

		});
		next.setFont(StartScreen.BUTTON_FONT);
		BorderPane.setAlignment(next, Pos.CENTER);
		window.setBottom(next);

		instructions = new Label("Press 'r' to rotate and click to place.\nPlace five ships and select a difficulty!");
		instructions.setFont(INSTRUCTIONS_FONT);
		BorderPane.setMargin(instructions, INSTRUCTIONS_INSETS);
		BorderPane.setAlignment(instructions, Pos.CENTER);
		window.setTop(instructions);
	}

	/**
	 * Called when all ships have been placed and the difficulty selected
	 */
	private void startGame() {
		DifficultyButton selected = (DifficultyButton) currentDifficultySelector.getSelectedToggle();
		PlayBattleship.getInstance().setDifficulty(selected.difficulty);

		fireEvent(new PropertyChangeEvent(this, AllProperties.SWITCH_SCREEN.property(), null,
				AllProperties.DISPLAY_BOARD));
		fireEvent(
				new PropertyChangeEvent(this, AllProperties.HUMAN_BOARD_READY.property(), null, boardView.getBoard()));
		Board b = new Board();
		b.setShipRendering(false);
		fireEvent(new PropertyChangeEvent(this, AllProperties.AI_BOARD_READY.property(), null, b));

	}

	/**
	 * Initialize all events not tied to a particular child of this Screen
	 */
	private void initEvents() {
		this.setOnKeyPressed((event) -> {
			// Rotate key
			if (event.getCode().equals(KeyCode.R)) {
				currentDirection = currentDirection.rotateRight();
			}

			// Auto-populate ships with default
			if (event.getCode().equals(KeyCode.P)) {
				boardView.setBoard(new Board(
						new ShipCollection(new Ship[] { new Ship(new Point2D(0, 0), Direction.RIGHT, 2, false),
								new Ship(new Point2D(0, 1), Direction.RIGHT, 3, false),
								new Ship(new Point2D(0, 2), Direction.RIGHT, 3, true),
								new Ship(new Point2D(0, 3), Direction.RIGHT, 4, false),
								new Ship(new Point2D(0, 4), Direction.RIGHT, 5, false) })));
				startGame();
			}
		});
	}

	/**
	 * Initialize the difficulyt selector menu
	 */
	private void initDifficultySelector() {
		currentDifficultySelector = new ToggleGroup();
		prefButtonWidth = (int) getWidth() / 5;

		difficultyButtons = new GridPane();
		difficultyButtons.setAlignment(Pos.CENTER_LEFT);
		difficultyButtons.setVgap(10);

		int counter = 0;
		for (Difficulties d : Difficulties.values()) {
			DifficultyButton db = new DifficultyButton(d);
			db.setToggleGroup(currentDifficultySelector);
			difficultyButtons.add(db, 0, counter);
			counter++;

			db.setPadding(new Insets(5));
		}

		// difficultyButtons.setBackground(new
		// Background(CSS.TOGGLE_BUTTON_BACKGROUND));
		BorderPane.setMargin(difficultyButtons, DIFFICULTY_INSETS);

		window.setLeft(difficultyButtons);
	}

	/**
	 * Create and load the panel of buttons to select the currently placing ship.
	 * 
	 * Load the right-most pane of buttons, which allows the user to select between
	 * the five kinds of ship. This selection determines which ship is placed when
	 * the user clicks on a square.
	 */
	private void initShipSelector() {
		currentShipSelector = new ToggleGroup();
		shipButtons = new GridPane();
		shipButtons.setVgap(10);
		shipButtons.setAlignment(Pos.CENTER_RIGHT);

		// Create all ship selector buttons
		ShipButton[] allButtons = new ShipButton[] { new ShipButton("Patrol", 2), new ShipButton("Submarine", 3),
				new ShipButton("Destroyer", 3), new ShipButton("Battleship", 4), new ShipButton("Carrier", 5) };

		// Add them all to their appropriate positions in the GridPane
		for (int i = 0; i < allButtons.length; i++) {
			shipButtons.add(allButtons[i], 0, i);
			allButtons[i].setToggleGroup(currentShipSelector);
			allButtons[i].setPadding(new Insets(5));
		}

		// shipButtons.setBackground(new Background(CSS.TOGGLE_BUTTON_BACKGROUND));
		BorderPane.setMargin(shipButtons, SHIPS_INSETS);

		window.setRight(shipButtons);
	}

	/**
	 * Create and load a new BoardViewer into the center of the screen
	 */
	private void initBoardPicker() {
		boardView = new BoardContainer(getHeight() / 1.5, true, true);
		boardView.addListener(this);
		window.setCenter(boardView);
	}

	/**
	 * Private inner class detailing how each Difficulty selector button is
	 * constructed.
	 * 
	 * @author Andrew Dennison, Adam Fehse
	 */
	public class DifficultyButton extends ToggleButton {

		private static final Font BUTTON_FONT = Font.font(25);
		private Difficulties difficulty;

		/**
		 * Create a new button used to select difficulty
		 * 
		 * @param difficulty The difficulty associated with this button
		 */
		public DifficultyButton(Difficulties difficulty) {
			super(difficulty.toString());
			this.setFont(BUTTON_FONT);
			this.setPrefWidth(prefButtonWidth);
			this.difficulty = difficulty;
			updateButtonStyle();
			this.setOnAction(event -> {
				playSound(BUTTON_CLICK_SOUND, false);
				updateButtonStyle();
			});
			this.setOnMouseEntered(event -> {
				if (!isSelected()) {
					setStyle(CSS.SHIP_PICKER_ENTERED);
				}
			});
			this.setOnMouseExited(event -> {
				if (!isSelected()) {
					setStyle(CSS.SHIP_PICKER_DEFAULT);
				}
			});
			selectedProperty().addListener((obs, oldVal, newVal) -> {
				if (newVal) {
					setStyle(CSS.SHIP_PICKER_SELECTED);
				} else {
					if (isHover()) {
						setStyle(CSS.SHIP_PICKER_ENTERED);
					} else {
						setStyle(CSS.SHIP_PICKER_DEFAULT);
					}
				}
			});
		}

		private void updateButtonStyle() {
			if (isSelected()) {
				setStyle(CSS.SHIP_PICKER_SELECTED);
			} else {
				setStyle(CSS.SHIP_PICKER_DEFAULT);
			}
		}
	}

	/**
	 * Private inner class detailing how each ship selector button is created
	 * 
	 * @author Andrew Dennison, Adam Fehse
	 */
	public class ShipButton extends ToggleButton {

		private int length;
		private boolean isUsed;

		/**
		 * Create a new button used to select which ship should be placed
		 * 
		 * @param name   The text to display on this button
		 * @param length The length of the ship associated with this button
		 */
		public ShipButton(String name, int length) {
			super(name);
			this.length = length;
			this.isUsed = false;

			setFont(new Font(20));
			setAlignment(Pos.CENTER);
			setPrefWidth(prefButtonWidth);
			BorderPane.setAlignment(this, Pos.CENTER);
			updateButtonStyle();

			selectedProperty().addListener((obs, oldVal, newVal) -> {
				if (isUsed) {
					return;
				}

				if (newVal) {
					setStyle(CSS.SHIP_PICKER_SELECTED);
				} else {
					setStyle(CSS.SHIP_PICKER_DEFAULT);
				}
			});

			setOnAction(event -> {
				if (isUsed) {
					boardView.removeFromBoard(name);
					setUsed(false);
				}
				
				playSound(BUTTON_CLICK_SOUND, false);
			});
			
			setOnMouseEntered(e -> {
				if (!isSelected() && !isUsed) {
					setStyle(CSS.SHIP_PICKER_ENTERED);
				}
			});

			setOnMouseExited(e -> {
				if (!isSelected() && !isUsed) {
					setStyle(CSS.SHIP_PICKER_DEFAULT);
				}
			});
		}
		
		
		/**
		 * Mark that this ship has been placed
		 * @param used	The value to set used to
		 */
		public void setUsed(boolean used) {
			isUsed = used;
			setStyle(CSS.END_BUTTON_SELECTED);
			Math.abs(3);
		}
		
		/**
		 * Reset this button when the game is restarted
		 */
		public void reset() {
			isUsed = false;
			setStyle(CSS.SHIP_PICKER_DEFAULT);
		}

		/**
		 * Get the length of the Ship associated with this button
		 * 
		 * @return The ship's length
		 */
		public int getShipLength() {
			return length;
		}

		private void updateButtonStyle() {
			if (isSelected()) {
				setStyle(CSS.SHIP_PICKER_SELECTED);
			} else {
				setStyle(CSS.SHIP_PICKER_DEFAULT);
			}
		}
	}
	
	

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// Handler for when the BoardViewer has been clicked
		if (evt.getPropertyName().equals(AllProperties.BOARD_CLICK.property())) {
			playSound(BUTTON_CLICK_SOUND, false);
			
			Ship s = getCurrentShipFromSelector((Point2D) evt.getNewValue());
			boolean added = boardView.addShipToBoard(s);

			if (added) {
				((ShipButton) currentShipSelector.getSelectedToggle()).setUsed(true);;
			} else {
				highlightInstructions();				
			}
		}
		
		else if (evt.getPropertyName().equals(AllProperties.MOUSE_OVER_EVENT.property())) {
			renderShipPreview((Point2D) evt.getNewValue());
		}
	}
	
	private void renderShipPreview(Point2D pos) {
		Ship s = getCurrentShipFromSelector(pos);
		if (s == null) {
			return;
		}
		boardView.renderPreviewAt(s);
	}
	
	private Ship getCurrentShipFromSelector(Point2D pos) {
		ShipButton currentShip = (ShipButton) currentShipSelector.getSelectedToggle();
		if (currentShip == null || currentShip.isUsed) {
			return null;
		}

		int length = currentShip.getShipLength();
		boolean isSub = currentShip.getText().equalsIgnoreCase("submarine");
				
		return new Ship((Point2D) pos, currentDirection, length, isSub);
	}

	private void highlightInstructions() {
		BackgroundFill bf = new BackgroundFill(Color.INDIANRED, CornerRadii.EMPTY, Insets.EMPTY);
		instructions.setBackground(new Background(bf));
		
		Timeline t = new Timeline(new KeyFrame(Duration.millis(1500), (e)->{
			instructions.setBackground(null);
		}));
		
		t.setCycleCount(1);
		t.play();
	}

	/**
	 * Reset the ShipPickerScreen
	 */
	public void reset() {
		boardView.setBoard(new Board(new ShipCollection()));
		currentDifficultySelector.selectToggle(null);
		
		// Reset the ship picker buttons
		for (Toggle t : currentShipSelector.getToggles()) {
			ShipButton sb = (ShipButton) t;
			sb.reset();
		}
	}
}
