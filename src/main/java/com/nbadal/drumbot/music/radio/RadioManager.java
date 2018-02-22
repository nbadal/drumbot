package com.nbadal.drumbot.music.radio;

import com.nbadal.drumbot.music.MusicSource;

import io.reactivex.Completable;

public interface RadioManager extends MusicSource {
    Completable connect();

    Completable reconnect();
}
