package com.example.lostescape;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import FlappyRato.FlappyBird;
import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import com.example.lostescape.Server.DatabaseManager;
import com.example.lostescape.Server.CurrentUser;
import Gunner.SpaceShooter;

public class Character implements OtherGameElements{
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
    private FlappyBird flappyBird;
    private boolean spaceShooterCompleted = false;
    private boolean flappyBirdComplete = false;

    private double xOffset = 0;
    private double yOffset = 0;

    public Character(Pane root, Stage stage, Scene scene, LinkedList<ObstacleTile> barrier, ImageView dialog, Text dialogText, ImageView key, ImageView character_image, MusicManager musicManager) {
        this.barrier = barrier;
        this.character_image = character_image;
        this.root = root;
        this.stage = stage;
        this.musicManager = musicManager;
        startTime = System.currentTimeMillis();
        dbManager = DatabaseManager.getInstance();

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

        this.spaceShooter = new SpaceShooter();
        this.flappyBird = new FlappyBird();

        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent e) {
                KeyCode code = e.getCode();
                if (code == KeyCode.UP) { moveUp = true; }
                else if (code == KeyCode.DOWN) { moveDown = true; }
                else if (code == KeyCode.RIGHT) { moveRight  = true; }
                else if (code == KeyCode.LEFT) { moveLeft  = true; }
                else if (code == KeyCode.SHIFT) { run = true; }
                else if (code == KeyCode.SPACE) {
                    if (nearLivingRoomTable()) {
                        if (spaceShooterCompleted) {
                            dialogText.setText("Clue: Min X = 580 & Max X = 600.");
                            dialogText.setOpacity(1);
                            dialog.setOpacity(1);

                            PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
                            pause.setOnFinished(ev -> { dialogText.setOpacity(0); dialog.setOpacity(0); });
                            pause.play();
                        } else {
                            startSpaceShooterGame();
                        }
                    }else if(nearLaptopTable()){
                        if(flappyBirdComplete){
                            dialogText.setText("Clue: A place that could make you 'wet'.");
                            dialogText.setOpacity(1);
                            dialog.setOpacity(1);

                            PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
                            pause.setOnFinished(ev -> { dialogText.setOpacity(0); dialog.setOpacity(0); });
                            pause.play();
                        }else{
                            startFlappyBird();
                        }
                    }else if (nearKey) {
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
        return (System.currentTimeMillis() - startTime - pauseTime) / 1000;
    }

    private void moveCharacter(int dx, int dy) {
        if (dx != 0 || dy != 0) {
            double cx = character_image.getBoundsInLocal().getWidth() / 2;
            double cy = character_image.getBoundsInLocal().getHeight() / 2;

            double x = cx + character_image.getLayoutX() + dx;
            double y = cy + character_image.getLayoutY() + dy;

            // Check if character should move
            if (x - cx >= 0 &&
                    x + cx <= 737 &&
                    y - cy >= 0 &&
                    y + cy <= 800 &&
                    !hitBarrier(x - cx, y - cy)) {
                character_image.relocate(x - cx, y - cy);
            }

            // Check if character is near the key
            if (x >= 587 && x <= 590 && y >= 130 && y <= 145) {
                nearKey = true;
            }

            // Check if character found key and is near the door
            if (foundKey && x >= 137 && x <= 257 && y >= 757 && y <= 778) {
                long timeTaken = getTimeTaken();
                timer.stop();

                int userID = CurrentUser.userID;
                dbManager.addHighScore(userID, timeTaken);

                root = new Pane();
                Scene scene = new Scene(root, 850, 405);
                stage.setTitle("You Won!");
                stage.setScene(scene);
                stage.show();

                centerStage(stage);
                setupMouseEvents(root, stage);

                Image backgroundImage = new Image("won-background.jpg");
                BackgroundSize size = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, false);
                Background background = new Background(new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, size));
                root.setBackground(background);

                createButton("Try Again", 0, e -> Platform.exit());
                createButton("View Board", 1, e -> loadLeaderboard(stage));

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
        }
    }

    public void createButton(String n, int pos, EventHandler<ActionEvent> e) {
        Button btn = new Button(n);
        //btn.setTranslateX(145 + pos * 200);
        btn.setTranslateX(250 + pos * 200);
        btn.setTranslateY(250);
        btn.setPrefSize(150, 150);
        btn.setStyle("-fx-font-family: 'Verdana'; -fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: black;");

        Image image = new Image("/button_image2.png", btn.getWidth(), btn.getHeight(), false, true, true);
        BackgroundImage bImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(btn.getWidth(), btn.getHeight(), true, true, true, false));
        Background backGround = new Background(bImage);

        btn.setBackground(backGround);
        btn.setOnAction(e);
        root.getChildren().add(btn);
    }

