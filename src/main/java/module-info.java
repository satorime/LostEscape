module com.example.lostescape {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires javafx.graphics;
    requires java.sql;

    opens com.example.lostescape to javafx.fxml;
    exports com.example.lostescape to javafx.graphics;
    exports Gunner to javafx.graphics;
    exports FlappyRato to javafx.graphics;
}
