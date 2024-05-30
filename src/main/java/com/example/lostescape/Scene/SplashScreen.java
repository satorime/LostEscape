package com.example.lostescape.Scene;

import com.example.lostescape.MusicManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.geometry.Rectangle2D;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SplashScreen {
    MusicManager musicManager;
    private ScheduledExecutorService scheduler;

    public void start(Stage primaryStage) {
        Stage splashStage = new Stage(StageStyle.TRANSPARENT);

        Image splashImage = new Image(Objects.requireNonNull(getClass().getResource("/logo/logo2.png")).toExternalForm());
        ImageView splashImageView = new ImageView(splashImage);
        musicManager = new MusicManager();
        scheduler = Executors.newScheduledThreadPool(1);

        splashImageView.setPreserveRatio(true);
        splashImageView.setFitWidth(400);

        StackPane root = new StackPane(splashImageView);
        Scene scene = new Scene(root, 400, 400);
        scene.setFill(null);

        splashStage.setScene(scene);

        splashStage.setOnShown(event -> {
            Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
            double centerX = primaryScreenBounds.getMinX() + (primaryScreenBounds.getWidth() - splashStage.getWidth()) / 2;
            double centerY = primaryScreenBounds.getMinY() + (primaryScreenBounds.getHeight() - splashStage.getHeight()) / 2;

            splashStage.setX(centerX);
            splashStage.setY(centerY);
        });

        splashStage.show();

        new Thread(() -> {
            musicManager.playSoundEffect("sound/intro.mp3");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> {
                musicManager.playBackgroundMusic("sound/rizz-sounds.mp3");
                scheduler.schedule(() -> Platform.runLater(() -> musicManager.stopBackgroundMusic()), 1, TimeUnit.SECONDS);
            });
        }).start();

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(7), event -> {
            splashStage.close();
            scheduler.shutdown();
        }));
        timeline.setCycleCount(1);
        timeline.play();
    }
}
