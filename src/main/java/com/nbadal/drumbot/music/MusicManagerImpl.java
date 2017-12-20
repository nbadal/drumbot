package com.nbadal.drumbot.music;

import io.reactivex.Observable;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class MusicManagerImpl implements MusicManager {

    private Subject<Song> nowPlayingSubject = PublishSubject.create();

    @Override
    public Observable<Song> observeNowPlaying() {
        return nowPlayingSubject.observeOn(JavaFxScheduler.platform());
    }

    @Override
    public void notifySongPlaying(Song song) {
        nowPlayingSubject.onNext(song);
    }
}
