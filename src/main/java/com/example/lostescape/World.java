package com.example.lostescape;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import com.example.lostescape.Scene.LoginScene;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class World extends Application {

    Pane root;
    public static final double tileWidth = 57.1;
    public static final double tileHeight = 56.2;
    LinkedList<ObstacleTile> barrier;
    Character character;
    Text timerDisplay;
    Timer timer;
    Stage mainStage;
    private MusicManager musicManager = new MusicManager(); // Initialize musicManager here

    @Override
    public void start(Stage stage) {
        // First show login screen
        musicManager = new MusicManager();
        mainStage = stage;

        stage.setScene(new LoginScene().getLoginScene(stage));
        stage.setTitle("Login");
        stage.show();
    }

    public void startGame(Stage stage) {
        root = new Pane();
        Scene scene = new Scene(root, 1000, 562);
        stage.setTitle("Starting Menu");
        stage.setScene(scene);
        stage.show();

        new Thread(() -> musicManager.playBackgroundMusic("background.mp3")).start();

        // Set starting screen background image
        Image startBackground = new Image("menu.jpg");
        BackgroundSize size = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, false);
        Background background = new Background(new BackgroundImage(startBackground, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, size));
        root.setBackground(background);

        // Dialog Text box - Shows when help button is clicked
        ImageView dialog = new ImageView(new Image("button_image.png"));
        dialog.setLayoutX(0);
        dialog.setLayoutY(400);
        dialog.setFitHeight(750);
        dialog.setFitWidth(750);
        dialog.setPreserveRatio(true);
        dialog.setOpacity(0);
        root.getChildren().add(dialog);

        // Dialog Text - Shows when help button is clicked
        Text dialog_text = new Text(125, 525, "The object of the game is to escape the room. The player must\n"
                + "obtain the key in order to exit the room. Use the arrow keys to\n"
                + "move the player around. Look around the objects of the room and\n"
                + "press the space bar to check if the key is there. Once the key is\n"
                + "obtained, the player may exit the room via the stairs.");
        dialog_text.setFont(Font.font("Verdana", 15));
        dialog_text.setOpacity(0);
        root.getChildren().add(dialog_text);

        // Create Menu Buttons
        createButton("Start", 0, e -> loadGame(stage));
        createButton("Help", 1, e -> {
            dialog.setOpacity(1);
            dialog_text.setOpacity(1);
        });
        createButton("Exit", 2, e -> Platform.exit());
    }

    public void createButton(String n, int pos, EventHandler<ActionEvent> e) {
        Button btn = new Button(n);
        btn.setTranslateX(200 + pos * 200);
        btn.setTranslateY(250);
        btn.setPrefSize(200, 200);
        btn.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        Image image = new Image("/button_image.png", btn.getWidth(), btn.getHeight(), false, true, true);
        BackgroundImage bImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(btn.getWidth(), btn.getHeight(), true, true, true, false));
        Background backGround = new Background(bImage);
        btn.setBackground(backGround);
        btn.setOnAction(e);
        root.getChildren().add(btn);
    }

    private void startTimer(Stage stage) {
        timerDisplay = new Text("00:00");
        timerDisplay.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        timerDisplay.setTextAlignment(TextAlignment.RIGHT);
        root.getChildren().add(timerDisplay);

        timerDisplay.setLayoutX(root.getWidth() - 100);
        timerDisplay.setLayoutY(50);

        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            int seconds = 0;

            @Override
            public void run() {
                seconds++;
                String time = String.format("%02d:%02d", seconds / 60, seconds % 60);
                Platform.runLater(() -> updateTimerDisplay(time));
            }
        };

        timer.scheduleAtFixedRate(timerTask, 0, 1000); // Run the timer task every 1000 milliseconds (1 second)
    }

    private void updateTimerDisplay(String time) {
        timerDisplay.setText(time);
    }

    public void loadGame(Stage stage) {
        musicManager.changeBackgroundMusic("game-background.mp3");

        root = new Pane();
        Scene scene2 = new Scene(root, 737, 800);
        stage.setTitle("Escape the Room");

        startTimer(mainStage);

        stage.setScene(scene2);
        stage.sizeToScene();

        double screenWidth = javafx.stage.Screen.getPrimary().getBounds().getWidth();
        double screenHeight = javafx.stage.Screen.getPrimary().getBounds().getHeight();

        stage.setX((screenWidth - scene2.getWidth()) / 2);
        stage.setY((screenHeight - scene2.getHeight()) / 2);

        stage.show();

        Image houseBackgroundImage = new Image("game-background.jpg");
        BackgroundSize size = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, false);
        Background background = new Background(new BackgroundImage(houseBackgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, size));
        root.setBackground(background);
        barrier = new LinkedList<ObstacleTile>();

        // Create barrier with ObstacleTiles
        createObstacleTile(tileWidth * 10, tileHeight * 3, 0, 0);  // rectangle width, height, x_coord, y_coord
        createObstacleTile(tileWidth * 3, tileHeight * 2, tileWidth * 10, 0);     // top wall
        createObstacleTile(tileWidth * 3, tileHeight * 2, tileWidth * 10, 0);     // top wall - bed
        createObstacleTile(tileWidth * 2, tileHeight * 6 + 6, tileWidth * 11, tileHeight * 2);  // right wall
        createObstacleTile(tileWidth, tileHeight * 6 + 6, tileWidth * 12, tileHeight * 8 + 6);  // right wall
        createObstacleTile(tileWidth, tileHeight * 5 + 6, tileWidth * 11, tileHeight * 9 + 6);  // right wall
        createObstacleTile(tileWidth, tileHeight * 11, 0, tileHeight * 3);  // left wall
        createObstacleTile(tileWidth - 5, tileHeight * 4 + 6, tileWidth, tileHeight * 4);  // left wall
        createObstacleTile(tileWidth + 6, tileHeight * 2, tileWidth, tileHeight * 12 + 13);  // left bottom wall
        createObstacleTile(tileWidth * 7, tileHeight * 2, tileWidth * 5 - 16, tileHeight * 12 + 13);   // bottom wall
        createObstacleTile(tileWidth, tileHeight, tileWidth * 6 - 3, tileHeight * 11 + 13);   // bottom wall
        createObstacleTile(tileWidth - 20, tileHeight - 20, tileWidth * 2 + 5, tileHeight * 9 + 22);  // living room chair - left
        createObstacleTile(tileWidth - 20, tileHeight - 20, tileWidth * 4 + 5, tileHeight * 9 + 22);  // living room chair - right
        createObstacleTile(tileWidth, tileHeight + 15, tileWidth * 3 - 5, tileHeight * 9 + 10);  // living room table
        createObstacleTile(tileWidth * 2, tileHeight, tileWidth * 3 - 5, tileHeight * 3);  // in between table/chair
        createObstacleTile(tileWidth - 18, tileHeight, tileWidth * 9 + 10, tileHeight * 3 - 10);  // square chair
        createObstacleTile(tileWidth * 2, tileHeight * 2, tileWidth * 5 - 5, tileHeight * 6 + 5);  // middle wall
        createObstacleTile(tileWidth, tileHeight * 5 + 10, tileWidth * 6 - 5, tileHeight * 4);  // middle wall
        createObstacleTile(tileWidth * 4 + 10, tileHeight * 3 + 4, tileWidth * 7 - 5, tileHeight * 5 + 4);  // middle wall
        createObstacleTile(tileWidth * 2 + 5, tileHeight + 5, tileWidth * 8 - 5, tileHeight * 10 + 10);  // kitchen table
        createObstacleTile(tileWidth * 2 - 22, tileHeight + 5, tileWidth * 8 + 10, tileHeight * 11 + 10);  // kitchen chairs

        // Create character image
        ImageView character_image = new ImageView(new Image("Down2.png"));
        character_image.setLayoutX(170);  // Starting position
        character_image.setLayoutY(730);  // Starting position
        root.getChildren().add(character_image);

        // Create inventory box
        ImageView inventory = new ImageView(new Image("inventory.jpg"));
        inventory.setLayoutX(5);
        inventory.setLayoutY(5);
        root.getChildren().add(inventory);

        // Create key
        ImageView key = new ImageView(new Image("key.png"));
        key.setLayoutX(10);
        key.setLayoutY(10);
        key.setOpacity(.2);
        root.getChildren().add(key);

        // Create dialog box
        ImageView dialog = new ImageView(new Image("dialog_box.png"));
        dialog.setLayoutX(17);
        dialog.setLayoutY(600);
        dialog.setOpacity(0);
        root.getChildren().add(dialog);

        // Create dialog text
        Text dialog_text = new Text(50, 700, "");
        dialog_text.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
        dialog_text.setOpacity(0);
        root.getChildren().add(dialog_text);

        // Create character
        character = new Character(root, stage, scene2, barrier, dialog, dialog_text, key, character_image, musicManager);  // The Character constructor adds functionality and event handlers to scene
    }

    public void createObstacleTile(double w, double h, double x, double y) {
        ObstacleTile tile = new ObstacleTile(w, h, x, y); // rectangle width, height, x_coord, y_coord
        root.getChildren().add(tile);
        barrier.add(tile);
    }
}