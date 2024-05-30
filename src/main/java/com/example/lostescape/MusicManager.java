package com.example.lostescape;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;

public class MusicManager {
    private MediaPlayer mediaPlayer;

    public void playBackgroundMusic(String musicFile) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }

        try {
            URL resource = getClass().getResource("/" + musicFile);
            if (resource != null) {
                Media sound = new Media(((URL) resource).toExternalForm());
                mediaPlayer = new MediaPlayer(sound);
                mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                mediaPlayer.play();
            } else {
                System.err.println("Resource not found: " + musicFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopBackgroundMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    public void changeBackgroundMusic(String newMusicFile) {
        new Thread(() -> playBackgroundMusic(newMusicFile)).start();
    }

    public void SoundEffect(String soundFile) {
        try {
            URL resource = getClass().getResource("/" + soundFile);
            if (resource != null) {
                Media sound = new Media(resource.toExternalForm());
                MediaPlayer soundEffectPlayer = new MediaPlayer(sound);
                soundEffectPlayer.setOnEndOfMedia(soundEffectPlayer::dispose);
                soundEffectPlayer.play();
            } else {
                System.err.println("Sound effect resource not found: " + soundFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playSoundEffect(String effect) {
        new Thread(() -> SoundEffect(effect)).start();
    }
}