    public void loadLeaderboard(Stage stage) {
        musicManager.changeBackgroundMusic("game-background.mp3");

        root = new Pane();
        Scene scene2 = new Scene(root, 900, 887);
        stage.setTitle("Leaderboard");

        stage.setScene(scene2);
        stage.sizeToScene();
        centerStage(stage);
        stage.show();
        setupMouseEvents(root, stage);

        Image leaderboardbg = new Image("leaderboard.jpg");
        BackgroundSize size = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, false);
        Background background = new Background(new BackgroundImage(leaderboardbg, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, size));
        root.setBackground(background);

        List<Map<String, String>> highScores = dbManager.getHighScores();

        // Ensure only the top 10 scores are displayed
        List<Map<String, String>> topTenScores = highScores.stream().limit(10).collect(Collectors.toList());

        VBox highScoreBox = new VBox();
        highScoreBox.setLayoutX(335);
        highScoreBox.setLayoutY(315);
        highScoreBox.setSpacing(5);

        Label boardlabel = new Label("NAME\tSCORE");
        boardlabel.setFont(Font.font("Verdana", FontWeight.BOLD, 25));
        boardlabel.setStyle("-fx-translate-y:-10px");
        highScoreBox.getChildren().add(boardlabel);

        for (Map<String, String> score : topTenScores) {
            String username = score.get("username");
            String time = score.get("time_taken");
            Label scoreLabel = new Label(username + "\t\t\t" + time + "s");
            scoreLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
            highScoreBox.getChildren().add(scoreLabel);
        }

        root.getChildren().add(highScoreBox);
    }

//    public void loadLeaderboard(Stage stage) {
//        musicManager.changeBackgroundMusic("game-background.mp3");
//
//        root = new Pane();
//        Scene scene2 = new Scene(root, 900, 887);
//        stage.setTitle("Leaderboard");
//
//        stage.setScene(scene2);
//        stage.sizeToScene();
//        centerStage(stage);
//        stage.show();
//        setupMouseEvents(root, stage);
//
//        Image leaderboardbg = new Image("leaderboard.jpg");
//        BackgroundSize size = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, false);
//        Background background = new Background(new BackgroundImage(leaderboardbg, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, size));
//        root.setBackground(background);
//
//        List<Map<String, String>> highScores = dbManager.getHighScores();
//        VBox highScoreBox = new VBox();
//        double highScoreBoxX = (scene2.getWidth() - highScoreBox.getBoundsInLocal().getWidth()) / 2;
//        double highScoreBoxY = (scene2.getHeight() - highScoreBox.getBoundsInLocal().getHeight()) / 2;
//
//        highScoreBox.setLayoutX(highScoreBoxX);
//        highScoreBox.setLayoutY(highScoreBoxY);
//        highScoreBox.setSpacing(5);
//        Label highScoreTitle = new Label("High Scores:");
//        highScoreTitle.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
//        highScoreBox.getChildren().add(highScoreTitle);
//        for (Map<String, String> score : highScores) {
//            String username = score.get("username");
//            String time = score.get("time_taken");
//            long totalSeconds = Long.parseLong(time);
//            long hours = totalSeconds / 3600;
//            long minutes = (totalSeconds % 3600) / 60;
//            long seconds = totalSeconds % 60;
//
//
//            Label scoreLabel;
//            if (hours > 0) {
//                scoreLabel = new Label(username + hours + ":" + minutes+ ":" + seconds);
//            } else {
//                scoreLabel = new Label(username + minutes + ":" + seconds);
//            }
//            scoreLabel.setFont(Font.font("Verdana", FontWeight.NORMAL, 16));
//            highScoreBox.getChildren().add(scoreLabel);
//        }
//        root.getChildren().add(highScoreBox);
//    }

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
        double tableMinX = 150;
        double tableMaxX = 250;
        double tableMinY = 490;
        double tableMaxY = 610;

        double cx = character_image.getBoundsInLocal().getWidth() / 2;
        double cy = character_image.getBoundsInLocal().getHeight() / 2;
        double x = cx + character_image.getLayoutX();
        double y = cy + character_image.getLayoutY();

        System.out.println("Character X: " + x + ", Y: " + y);
