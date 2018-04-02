package com.example.boyanyotov.songle.DownloadTasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.example.boyanyotov.songle.Generators.UrlGenerator;
import com.example.boyanyotov.songle.LevelData;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.example.boyanyotov.songle.Networks.NetworkUtil.getConnectivityStatusString;


public class DownloadLyricsTask extends AsyncTask <String, Void, Map<String,String>> {
    static private String TAG = DownloadLyricsTask.class.getSimpleName();
    static private SharedPreferences gameDataPreferences;
    static private SharedPreferences progressPreferences;
    static private SharedPreferences levelPreferences;
    static private SharedPreferences.Editor editor;
    private Context myContext;

    private int rowsCount;
    private HashMap<Integer,Integer> wordsCount = new HashMap<>();
    public  DownloadLyricsTask(Context context){
        myContext = context;
        gameDataPreferences = myContext.getSharedPreferences("GameData",MODE_PRIVATE);
        progressPreferences = myContext.getSharedPreferences("Progress",MODE_PRIVATE);
        levelPreferences    = myContext.getSharedPreferences("Level",MODE_PRIVATE);
    }

    @Override
    protected Map<String,String> doInBackground(String... urls) {
        try {
            return loadFromNetwork(urls[0]);
        } catch (IOException e) {
            //return "Unable to load content.Check your network connection";
            Log.i (TAG, " -> IOException");
            return null;
        }
    }
    private Map<String,String> loadFromNetwork(String urlString) throws IOException {
        InputStream stream = null;
        Map<String,String> lyrics = new HashMap<>();

        try {
            // Do something with stream e.g. parse as XML, build result
            stream = downloadLyricsUrl(urlString);
            lyrics = parseLyrics(stream);

        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        Log.i (TAG, "Lyrics for given song downloaded and parsed");
        return lyrics;
    }

    private InputStream downloadLyricsUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        // Also available: HttpsURLConnection
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        Log.i(TAG, " -> downloadLyricsUrl ( " + urlString + " ) done!");
        return conn.getInputStream();
    }

    private Map<String,String> parseLyrics (InputStream inputStream){
        Map<String,String> lyrics = new HashMap<String,String>();

            java.util.Scanner s = new java.util.Scanner(inputStream).useDelimiter("\\A");
            String textString = s.hasNext() ? s.next() : "";

        String[] lineSplitter = textString.split("\n");
        int lineNum = 1;
        for (String line : lineSplitter){
                String trimmedString = lineSplitter[lineNum-1].trim();
                String[] wordSplitter = trimmedString.split(" ");
            int wordNum = 1;
            for( String word : wordSplitter){
                lyrics.put(Integer.toString(lineNum) + ":" + Integer.toString(wordNum),
                        wordSplitter[wordNum-1]);
                wordNum ++;
            }
            wordsCount.put(lineNum,wordNum);
            lineNum ++;
        }
        rowsCount = lineNum;
        return lyrics;
    }
    @Override
    protected void onPostExecute(Map<String,String> lyrics)
    {
        // Save the Lyrics map in the Level Data
        LevelData.getInstance().setLyricsMap(lyrics);
        LevelData.getInstance().setLyricsRows(rowsCount);
        LevelData.getInstance().setLyricsCount(wordsCount);
        // Download Placemarks
        downloadLevelPlacemarks();
    }

    private void downloadLevelPlacemarks() {

        if(!(getConnectivityStatusString(myContext).equals( "Not connected to Internet"))){
            String songNumber = levelPreferences.getString("LevelSongNumber","null");
            int diff = progressPreferences.getInt("Difficulty",3);

            if(songNumber.equals("null")) Log.w(TAG,"Download Level Lyrics: songNumber = null");
            else new DownloadPlacemarksTask(myContext).execute(UrlGenerator.getInstance().songPlacemarksUrl(songNumber,diff));
        }
    }

}
