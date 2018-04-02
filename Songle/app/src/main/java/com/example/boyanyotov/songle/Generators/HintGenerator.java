package com.example.boyanyotov.songle.Generators;

import android.util.Log;

/**
 * Created by Boyan Yotov on 10/31/2017.
 */

public class HintGenerator {
    private static final HintGenerator ourInstance = new HintGenerator();
    static private String TAG = HintGenerator.class.getSimpleName();
    public static HintGenerator getInstance() {
        return ourInstance;
    }

    private HintGenerator() {
    }
    private int difficulty;
    private String songTitle;
    private String songArtist;
    private String songLink;
    public void setGeneratorParams(int difficulty,String songTitle, String songArtist, String songLink){
        this.difficulty = difficulty;
        this.songArtist = songArtist;
        this.songLink = songLink;
        this.songTitle = songTitle;
    }
    public String generateHint(){

        switch (difficulty){
            case 1:
                return songLink;
            case 2:
                return songTitle;
            case 3:
                return songArtist;
            case 4:
                return songArtist;
            default: return "";
        }
    }

    public String generateType(){
        switch (difficulty){
            case 1:
                return "Song Link (youtube)";
            case 2:
                return "Song Title";
            case 3:
                return "Artist";
            case 4:
                return "Artist";
            default: return "";
        }
    }

    public int generateHintCost() {
        // TODO: put new hint cost values!
        switch (difficulty){
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 3;
            case 4:
                return 4;
            default: {
                Log.e(TAG,"Difficulty not maching required names");
                return 0;
            }
        }
    }

}
