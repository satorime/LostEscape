package com.example.lostescape.Scene;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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

public class SplashScreen {
    public void start(Stage primaryStage) {
        Stage splashStage = new Stage(StageStyle.TRANSPARENT);

        Image splashImage = new Image(Objects.requireNonNull(getClass().getResource("/logo/logo2.png")).toExternalForm());
        ImageView splashImageView = new ImageView(splashImage);
        splashImageView.setPreserveRatio(true);
        splashImageView.setFitWidth(400);

        StackPane root = new StackPane(splashImageView);
        Scene scene = new Scene(root, 400, 400);
        scene.setFill(null);

        splashStage.setScene(scene);

        splashStage.setOnShown(event -> {
            // Centering the stage on the screen
            Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
            double centerX = primaryScreenBounds.getMinX() + (primaryScreenBounds.getWidth() - splashStage.getWidth()) / 2;
            double centerY = primaryScreenBounds.getMinY() + (primaryScreenBounds.getHeight() - splashStage.getHeight()) / 2;

            splashStage.setX(centerX);
            splashStage.setY(centerY);
        });

        splashStage.show();

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), event -> splashStage.close()));
        timeline.setCycleCount(1);
        timeline.play();
    }
}