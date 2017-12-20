package com.nbadal.drumbot;

import com.nbadal.drumbot.controllers.ControlsController;
import com.nbadal.drumbot.controllers.OverlayController;
import com.nbadal.drumbot.music.MusicManager;
import com.nbadal.drumbot.spotify.SpotifyManager;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {
    void inject(ControlsController controller);
    void inject(OverlayController controller);
    void inject(SpotifyManager manager);
    void inject(MusicManager manager);
}
