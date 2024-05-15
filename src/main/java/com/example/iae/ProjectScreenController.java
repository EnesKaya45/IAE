package com.example.iae;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

public class ProjectScreenController implements Initializable{
    @FXML
    public TextField projectTitleTF;

    @FXML
    public ListView<String> configurationsLV;

    @FXML
    public TextField filesToCompileTF;

    @FXML
    public TextField mainFileToRunTF;

    @FXML
    public TextField argumentsTF;

    @FXML
    public TextArea expectedOutputTA;

    @FXML
    public ListView submissionsLV;

    @FXML
    public Button addButton;

    // Todo bir ara bak
    private Project project;
    private String title;
    private boolean ready = false;
    private List<File> files  = new ArrayList<>();

    @FXML
    public void save(ActionEvent event) {
        title = projectTitleTF.getText();
        String configurationTitle = configurationsLV.getSelectionModel().getSelectedItem();
        String filesToCompile = filesToCompileTF.getText();
        String mainFileToRun = mainFileToRunTF.getText();
        String arguments = argumentsTF.getText();
        String expectedOutput = expectedOutputTA.getText();
        List<File> submissionZipFiles = files;
        Stage stage = (Stage) addButton.getScene().getWindow();
        if (!(title.isBlank() || configurationTitle.isBlank() || expectedOutput.isBlank())) {
            ready = true;
            project = new Project(configurationTitle, filesToCompile, mainFileToRun, arguments, expectedOutput, submissionZipFiles);
            stage.close();
        } else stage.close();
    }

    public Project getProject() {
        return project;
    }

    public String getTitle() {
        return title;
    }

    // TODO Edit screen
    public void init(String title, Project project, HashMap<String, Configuration> configurations) {
        projectTitleTF.setText(title);
        filesToCompileTF.setText(project.getFilesToCompile());
        mainFileToRunTF.setText(project.getMainFileToRun());
        argumentsTF.setText(project.getArguments());
        expectedOutputTA.setText(project.getExpectedOutput());
        // this.configurations = configurations;
        configurationsLV.getItems().addAll(configurations.keySet());
    }

    // TODO File chooser
    public void selectFiles(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All Files", "*.zip"));
        Stage stage = new Stage();
        files = fileChooser.showOpenMultipleDialog(stage);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurationsLV.getItems().addAll(MainScreenController.configurations.keySet());
    }

    public boolean isReady() {
        return ready;
    }
}
