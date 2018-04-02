package com.example.boyanyotov.songle.DownloadTasks;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.example.boyanyotov.songle.Activities.MapsActivity;
import com.example.boyanyotov.songle.DataStructures.Placemark;
import com.example.boyanyotov.songle.LevelData;
import com.example.boyanyotov.songle.XmlParsers.PlacemarksXmlParser;
import com.google.android.gms.maps.model.LatLng;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Boyan Yotov on 10/31/2017.
 */

public class DownloadPlacemarksTask extends AsyncTask<String, Void, List<Placemark>> {

    static private String TAG = DownloadPlacemarksTask.class.getSimpleName();
    static private SharedPreferences gameDataPreferences;
    static private SharedPreferences.Editor editor;
    private Context myContext;

    public DownloadPlacemarksTask(Context context){
        myContext = context;
        gameDataPreferences = myContext.getSharedPreferences("GameData",MODE_PRIVATE);
    }

    @Override
    protected List<Placemark> doInBackground(String... urls) {
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

    private List<Placemark> loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {

        InputStream stream = null;

        // Instantiate parser
        PlacemarksXmlParser placemarksParser = new PlacemarksXmlParser();
        List<Placemark> placemarks = null;
        String name = null;
        String  descr = null;
        String styleUrl = null;
        LatLng point = null;

        //StringBuilder result = new StringBuilder();

        try {
            // Do something with stream e.g. parse as XML, build result
            stream = downloadPlacemarksUrl(urlString);
            placemarks = placemarksParser.parse(stream);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        Log.i (this.TAG, "Placemarks for given song downloaded and parsed");
        return placemarks;
    }

    // Given a string representation of a URL, sets up a connection and gets
    // an input stream.
    private InputStream downloadPlacemarksUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        // Also available: HttpsURLConnection
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        Log.i(TAG, " -> downloadPlacemarksUrl ( " + urlString + " ) done!");
        return conn.getInputStream();
    }

    @Override
    protected void onPostExecute(List<Placemark> placemarks)
    {
        // Save the Placemarks list in the Level Data
        LevelData.getInstance().setPlacemarks(placemarks);

        // Log all data downloaded for the level
        LevelData.getInstance().logLevelData();

        // Go to the Level
        Log.i(TAG,"Starting PlayGameActivity");
       Intent intent = new Intent(myContext, MapsActivity.class);
        myContext.startActivity(intent);
    }
}

