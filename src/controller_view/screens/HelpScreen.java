package controller_view.screens;

import java.beans.PropertyChangeEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import controller_view.AllProperties;
import controller_view.SwitchableScreen;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/**
 * Help screen designed to show the game to users
 * 
 * @author Adam Fehse
 */
public class HelpScreen extends SwitchableScreen {
	
	private Button backButton = new Button("Go back");

	
	/**
	 * Create a new help screen with the given dimension
	 * @param root		The root of the window, normally new BorderPane()
	 * @param width		The width of the screen in pixels
	 * @param height	The height of the screen in pixels
	 */
	public HelpScreen(BorderPane root, double width, double height) {
        super(root, width, height, "/img/bo.jpg");

        StackPane centerPane = new StackPane();
        root.setCenter(centerPane);

        // Create a sequence of static images and GIFs
        initHandlers();
        playSequence(centerPane, width, height);
        
        // Add the back button to the bottom right corner
        BorderPane.setAlignment(backButton, Pos.BOTTOM_RIGHT);
        BorderPane.setMargin(backButton, new Insets(10)); // Adjust the margins as needed
        root.setBottom(backButton);
    }

    private void playSequence(StackPane centerPane, double width, double height) {
        // Create a Timeline to control the sequence
        Timeline sequenceTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0), e -> {
                    // Display the first static image
                    displayStaticImage(centerPane, width, height, "src/img2/welcomeHelp.png");
                }),
                new KeyFrame(Duration.seconds(5), e -> {
                    // Display the first GIF animation after 3 seconds
                    displayGifAnimation(centerPane, width, height, "src/img2/helpRules.png");
                }),
                
                new KeyFrame(Duration.seconds(10), e -> {
                    // Display the second static image after 6 seconds
                    displayStaticImage(centerPane, width, height, "src/img2/patrolHelp.png");
                }),
                new KeyFrame(Duration.seconds(13), e -> {
                    // Display the second static image after 6 seconds
                    displayStaticImage(centerPane, width, height, "src/img2/subHelp.png");
                }),
                new KeyFrame(Duration.seconds(16), e -> {
                    // Display the second static image after 6 seconds
                    displayStaticImage(centerPane, width, height, "src/img2/destroyerHELP.png");
                }),
                new KeyFrame(Duration.seconds(19), e -> {
                    // Display the second static image after 6 seconds
                    displayStaticImage(centerPane, width, height, "src/img2/battleShipHELP.png");
                }),
                new KeyFrame(Duration.seconds(22), e -> {
                    // Display the second static image after 6 seconds
                    displayStaticImage(centerPane, width, height, "src/img2/carrHELP.png");
                }),
                
                new KeyFrame(Duration.seconds(25), e -> {
                    // Display the second static image after 6 seconds
                    displayStaticImage(centerPane, width, height, "src/img2/hitHelp.png");
                }),
                new KeyFrame(Duration.seconds(27), e -> {
                    // Display the second static image after 6 seconds
                    displayStaticImage(centerPane, width, height, "src/img/boom.gif");
                }),
                new KeyFrame(Duration.seconds(29), e -> {
                    // Display the second GIF animation after 9 seconds
                    displayGifAnimation(centerPane, width, height, "src/img2/MissHelp.png");
                }),
                new KeyFrame(Duration.seconds(31), e -> {
                    // Display the second static image after 6 seconds
                    displayStaticImage(centerPane, width, height, "src/img/miss.gif");
                }),
                new KeyFrame(Duration.seconds(33), e -> {
                    // Display the first static image
                    displayStaticImage(centerPane, width, height, "src/img2/spec1.png");
                }),
                new KeyFrame(Duration.seconds(35), e -> {
                    // Display the first static image
                    displayStaticImage(centerPane, width, height, "src/img2/nuke.gif");
                }),
                new KeyFrame(Duration.seconds(38), e -> {
                    // Display the first static image
                    displayStaticImage(centerPane, width, height, "src/img2/spec2.png");
                }),
                new KeyFrame(Duration.seconds(41), e -> {
                    // Display the first static image
                    displayStaticImage(centerPane, width, height, "src/img2/strafing.gif");
                }),
                new KeyFrame(Duration.seconds(43), e -> {
                    // Display the first static image
                    displayStaticImage(centerPane, width, height, "src/img2/spec3.png");
                }),
                new KeyFrame(Duration.seconds(46), e -> {
                    // Display the first static image
                    displayStaticImage(centerPane, width, height, "src/img2/revealHelp.gif");
                }),
                new KeyFrame(Duration.seconds(49), e -> {
                    // Display the first static image
                    displayStaticImage(centerPane, width, height, "src/img2/spec5.png");
                }),
                new KeyFrame(Duration.seconds(52), e -> {
                    // Display the first static image
                    displayStaticImage(centerPane, width, height, "src/img2/shieldHelp.gif");
                }),
                new KeyFrame(Duration.seconds(54), e -> {
                    // Display the first static image
                    displayStaticImage(centerPane, width, height, "src/img2/endHelp.png");
                })
        );

        sequenceTimeline.play();
    }

    private void displayStaticImage(StackPane centerPane, double width, double height, String imagePath) {
        try {
            ImageView staticImageView = new ImageView(new Image(new FileInputStream(imagePath)));
            staticImageView.setFitWidth(width * 0.8);
            staticImageView.setFitHeight(height * 0.8);
            centerPane.getChildren().setAll(staticImageView);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    private void displayGifAnimation(StackPane centerPane, double width, double height, String gifPath) {
        try {
            ImageView gifImageView = new ImageView(new Image(new FileInputStream(gifPath)));
            gifImageView.setFitWidth(width * 0.8);
            gifImageView.setFitHeight(height * 0.8);
            centerPane.getChildren().setAll(gifImageView);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }
    
    private void initHandlers() {
		// Notify the main window the user wants to go back to the menu
		backButton.setOnAction((event) -> {
			playSound(BUTTON_CLICK_SOUND, false);
			PropertyChangeEvent pce = new PropertyChangeEvent(this, AllProperties.SWITCH_SCREEN.property(), AllProperties.RESET_GAME, AllProperties.OPEN_OPTIONS);
			fireEvent(pce);
		});

	}
}
