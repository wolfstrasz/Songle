package com.example.boyanyotov.songle.DownloadTasks;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.example.boyanyotov.songle.Activities.MainMenuActivity;
import com.example.boyanyotov.songle.Generators.UrlGenerator;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.content.Context.MODE_PRIVATE;
import static com.example.boyanyotov.songle.Networks.NetworkUtil.getConnectivityStatusString;

public class DownloadDataTimestampTask extends AsyncTask<String,Void,String> {
    static private  String TAG = DownloadDataTimestampTask.class.getSimpleName();
    static private  SharedPreferences gameDataPreferences;
    static private  SharedPreferences.Editor editor;
    private         Context myContext = null;


    public DownloadDataTimestampTask(Context context){
        myContext=context;
        gameDataPreferences = myContext.getSharedPreferences("GameData",MODE_PRIVATE);
    }
    protected String doInBackground(String... urls) {
        try {
            return loadFromNetwork(urls[0]);
        } catch (IOException e) {
            //return "Unable to load content.Check your network connection";
            Log.i (TAG, " -> IOException");
            return null;
        }
    }
    private String loadFromNetwork(String urlString) throws IOException {
        InputStream stream = null;
        String timestamp;

        try {
            // Do something with stream e.g. parse as XML, build result
            stream = downloadDataTimestamp(urlString);
            timestamp = parseDataTimestamp(stream);

        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        Log.i (TAG, "Lyrics for given song downloaded and parsed");
        return timestamp;
    }

    private InputStream downloadDataTimestamp(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        // Also available: HttpsURLConnection
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        Log.i(TAG, " -> downloadDataTimestamp ( " + urlString + " ) started");
        return conn.getInputStream();
    }

    private String parseDataTimestamp(InputStream inputStream){


        java.util.Scanner s = new java.util.Scanner(inputStream).useDelimiter("\\A");
        String textString = s.hasNext() ? s.next() : "";

        String[] lineSplitter = textString.split("\n");
        String[] splitToGetTimestamp = lineSplitter[1].split("\"");

        return splitToGetTimestamp[1];
    }


    @Override
    protected void onPostExecute(String dataTimestamp){
        Log.i(TAG,"Dowloaded Data timestamp: " + dataTimestamp);

        // Load saved Data Timestamp
        String oldTimestamp = gameDataPreferences.getString("DataTimestamp","No data timestamp");

        // Check if updates are required
        if(!dataTimestamp.equals(oldTimestamp))
        {
            // Save new Data Timestamp
            Log.i(TAG,"Saving new Data Timestamp");
            editor = gameDataPreferences.edit();
            editor.putString("DataTimestamp",dataTimestamp);
            editor.apply();

            // Download set of songs
            Log.i(TAG,"Download All Songs Requested");
            downloadAllSongs();
        }
        else {
            // Skip same songs download and jump to Main Menu
            Log.i(TAG,"Data timestamp is the same. No update required.Starting Main Menu Activity");
            Intent intent = new Intent(myContext, MainMenuActivity.class);
            myContext.startActivity(intent);
        }
    }

    private void downloadAllSongs(){
        if(!(getConnectivityStatusString(myContext).equals( "Not connected to Internet")))
        {
            new DownloadSongsTask(myContext).execute(UrlGenerator.getInstance().songDatabaseUrl());
        }
    }
}
