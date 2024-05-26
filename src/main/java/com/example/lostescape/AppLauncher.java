package com.example.lostescape;

import com.example.lostescape.Scene.SplashScreen;
import com.example.lostescape.Server.DatabaseManager;
import javafx.application.Application;
import javafx.animation.PauseTransition;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.SQLException;

public class AppLauncher extends Application {
    @Override
    public void start(Stage primaryStage) {
        SplashScreen splashScreen = new SplashScreen();
        splashScreen.start(primaryStage);

        PauseTransition pause = new PauseTransition(Duration.seconds(5));
        pause.setOnFinished(event -> {
            World world = new World();
            try {
                world.start(primaryStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        pause.play();
    }

    public static void main(String[] args) {
        try {
            DatabaseManager.getInstance().initializeDB();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        launch(args);
    }
}
