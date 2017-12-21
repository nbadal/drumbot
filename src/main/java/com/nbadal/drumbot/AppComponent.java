package com.nbadal.drumbot;

import com.nbadal.drumbot.controllers.ControlsController;
import com.nbadal.drumbot.controllers.OverlayController;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {
    void inject(DrumbotApplication app);
    void inject(ControlsController controller);
    void inject(OverlayController controller);
}
