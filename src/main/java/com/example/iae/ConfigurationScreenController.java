package com.example.iae;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ConfigurationScreenController {

    @FXML
    public TextField titleTF;

    @FXML
    public Label invalidL;

    @FXML
    public TextArea commandTA;

    @FXML
    public Button addButton;

    private Configuration configuration;
    private String title;
    private boolean ready = false;
    private String command;

    @FXML
    public void save(ActionEvent event) {
        title = titleTF.getText();
        command = commandTA.getText();
        configuration = new Configuration(command);
        ready = true;
        Stage stage = (Stage) addButton.getScene().getWindow();
        if (!(title.isBlank() || command.isBlank())) {
            ready = true;
            configuration = new Configuration(command);
        }
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

    public void setTitle(String title) {
        this.titleTF.setText(title);
    }

    public void setCommand(String command) {
        this.commandTA.setText(command);
    }

    public TextField getTitleTF() {
        return titleTF;
    }
}
