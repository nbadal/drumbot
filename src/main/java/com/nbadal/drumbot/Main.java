package com.nbadal.drumbot;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage overlayStage) throws Exception {
        final Parent overlay = FXMLLoader.load(getClass().getResource("/overlay.fxml"));
        overlayStage.setTitle("Drumbot Overlay");
        overlayStage.setScene(new Scene(overlay, 300, 275));
        overlayStage.show();

        final Parent controls = FXMLLoader.load(getClass().getResource("/controls.fxml"));
        final Stage controlsStage = new Stage();
        controlsStage.setTitle("Drumbot Controls");
        controlsStage.setScene(new Scene(controls, 300, 275));
        controlsStage.show();
    }

}
