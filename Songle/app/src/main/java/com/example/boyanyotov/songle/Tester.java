package com.example.boyanyotov.songle;

/**
 * Created by Boyan Yotov on 11/2/2017.
 */

public class Tester {
    private static final Tester ourInstance = new Tester();
    static private String TAG = Tester.class.getSimpleName();

    public static Tester getInstance() {
        return ourInstance;
    }

    private Tester() {
    }
//
//    public void ApplyTests()
//    {
//        /* Test GameDataManager */
//        Log.w(TAG,"TESTING GameDataManager");
//
//        Log.w(TAG,"Test getCurrentSong");
//        Log.i(TAG," test return = " + GameDataManager.getInstance().getCurrentSong().getTitle());
//
//        Log.w(TAG,"Test getCurrentDifficulty");
//        //Log.i(TAG," test return = " + GameDataManager.getInstance().getCurrentDifficulty());
//
//        /* Test UrlGenerator */
//        Log.w(TAG,"TESTING UrlGenerator");
//
//        Song testSong1 = new Song("TestNumber","TestArtist","TestTitle","TestLink");
//        GameDataManager.getInstance().setCurrentSong(testSong1);
//       // GameDataManager.getInstance().setCurrentDifficulty("Test_Difficulty");
//
//        Log.w(TAG,"Test songDatabaseUrl");
//        Log.i(TAG," return = " + UrlGenerator.getInstance().songDatabaseUrl());
//
//        Log.w(TAG,"Test songLyricsUrl");
//        Log.i(TAG,"return = " + UrlGenerator.getInstance().songLyricsUrl());
//
//        Log.w(TAG,"Test songPlacemarksUrl");
//        Log.i(TAG,"return = " + UrlGenerator.getInstance().songPlacemarksUrl());
//
//
//        /* Test Hint Generator */
//        /* Test DownloadSongsTask */
//        /* Test DownloadPlacemarksTask */
//    }
}
