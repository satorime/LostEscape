package com.example.lostescape.Server;

import com.example.lostescape.OtherGameElements;
import com.example.lostescape.World;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class ProfileSetting implements OtherGameElements {
    private double xOffset = 0;
    private double yOffset = 0;

    public Scene getSettingScene(Stage stage) {
        Insets header = new Insets(-10, 0, -13, 0);
        Insets loginheader1 = new Insets(13, 0, 0, 0);
        Insets loginheader2 = new Insets(5, 0, 0, 0);
        Insets register = new Insets(-2, 0, -15, 0);

        AnchorPane root = new AnchorPane();
        root.getStyleClass().add("hbox");

        VBox vBoxCenter = new VBox();
        vBoxCenter.setPrefWidth(300);
        vBoxCenter.getStyleClass().add("vboxcenter");
        vBoxCenter.setAlignment(Pos.CENTER);
        vBoxCenter.setSpacing(10);

        Label welcomeLabel = new Label("Profile Settings");
        welcomeLabel.setPadding(header);
        welcomeLabel.getStyleClass().add("welcome");
        welcomeLabel.setAlignment(Pos.CENTER);

        Label groupLabel = new Label("Change is a must, begin with your password");
        groupLabel.setMaxWidth(Double.MAX_VALUE);
        groupLabel.setAlignment(Pos.CENTER);
        groupLabel.setStyle("-fx-translate-y: -5px;");
        groupLabel.getStyleClass().add("group");

        Label oldUsernameLabel = new Label("Old Password");
        oldUsernameLabel.setPadding(loginheader1);
        oldUsernameLabel.setMaxWidth(Double.MAX_VALUE);
        oldUsernameLabel.setAlignment(Pos.BOTTOM_LEFT);
        oldUsernameLabel.setStyle("-fx-font-size: 15px; -fx-padding: 0 0 0 10px;");
        oldUsernameLabel.getStyleClass().add("login-indicator");

        TextField oldUsernameField = new TextField();
        oldUsernameField.setPromptText("Enter your old password");
        oldUsernameField.getStyleClass().add("login-field");

        Label newUsernameLabel = new Label("New Password");
        newUsernameLabel.setPadding(loginheader1);
        newUsernameLabel.setMaxWidth(Double.MAX_VALUE);
        newUsernameLabel.setAlignment(Pos.BOTTOM_LEFT);
        newUsernameLabel.setStyle("-fx-font-size: 15px; -fx-padding: 0 0 0 10px;");
        newUsernameLabel.getStyleClass().add("login-indicator");

        TextField newUsernameField = new TextField();
        newUsernameField.setPromptText("Enter your new password");
        newUsernameField.getStyleClass().add("login-field");

        Button changeName = new Button("Change Password");
        changeName.setPadding(header);
        changeName.setMaxWidth(Double.MAX_VALUE);
        changeName.setAlignment(Pos.CENTER);
        changeName.getStyleClass().add("change-btn");

        Button deleteAccount = new Button("Delete Account");
        deleteAccount.setPadding(header);
        deleteAccount.setMaxWidth(Double.MAX_VALUE);
        deleteAccount.setAlignment(Pos.CENTER);
        deleteAccount.getStyleClass().add("change-btn");

        Button back = new Button("◀");
        back.setPadding(new Insets(5));
        back.setAlignment(Pos.TOP_LEFT);
        back.setStyle("-fx-translate-x:20px; -fx-translate-y:20px; -fx-border-radius: 5px; -fx-background-radius: 5px;");
        back.getStyleClass().add("login-btn");

        back.setOnAction(event -> {
            World world = new World();
            world.startGame(stage);
        });

        root.getChildren().add(back);


        vBoxCenter.getChildren().addAll(welcomeLabel, groupLabel, oldUsernameLabel, oldUsernameField, newUsernameLabel, newUsernameField, changeName, deleteAccount);

        root.getChildren().add(vBoxCenter);
        AnchorPane.setTopAnchor(vBoxCenter, 50.0);
        AnchorPane.setLeftAnchor(vBoxCenter, 50.0);
        AnchorPane.setRightAnchor(vBoxCenter, 50.0);
        AnchorPane.setBottomAnchor(vBoxCenter, 50.0);

        Scene scene = new Scene(root, 500, 400);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add("css-files/login-css.css");

        stage.setTitle("Profile Settings");
        stage.setScene(scene);
        stage.show();
        centerStage(stage);

        changeName.setOnAction(event -> {
            String oldPassword = oldUsernameField.getText();
            String newPassword = newUsernameField.getText();
            DatabaseManager dbManager = DatabaseManager.getInstance();
            Status status = dbManager.updatePassword(CurrentUser.userID, oldPassword, newPassword);

            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Change Password");
            alert.setHeaderText(null);
            if (status == Status.ACCOUNT_UPDATED_SUCCESSFULLY) {
                alert.setContentText("Password changed successfully!");
            } else {
                alert.setContentText("Failed to change password. Please try again.");
            }
            alert.showAndWait();
        });

        // Event handler for deleting account
        deleteAccount.setOnAction(event -> {
            DatabaseManager dbManager = DatabaseManager.getInstance();
            Status status = dbManager.deleteUser(CurrentUser.userID);

            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Delete Account");
            alert.setHeaderText(null);
            if (status == Status.ACCOUNT_DELETED_SUCCESSFULLY) {
                alert.setContentText("Account deleted successfully!");
                stage.close();
                // Add logic to redirect to the login page or close the application
            } else {
                alert.setContentText("Failed to delete account. Please try again.");
            }
            alert.showAndWait();
        });

        return scene;
    }

    @Override
    public void centerStage(Stage stage) {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);
    }

    @Override
    public void setupMouseEvents(Node root, Stage stage) {
        // Uncomment if needed for draggable window
        // root.setOnMousePressed(event -> {
        //     xOffset = event.getSceneX();
        //     yOffset = event.getSceneY();
        // });
        //
        // root.setOnMouseDragged(event -> {
        //     stage.setX(event.getScreenX() - xOffset);
        //     stage.setY(event.getScreenY() - yOffset);
        // });
    }
}
