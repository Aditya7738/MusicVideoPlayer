package com.example.adimusic;

import android.graphics.Bitmap;
import android.net.Uri;

import java.io.Serializable;

public class VideoModel implements Serializable {
    String videoName = null;
    String videopath= null;
    Bitmap thumbnail= null;

  Uri uriURI= null;
    String uri;

    public VideoModel(String videoName, String videopath, Bitmap thumbnail, String uri) {
        this.videoName = videoName;
        this.videopath = videopath;
      //  this.thumbnail = thumbnail;
        this.uri = uri;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getVideopath() {
        return videopath;
    }

    public void setVideopath(String videopath) {
        this.videopath = videopath;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Uri getUriURI()
    { return uriURI;}

    public void setUriURI(Uri uri)
    {
        this.uriURI = uri;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
