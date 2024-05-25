package rpgworld.Scene;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import rpgworld.Server.DatabaseManager;
import rpgworld.Server.Status;

import java.util.ArrayList;
import java.util.List;

public class SignUpScene {
    private List<ImageView> imageViews = new ArrayList<>();
    private int currentIndex = 0;

    public Scene getSignUpScene(Stage stage) {
        // VBox for the sign-up form
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.CENTER_LEFT);
        VBox.setVgrow(vbox, Priority.ALWAYS);

        Label signUpLabel = new Label("Sign up");
        signUpLabel.getStyleClass().add("signup-label");

        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter your username");
        usernameField.getStyleClass().add("text-field");

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.getStyleClass().add("password-field");

        Button signUpButton = new Button("Sign Up");
        signUpButton.setMaxWidth(Double.MAX_VALUE);

        Button backButton = new Button("Back to Login");
        backButton.setMaxWidth(Double.MAX_VALUE);

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

        vbox.getChildren().addAll(signUpLabel, usernameLabel, usernameField, passwordLabel, passwordField, signUpButton, backButton);

        // StackPane to hold the form
        StackPane formPane = new StackPane(vbox);
        StackPane.setAlignment(vbox, Pos.CENTER_LEFT);
        StackPane.setMargin(vbox, new Insets(10));

        // Load images and create ImageViews
        String[] imagePaths = {
                "jeastel.jpg",
                "jake.jpg",
                "hanz.jpg",
                "brix.jpg"
        };

        for (String imagePath : imagePaths) {
            ImageView imageView = new ImageView(new Image(imagePath));
            imageView.setPreserveRatio(true);
            imageView.fitWidthProperty().bind(formPane.widthProperty().multiply(0.5));
            imageView.fitHeightProperty().bind(formPane.heightProperty());
            imageView.setVisible(false);
            imageViews.add(imageView);
        }

        // Initially show the first image
        imageViews.get(0).setVisible(true);

        // StackPane to manage images
        StackPane imagePane = new StackPane();
        imagePane.setAlignment(Pos.CENTER_RIGHT);
        imagePane.getChildren().addAll(imageViews);

        // Create a timeline to switch images every 2 seconds
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), event -> {
            imageViews.get(currentIndex).setVisible(false);
            currentIndex = (currentIndex + 1) % imageViews.size();
            imageViews.get(currentIndex).setVisible(true);
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        // BorderPane to hold the background image and the form
        BorderPane borderPane = new BorderPane();
        borderPane.setRight(imagePane);
        borderPane.setCenter(formPane);

        Scene scene = new Scene(borderPane, 800, 600);
        scene.getStylesheets().add("styles.css");  // Add path to your CSS file

        return scene;
    }
}
