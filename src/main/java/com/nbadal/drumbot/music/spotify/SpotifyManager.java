package com.nbadal.drumbot.music.spotify;

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

    Maybe<SpotifySong> getSongInfo();

    Completable play(String songUri);
}
