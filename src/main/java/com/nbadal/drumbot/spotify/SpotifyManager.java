package com.nbadal.drumbot.spotify;

import com.nbadal.drumbot.music.Song;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;

public interface SpotifyManager {
    Completable openLoginAuth();

    Observable<String> observeAccessToken();

    Observable<String> observeRefreshToken();

    Single<SpotifyTokenResponse> createAccessToken(String authCode);

    Single<SpotifyTokenResponse> refreshAuthToken();

    Maybe<Song> getSongInfo();

    Completable play(String songUri);
}
