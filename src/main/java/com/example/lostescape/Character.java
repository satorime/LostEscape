package com.example.lostescape;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import com.example.lostescape.Server.DatabaseManager;
import com.example.lostescape.Server.CurrentUser;
import spacewar.SpaceShooter;

public class Character {
    Pane root;
    Stage stage;
    AnimationTimer timer;
    public String standingImage = "Down2.png";
    public ImageView character_image;
    LinkedList<ObstacleTile> barrier;
    boolean moveUp, moveRight, moveDown, moveLeft, run;
    ArrayList<Image> walkingUpImageList;
    ArrayList<Image> walkingDownImageList;
    ArrayList<Image> walkingRightImageList;
    ArrayList<Image> walkingLeftImageList;
    int switchWhenZero = 0;
    int upCount;
    int downCount;
    int rightCount;
    int leftCount;
    boolean foundKey = false;
    boolean nearKey = false;

    private long startTime;
    private long pauseTime;
    private MusicManager musicManager;

    private DatabaseManager dbManager;
    private SpaceShooter spaceShooter;
    private boolean spaceShooterCompleted = false;

    public Character(Pane root, Stage stage, Scene scene, LinkedList<ObstacleTile> barrier, ImageView dialog, Text dialogText, ImageView key, ImageView character_image, MusicManager musicManager) {
        this.barrier = barrier;
        this.character_image = character_image;
        this.root = root;
        this.stage = stage;
        this.musicManager = musicManager;
        startTime = System.currentTimeMillis();
        dbManager = DatabaseManager.getInstance();

        // Initialize the Character's Walking Images Lists
        walkingUpImageList = new ArrayList<>();
        walkingUpImageList.add(new Image("Up1.png"));
        walkingUpImageList.add(new Image("Up2.png"));
        walkingUpImageList.add(new Image("Up3.png"));

        walkingDownImageList = new ArrayList<>();
        walkingDownImageList.add(new Image("Down1.png"));
        walkingDownImageList.add(new Image("Down2.png"));
        walkingDownImageList.add(new Image("Down3.png"));

        walkingRightImageList = new ArrayList<>();
        walkingRightImageList.add(new Image("Right1.png"));
        walkingRightImageList.add(new Image("Right2.png"));
        walkingRightImageList.add(new Image("Right3.png"));

        walkingLeftImageList = new ArrayList<>();
        walkingLeftImageList.add(new Image("Left1.png"));
        walkingLeftImageList.add(new Image("Left2.png"));
        walkingLeftImageList.add(new Image("Left3.png"));

        upCount = 0;
        downCount = 0;
        rightCount = 0;
        leftCount = 0;

        this.spaceShooter = new SpaceShooter(); // Initialize the SpaceShooter game

        // Set EventHandler on arrow key press/release
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent e) {
                KeyCode code = e.getCode();
                if (code == KeyCode.UP) { moveUp = true; }
                else if (code == KeyCode.DOWN) { moveDown = true; }
                else if (code == KeyCode.RIGHT) { moveRight  = true; }
                else if (code == KeyCode.LEFT) { moveLeft  = true; }
                else if (code == KeyCode.SHIFT) { run = true; }
                else if (code == KeyCode.SPACE) {  // Space pressed - check for key and pop up dialog message
                    if (nearLivingRoomTable()) {
                        if (spaceShooterCompleted) {
                            dialogText.setText("Didn't find key.");
                            dialogText.setOpacity(1);
                            dialog.setOpacity(1);

                            PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
                            pause.setOnFinished(ev -> { dialogText.setOpacity(0); dialog.setOpacity(0); });
                            pause.play();
                        } else {
                            startSpaceShooterGame();
                        }
                    } else if (nearKey) {
                        foundKey = true;
                        dialogText.setText("Got Key!");
                        dialogText.setOpacity(1);
                        dialog.setOpacity(1);
                        key.setOpacity(1);

                        PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
                        pause.setOnFinished(ev -> { dialogText.setOpacity(0); dialog.setOpacity(0); });
                        pause.play();

                    } else {
                        dialogText.setText("Didn't find key.");
                        dialogText.setOpacity(1);
                        dialog.setOpacity(1);

                        PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
                        pause.setOnFinished(ev -> { dialogText.setOpacity(0); dialog.setOpacity(0); });
                        pause.play();
                    }
                }
            }
        });

        scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent e) {
                KeyCode code = e.getCode();
                if (code == KeyCode.UP) { moveUp = false; }
                else if (code == KeyCode.DOWN) { moveDown = false; }
                else if (code == KeyCode.RIGHT) { moveRight  = false; }
                else if (code == KeyCode.LEFT) { moveLeft  = false; }
                else if (code == KeyCode.SHIFT) { run = false; }
            }
        });

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                int dx = 0;
                int dy = 0;

                if (moveUp) {
                    dy -= 3;
                    if (switchWhenZero == 0) {
                        character_image.setImage(walkingUpImageList.get(upCount % 3));
                        upCount++;
                        switchWhenZero = 4;
                    } else {
                        switchWhenZero--;
                    }
                } else if (moveDown) {
                    dy += 3;
                    if (switchWhenZero == 0) {
                        character_image.setImage(walkingDownImageList.get(downCount % 3));
                        downCount++;
                        switchWhenZero = 4;
                    } else {
                        switchWhenZero--;
                    }
                } else if (moveRight) {
                    dx += 3;
                    if (switchWhenZero == 0) {
                        character_image.setImage(walkingRightImageList.get(rightCount % 3));
                        rightCount++;
                        switchWhenZero = 4;
                    } else {
                        switchWhenZero--;
                    }
                } else if (moveLeft) {
                    dx -= 3;
                    if (switchWhenZero == 0) {
                        character_image.setImage(walkingLeftImageList.get(leftCount % 3));
                        leftCount++;
                        switchWhenZero = 4;
                    } else {
                        switchWhenZero--;
                    }
                }
                if (run) {
                    dx *= 2;
                    dy *= 2;
                }

                moveCharacter(dx, dy);
            }
        };
        timer.start();
    }

    public long getTimeTaken() {
        return (System.currentTimeMillis() - startTime - pauseTime) / 1000; // Return the time taken in seconds
    }

    // Move character in the correct direction and also check for key/exit
    private void moveCharacter(int dx, int dy) {
        if (dx != 0 || dy != 0) {  // Only move if character has "speed" - added/subtracted from the key presses
            double cx = character_image.getBoundsInLocal().getWidth() / 2;
            double cy = character_image.getBoundsInLocal().getHeight() / 2;

            double x = cx + character_image.getLayoutX() + dx;
            double y = cy + character_image.getLayoutY() + dy;

            // Check if character should move
            if (x - cx >= 0 &&
                    x + cx <= 737 &&  // Scene width
                    y - cy >= 0 &&
                    y + cy <= 800 && // Scene height
                    !hitBarrier(x - cx, y - cy)) {
                character_image.relocate(x - cx, y - cy);
            }

            // Check if character is near the key
            if (x >= 587 && x <= 590 && y >= 130 && y <= 145) {
                nearKey = true;
            }

            // Check if character found key and is near the door
            // Won the game!
            if (foundKey && x >= 137 && x <= 257 && y >= 757 && y <= 778) {
                long timeTaken = getTimeTaken(); // Get the time taken by the user
                timer.stop(); // Need to end animation timer or else it will keep entering this if statement and pop up the win screen infinitely

                int userID = CurrentUser.userID; // Get the current user ID from a global state or session
                dbManager.addHighScore(userID, timeTaken);

                root = new Pane();
                Scene scene = new Scene(root, 737, 800);
                stage.setTitle("You Won!");
                stage.setScene(scene);
                stage.show();

                // Set background image
                Image backgroundImage = new Image("menu.jpg");
                BackgroundSize size = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, false);
                Background background = new Background(new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, size));
                root.setBackground(background);

                // Title
                ImageView title = new ImageView(new Image("You-Won.png"));
                title.setLayoutX(160);
                title.setLayoutY(275);
                root.getChildren().add(title);

                // Display the time taken
                Text timeText = new Text("Time taken: " + timeTaken + " seconds");
                timeText.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
                timeText.setLayoutX(230);
                timeText.setLayoutY(450);
                root.getChildren().add(timeText);

                // Retrieve and display high scores
                List<Map<String, String>> highScores = dbManager.getHighScores();
                VBox highScoreBox = new VBox();
                highScoreBox.setLayoutX(230);
                highScoreBox.setLayoutY(500);
                highScoreBox.setSpacing(5);

                Label highScoreTitle = new Label("High Scores:");
                highScoreTitle.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
                highScoreBox.getChildren().add(highScoreTitle);

                for (Map<String, String> score : highScores) {
                    String username = score.get("username");
                    String time = score.get("time_taken");
                    Label scoreLabel = new Label(username + ": " + time + " seconds");
                    scoreLabel.setFont(Font.font("Verdana", FontWeight.NORMAL, 16));
                    highScoreBox.getChildren().add(scoreLabel);
                }

                root.getChildren().add(highScoreBox);
            }
        } else {
            character_image.setImage(new Image(standingImage));
        }  // if not moving, set character image to standing image
    }

    // Returns true if character hit a wall/object
    private boolean hitBarrier(double wantsToGoToThisX, double wantsToGoToThisY) {
        Iterator<ObstacleTile> it = barrier.iterator();
        while (it.hasNext()) {
            ObstacleTile t = it.next();

            double spriteMinX = wantsToGoToThisX + 5;
            double spriteMinY = wantsToGoToThisY + 5;
            double spriteMaxX = wantsToGoToThisX + character_image.getBoundsInLocal().getWidth() - 10;
            double spriteMaxY = wantsToGoToThisY + character_image.getBoundsInLocal().getHeight();

            double tMinX = t.getX();
            double tMinY = t.getY();
            double tMaxX = t.getX() + t.getWidth();
            double tMaxY = t.getY() + t.getHeight();

            boolean inside = spriteMaxX > tMinX && spriteMinX < tMaxX && spriteMaxY > tMinY && spriteMinY < tMaxY;

            if (inside) {
                return true;
            }
        }
        return false;
    }

    private boolean nearLivingRoomTable() {
        double tableMinX = 150; // Adjusted slightly for more accurate detection
        double tableMaxX = 250;
        double tableMinY = 490;
        double tableMaxY = 610;

        double cx = character_image.getBoundsInLocal().getWidth() / 2;
        double cy = character_image.getBoundsInLocal().getHeight() / 2;
        double x = cx + character_image.getLayoutX();
        double y = cy + character_image.getLayoutY();

        System.out.println("Character X: " + x + ", Y: " + y);
        System.out.println("Table X range: " + tableMinX + " - " + tableMaxX + ", Y range: " + tableMinY + " - " + tableMaxY);

        return (x >= tableMinX && x <= tableMaxX && y >= tableMinY && y <= tableMaxY);
    }

    private void startSpaceShooterGame() {
        root.getChildren().remove(character_image); // Remove character image from the root

        // Save current character state
        double characterX = character_image.getLayoutX();
        double characterY = character_image.getLayoutY();
        boolean wasMovingUp = moveUp;
        boolean wasMovingDown = moveDown;
        boolean wasMovingLeft = moveLeft;
        boolean wasMovingRight = moveRight;

        // Pause the timer
//        pauseTime += System.currentTimeMillis() - startTime;
//        timer.stop();

        Stage spaceShooterStage = new Stage();
        try {
            spaceShooter.start(spaceShooterStage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        spaceShooterStage.setOnCloseRequest(event -> {
            // Restore the original game UI
            root.getChildren().add(character_image);

            // Restore character state
            character_image.relocate(characterX, characterY);
            moveUp = wasMovingUp;
            moveDown = wasMovingDown;
            moveLeft = wasMovingLeft;
            moveRight = wasMovingRight;

            // Resume the timer
//            startTime = System.currentTimeMillis();
//            timer.start();

            stage.show(); // Show the original stage

            spaceShooterCompleted = true;
        });
    }
}