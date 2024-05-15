package com.example.iae;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ConfigurationScreenController {

    @FXML
    public TextField titleTF;

    @FXML
    public Label invalidL;

    @FXML
    public TextArea commandTA;

    @FXML
    public Button addButton;

    private String title;
    private Configuration configuration;
    private boolean ready = false;

    @FXML
    public void save(ActionEvent event) {
        title = titleTF.getText();
        String command = commandTA.getText();
        configuration = new Configuration(command);
        ready = true;
        Stage stage = (Stage) addButton.getScene().getWindow();
        stage.close();
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public String getTitle() {
        return title;
    }

    public boolean isReady() {
        return ready;
    }
}
