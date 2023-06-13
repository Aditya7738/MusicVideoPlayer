package com.example.adimusic;

public class MyMediaPlayer {
    static android.media.MediaPlayer instance;

    public static android.media.MediaPlayer getInstance(){
        if (instance == null){
            instance = new android.media.MediaPlayer();
        }
        return instance;

    }

    public static int currentIndex = -1;
}
