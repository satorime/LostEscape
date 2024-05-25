package rpgworld;

import rpgworld.Server.DatabaseManager;

import java.sql.SQLException;
import javafx.application.Application;
import javafx.stage.Stage;
import rpgworld.Scene.SplashScreen;

public class AppLauncher /*extends Application*/{
//    @Override
//    public void start(Stage primaryStage) {
//        SplashScreen splashScreen = new SplashScreen();
//        splashScreen.start(primaryStage);
//    }
    public static void main(String[] args) throws SQLException {
        DatabaseManager.getInstance().initializeDB();
        World.main(args);
    }
}