package com.nbadal.drumbot.spotify;

public class SpotifyCurrentlyPlaying {
    public Context context;
    public long timestamp;
    public long progressMs;
    public boolean isPlaying;
    public SpotifyTrack item;

    public class Context {
        public String uri;
        public String href;
        public Object externalUrls;
        public String type;
    }
}
