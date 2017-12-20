package com.nbadal.drumbot;

import com.nbadal.drumbot.spotify.Spotify;
import com.nbadal.drumbot.util.StringUtils;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import static io.reactivex.rxjavafx.observables.JavaFxObservable.valuesOf;

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
        valuesOf(authCodeField.textProperty()).map(StringUtils::isEmpty)
                .subscribe(authTokenButton::setDisable);

        valuesOf(accessTokenField.textProperty()).map(StringUtils::isEmpty)
                .subscribe(getInfoButton::setDisable);
        valuesOf(accessTokenField.textProperty()).map(StringUtils::isEmpty)
                .subscribe(playSongField::setDisable);

        valuesOf(refreshTokenField.textProperty()).map(StringUtils::isEmpty)
                .subscribe(refreshTokenButton::setDisable);

        valuesOf(playSongField.textProperty()).map(value -> !isValidTrackUri(value))
                .subscribe(playSongButton::setDisable);

        spotify.observeAccessToken().subscribe(accessTokenField::setText);
        spotify.observeRefreshToken().subscribe(refreshTokenField::setText);
    }

    private boolean isValidTrackUri(String value) {
        return !StringUtils.isEmpty(value) && value.startsWith("spotify:track:");
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
