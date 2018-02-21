package com.nbadal.drumbot.radio;

import com.nbadal.drumbot.music.Song;

public class RadioSong extends Song {
    private final String name;
    private final String artist;
    private final String thumbnailUrl;

    public RadioSong(String name, String artist, String thumbnailUrl) {
        this.name = name;
        this.artist = artist;
        this.thumbnailUrl = thumbnailUrl;
    }

    @Override
    public Source getSource() {
        return Source.RADIO;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getArtist() {
        return artist;
    }

    @Override
    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RadioSong radioSong = (RadioSong) o;

        if (name != null ? !name.equals(radioSong.name) : radioSong.name != null) return false;
        if (artist != null ? !artist.equals(radioSong.artist) : radioSong.artist != null) return false;
        return thumbnailUrl != null ? thumbnailUrl.equals(radioSong.thumbnailUrl) : radioSong.thumbnailUrl == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (artist != null ? artist.hashCode() : 0);
        result = 31 * result + (thumbnailUrl != null ? thumbnailUrl.hashCode() : 0);
        return result;
    }
}
