package com.nbadal.drumbot.music;

public abstract class Song {
    public abstract Source getSource();
    public abstract String getName();
    public abstract String getArtist();
    public abstract String getThumbnailUrl();

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract int hashCode();

    public enum Source {
        SPOTIFY, RADIO
    }
}
