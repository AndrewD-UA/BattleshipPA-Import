package controller_view;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class GameMenuExample extends Application {
    private Scene mainScreen, screen1, screen2;

    @Override
    public void start(Stage primaryStage) {
        // Create buttons for each screen
        Button buttonToMain = new Button("Go to Main Screen");
        Button buttonToScreen1 = new Button("Go to Screen 1");
        Button buttonToScreen2 = new Button("Go to Screen 2");

        // Create text for each screen
        Text mainText = new Text("Welcome to the Main Screen!");
        Text screen1Text = new Text("Welcome to Screen 1!");
        Text screen2Text = new Text("Welcome to Screen 2!");

        // Layout for Screens using HBox
        HBox mainPane = new HBox(10); // 10 is the spacing between nodes
        mainPane.getChildren().addAll(mainText, buttonToScreen1, buttonToScreen2);
        
        HBox screen1Pane = new HBox(10);
        screen1Pane.getChildren().addAll(screen1Text, buttonToMain, buttonToScreen2);
        
        HBox screen2Pane = new HBox(10);
        screen2Pane.getChildren().addAll(screen2Text, buttonToMain, buttonToScreen1);

        // Create Scenes
        mainScreen = new Scene(mainPane, 300, 200);
        screen1 = new Scene(screen1Pane, 300, 200);
        screen2 = new Scene(screen2Pane, 300, 200);

        // Event handlers for buttons
        buttonToMain.setOnAction(event -> primaryStage.setScene(mainScreen));       
        buttonToScreen1.setOnAction(event -> primaryStage.setScene(screen1));  
        buttonToScreen2.setOnAction(event -> primaryStage.setScene(screen2));        
        
        // Set the initial screen
        primaryStage.setScene(mainScreen);
        primaryStage.setTitle("Game Menu Example");
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}