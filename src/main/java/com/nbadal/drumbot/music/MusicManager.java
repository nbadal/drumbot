package com.nbadal.drumbot.music;

import io.reactivex.Observable;

public interface MusicManager {
    void notifySongPlaying(Song song);
    Observable<Song> observeNowPlaying();

    void setSelectedSource(Song.Source source);
    Observable<Song.Source> observeSelectedSource();
}
