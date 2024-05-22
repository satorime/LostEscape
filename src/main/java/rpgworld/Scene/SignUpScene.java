package rpgworld.Scene;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import rpgworld.Server.DatabaseManager;
import rpgworld.Server.Status;

public class SignUpScene {
    public Scene getSignUpScene(Stage stage) {
        VBox vbox = new VBox();

        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();

        Button signUpButton = new Button("Sign Up");
        Button backButton = new Button("Back to Login");

        signUpButton.setOnAction(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            DatabaseManager dbManager = DatabaseManager.getInstance();
            Status status = dbManager.createUser(username, password);
            if (status == Status.ACCOUNT_CREATED_SUCCESSFULLY) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Registration successful. Please log in.");
                alert.show();
                LoginScene loginScene = new LoginScene();
                stage.setScene(loginScene.getLoginScene(stage));
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText(status.getMessage());
                alert.show();
            }
        });

        backButton.setOnAction(event -> {
            LoginScene loginScene = new LoginScene();
            stage.setScene(loginScene.getLoginScene(stage));
        });

        vbox.getChildren().addAll(usernameLabel, usernameField, passwordLabel, passwordField, signUpButton, backButton);

        return new Scene(vbox, 400, 300);
    }
}
