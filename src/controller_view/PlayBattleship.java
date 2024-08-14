package controller_view;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import controller_view.screens.GameOverScreen;
import controller_view.screens.GamePlayScreen;
import controller_view.screens.HelpScreen;
import controller_view.screens.OptionsScreen;
import controller_view.screens.ShipPickScreen;
import controller_view.screens.StartScreen;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import model.ai.Difficulties;

/**
 * The overaching container which runs the entire Battleship game
 * 
 * @author Andrew Dennison
 */
public class PlayBattleship extends Application implements PropertyChangeListener{
	
	/**
	 * When the game is launched, immediately start the JavaFX application
	 * @param args	Command line arguments
	 */
	public static void main(String[] args) {
		launch();
	}
	
	private StartScreen 	startScene;
	private ShipPickScreen 	shipPickScene;
	private OptionsScreen 	optionsScene;
	private GamePlayScreen 	gamePlayScene;
	private GameOverScreen 	gameOverScene;
	private HelpScreen 	   	helpScene;
	
	/**
	 * The stage on which all screens are displayed
	 */
	private Stage stage;
	
	/**
	 * Launch width in pixels
	 */
	private static final double LAUNCH_WIDTH = 	1000;
	
	/**
	 * Launch height in pixels
	 */
	private static final double LAUNCH_HEIGHT = 600;
	
	/**
	 * The currently active instance of the game
	 */
	private static PlayBattleship instance;
	
	/**
	 * The currently selected difficulty of the game
	 */
	private static Difficulties playerDifficulty;
	
	/**
	 * True if the game is in fullscreen mode, false otherwise
	 */
	private boolean isFullscreen;
	
	/**
	 * Current volume of all sound effects from 0.0 to 1.0
	 */
	private double soundSetting;
	
	/**
	 * A list of all MediaPlayers currently producing sound
	 */
	private ArrayList<MediaPlayer> activeMediaPlayers;
	
	/**
	 * True if a game is being played, false if the user is in the menu but not at the gameplay screen yet
	 */
	private boolean gameRunning = false;
				
	@Override
	public void start(Stage mainStage) throws Exception {
		instance = this;
		
		initSound();
		initScenes();
		
		stage = mainStage;
		stage.setScene(startScene);
		String imgPath = getClass().getResource("/img/patrolTDL.png").toExternalForm();
		stage.getIcons().add(new Image(imgPath));
		stage.setTitle("Battleship");
		stage.show();		
	}
	
	/**
	 * Create and register each Screen with the main application
	 */
	private void initScenes() {
		helpScene 		= (HelpScreen) 		getNewScreen("help");
		startScene 		= (StartScreen) 	getNewScreen("start");
		shipPickScene 	= (ShipPickScreen) 	getNewScreen("shipPicker");
		optionsScene 	= (OptionsScreen) 	getNewScreen("options");
		gamePlayScene 	= (GamePlayScreen) 	getNewScreen("gameplay");
		gameOverScene 	= (GameOverScreen) 	getNewScreen("gameover");

		shipPickScene.addListener(gamePlayScene);
		gamePlayScene.addListener(gameOverScene);
	}
	
