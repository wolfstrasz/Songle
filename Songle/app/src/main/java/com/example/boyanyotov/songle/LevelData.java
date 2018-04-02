package com.example.boyanyotov.songle;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.boyanyotov.songle.DataStructures.IconStyles;
import com.example.boyanyotov.songle.DataStructures.Placemark;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Boyan Yotov on 12/8/2017.
 */

public class LevelData {
    private static final LevelData ourInstance = new LevelData();

    public static LevelData getInstance() {
        return ourInstance;
    }

    private LevelData() {
    }

    static private String TAG = LevelData.class.getSimpleName();
    static private SharedPreferences gameDataPreferences;
    static private SharedPreferences progressPreferences;
    static private SharedPreferences levelPreferences;
    static private SharedPreferences.Editor editor;
    private Context myContext;

    private Map<String,String> lyricsMap;
    private int lyricsRows;
    private HashMap<Integer,Integer> lyricsCount;
    private List<Placemark> placemarks;
    private Map<String,IconStyles> iconStylesMap;

    public Map<String, IconStyles> getIconStylesMap() {
        return iconStylesMap;
    }

    public void setIconStylesMap(Map<String, IconStyles> iconStylesMap) {
        this.iconStylesMap = iconStylesMap;
    }

    public void setContext(Context context){
        myContext = context;
    }

    public void setLyricsMap(Map<String,String> lyricsMap){
        this.lyricsMap = lyricsMap;
    }

    public Map<String, String> getLyricsMap() {
        return lyricsMap;
    }

    public int getLyricsRows() {
        return lyricsRows;
    }

    public void setLyricsRows(int lyricsRows) {
        this.lyricsRows = lyricsRows;
    }

    public HashMap<Integer, Integer> getLyricsCount() {
        return lyricsCount;
    }

    public void setLyricsCount(HashMap<Integer,Integer> lyricsCount) {
        this.lyricsCount = lyricsCount;
    }

    public void setPlacemarks(List<Placemark> placemarks){
        this.placemarks = placemarks;
    }

    public List<Placemark> getPlacemarks(){
        return this.placemarks;
    }

    private void logStyles(){
        Log.e(TAG,"Styles count = " + Integer.toString(iconStylesMap.keySet().size()));
        Log.e(TAG,"Style id | scale | link");
        for(String key : iconStylesMap.keySet()){
            Log.w(TAG,key + " | " + iconStylesMap.get(key).getScale() + " | " + iconStylesMap.get(key).getLink());
        }
    }
    private void logPlacemarks(){
        Log.e(TAG,"Placemarks count = " + Integer.toString(placemarks.size()));
        Log.e(TAG,"Placemark Name | Description | StyleURL | Cooordinates");
        for(Placemark placemark : placemarks){
            Log.w(TAG,placemark.getName() + " | " + placemark.getDescription() + " | " +
                    placemark.getStyleUrl() + " | " + placemark.getCoordinates().toString());
        }
    }

    private void logLyrics(){

        Log.e(TAG,"Lyrics count: " + Integer.toString(lyricsMap.keySet().size()));
        Log.e(TAG,"Lyrics Key | Text");
        for( String key : lyricsMap.keySet()){
            Log.w(TAG, key + "  :  " + lyricsMap.get(key));
        }
    }

    public void logLevelData(){
        Log.e("TAG","LOGING LEVEL DATA");
        logLyrics();
        logStyles();
        logPlacemarks();
    }

}
