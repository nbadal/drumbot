package com.nbadal.drumbot.controllers;

import com.nbadal.drumbot.midi.MidiManager;
import com.nbadal.drumbot.music.MusicManager;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

import javax.inject.Inject;

import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class OverlayController implements Initializable {

    public ImageView thumbnail;
    public Label songName;
    public Label artistName;
    public Label midiCounter;

    @Inject
    MusicManager music;

    @Inject
    MidiManager midi;

    private long totalNoteCount = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        music.observeNowPlaying().subscribe(newSong -> {
            totalNoteCount = 0;
            updateNoteCount();

            songName.setText(newSong.getName());
            artistName.setText(newSong.getArtist());
            thumbnail.setImage(new Image(newSong.getThumbnailUrl(), true));
        });

        midi.observeMidiNotes().doOnNext(msg -> {
            totalNoteCount++;

            updateNoteCount();
        }).subscribe();
    }

    private void updateNoteCount() {
        midiCounter.setText(String.format("MIDI hits: %d", totalNoteCount));
    }
}
