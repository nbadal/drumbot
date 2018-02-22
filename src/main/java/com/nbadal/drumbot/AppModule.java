package com.nbadal.drumbot;

import com.nbadal.drumbot.music.MusicManager;
import com.nbadal.drumbot.music.MusicManagerImpl;
import com.nbadal.drumbot.music.MusicSource;
import com.nbadal.drumbot.music.radio.RadioManager;
import com.nbadal.drumbot.music.radio.RadioManagerImpl;
import com.nbadal.drumbot.music.spotify.SpotifyManager;
import com.nbadal.drumbot.music.spotify.SpotifyManagerImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {
    @Provides
    @Singleton
    MusicManager provideMusicManager(SpotifyManager spotify, RadioManager radio) {
        List<MusicSource> sources = new ArrayList<>();
        sources.add(spotify);
//        sources.add(radio);
        return new MusicManagerImpl(sources);
    }

    @Provides
    @Singleton
    SpotifyManager provideSpotifyManager() {
        return new SpotifyManagerImpl();
    }

    @Provides
    @Singleton
    RadioManager provideRadioManager() {
        return new RadioManagerImpl();
    }
}
