package controller_view.board;

import java.util.ArrayList;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;
import model.animations.HitResultAnimation;

/**
 * A BorderPane which displays animations via replacing its background
 * 
 * @author Andrew Dennison
 */
public class BoardOverlay extends BorderPane {

	private ArrayList<HitResultAnimation> activeAnimations;
	private static final String HIT_PATH = "/img/boom.gif";
	private static final String MISS_PATH = "/img/miss.gif";
	private static final String MISS_ICON_PATH = "/img/miss.png";
	private static final String FIRE_PATH = "/img/fire.gif";
	private static final String SHIELD_PATH = "/img/bubble.png";
		
	private double cellSize;
	
	/**
	 * Create a new Board Overlay object, responsible for rendering animations on
	 * top of a board.
	 * 
	 * All BoardOverlays cannot be directly interacted with and will not generate
	 * mouse click events.
	 * @param size Size in pixels of one cell of the BoardContainer parent
	 */
	public BoardOverlay(double size) {
		super();
		cellSize = size;
		setPickOnBounds(false);
		activeAnimations = new ArrayList<HitResultAnimation>();
	}

	/**
	 * Tell the Overlay to play the appropriate hit or miss animation at a given
	 * position
	 * 
	 * @param pos       Point2D to play the animation at
	 * @param hitResult True for a hit animation, False for a miss animation
	 */
	public void playMoveAnimation(Point2D pos, int hitResult) {		
		Duration hitDuration = new Duration(1500);
		
		Timeline t = createNewTimeline(pos, hitDuration, hitResult);

		t.play();
	}

	private Timeline createNewTimeline(Point2D pos, Duration d, int moveType) {
		HitResultAnimation anim1;
		HitResultAnimation anim2;
		
		if (moveType == -1) {
			BackgroundImage bubbleImg = generateBackgroundImage(pos, SHIELD_PATH);
			anim1 = new HitResultAnimation(pos, Duration.INDEFINITE, bubbleImg);
			anim2 = null;

		} 
		else if (moveType == 1) {
			BackgroundImage boomImg = generateBackgroundImage(pos, HIT_PATH);
			anim1 = new HitResultAnimation(pos, d, boomImg);
			
			BackgroundImage fireImg = generateBackgroundImage(pos, FIRE_PATH);
			anim2 = new HitResultAnimation(pos, Duration.INDEFINITE, fireImg);
		} 
		else {
			BackgroundImage boomImg = generateBackgroundImage(pos, MISS_PATH);
			anim1 = new HitResultAnimation(pos, d, boomImg);
			BackgroundImage missImg = generateBackgroundImage(pos, MISS_ICON_PATH);
			anim2 = new HitResultAnimation(pos, Duration.INDEFINITE, missImg);
		}		
		
		activeAnimations.add(anim1);
		refreshOverlay();
		
		Timeline t = new Timeline(
				new KeyFrame(anim1.duration(), (e) -> {
					activeAnimations.remove(anim1);
					if (anim2 != null) {
						activeAnimations.add(anim2);
					}
					refreshOverlay();
				}));
		
		if (moveType == 1) {
			// Add a KeyFrame with infinite duration
			t.getKeyFrames().add(
					new KeyFrame(anim2.duration(), (e)->{}));
		} else {
			t.setCycleCount(1);
		}
		
		return t;
	}
	
	private BackgroundImage generateBackgroundImage(Point2D pos, String path) {
		Image img = new Image(getClass().getResource(path).toExternalForm());
		BackgroundRepeat br = BackgroundRepeat.NO_REPEAT;
		
		double xPos = pos.getX() * cellSize;
		double yPos = pos.getY() * cellSize;
		
		BackgroundPosition bgPosition = new BackgroundPosition(Side.LEFT, xPos, false, Side.TOP, yPos, false);
		BackgroundSize bgSize = new BackgroundSize(cellSize, cellSize, false, false, false, false);
		
		return new BackgroundImage (img, br, br, bgPosition, bgSize);
	}

	protected void refreshOverlay() {
		setBackground(null);

		BackgroundImage[] imgs = new BackgroundImage[activeAnimations.size()];
		int index = 0;

		for (HitResultAnimation hra : activeAnimations) {
			// Render each animation on the background
			imgs[index] = hra.getImage();
			index++;
		}

		setBackground(new Background(imgs));
	}
	
	/**
	 * Clear the Overlay
	 */
	protected void reset() {
		activeAnimations = new ArrayList<HitResultAnimation>();
		refreshOverlay();
	}
}
