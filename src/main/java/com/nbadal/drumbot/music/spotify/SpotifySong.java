package com.nbadal.drumbot.music.spotify;

import com.nbadal.drumbot.music.Song;

import java.util.Comparator;

public class SpotifySong extends Song {
    private final SpotifyCurrentlyPlaying song;

    public SpotifySong(SpotifyCurrentlyPlaying song) {
        this.song = song;
    }

    @Override
    public Source getSource() {
        return Source.SPOTIFY;
    }

    @Override
    public String getName() {
        return song.item.name;
    }

    @Override
    public String getArtist() {
        return song.item.artists.get(0).name;
    }

    @Override
    public String getThumbnailUrl() {
        return song.item.album.images.stream()
                .sorted(Comparator.<SpotifyImage>comparingInt(image -> image.height).reversed())
                .map(image -> image.url)
                .findFirst().orElse(null);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof SpotifySong)) return false;
        SpotifySong other = (SpotifySong) obj;

        return other.song.item.id.equals(song.item.id);
    }

    @Override
    public int hashCode() {
        return song.item.id.hashCode();
    }
}
