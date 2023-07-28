package com.example.adimusic;

import android.net.Uri;

import java.io.Serializable;

public class AudioModel implements Serializable{
    String path;
    String title;
    String duration;

    Uri uri;

    public AudioModel(String path, String title, String duration, Uri uri) {
        this.path = path;
        this.title = title;
        this.duration = duration;
        this.uri = uri;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
