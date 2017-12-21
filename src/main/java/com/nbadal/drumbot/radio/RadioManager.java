package com.nbadal.drumbot.radio;

import io.reactivex.Completable;

public interface RadioManager {
    Completable connect();

    Completable reconnect();
}
