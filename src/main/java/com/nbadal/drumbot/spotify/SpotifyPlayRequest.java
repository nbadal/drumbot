package com.nbadal.drumbot.spotify;

import java.util.Collections;
import java.util.List;

public class SpotifyPlayRequest {
    public final List<String> uris;

    public SpotifyPlayRequest(String songUri) {
        uris = Collections.singletonList(songUri);
    }
}