	/**
	 * Implementation of the factory design pattern to create screens
	 * @param screen	String name of the screen to create
	 * @return			Pointer to the new SwitchableScreen
	 */
	private SwitchableScreen getNewScreen(String screen) {
		SwitchableScreen s;
		
		switch(screen) {
		case "start":
			s = new StartScreen(new BorderPane(), LAUNCH_WIDTH, LAUNCH_HEIGHT);		break;
		case "shipPicker":
			s =  new ShipPickScreen(new BorderPane(), LAUNCH_WIDTH, LAUNCH_HEIGHT);	break;
		case "options":
			s = new OptionsScreen(new BorderPane(), LAUNCH_WIDTH, LAUNCH_HEIGHT);	break;
		case "gameplay":
			s = new GamePlayScreen(new BorderPane(), LAUNCH_WIDTH, LAUNCH_HEIGHT);	break;
		case "gameover":
			s =  new GameOverScreen(new BorderPane(), LAUNCH_WIDTH, LAUNCH_HEIGHT);	break;
		case "help":
			s =  new HelpScreen(new BorderPane(), LAUNCH_WIDTH, LAUNCH_HEIGHT);	break;
		default:
			s = new StartScreen(new BorderPane(), LAUNCH_WIDTH, LAUNCH_HEIGHT);		break;
			
		}
		
		s.addListener(this);
		return s;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();
		
		// Logic for switching screens
		if (prop.equals(AllProperties.SWITCH_SCREEN.property())) {
			AllProperties goingTo = (AllProperties) evt.getNewValue();	
			AllProperties comingFrom = (AllProperties) evt.getOldValue();
			
			// Store the last screen if we are going to the options screen
			if (goingTo == AllProperties.OPEN_OPTIONS) {
				optionsScene.setReturn(comingFrom);
			}
			
			// Reset each scene if the gameplay loop is restarting
			if (comingFrom == AllProperties.RESET_GAME) {
				shipPickScene.reset();
				gamePlayScene.reset();
			}
			
			switchScreenTo(goingTo);
		} 
		
		// Logic for manipulating fullscreen mode
		if (prop.equals(AllProperties.SET_FULLSCREEN.property())) {
			System.out.println("received fullscreen");
			isFullscreen = !isFullscreen;
			System.out.println(isFullscreen);
			stage.setMaximized(isFullscreen);
			stage.setFullScreen(isFullscreen);
			if (!isFullscreen) {
				stage.setWidth(LAUNCH_WIDTH);
				stage.setHeight(LAUNCH_HEIGHT);
			}
		}
		
	    // Check if the property change event is for switching to the Help Screen
	    if (prop.equals(AllProperties.HELP_SCREEN.property())) {
	        switchScreenTo(AllProperties.HELP_SCREEN); // Switch to the Help Screen
	    }
	}
	
	/**
	 * Utility function to queue up a different stage than is currently on the screen.
	 * @param nextScreen	Enum representation of the screen to switch to.
	 */
	private void switchScreenTo(AllProperties nextScreen) {
		switch(nextScreen) {
			case OPEN_OPTIONS:	changeScene(optionsScene);		break;
			case BACK_TO_MENU: 	changeScene(startScene);		break;
			case START_GAME:	changeScene(shipPickScene);		break;
			case HELP_SCREEN:	changeScene(helpScene);			break;
			case DISPLAY_BOARD:
				if (!gameRunning) {
					gameRunning = true;
				}
				changeScene(gamePlayScene);
				break;
				
			case GAME_OVER:		changeScene(gameOverScene);		break;
			default:											break;
		}
	}
	
	/**
	 * Switch the currently loaded and displayed Scene.
	 * @param s	Scene to change to
	 */
	private void changeScene(Scene s) {
		stage.setScene(s);
		stage.setMaximized(isFullscreen);
		stage.setFullScreen(isFullscreen);
	}
	
	/**
	 * Initialize the game's sound.
	 */
	private void initSound() {
		soundSetting = 0.01;
		activeMediaPlayers = new ArrayList<MediaPlayer>();
	}
	
	/**
	 * Play a sound for this instance of Battleship.
	 * @param resource		String of the resource to play, generally in "/audio/file.mp3" format
	 * @param isInfinite	True if this should loop indefinitely, false otherwise
	 */
	public void playSound(String resource, boolean isInfinite) {
		Media m = new Media(getClass().getResource(resource).toExternalForm());
		MediaPlayer mp = new MediaPlayer(m);
		
		if (isInfinite) {
			mp.setOnEndOfMedia(()->{
				mp.seek(mp.getStartTime());
				mp.setVolume(soundSetting);
				mp.play();
			});
		} else {
			mp.setOnEndOfMedia(()->{
				activeMediaPlayers.remove(mp);
			});
		}
				
		mp.setVolume(soundSetting);
		activeMediaPlayers.add(mp);
		mp.play();
	}
	
	/**
	 * Get the current instance of the game, which is an implementation of the Singleton design pattern
	 * @return	The currently active instance of the game
	 */
	public static PlayBattleship getInstance() {
		return instance;
	}
	
	/**
	 * Get the current difficulty setting for the game
	 * @return	The currently active difficulty setting
	 */
	public Difficulties getDifficulty() {
		return playerDifficulty;
	}
	
	/**
	 * Set the sound level for all active media players in this instance of Battleshipo=
	 * @param newSoundLevel	New sound level from 0.0 to 1.0
	 */
	public void setSoundLevel(double newSoundLevel) {
		soundSetting = newSoundLevel;
		
		for (MediaPlayer mp : activeMediaPlayers) {
			mp.setVolume(soundSetting);
		}
	}
	
	/**
	 * Set the difficulty of the game
	 * @param d	The difficulty to set the game to
	 */
	public void setDifficulty(Difficulties d) {
		playerDifficulty = d;
	}

}
