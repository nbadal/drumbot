package com.nbadal.drumbot.music.spotify;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.nbadal.drumbot.lifecycle.LifecycleListener;
import com.nbadal.drumbot.lifecycle.LifecycleManager;
import com.nbadal.drumbot.util.StringUtils;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeUnit;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public class SpotifyManagerImpl implements SpotifyManager, LifecycleListener {

    private static final String BASE_URL = "https://api.spotify.com";
    private static final String AUTH_URL = "https://accounts.spotify.com";
    private static final String SCOPES = Stream.of(
            "user-read-currently-playing",
            "user-modify-playback-state"
    ).collect(Collectors.joining("%20"));
    private static final String CLIENT_ID = "cdc7f38bf9b84bc9aab2e78461d91638";
    private static final String CLIENT_SECRET = "7c1443b6993f481294270d7ad4d0ec53";
    private static final String REDIRECT_URI = "https://nbad.al/spotify-callback";

    private static final String PREF_REFRESH_TOKEN = "refreshToken";


    private final AuthAPI authApi;
    private final SpotifyAPI spotifyApi;

    private final Preferences preferences;

    private final Subject<String> accessTokenSubject = BehaviorSubject.create();
    private final Subject<String> refreshTokenSubject = BehaviorSubject.create();

    private Disposable pollingDisposable;
    private Subject<SpotifySong> currentlyPlayingSubject = BehaviorSubject.create();

    @Inject
    public SpotifyManagerImpl(LifecycleManager lifecycle) {
        HttpLoggingInterceptor logger = new HttpLoggingInterceptor(System.out::println);
        logger.setLevel(HttpLoggingInterceptor.Level.BODY);

        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        authApi = new Retrofit.Builder()
                .baseUrl(AUTH_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(new OkHttpClient.Builder()
                        .addInterceptor(logger)
                        .build())
                .build().create(AuthAPI.class);

        BearerAuthenticator bearerAuth = new BearerAuthenticator();
        accessTokenSubject.subscribe(bearerAuth::setBearer);

        spotifyApi = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(new OkHttpClient.Builder()
                        .addInterceptor(logger)
                        .addInterceptor(bearerAuth)
                        .build())
                .build().create(SpotifyAPI.class);

        preferences = Preferences.userNodeForPackage(getClass());
        String prefToken = preferences.get(PREF_REFRESH_TOKEN, null);
        if (!StringUtils.isEmpty(prefToken)) {
            refreshTokenSubject.onNext(prefToken);
        }
        refreshTokenSubject.subscribe(refresh -> preferences.put(PREF_REFRESH_TOKEN, refresh));

        lifecycle.addListener(this);
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onStop() {
        stopPolling();
    }

    @Override
    public String toString() {
        return "Spotify";
    }

    @Override
    public Observable<SpotifySong> observeCurrentlyPlaying() {
        return currentlyPlayingSubject
                .doOnSubscribe(__ -> startPolling())
                .doFinally(this::stopPolling);
    }

    @Override
    public Observable<String> observeAccessToken() {
        return accessTokenSubject;
    }

    @Override
    public Observable<String> observeRefreshToken() {
        return refreshTokenSubject;
    }

    @Override
    public Completable openLoginAuth() {
        return Completable.defer(() -> {
            Desktop.getDesktop().browse(URI.create(AUTH_URL
                    + "/authorize"
                    + "?client_id=" + CLIENT_ID
                    + "&scope=" + SCOPES
                    + "&response_type=code"
                    + "&show_dialog=false"
                    + "&redirect_uri=" + REDIRECT_URI));

            return Completable.complete();
        });
    }

    @Override
    public Single<SpotifyTokenResponse> createAccessToken(String authCode) {
        return authApi.createAccessToken(authCode)
                .doOnSuccess(this::handleNewTokenInfo)
                .observeOn(JavaFxScheduler.platform())
                .subscribeOn(Schedulers.computation());
    }

    @Override
    public Single<SpotifyTokenResponse> refreshAuthToken() {
        return refreshTokenSubject.firstElement()
                .flatMapSingle(authApi::refreshToken)
                .doOnSuccess(this::handleNewTokenInfo)
                .observeOn(JavaFxScheduler.platform())
                .subscribeOn(Schedulers.computation());
    }

    private void startPolling() {
        if (pollingDisposable != null && !pollingDisposable.isDisposed()) return;

        pollingDisposable = Observable.interval(1500, TimeUnit.MILLISECONDS)
                .flatMapMaybe(tick -> getSongInfo())
                .subscribe(currentlyPlayingSubject::onNext);
    }

    private void stopPolling() {
        if (pollingDisposable == null || pollingDisposable.isDisposed()) return;

        pollingDisposable.dispose();
        pollingDisposable = null;
    }

    @Override
    public Maybe<SpotifySong> getSongInfo() {
        return spotifyApi.getCurrentlyPlaying()
                .map(SpotifySong::new)
                .onErrorResumeNext(Maybe.empty())
                .observeOn(JavaFxScheduler.platform())
                .subscribeOn(Schedulers.computation());
    }

    @Override
    public Completable play(String songUri) {
        return spotifyApi.play(new SpotifyPlayRequest(songUri))
                .observeOn(JavaFxScheduler.platform())
                .subscribeOn(Schedulers.computation());
    }

    private void handleNewTokenInfo(SpotifyTokenResponse info) {
        // Update Access Token
        accessTokenSubject.onNext(info.accessToken);

        // Refresh will be null if it is still valid.
        if (!StringUtils.isEmpty(info.refreshToken)) {
            refreshTokenSubject.onNext(info.refreshToken);
        }
    }

    public interface AuthAPI {
        default Single<SpotifyTokenResponse> createAccessToken(String code) {
            return createAccessTokenImpl("authorization_code", code, REDIRECT_URI, CLIENT_ID, CLIENT_SECRET);
        }

        default Single<SpotifyTokenResponse> refreshToken(String refreshToken) {
            return refreshTokenImpl("refresh_token", refreshToken, CLIENT_ID, CLIENT_SECRET);
        }

        @FormUrlEncoded
        @POST("/api/token")
        Single<SpotifyTokenResponse> createAccessTokenImpl(
                @Field("grant_type") String grantType,
                @Field("code") String code,
                @Field("redirect_uri") String redirectUri,
                @Field("client_id") String clientId,
                @Field("client_secret") String clientSecret
        );

        @FormUrlEncoded
        @POST("/api/token")
        Single<SpotifyTokenResponse> refreshTokenImpl(
                @Field("grant_type") String grantType,
                @Field("refresh_token") String refreshToken,
                @Field("client_id") String clientId,
                @Field("client_secret") String clientSecret
        );
    }

    public interface SpotifyAPI {
        @GET("/v1/me/player/currently-playing")
        Maybe<SpotifyCurrentlyPlaying> getCurrentlyPlaying();

        @PUT("/v1/me/player/play")
        Completable play(@Body SpotifyPlayRequest request);
    }

    private class BearerAuthenticator implements Interceptor {
        private String bearer;

        public void setBearer(String bearer) {
            this.bearer = bearer;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if (!StringUtils.isEmpty(bearer)) {
                request = request.newBuilder()
                        .addHeader("Authorization", "Bearer " + bearer)
                        .build();
            }
            return chain.proceed(request);
        }
    }
}
