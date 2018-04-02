package com.example.boyanyotov.songle.Generators;

/**
 * Created by Boyan Yotov on 11/1/2017.
 */

public class UrlGenerator {
    private static final UrlGenerator ourInstance = new UrlGenerator();
    static private String TAG = UrlGenerator.class.getSimpleName();
    public static UrlGenerator getInstance() {
        return ourInstance;
    }

    private UrlGenerator() {
    }

    private String databaseUrl = "http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/";

    public String songDatabaseUrl ()
    {
        return databaseUrl + "songs.xml";
    }

    public String songLyricsUrl (String songNumber) {
        return databaseUrl + songNumber + "/lyrics.txt";
    }

    public String songPlacemarksUrl(String songNumber,int diff) {
        String difficulty = Integer.toString(6 - diff);
        return databaseUrl + songNumber + "/map" + difficulty + ".txt";
    }

    public String songDatabaseTimestampUrl(){
        return databaseUrl + "songs.txt";
    }
}
