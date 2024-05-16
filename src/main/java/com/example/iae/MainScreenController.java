package com.example.iae;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.*;

import java.io.*;
import java.io.File;
import java.net.URL;
import java.nio.file.*;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

public class MainScreenController implements Initializable {

    @FXML
    public ListView<String> projectsLV;

    @FXML
    public ListView<String> configurationsLV;

    @FXML
    public ListView<String> zipFilesLV;

    @FXML
    public AnchorPane mainScreenAP;

    @FXML
    public MenuItem exportProjectButton;

    @FXML
    public MenuItem exportConfigurationButton;
    public MenuItem editConfigurationButton;
    public MenuItem deleteConfigurationButton;
    public Label statusL;

    @FXML
    private TableView<Result> resultsTV;

    @FXML
    private TableColumn<Result, File> submittedFilesTC;

    @FXML
    private TableColumn<Result, String> resultsTC;

    // Json converters
    Gson configurationGson = new GsonBuilder().registerTypeAdapter(Configuration.class, new ConfigurationAdapter()).disableHtmlEscaping().setPrettyPrinting().create();
    Gson projectGson = new GsonBuilder().registerTypeAdapter(Project.class, new ProjectAdapter()).disableHtmlEscaping().setPrettyPrinting().create();

    // HashMaps to hold configurations and projects
    // Filenames without the extension are used to specify configurations and projects
    static HashMap<String, Configuration> configurations = new HashMap<>();
    static HashMap<String, Project> projects = new HashMap<>();

    ArrayList<Result> results = new ArrayList<>();

    // Path to saved configurations and projects
    Path configurationsDirectory = Paths.get("configurations");
    Path projectsDirectory = Paths.get("projects");

    // Variables to hold the selected project, configuration, results
    String selectedProject;
    String selectedConfiguration;
    List<File> zipFiles = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        checkIfFoldersExists();
        refresh();

        // Prepare table view columns
        submittedFilesTC.setCellValueFactory(new PropertyValueFactory<>("file"));
        resultsTC.setCellValueFactory(new PropertyValueFactory<>("result"));

        projectsLV.getSelectionModel().selectedItemProperty().addListener((observableValue, s, t1) -> {
            selectedProject = projectsLV.getSelectionModel().getSelectedItem();
            if (selectedProject!=null) {
                exportProjectButton.setDisable(false);
                configurationsLV.getSelectionModel().select(projects.get(selectedProject).getConfiguration());
                selectedConfiguration = projects.get(selectedProject).getConfiguration();

                zipFilesLV.getItems().clear();
                zipFiles = projects.get(selectedProject).getSubmissionZipFiles();
                for (File f : zipFiles) zipFilesLV.getItems().add(f.getName());

                // Get results of the selected project, convert to observable list, show results on the screen
                resultsTV.setItems(FXCollections.observableArrayList(projects.get(selectedProject).getResults()));
            }
        });

