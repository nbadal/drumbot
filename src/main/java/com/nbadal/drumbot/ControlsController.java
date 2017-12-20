package com.nbadal.drumbot;

import com.nbadal.drumbot.spotify.Spotify;
import com.nbadal.drumbot.util.StringUtils;

import org.omg.PortableInterceptor.SUCCESSFUL;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.Single;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class ControlsController implements Initializable {

    public TextField authCodeField;
    public TextField accessTokenField;
    public TextField refreshTokenField;
    public TextField songInfoField;
    public TextField playSongField;

    public Button authTokenButton;
    public Button refreshTokenButton;
    public Button getInfoButton;
    public Button playSongButton;

    private final Spotify spotify = new Spotify();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        authCodeField.textProperty().addListener((observable, oldValue, newValue) -> {
            authTokenButton.setDisable(StringUtils.isEmpty(newValue));
        });

        accessTokenField.textProperty().addListener(((observable, oldValue, newValue) -> {
            getInfoButton.setDisable(StringUtils.isEmpty(newValue));
            playSongField.setDisable(StringUtils.isEmpty(newValue));
        }));

        refreshTokenField.textProperty().addListener(((observable, oldValue, newValue) -> {
            refreshTokenButton.setDisable(StringUtils.isEmpty(newValue));
        }));

        playSongField.textProperty().addListener(((observable, oldValue, newValue) -> {
            playSongButton.setDisable(StringUtils.isEmpty(newValue));
        }));

        spotify.observeAccessToken().subscribe(accessTokenField::setText);
        spotify.observeRefreshToken().subscribe(refreshTokenField::setText);
    }

    public void createTokenClicked() {
        spotify.createAccessToken(authCodeField.getText()).subscribe();
    }

    public void openAuthSiteClicked() {
        spotify.openLoginAuth().subscribe();
    }

    public void refreshClicked() {
        spotify.refreshAuthToken().subscribe();
    }

    public void getInfoClicked() {
        songInfoField.setText("");
        spotify.getSongInfo().subscribe(songInfoField::setText);
    }

    public void playSongClicked() {
        spotify.play(playSongField.getText())
                .andThen(Completable.timer(1, TimeUnit.SECONDS))
                .andThen(spotify.getSongInfo())
                .subscribe(songInfoField::setText);
    }
}
