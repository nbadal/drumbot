package com.nbadal.drumbot.music;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class MusicManagerImpl implements MusicManager {

    private final List<MusicSource> sources;

    private MusicSource selectedSource = null;
    private final Subject<MusicSource> selectedSourceSubject = PublishSubject.create();

    private final Subject<Song> currentlyPlayingSubject = PublishSubject.create();

    @Inject
    public MusicManagerImpl(List<MusicSource> sources) {
        this.sources = sources;
        for (MusicSource source : sources) {
            source.observeCurrentlyPlaying()
                    .filter(song -> source.equals(selectedSource))
                    .subscribe(currentlyPlayingSubject::onNext);
        }
    }

    @Override
    public List<MusicSource> getSources() {
        return sources;
    }

    @Override
    public void selectSource(MusicSource source) {
        selectedSource = source;
        selectedSourceSubject.onNext(source);
    }

    @Override
    public Observable<MusicSource> observeSelectedSource() {
        return selectedSourceSubject.observeOn(JavaFxScheduler.platform());
    }

    @Override
    public Observable<Song> observeNowPlaying() {
        return currentlyPlayingSubject.distinctUntilChanged().observeOn(JavaFxScheduler.platform());
    }
}
