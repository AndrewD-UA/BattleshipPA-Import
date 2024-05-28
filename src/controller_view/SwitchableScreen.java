package controller_view;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Parent class of all Scenes in the Battleship game.
 * 
 * This class provides a template for all scenes to be able to notify 
 * the main window of changes the user makes.
 * 
 * @author Andrew Dennison
 */
public abstract class SwitchableScreen extends Scene implements PropertyChangeListener{
	
	protected BorderPane window;
	protected PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	protected static final String BUTTON_CLICK_SOUND = "/audio/buttonClick.mp3";
	
	/**
	 * A large font, bold font styling
	 */
	protected static final Font TITLE_FONT = Font.font("Arial", FontWeight.BOLD, 60);
	
	/**
	 * Smaller than the title font, but larger than any individual elements should be
	 */
	protected static final Font BUTTON_FONT = Font.font("Arial", FontWeight.BOLD, 40);
		
	/**
	 * Create a new SwitchableScreen of a particular size
	 * @param root					The root node of this scene
	 * @param width					The width of the scene
	 * @param height				The height of the scene
	 * @param backgroundResource	The path to the resource containing the background image for this screen
	 */
	public SwitchableScreen(BorderPane root, double width, double height, String backgroundResource) {
		super(root, width, height);
		window = root;
		setBackground(backgroundResource);
	}
	
	/**
	 * Fire an event for this scene
	 * @param pce	Event to fire
	 */
	protected void fireEvent(PropertyChangeEvent pce) {
		pcs.firePropertyChange(pce);
	}
	
	/**
	 * Add a listener to this scene
	 * @param pcl	The listener to add
	 */
	public void addListener(PropertyChangeListener pcl) {
		pcs.addPropertyChangeListener(pcl);
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// Shell function, which does nothing
	}
	
	protected void playSound(String resource, boolean isInfinite) {
		PlayBattleship.getInstance().playSound(resource, isInfinite);
	}
	
	protected void setBackground(String resource) {
		Image backgroundImage = new Image(getClass().getResource(resource).toExternalForm());
		BackgroundSize bs = new BackgroundSize(100.0, 100.0, true, true, true, true);
		BackgroundRepeat br = BackgroundRepeat.NO_REPEAT;
		BackgroundPosition bp = BackgroundPosition.CENTER;
		
		BackgroundImage background = new BackgroundImage(backgroundImage, br, br, bp, bs);
		window.setBackground(new Background(background));
	}	
}
