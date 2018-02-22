package com.nbadal.drumbot.music;

import java.util.List;

import io.reactivex.Observable;

public interface MusicManager {
    List<MusicSource> getSources();

    void selectSource(MusicSource source);
    Observable<MusicSource> observeSelectedSource();

    Observable<Song> observeNowPlaying();
}
