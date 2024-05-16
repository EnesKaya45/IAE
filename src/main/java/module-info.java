module com.example.iae {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires zip4j;

    opens com.example.iae to javafx.fxml;
    exports com.example.iae;
}