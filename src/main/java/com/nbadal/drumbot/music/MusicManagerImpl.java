package com.nbadal.drumbot.music;

import io.reactivex.Observable;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class MusicManagerImpl implements MusicManager {

    private Song.Source selectedSource = null;
    private Subject<Song> nowPlayingSubject = PublishSubject.create();

    @Override
    public Observable<Song> observeNowPlaying() {
        return nowPlayingSubject
                .filter(song -> song.source.equals(selectedSource))
                .observeOn(JavaFxScheduler.platform());
    }

    @Override
    public void notifySongPlaying(Song song) {
        nowPlayingSubject.onNext(song);
    }

    @Override
    public void setSelectedSource(Song.Source source) {
        // TODO: handle previously-playing song on this source. is it still playing? etc.
        selectedSource = source;
    }
}
