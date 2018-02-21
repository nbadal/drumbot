package com.nbadal.drumbot.music;

public class Song {
    public final Source source;
    public final String name;
    public final String artist;
    public final String thumbnailUrl;

    public Song(Source source, String name, String artist, String thumbnailUrl) {
        this.source = source;
        this.name = name;
        this.artist = artist;
        this.thumbnailUrl = thumbnailUrl;
    }

    public enum Source {
        SPOTIFY, RADIO
    }
}
