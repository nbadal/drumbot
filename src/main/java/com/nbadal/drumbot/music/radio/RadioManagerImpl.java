package com.nbadal.drumbot.music.radio;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.nbadal.drumbot.lifecycle.LifecycleListener;
import com.nbadal.drumbot.lifecycle.LifecycleManager;

import org.json.JSONObject;

import java.util.HashMap;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import io.socket.client.IO;
import io.socket.client.Socket;
import okhttp3.OkHttpClient;

public class RadioManagerImpl implements RadioManager, LifecycleListener {

    private Socket socket;
    private Subject<JSONObject> socketSubject = PublishSubject.create();
    private Gson mGson;

    private Subject<RadioSong> currentlyPlayingSubject = PublishSubject.create();
    private Disposable connectionDisposable;

    public RadioManagerImpl() {
        mGson = new GsonBuilder().create();
        socketSubject
                .map(JSONObject::toString)
                .doOnNext(this::log)
                .map(json -> mGson.fromJson(json, TagStationResult.class))
                .filter(result -> result.data.currentEvent.category.equals("Song"))
                .map(this::createSong)
                .subscribe(currentlyPlayingSubject::onNext);
    }

    private RadioSong createSong(TagStationResult result) {
        return new RadioSong(
                result.data.currentEvent.lookupTitle,
                result.data.currentEvent.lookupArtist,
                result.data.currentEvent.imageUrlHd
        );
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onStop() {
        disconnect().subscribe();
    }

    @Override
    public String toString() {
        return "Radio";
    }

    @Override
    public Observable<RadioSong> observeCurrentlyPlaying() {
        return connect().andThen(currentlyPlayingSubject
                .doFinally(() -> disconnect().subscribe())
                .subscribeOn(JavaFxScheduler.platform()));
    }

    @Override
    public Completable connect() {
        return Completable.defer(() -> {
            IO.Options opts = new IO.Options();
            OkHttpClient client = new OkHttpClient.Builder()
                    .cookieJar(new NonPersistentCookieJar())
                    .build();
            opts.callFactory = client;
            opts.webSocketFactory = client;

            socket = IO.socket("https://sockets.tagstation.com/v2.0", opts);

            socket.on(Socket.EVENT_CONNECT, args -> {
                log("onConnect");
                socket.emit("subscribe", new HashMap<String, String>() {{
                    put("stationId", "9490");
                    put("station", "${stationId}_${eventModel}");
                    put("eventModel", "full");
                }});
            });
            socket.on("messages", args -> {
                log("onMessage");
                if (args[0] != null && args[0] instanceof JSONObject) {
                    socketSubject.onNext((JSONObject) args[0]);
                }
            });
            socket.on(Socket.EVENT_DISCONNECT, args -> log("onDisconect"));
            socket.on(Socket.EVENT_ERROR, args -> log("onError"));
            socket.on(Socket.EVENT_RECONNECT_FAILED, args -> log("onReconnectFail"));
            socket.connect();

            return Completable.complete();
        });
    }

    private Completable disconnect() {
        return Completable.defer(() -> {
            if (socket != null && socket.connected()) {
                socket.disconnect();
            }
            return Completable.complete();
        });
    }

    @Override
    public Completable reconnect() {
        return disconnect().andThen(connect());
    }

    private void log(String msg) {
        System.out.println("[Socket] " + msg);
    }

}
