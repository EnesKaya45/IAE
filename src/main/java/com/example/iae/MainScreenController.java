package com.example.iae;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
    public MenuItem exportProjectButton;
    public MenuItem exportConfigurationButton;

    @FXML
    private TableView<?> resultsTableView;



    // Json converters
    Gson configurationGson = new GsonBuilder().registerTypeAdapter(Configuration.class, new ConfigurationAdapter()).disableHtmlEscaping().setPrettyPrinting().create();
    Gson projectGson = new GsonBuilder().registerTypeAdapter(Project.class, new ProjectAdapter()).disableHtmlEscaping().setPrettyPrinting().create();

    // HashMaps to hold configurations and projects
    // Filenames without the extension are used to specify configurations and projects
    static HashMap<String, Configuration> configurations = new HashMap<>();
    static HashMap<String, Project> projects = new HashMap<>();
    List<File> zipFiles = new ArrayList<>();
    ArrayList<Result> results = new ArrayList<>();

    // Path to saved configurations and projects
    Path configurationsDirectory = Paths.get("configurations");
    Path projectsDirectory = Paths.get("projects");

    // Variables to hold the selected project, configuration, results
    String selectedProject;
    String selectedConfiguration;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        checkIfFoldersExists();
        refresh();

        /*

         */
        projectsLV.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                selectedProject = projectsLV.getSelectionModel().getSelectedItem();
                exportProjectButton.setDisable(false);
                configurationsLV.getSelectionModel().select(projects.get(selectedProject).getConfiguration());
                // TODO results
                // results = projects.get(selectedProject).getResults();
            }
        });

        configurationsLV.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                // When user selected another configuration, results will be derived from that configuration
                selectedConfiguration = projectsLV.getSelectionModel().getSelectedItem();
                exportConfigurationButton.setDisable(false);
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
    public void newProject(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ProjectScreen.fxml"));
            Parent root = (Parent) fxmlLoader.load();
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
    public void newConfiguration(ActionEvent event) {
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
    public void runButtonPressed(ActionEvent event) {
        ArrayList<Submission> submissions = new ArrayList<>();
        ArrayList<Thread> threads = new ArrayList<>();
        try {
            for (File file : zipFiles) {
                Submission sub = new Submission(
                        file.getName(),
                        projects.get(selectedProject),
                        configurations.get(selectedConfiguration),
                        Files.createTempDirectory(file.getName()),
                        file.toPath());
                Thread thread = new Thread(sub);
                thread.start();

                submissions.add(sub);
                threads.add(thread);
            }
            mainScreenAP.setDisable(true);

            // TODO Run'a basıldıktan sonra beklet
            /*
            long beginTime = System.currentTimeMillis();
            boolean finished = false;
            while (System.currentTimeMillis() - beginTime < 10000) {
                for (Thread t : threads) {
                    if t.
                }
            }
            mainScreenAP.setDisable(false);
             */
            Thread.sleep(5000);
            for (Thread t : threads) if (!t.isInterrupted()) t.interrupt();
            for (Submission s : submissions) {
                results.add(new Result(s.getZip(), s.getResult()));
            }
            for (Result r : results) {
                // TODO sonuçları resultsLV'ye yazdır.
                System.out.println(r.getPath() + " result is: " + r.getResult());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
                        Files.copy(file.toPath(), projectsDirectory, StandardCopyOption.REPLACE_EXISTING);
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
                        Files.copy(file.toPath(), configurationsDirectory, StandardCopyOption.REPLACE_EXISTING);
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
    public void importProject(ActionEvent event){
        FileChooser fc = new FileChooser();
        Stage stage = new Stage();
        List<File> files = fc.showOpenMultipleDialog(stage);
        String answer = "Cancel";
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
    public void exportProject(ActionEvent event) {
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
    public void importConfiguration(ActionEvent event){
        FileChooser fc = new FileChooser();
        Stage stage = new Stage();
        List<File> files = fc.showOpenMultipleDialog(stage);
        String answer = "Cancel";
        if(files != null) {
            for (File file : files) {
                if (configurations.containsKey(removeExtension(file.getName()))) {
                    answer = ask("Some .configuration files will be overwritten.", "Cancel", "OK");
                    break;
                }
            }

            if (answer.equals("OK")) {
                for (File file : files) {
                    if (file.getName().endsWith(".configuration")) {
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
    public void exportConfiguration(ActionEvent event){
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

    // Refresh all saved files whenever any file is added/edited/deleted
    public void refresh() {
        configurations.clear();
        projects.clear();
        results.clear();
        projectsLV.getItems().clear();
        configurationsLV.getItems().clear();

        configurations = getSavedConfigurations();
        projects = getSavedProjects();

        configurationsLV.getItems().addAll(configurations.keySet());

        // Disable selecting configuration until a project is selected
        exportConfigurationButton.setDisable(true);
        exportProjectButton.setDisable(true);
        projectsLV.getItems().addAll(projects.keySet());

        selectedProject = "";
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
        Optional<String> result = dialog.showAndWait();
        return result.orElse(defaultOption);
    }

    public Stage getStage() {
        return (Stage) mainScreenAP.getScene().getWindow();
    }
}
