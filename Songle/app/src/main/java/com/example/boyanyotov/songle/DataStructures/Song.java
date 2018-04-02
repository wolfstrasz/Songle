package com.example.boyanyotov.songle.DataStructures;

/**
 * Created by Boyan Yotov on 10/31/2017.
 */

public class Song {
    static private String TAG = Song.class.getSimpleName();
    private String number;
    private String artist;
    private String title;
    private String link;
    private boolean isGuessed = false;

    public Song(String number, String artist, String title, String link) {
        this.number = number;
        this.artist = artist;
        this.title = title;
        this.link = link;
        this.isGuessed = false;
    }


    public Song(String number, String artist, String title, String link, boolean isGuessed) {
        this.number = number;
        this.artist = artist;
        this.title = title;
        this.link = link;
        this.isGuessed = isGuessed;
    }


    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public boolean isGuessed() {
        return isGuessed;
    }

    public void setGuessed(boolean guessed) {
        isGuessed = guessed;
    }
}
