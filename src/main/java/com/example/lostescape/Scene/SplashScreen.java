package com.example.lostescape.Scene;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class SplashScreen {
    public void start(Stage primaryStage) {
        Stage splashStage = new Stage(StageStyle.TRANSPARENT);

        // Load image from resources
        Image splashImage = new Image(getClass().getResource("/Down2.png").toExternalForm());
        ImageView splashImageView = new ImageView(splashImage);
        splashImageView.setPreserveRatio(true);
        splashImageView.setFitWidth(400);

        StackPane root = new StackPane(splashImageView);
        Scene scene = new Scene(root, 400, 400);
        scene.setFill(null);

        splashStage.setScene(scene);
        splashStage.show();

        // Close splash screen after a duration
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), event -> splashStage.close()));
        timeline.setCycleCount(1);
        timeline.play();
    }
}
