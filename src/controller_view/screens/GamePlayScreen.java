package controller_view.screens;

import java.beans.PropertyChangeEvent;

import controller_view.AllProperties;
import controller_view.PlayBattleship;
import controller_view.SwitchableScreen;
import controller_view.board.BoardContainer;
import controller_view.board.BoardViewer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import model.ai.BattleshipAI;
import model.ai.Difficulties;
import model.ai.EasyAI;
import model.ai.ExtremeAI;
import model.ai.HardAI;
import model.ai.ModerateAI;
import model.animations.HitResultAnimation;
import model.board.Board;
import model.board.GameplayRecord;

/**
 * The screen used to actually play the game and enter moves.
 * 
 * Background image credit to lachetas on Freepik
 * <a href="https://www.freepik.com/free-photo/sea-seen-from_977581.htm#query=ocean%20top%20down&position=15&from_view=keyword&track=ais&uuid=0256255b-9a43-4074-a9b7-edd4256a5199">Image by lachetas</a> on Freepik
 * 
 * @author Andrew, Adam, Rene, Tom
 */
public class GamePlayScreen extends SwitchableScreen {

	// Path to the sound file
	private static final String SR_SOUND = "/audio/special.mp3";
	private static final String NUKE_SOUND = "/audio/nuke.mp3";
	private static final String REVEAL_SOUND = "/audio/reveal.mp3";
	private static final String SHIELD_SOUND = "/audio/shield.mp3";
	private static final String SC_SOUND = "/audio/second-chance.mp3";
	
	private Label specialLabel;
	
	private BoardContainer humanBoard;
	private BoardContainer aiBoard;
	private BattleshipAI currentAI;
	private Difficulties difficulty;

	private boolean isWaitingToMove;

	private ToggleGroup specialToggle = new ToggleGroup();

	/**
	 * Create a new gameplay screen
	 * 
	 * @param root   The root of this screen
	 * @param width  The width of the screen in pixels
	 * @param height The height of the screen in pixels
	 */
	public GamePlayScreen(BorderPane root, double width, double height) {
		super(root, width, height, "/img/west.jpg");

		root.setCenter(initBoards());
		root.setBottom(initStatusPane());
		
		initHandlers();
	}
	
	/**
	 * Add the ability to access the options screen
	 */
	private void initHandlers() {
		getRoot().setOnKeyReleased((e)->{
			if (e.getCode() == KeyCode.ESCAPE) {
				fireEvent(new PropertyChangeEvent(this, AllProperties.SWITCH_SCREEN.property(), AllProperties.DISPLAY_BOARD, AllProperties.OPEN_OPTIONS));
			}
			
			else if (e.getCode() == KeyCode.E) {
				GameplayRecord gr= new GameplayRecord(17, 100.00, 5, 1);
				fireEvent(new PropertyChangeEvent(this, AllProperties.GAME_STATS_READY.property(), null, gr));
				fireEvent(new PropertyChangeEvent(this, AllProperties.SWITCH_SCREEN.property(), null,
						AllProperties.GAME_OVER));
			}
			
			else if (e.getCode() == KeyCode.F) {
				aiBoard.getBoard().cheatWin();
			}
		});
	}

	private FlowPane initBoards() {
		// flowpane container for boards with a border
		FlowPane boardsContainer = createBorderedContainer();
		double boardHeight = this.getHeight() / 1.5;
		
		BorderPane human = new BorderPane();
		BorderPane ai = new BorderPane();
		humanBoard = new BoardContainer(boardHeight, false, false);
		aiBoard = new BoardContainer(boardHeight, true, false);
		
		human.setTop(generateLabel("Your Board"));
		human.setCenter(humanBoard);
		
		ai.setTop(generateLabel("Opponent board"));
		ai.setCenter(aiBoard);
		
		boardsContainer.getChildren().addAll(human, ai);
		boardsContainer.setOrientation(Orientation.HORIZONTAL);
		
		BorderPane.setAlignment(boardsContainer, Pos.CENTER);
		return boardsContainer;
	}
	
