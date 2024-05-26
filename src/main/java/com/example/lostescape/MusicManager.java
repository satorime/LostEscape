package com.example.lostescape;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;

public class MusicManager {
    private MediaPlayer mediaPlayer;

    public void playBackgroundMusic(String musicFile) {
        // Stop the current music if it's playing
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }

        // Load and play the new music file
        try {
            URL resource = getClass().getResource("/" + musicFile);
            if (resource != null) {
                Media sound = new Media(((URL) resource).toExternalForm());
                mediaPlayer = new MediaPlayer(sound);
                mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Loop the music
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

}
