package com.app1.musicmediaplayer;

public class MusicFiles {
    private  String path;
    private  String title;
    private  String artist;
    private  String album;
    private  String duration;
    private  String id;

    public MusicFiles(String path, String title, String artist, String album, String duration,String id) {
        this.path = path;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
        this.id=id;

    }

    public MusicFiles() {
    }

    public String getPath() {
        return path;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public String getDuration() {
        return duration;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
