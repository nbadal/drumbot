package com.nbadal.drumbot.music;

import io.reactivex.Observable;

public interface MusicSource {
    Observable<? extends Song> observeCurrentlyPlaying();
}
