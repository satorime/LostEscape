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
import rpgworld.World;

import java.util.ArrayList;
import java.util.List;

public class LoginScene {
    private List<ImageView> imageViews = new ArrayList<>();
    private int currentIndex = 0;

    public Scene getLoginScene(Stage stage) {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.CENTER_LEFT);
        VBox.setVgrow(vbox, Priority.ALWAYS);

        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter your username");
        usernameField.getStyleClass().add("text-field");

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.getStyleClass().add("password-field");

        CheckBox staySignedIn = new CheckBox("Stay signed in");

        Button loginButton = new Button("Login");
        loginButton.setMaxWidth(Double.MAX_VALUE);

        Button signUpButton = new Button("Create account");
        signUpButton.setMaxWidth(Double.MAX_VALUE);

        loginButton.setOnAction(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            DatabaseManager dbManager = DatabaseManager.getInstance();
            Status status = dbManager.validate(username, password);
            if (status == Status.LOGIN_SUCCESS) {
                World world = new World();
                world.startGame(stage);
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

        vbox.getChildren().addAll(usernameLabel, usernameField, passwordLabel, passwordField, staySignedIn, loginButton, signUpButton);

        StackPane formPane = new StackPane(vbox);
        StackPane.setAlignment(vbox, Pos.CENTER_LEFT);
        StackPane.setMargin(vbox, new Insets(10));

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

        imageViews.get(0).setVisible(true);

        StackPane imagePane = new StackPane();
        imagePane.setAlignment(Pos.CENTER_RIGHT);
        imagePane.getChildren().addAll(imageViews);

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), event -> {
            imageViews.get(currentIndex).setVisible(false);
            currentIndex = (currentIndex + 1) % imageViews.size();
            imageViews.get(currentIndex).setVisible(true);
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        BorderPane borderPane = new BorderPane();
        borderPane.setRight(imagePane);
        borderPane.setCenter(formPane);

        Scene scene = new Scene(borderPane, 800, 600);
        scene.getStylesheets().add("styles.css");

        return scene;
    }
}
