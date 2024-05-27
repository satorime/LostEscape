package com.example.lostescape.Scene;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import com.example.lostescape.Server.DatabaseManager;
import com.example.lostescape.Server.Status;


public class SignUpScene {
    private double xOffset = 0;
    private double yOffset = 0;

    public Scene getSignUpScene(Stage stage) {
        Insets header = new Insets(-10, 0, -13, 0);
        Insets loginheader1 = new Insets(13, 0, 0, 0);
        Insets loginheader2 = new Insets(5, 0, 0, 0);
        Insets register = new Insets(-2, 0, -15, 0);

        AnchorPane root = new AnchorPane();
        root.getStyleClass().add("root");

        HBox hBox = new HBox();
        hBox.getStyleClass().add("hbox");

        VBox vBoxLeft = new VBox();
        vBoxLeft.setPrefWidth(300);
        vBoxLeft.getStyleClass().add("vboxleft");

        Label welcomeLabel = new Label("Registration");
        welcomeLabel.setPadding(header);
        welcomeLabel.getStyleClass().add("welcome");

        Label groupLabel = new Label(" welcome to cabin quest, newbie");
        groupLabel.setMaxWidth(Double.MAX_VALUE);
        groupLabel.setAlignment(Pos.CENTER);
        groupLabel.getStyleClass().add("group");

        VBox.setMargin(groupLabel, new Insets(0, 0, 10, 0));

        Label usernameLabel = new Label("     Username");
        usernameLabel.setPadding(loginheader1);
        usernameLabel.setMaxWidth(Double.MAX_VALUE);
        usernameLabel.setAlignment(Pos.BOTTOM_LEFT);
        usernameLabel.getStyleClass().add("login-indicator");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter your username");
        usernameField.getStyleClass().add("login-field");

        Label passwordLabel = new Label("     Password");
        passwordLabel.setPadding(loginheader2);
        passwordLabel.setMaxWidth(Double.MAX_VALUE);
        passwordLabel.setAlignment(Pos.BOTTOM_LEFT);
        passwordLabel.getStyleClass().add("login-indicator");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.getStyleClass().add("login-field");

        VBox.setMargin(passwordField, new Insets(0, 0, 10, 0));

        Button signUpButton = new Button("Sign Up");
        signUpButton.setPadding(header);
        signUpButton.setMaxWidth(Double.MAX_VALUE);
        signUpButton.setAlignment(Pos.CENTER);
        signUpButton.getStyleClass().add("login-btn");

        VBox.setMargin(signUpButton, new Insets(15, 0, 0, 0));

        Label accountLabel = new Label("Already have an account?");
        accountLabel.setPadding(loginheader1);
        accountLabel.getStyleClass().add("noacc-indicator");

        Button backButton = new Button("Login Now");
        backButton.setPadding(register);
        backButton.getStyleClass().add("register-btn");

        VBox.setMargin(backButton, new Insets(0, 0, 10, 0));

        vBoxLeft.getChildren().addAll(welcomeLabel, groupLabel, usernameLabel, usernameField, passwordLabel, passwordField, signUpButton, accountLabel, backButton);

        VBox vBoxRight = new VBox();
        vBoxRight.getStyleClass().add("vboxright");

        Rectangle imageView = new Rectangle(0, 0, 300, 320);
        imageView.setArcWidth(40.0);
        imageView.setArcHeight(40.0);

        ImagePattern pattern = new ImagePattern(
                new Image("signin-img.png", 300, 320, false, false)
        );
        imageView.setFill(pattern);
        imageView.setEffect(new DropShadow(30, Color.BROWN));

        vBoxRight.getChildren().addAll(imageView);

        Separator separator = new Separator();
        separator.setOrientation(javafx.geometry.Orientation.HORIZONTAL);
        separator.setPrefWidth(30);
        separator.setOpacity(0);

        hBox.getChildren().addAll(vBoxLeft, separator, vBoxRight);
        root.getChildren().add(hBox);

        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        root.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add("css-files/login-css.css");

        stage.setScene(scene);
        stage.show();
        centerStage(stage);

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

        return scene;
    }

    private void centerStage(Stage stage) {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);
    }
}
