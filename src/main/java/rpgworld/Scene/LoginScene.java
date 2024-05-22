package rpgworld.Scene;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import rpgworld.Server.DatabaseManager;
import rpgworld.Server.Status;
import rpgworld.World;

public class LoginScene {
    public Scene getLoginScene(Stage stage) {
        VBox vbox = new VBox();

        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();

        Button loginButton = new Button("Login");
        Button signUpButton = new Button("Sign Up");

        loginButton.setOnAction(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            DatabaseManager dbManager = DatabaseManager.getInstance();
            Status status = dbManager.validate(username, password);
            if (status == Status.LOGIN_SUCCESS) {
                World world = new World();
                world.startGame(stage);  // Call the startGame method from World class
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText(status.getMessage());
                alert.show();
            }
        });

        signUpButton.setOnAction(event -> {
            SignUpScene signUpScene = new SignUpScene();
            stage.setScene(signUpScene.getSignUpScene(stage));
        });

        vbox.getChildren().addAll(usernameLabel, usernameField, passwordLabel, passwordField, loginButton, signUpButton);

        return new Scene(vbox, 400, 300);
    }
}
