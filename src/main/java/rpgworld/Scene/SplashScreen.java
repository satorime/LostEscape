package rpgworld.Scene;

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

        ImageView splashImage = new ImageView(new Image("Down2.png"));
        splashImage.setPreserveRatio(true);
        splashImage.setFitWidth(400);

        StackPane root = new StackPane(splashImage);
        Scene scene = new Scene(root, 400, 400);
        scene.setFill(null);

        splashStage.setScene(scene);
        splashStage.show();

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(10), event -> {
            splashStage.close();

            LoginScene loginScene = new LoginScene();
            primaryStage.setScene(loginScene.getLoginScene(primaryStage));
            primaryStage.show();
        }));
        timeline.setCycleCount(1);
        timeline.play();
    }
}