        configurationsLV.getSelectionModel().selectedItemProperty().addListener((observableValue, s, t1) -> {
            selectedConfiguration = configurationsLV.getSelectionModel().getSelectedItem();
            if (selectedConfiguration!=null) {
                exportConfigurationButton.setDisable(false);
                editConfigurationButton.setDisable(false);
                deleteConfigurationButton.setDisable(false);
            }
        });
    }

    private HashMap<String, Project> getSavedProjects() {
        HashMap<String, Project> returnArray = new HashMap<>();
        Set<File> files = getFilesInTheDirectory(projectsDirectory);
        for (File file : files) {
            if (file.getName().endsWith(".project")) {
                try (FileReader reader = new FileReader(file.getAbsolutePath())) {
                    Project project = projectGson.fromJson(reader, Project.class);
                    returnArray.put(removeExtension(file.getName()), project);
                } catch (Exception e) {e.printStackTrace();}
            }
        }
        return returnArray;
    }

    private HashMap<String, Configuration> getSavedConfigurations() {
        HashMap<String, Configuration> returnArray = new HashMap<>();
        Set<File> files = getFilesInTheDirectory(configurationsDirectory);

        for (File file : files) {
            if (file.getName().endsWith(".configuration")) {
                try (FileReader reader = new FileReader(file.getAbsolutePath())){
                    Configuration configuration = configurationGson.fromJson(reader, Configuration.class);
                    returnArray.put(removeExtension(file.getName()), configuration);
                }
                catch (Exception e) {e.printStackTrace();}
            }
        }
        return returnArray;
    }


    @FXML
    public void newProject() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ProjectScreen.fxml"));
            Parent root = fxmlLoader.load();
            ProjectScreenController projectScreenController = fxmlLoader.getController();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(getStage());
            stage.setScene(new Scene(root));
            stage.showAndWait();

            if (projectScreenController.isReady()) {
                Project project = projectScreenController.getProject();
                File path = projectsDirectory.resolve(Path.of(projectScreenController.getTitle() + ".project")).toFile();
                try (FileWriter fw = new FileWriter(path)){
                    projectGson.toJson(project, fw);
                }
                refresh();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void newConfiguration() {
        try {
            FXMLLoader fxmlLoader  = new FXMLLoader(getClass().getResource("ConfigurationScreen.fxml"));
            Parent root = fxmlLoader.load();
            ConfigurationScreenController configurationScreenController = fxmlLoader.getController();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(getStage());
            stage.setScene(new Scene(root));
            stage.showAndWait();

            if (configurationScreenController.isReady()) {
                Configuration configuration = configurationScreenController.getConfiguration();
                File path = configurationsDirectory.resolve(Path.of(configurationScreenController.getTitle() + ".configuration")).toFile();
                try (FileWriter fw = new FileWriter(path)) {
                    configurationGson.toJson(configuration, fw);
                }
                refresh();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void runButtonPressed() {
        if (selectedProject == null || selectedProject.isBlank()) {
            warn("Project is not selected");
            return;
        }
        if (selectedConfiguration == null || selectedConfiguration.isBlank()) {
            warn("Configuration is not selected.");
            return;
        }
        if (zipFiles.isEmpty()) {
            warn("No zip file is selected.");
            return;
        }
        for (File f : zipFiles) {
            if (!f.exists()) {
                warn("Zip files not found.");
                return;
            }
        }
        statusL.setText("Executing...");
        ArrayList<Submission> submissions = new ArrayList<>();
        ArrayList<Thread> threads = new ArrayList<>();
        resultsTV.getItems().clear();
        for (File file : zipFiles) {
            Submission sub = new Submission(
                    file.getName(),
                    projects.get(selectedProject),
                    configurations.get(selectedConfiguration),
                    file);
            Thread thread = new Thread(sub);
            thread.start();

            submissions.add(sub);
            threads.add(thread);
        }
        // Wait only if there are unfinished threads, up to 5 seconds

        mainScreenAP.setDisable(true);
        boolean complete;
        long begin = Instant.now().getEpochSecond();
        do {
            complete = true;
            for (Thread t : threads) {
                if (!t.isAlive()) {
                    complete = false;
                    break;
                }
            }
        }
        while (Instant.now().getEpochSecond() - begin < 10 || complete);
        mainScreenAP.setDisable(false);



        // Stop any unfinished (infinite loop) threads
        for (Thread t : threads) if (!t.isInterrupted()) t.interrupt();
        for (Submission s : submissions) results.add(new Result(s.getZip(), s.getResult()));

        projects.get(selectedProject).setResults(results);
        projects.get(selectedProject).setSubmissionZipFiles(zipFiles);
        File path = projectsDirectory.resolve(Path.of(selectedProject + ".project")).toFile();
        try (FileWriter fw = new FileWriter(path)){
            projectGson.toJson(projects.get(selectedProject), fw);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        resultsTV.setItems(FXCollections.observableArrayList(results));
        String previous = selectedProject;
        refresh();
        selectedProject = previous;
        statusL.setText("Submitted files and results are saved.");
        projectsLV.getSelectionModel().select(selectedProject);
    }

    @FXML
    public void draggedOver(DragEvent event) {
        if (event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.ANY);
        }
    }
    
    @FXML
    public void projectFilesDropped(DragEvent event) {
        List<File> files = event.getDragboard().getFiles();

        String answer = "OK";
        for (File file : files) {
            if (projects.containsKey(removeExtension(file.getName()))) {
                answer = ask("Some .project files will be overwritten.", "Cancel", "OK");
                break;
            }
        }

        if (answer.equals("OK")){
            for (File file : files) {
                if (file.getName().endsWith(".project")) {
                    try {
                        Files.copy(file.toPath(), projectsDirectory.resolve(file.getName()), StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        refresh();
    }

    @FXML
    public void configurationFilesDropped(DragEvent event) {
        List<File> files = event.getDragboard().getFiles();

        String answer = "OK";
        for (File file : files) {
            if (projects.containsKey(removeExtension(file.getName()))) {
                answer = ask("Some .configuration files will be overwritten.", "Cancel", "OK");
                break;
            }
        }

        if (answer.equals("OK")) {
            for (File file : files) {
                if (file.getName().endsWith(".configuration")) {
                    try {
                        Files.copy(file.toPath(), configurationsDirectory.resolve(file.getName()), StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        refresh();
    }

    @FXML
    public void zipFilesDropped(DragEvent event) {

        List<File> files = event.getDragboard().getFiles();

        zipFilesLV.getItems().clear();
        zipFiles.clear();

        for (File file : files) {
            if (file.getName().endsWith(".zip")) {
                zipFilesLV.getItems().add(file.getName());
                zipFiles.add(file);
            }
        }
    }

    @FXML
    public void importProject(){
        FileChooser fc = new FileChooser();
        Stage stage = new Stage();
        List<File> files = fc.showOpenMultipleDialog(stage);
        String answer = "OK";
        if(files != null) {
            for (File file : files) {
                if (projects.containsKey(removeExtension(file.getName()))) {
                    answer = ask("Some .project files will be overwritten.", "Cancel", "OK");
                    break;
                }
            }

            if (answer.equals("OK")) {
                for (File file : files) {
                    if (file.getName().endsWith(".project")) {
                        try {
                            Files.copy(file.toPath(), projectsDirectory.resolve(file.getName()), StandardCopyOption.REPLACE_EXISTING);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        refresh();

    }

    @FXML
    public void exportProject() {
        DirectoryChooser dc = new DirectoryChooser();
        Stage stage = new Stage();
        File f = dc.showDialog(stage);
        if (f != null) {
            Path path = f.toPath();
            File file = path.resolve(Path.of(selectedProject + ".project")).toFile();
            try (FileWriter fw = new FileWriter(file)) {
                projectGson.toJson(projects.get(selectedProject), fw);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    public void importConfiguration(){
        FileChooser fc = new FileChooser();
        Stage stage = new Stage();
        List<File> files = fc.showOpenMultipleDialog(stage);
        String answer = "OK";
        if(files != null) {
            for (File file : files) {
                if (configurations.containsKey(removeExtension(file.getName()))) {
                    answer = ask("Some .configuration files will be overwritten.", "Cancel", "OK");
                    break;
                }
            }

            if (answer.equals("OK")) {
                for (File file : files) {
                    System.out.println(file);
                    if (file.getName().endsWith(".configuration")) {
                        try {
                            Files.copy(file.toPath(), configurationsDirectory.resolve(file.getName()), StandardCopyOption.REPLACE_EXISTING);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        refresh();
    }

    @FXML
    public void exportConfiguration(){
        DirectoryChooser dc = new DirectoryChooser();
        Stage stage = new Stage();
        File f = dc.showDialog(stage);
        if (f != null) {
            Path path = f.toPath();
            File file = path.resolve(Path.of(selectedConfiguration + ".configuration")).toFile();
            try (FileWriter fw = new FileWriter(file)) {
                configurationGson.toJson(configurations.get(selectedConfiguration), fw);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    public void editConfiguration(){
        try {
            FXMLLoader fxmlLoader  = new FXMLLoader(getClass().getResource("ConfigurationScreen.fxml"));
            Parent root = fxmlLoader.load();
            ConfigurationScreenController configurationScreenController = fxmlLoader.getController();
            configurationScreenController.setTitle(selectedConfiguration);
            configurationScreenController.setCommand(configurations.get(selectedConfiguration).getCommand());

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(getStage());
            stage.setScene(new Scene(root));
            stage.showAndWait();

            if (configurationScreenController.isReady()) {
                Configuration configuration = configurationScreenController.getConfiguration();
                File path = configurationsDirectory.resolve(Path.of(configurationScreenController.getTitle() + ".configuration")).toFile();
                try (FileWriter fw = new FileWriter(path)) {
                    configurationGson.toJson(configuration, fw);
                }
                refresh();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void deleteConfiguration(){
        try {
            Files.deleteIfExists(configurationsDirectory.resolve(Path.of(selectedConfiguration + ".configuration")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        refresh();
    }

    private void checkIfFoldersExists() {
        if (!Files.exists(configurationsDirectory)) {
            try {
                Files.createDirectory(configurationsDirectory);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (!Files.exists(projectsDirectory)) {
            try {
                Files.createDirectory(projectsDirectory);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // Refresh all whenever any file is added/edited/deleted
    public void refresh() {
        configurations.clear();
        projects.clear();
        results.clear();
        zipFiles.clear();

        projectsLV.getItems().clear();
        configurationsLV.getItems().clear();
        zipFilesLV.getItems().clear();
        resultsTV.getItems().clear();

        configurations = getSavedConfigurations();
        projects = getSavedProjects();

        exportConfigurationButton.setDisable(true);
        exportProjectButton.setDisable(true);
        editConfigurationButton.setDisable(true);
        deleteConfigurationButton.setDisable(true);

        configurationsLV.getItems().addAll(configurations.keySet());
        projectsLV.getItems().addAll(projects.keySet());
        statusL.setText("Press button above to run");

        selectedProject = "";
        selectedConfiguration = "";
    }

    public Set<File> getFilesInTheDirectory(Path dir) {
        try (Stream<Path> stream = Files.list(dir)) {
            return stream
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String removeExtension(String s) {
        if (s.indexOf(".") > 0) {
            return s.substring(0, s.lastIndexOf("."));
        } else {
            return s;
        }
    }

    public String ask(String question, String defaultOption, String otherOption) {
        ChoiceDialog<String> dialog = new ChoiceDialog<>(defaultOption, otherOption);
        dialog.setTitle(null);
        dialog.setHeaderText(null);
        dialog.setContentText(question);
        dialog.initOwner(getStage());
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(getStage());
        Optional<String> result = dialog.showAndWait();
        return result.orElse(defaultOption);
    }

    public void warn(String context) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle(null);
        a.setHeaderText(null);
        a.setContentText(context);
        a.initOwner(getStage());
        a.show();
    }

    public Stage getStage() {
        return (Stage) mainScreenAP.getScene().getWindow();
    }
}
