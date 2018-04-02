package com.example.boyanyotov.songle;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.boyanyotov.songle.DownloadTasks.DownloadDataTimestampTask;
import com.example.boyanyotov.songle.Generators.UrlGenerator;

import static com.example.boyanyotov.songle.Networks.NetworkUtil.getConnectivityStatusString;

public class MainActivity extends AppCompatActivity {
    static private String TAG = MainActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e(TAG, getConnectivityStatusString(MainActivity.this));
        downloadDataTimestamp();
    }

    public void downloadDataTimestamp(){
        if(!(getConnectivityStatusString(this).equals( "Not connected to Internet")))
        {
            //Checks for updates to be made
            new DownloadDataTimestampTask(this).execute(UrlGenerator.getInstance().songDatabaseTimestampUrl());
        }
    }


}
