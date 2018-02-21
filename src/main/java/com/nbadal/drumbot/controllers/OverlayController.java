package com.nbadal.drumbot.controllers;

import com.nbadal.drumbot.music.MusicManager;

import java.net.URL;
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

    @Inject
    MusicManager music;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        music.observeNowPlaying().subscribe(newSong -> {
            songName.setText(newSong.getName());
            artistName.setText(newSong.getArtist());
            thumbnail.setImage(new Image(newSong.getThumbnailUrl(), true));
        });
    }
}
