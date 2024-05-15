package com.example.iae;

import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.structure.Group;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("MainScreen.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Integrated Assignment Environment");
        stage.setScene(scene);
        try{
            stage.getIcons().add(new Image(getClass().getResourceAsStream("ieu_logo_en_3.jpg")));
        } catch (Exception e) {
            System.out.println("Warning: Icon not found.");
        }
        stage.show();
    }

    public static void main(String[] args) {
        launch();
        /*
        Gson configurationGson = new GsonBuilder().registerTypeAdapter(Configuration.class, new ConfigurationAdapter()).disableHtmlEscaping().setPrettyPrinting().create();
        Gson projectGson = new GsonBuilder().registerTypeAdapter(Project.class, new ProjectAdapter()).disableHtmlEscaping().setPrettyPrinting().create();

        Project pro = new Project(
                "python",
                "",
                "main.py",
                "",
                "Hello World\n"
        );
        try (FileWriter fw = new FileWriter("deneme.json")) {
            projectGson.toJson(pro, fw);
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
    }
}