	private Label generateLabel(String message) {
		Label l = new Label(message);
		
		l.setFont(OptionsScreen.OPTIONS_FONT);
		BorderPane.setAlignment(l, Pos.CENTER);
		
		return l;
	}

	private BorderPane initStatusPane() {
		BorderPane bp = new BorderPane();
		// flowpane container for labels with a border
		specialLabel = createLabel("Special Ability: 0%", 30, Color.ORANGE, FontWeight.BLACK);
		
		// add hover effect to Special Ability label
		specialLabel.setOnMouseEntered(event -> {
			specialLabel.setEffect(new DropShadow(10, Color.GOLD));
		});

		specialLabel.setOnMouseExited(event -> {
			specialLabel.setEffect(null); // restore off hover
		});
		
		specialLabel.setOnMouseReleased((event)->{
			specialToggle.selectToggle(null);
		});

		
		bp.setTop(specialLabel);
		bp.setBottom(initSpecialAbilities(specialLabel));		

		BorderPane.setAlignment(bp, Pos.CENTER);
		return bp;
	}
	
	private FlowPane initSpecialAbilities(Label l) {
		FlowPane labelsBox = createBorderedContainer();

		// Add all powerup buttons
		RadioButton special1 = addSpecialButton("Nuke");
		RadioButton special2 = addSpecialButton("Strafing Run");
		RadioButton special3 = addSpecialButton("Reveal");
		RadioButton special4 = addSpecialButton("Second Chance");
		RadioButton special5 = addSpecialButton("Shield");
		labelsBox.getChildren().addAll(special1, special2, special3, special4, special5);
		specialToggle.getToggles().addAll(special1, special2, special3, special4, special5);
		labelsBox.setPadding(new Insets(10));
		labelsBox.setOrientation(Orientation.HORIZONTAL);
		
		return labelsBox;
	}

	private RadioButton addSpecialButton(String title) {
		RadioButton b = new RadioButton(title);
		b.setPrefWidth(150);
		b.setAlignment(Pos.CENTER);
		b.setFont(Font.font("Arial", FontWeight.BOLD, 16));
		b.setStyle(CSS.SPECIAL_BUTTON);
		b.setOnMouseEntered(event -> {
			b.setEffect(new DropShadow());
		});
		return b;
	}
	
