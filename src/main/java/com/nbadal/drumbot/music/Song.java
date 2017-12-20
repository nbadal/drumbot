package com.nbadal.drumbot.music;

public class Song {
    public final String name;
    public final String artist;
    public final String thumbnailUrl;

    public Song(String name, String artist, String thumbnailUrl) {
        this.name = name;
        this.artist = artist;
        this.thumbnailUrl = thumbnailUrl;
    }
}
