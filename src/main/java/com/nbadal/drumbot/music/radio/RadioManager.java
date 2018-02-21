package com.nbadal.drumbot.music.radio;

import io.reactivex.Completable;

public interface RadioManager {
    Completable connect();

    Completable reconnect();
}
