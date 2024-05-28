package model.animations;

import javafx.geometry.Point2D;
import javafx.scene.layout.BackgroundImage;
import javafx.util.Duration;

/**
 * A shell class to store details of an animation
 * 
 * @author Andrew Dennison
 */
public class HitResultAnimation{
	
	private Point2D activeCenterTile;
	private Duration duration;
	private BackgroundImage img;
	
	/**
	 * The duration of a HIT or MISS animation in milliseconds
	 */
	public static final long HIT_DURATION = 1500;
	
	/**
	 * Create a new animation at a given position with a given duration.
	 * @param centerTile	The x and y coordinates of the center of the animation
	 * @param duration		The length of the animation
	 * @param img			The image this animation will play
	 */
	public HitResultAnimation(Point2D centerTile, Duration duration, BackgroundImage img) {
		this.duration = duration;
		this.img = img;
		activeCenterTile = centerTile;
	}
	
	/**
	 * Get the tile this animation is centered on
	 * @return	Point2D with X and Y in range [0, 10)
	 */
	public Point2D getCenterTile() {
		return activeCenterTile;
	}
	
	/**
	 * Get the duration of this animation
	 * @return	The number of milliseconds this duration takes
	 */
	public Duration duration() {
		return duration;
	}
	
	/**
	 * Get the image this animation displays
	 * @return	The image to display
	 */
	public BackgroundImage getImage() {
		return img;
	}
}