	/**
	 * Create a new centered flowpane with borders
	 * 
	 * @return Reference to a flowpane with all black border
	 */
	private FlowPane createBorderedContainer() {
		FlowPane container = new FlowPane();
		container.setAlignment(Pos.CENTER);
		container.setVgap(20);
		container.setHgap(20);

		container.setBorder(new Border(new javafx.scene.layout.BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID,
				CornerRadii.EMPTY, new BorderWidths(2))));
		return container;
	}

	/**
	 * Create a new Label
	 * 
	 * @param text      Text to display in the label
	 * @param fontSize  Size of the Font in the label
	 * @param textColor Color of the text in the label
	 * @param extraBold FontWeight applied to Label
	 * @return A reference to the new Label
	 */
	private Label createLabel(String text, double fontSize, Color textColor, FontWeight extraBold) {
		Label label = new Label(text);
		label.setFont(Font.font("Arial", FontWeight.BOLD, fontSize));
		label.setTextFill(textColor);
		return label;
	}

	/**
	 * Called to start the gameplay loop
	 * @param b	The board belonging to the player
	 */
	public void startGame(Board b) {
		difficulty = PlayBattleship.getInstance().getDifficulty();

		if (difficulty == Difficulties.EASY) {
			currentAI = new EasyAI();
		} else if (difficulty == Difficulties.MEDIUM) {
			currentAI = new ModerateAI();
		} else if (difficulty == Difficulties.HARD) {
			currentAI = new HardAI(b);
		} else {
			System.out.println("starting extreme");
			currentAI = new ExtremeAI(b);
		}

		aiBoard.notifyStarted();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		
		// Read in the player's board once it's been offered
		if (evt.getPropertyName().equals(AllProperties.HUMAN_BOARD_READY.property())) {
			Board b = (Board) evt.getNewValue();
			humanBoard.setBoard(b);
			startGame(b);
			humanBoard.addListener(this);
		}

		// Read in the AI's board once it's been offered
		if (evt.getPropertyName().equals(AllProperties.AI_BOARD_READY.property())) {
			Board b = (Board) evt.getNewValue();
			aiBoard.setBoard(b);
			b.startStats();
			aiBoard.addListener(this);
		}

		// If a board throws the end of the game notification, switch screens
		if (evt.getPropertyName().equals(AllProperties.GAME_OVER.property())) {
			fireEvent(new PropertyChangeEvent(this, AllProperties.SWITCH_SCREEN.property(), null,
					AllProperties.GAME_OVER));
		}

		// If a player clicks on a board
		if (evt.getPropertyName().equals(AllProperties.BOARD_CLICK.property())) {
			if (isWaitingToMove) {
				return;
			}
			
			boolean aiBoardClicked = aiBoard.containsBoardViewer((BoardViewer) evt.getSource());
			handleHumanMove((Point2D) evt.getNewValue(), aiBoardClicked);			
		}
		
		// If the board updates the progress of the special ability meter
		if (evt.getPropertyName().equals(AllProperties.UPDATE_SPECIAL.property())) {
			// Graphical updates to a JavaFX application must be done from within the application
			// The UPDATE_SPECIAL notification is run from a separate thread, so here we use
			// Platform.runLater to run the setting of the text from within the JavaFX application
			Platform.runLater(()->{
				String text = String.format("Special Ability: %d", (int) evt.getNewValue());
				specialLabel.setText(text+ "%");
			});	
		}
		
		if (evt.getPropertyName().equals(AllProperties.GAME_STATS_READY.property())) {
			fireEvent(new PropertyChangeEvent(this, AllProperties.GAME_STATS_READY.property(), null, evt.getNewValue()));
		}
	}
		
	private void handleHumanMove(Point2D playerMove, boolean onAIBoard) {
		isWaitingToMove = true;
		
		RadioButton specialButton = (RadioButton) specialToggle.getSelectedToggle();
		if (specialButton != null) {
			String text = specialButton.getText().toLowerCase();
			
			// If we're placing a shield, place it on the human's board
			if (text.equals("shield")) {
				// If the player placed a shield on the AI's board, skip the turn
				if (onAIBoard) {
					endTurn();
					return;
				}
				
				// If a shield move is valid, then make it
				// The player's special ability counter is stored on the ai's board.
				if (aiBoard.checkStatusOfSpecial()) {
					humanBoard.shieldMove(playerMove);
					playSound(SHIELD_SOUND, false);
				}
				
				endTurn();
				return;
			}
			
			else if(aiBoard.attemptSpecialMove(text, playerMove)) {
				playSpecialSound(text);
				if (text.equals("second chance")) {
					endTurn();
					return;
				}
			}
		}
		
		// The player clicked on their own board and it wasn't a shield move
		if (!onAIBoard) {
			endTurn();
			return;
		}
		
		aiBoard.makeMoveOnBoard(playerMove);

		// Make a timeline that runs for 1.5 seconds and calls the AI move afterwards
		Timeline t = new Timeline(new KeyFrame(new Duration(HitResultAnimation.HIT_DURATION), (e) -> {
			Point2D aiMove = currentAI.nextMove();						
			currentAI.registerHit(aiMove, humanBoard.makeMoveOnBoard(aiMove));
			endTurn();
		}));

		t.setCycleCount(1);
		t.play();
	}
	
	private void playSpecialSound(String text) {
		switch(text) {
		case "second chance": playSound(SC_SOUND, false);	break;
		case "nuke": playSound(NUKE_SOUND, false); 			break;
		case "strafing run": playSound(SR_SOUND, false); 	break;
		case "reveal": playSound(REVEAL_SOUND, false);		break;
		}
		specialToggle.selectToggle(null);
	}
	
	private void endTurn() {
		isWaitingToMove = false;
		specialToggle.selectToggle(null);
	}

	/**
	 * Reset both BoardContainers on the screen
	 */
	public void reset() {
		currentAI = null;
		aiBoard.reset();
		humanBoard.reset();
	}

}
