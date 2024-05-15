package com.example.iae;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

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
    }
}