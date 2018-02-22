package com.nbadal.drumbot;

import com.nbadal.drumbot.controllers.ControlsController;
import com.nbadal.drumbot.controllers.OverlayController;
import com.nbadal.drumbot.lifecycle.LifecycleManager;

import javax.inject.Inject;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DrumbotApplication extends Application {

    private AppComponent appComponent;

    @Inject
    LifecycleManager lifecycleManager;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage overlayStage) throws Exception {
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule())
                .build();
        appComponent.inject(this);

        final FXMLLoader overlayLoader = new FXMLLoader(getClass().getResource("/overlay.fxml"));
        overlayLoader.setControllerFactory(this::createController);
        final Parent overlay = overlayLoader.load();
        overlayStage.setTitle("Drumbot Overlay");
        overlayStage.setScene(new Scene(overlay, 300, 275));
        overlayStage.show();

        final FXMLLoader controlsLoader = new FXMLLoader(getClass().getResource("/controls.fxml"));
        controlsLoader.setControllerFactory(this::createController);
        final Parent controls = controlsLoader.load();
        final Stage controlsStage = new Stage();
        controlsStage.setTitle("Drumbot Controls");
        controlsStage.setScene(new Scene(controls, 300, 275));
        controlsStage.show();

        lifecycleManager.onStart();
    }

    @Override
    public void stop() throws Exception {
        lifecycleManager.onStop();
    }

    public Object createController(Class<?> type) {
        if (type == OverlayController.class) {
            OverlayController controller = new OverlayController();
            appComponent.inject(controller);
            return controller;
        } else if (type == ControlsController.class) {
            ControlsController controller = new ControlsController();
            appComponent.inject(controller);
            return controller;
        }
        return null;
    }

}
