package com.nbadal.drumbot.music.radio;

public class TagStationResult {
    public Data data;

    public static class Data {
        public CurrentEvent currentEvent;
    }

    public static class CurrentEvent {
        public String lookupArtist;
        public String lookupTitle;
        public String imageUrlHd;
        public String category;
    }
}
