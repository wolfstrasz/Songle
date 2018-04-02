package com.example.boyanyotov.songle.DownloadTasks;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.example.boyanyotov.songle.Activities.MainMenuActivity;
import com.example.boyanyotov.songle.DataStructures.Song;
import com.example.boyanyotov.songle.XmlParsers.SongsXmlParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class DownloadSongsTask extends AsyncTask<String, Void, List<Song>> {
    static private String TAG = DownloadSongsTask.class.getSimpleName();
    static private SharedPreferences gameDataPreferences;
    static private SharedPreferences.Editor editor;
    private Context myContext;

    public DownloadSongsTask (Context context){
        myContext = context;
        gameDataPreferences = myContext.getSharedPreferences("GameData",MODE_PRIVATE);
    }

    @Override
    protected List<Song> doInBackground(String... urls) {
        try {
            return loadXmlFromNetwork(urls[0]);
        } catch (IOException e) {
            //return "Unable to load content.Check your network connection";
            Log.i (TAG, " -> IOException");
            return null;
        } catch (XmlPullParserException e) {
           // return "Error parsing XML";
            Log.i (TAG, " -> XmlPullParserException ");
            return null;
        }
    }

    /*
    @Override
    protected void onPostExecute(String result) {
    // Do something with result
    }
    */

    private List<Song> loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {

        InputStream stream = null;

        // Instantiate parser
        SongsXmlParser songsParser = new SongsXmlParser();
        List <Song> songs = null;
        String number = null;
        String artist = null;
        String title = null;


        //StringBuilder result = new StringBuilder();

        try {
        // Do something with stream e.g. parse as XML, build result
            stream = downloadSongsUrl(urlString);
            songs = songsParser.parse(stream);
        } finally {
            if (stream != null) {
                stream.close();
            }
         }
        Log.i (TAG, "Songs downloaded and parsed");
        return songs;
    }

    // Given a string representation of a URL, sets up a connection and gets
    // an input stream.
    private InputStream downloadSongsUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        // Also available: HttpsURLConnection
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        Log.i(TAG, " -> downloadSongsUrl ( " + urlString + " ) done!");
        return conn.getInputStream();
    }

    @Override
    protected void onPostExecute(List<Song> allSongs)
    {
        // Log downloaded songs
        Log.i(TAG,"Songs count: " + Integer.toString(allSongs.size()));
        for (Song song : allSongs){
            Log.i(TAG,song.getTitle());
        }

        // Save downloaded songs
        saveAllSongs(allSongs);

        // Go to Main Menu
        Log.i(TAG,"Starting Main Menu Activity");
        Intent intent = new Intent(myContext, MainMenuActivity.class);
        myContext.startActivity(intent);
    }

    private void saveAllSongs(List<Song> allSongs){
        Log.i(TAG,"Songs saving started");
        gameDataPreferences = this.myContext.getSharedPreferences("GameData",MODE_PRIVATE);
        editor = gameDataPreferences.edit();
        editor.putInt("SongsListSize",allSongs.size());
        for (Song song: allSongs){
            String songNumber = Integer.toString(Integer.parseInt(song.getNumber()));
            editor.putString("Song_" + songNumber + "_Number",song.getNumber());
            editor.putString("Song_" + songNumber + "_Title", song.getTitle());
            editor.putString("Song_" + songNumber + "_Artist",song.getArtist());
            editor.putString("Song_" + songNumber + "_Link",  song.getLink());
        }
        editor.apply();
    }
}
