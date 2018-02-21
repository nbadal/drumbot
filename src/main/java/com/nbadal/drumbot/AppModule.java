package com.nbadal.drumbot;

import com.nbadal.drumbot.music.MusicManager;
import com.nbadal.drumbot.music.MusicManagerImpl;
import com.nbadal.drumbot.music.radio.RadioManager;
import com.nbadal.drumbot.music.radio.RadioManagerImpl;
import com.nbadal.drumbot.music.spotify.SpotifyManager;
import com.nbadal.drumbot.music.spotify.SpotifyManagerImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {
    @Provides
    @Singleton
    MusicManager provideMusicManager() {
        return new MusicManagerImpl();
    }

    @Provides
    @Singleton
    SpotifyManager provideSpotifyManager(MusicManager manager) {
        return new SpotifyManagerImpl(manager);
    }

    @Provides
    @Singleton
    RadioManager provideRadioManager(MusicManager manager) {
        return new RadioManagerImpl(manager);
    }
}