//        System.out.println("Table X range: " + tableMinX + " - " + tableMaxX + ", Y range: " + tableMinY + " - " + tableMaxY);

        return (x >= tableMinX && x <= tableMaxX && y >= tableMinY && y <= tableMaxY);
    }

    private boolean nearLaptopTable() {
        double tableMinX = 130;
        double tableMaxX = 131;
        double tableMinY = 230;
        double tableMaxY = 300;

        double cx = character_image.getBoundsInLocal().getWidth() / 2;
        double cy = character_image.getBoundsInLocal().getHeight() / 2;
        double x = cx + character_image.getLayoutX();
        double y = cy + character_image.getLayoutY();

//        System.out.println("Character X: " + x + ", Y: " + y);
//        System.out.println("Table X range: " + tableMinX + " - " + tableMaxX + ", Y range: " + tableMinY + " - " + tableMaxY);

        return (x >= tableMinX && x <= tableMaxX && y >= tableMinY && y <= tableMaxY);
    }

    private void startSpaceShooterGame() {
        root.getChildren().remove(character_image);

        // Save current character state
        double characterX = character_image.getLayoutX();
        double characterY = character_image.getLayoutY();
        boolean wasMovingUp = moveUp;
        boolean wasMovingDown = moveDown;
        boolean wasMovingLeft = moveLeft;
        boolean wasMovingRight = moveRight;


        Stage spaceShooterStage = new Stage();
        try {
            spaceShooter.start(spaceShooterStage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        spaceShooterStage.setOnHiding(event -> {
            root.getChildren().add(character_image);

            character_image.relocate(characterX, characterY);
            moveUp = wasMovingUp;
            moveDown = wasMovingDown;
            moveLeft = wasMovingLeft;
            moveRight = wasMovingRight;


            stage.show();

            spaceShooterCompleted = true;
        });
    }

    private void startFlappyBird() {
        root.getChildren().remove(character_image);

        double characterX = character_image.getLayoutX();
        double characterY = character_image.getLayoutY();
        boolean wasMovingUp = moveUp;
        boolean wasMovingDown = moveDown;
        boolean wasMovingLeft = moveLeft;
        boolean wasMovingRight = moveRight;


        Stage flappyBirdStage = new Stage();
        try {
            flappyBird.start(flappyBirdStage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        flappyBirdStage.setOnHiding(event -> {
            root.getChildren().add(character_image);

            character_image.relocate(characterX, characterY);
            moveUp = wasMovingUp;
            moveDown = wasMovingDown;
            moveLeft = wasMovingLeft;
            moveRight = wasMovingRight;


            stage.show();

            flappyBirdComplete = true;
        });
    }

    @Override
    public void centerStage(Stage stage) {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);
    }

    @Override
    public void setupMouseEvents(Node root, Stage stage) {
        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        root.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
    }
}
