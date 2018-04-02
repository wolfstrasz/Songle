package com.example.boyanyotov.songle.Activities;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.boyanyotov.songle.DownloadTasks.DownloadLyricsTask;
import com.example.boyanyotov.songle.Generators.UrlGenerator;
import com.example.boyanyotov.songle.R;

import static com.example.boyanyotov.songle.Networks.NetworkUtil.getConnectivityStatusString;

public class ChooseLevelActivity extends AppCompatActivity {
    static private String TAG = ChooseLevelActivity.class.getSimpleName();

    private static SharedPreferences settingsPreferences;
    private static SharedPreferences gameDataPreferences;
    private static SharedPreferences progressPreferences;
    private static SharedPreferences levelPreferences;
    private static SharedPreferences.Editor editor;

    // GUI Elemets
    private Toolbar toolbar;
    private LinearLayout layout;
    private ProgressBar progressBar;
    private TextView progressTxt;
    private Button backBtn;
    private CoordinatorLayout cl_selectlevel;

    // Parameters
    private int songsCount;
    private int selectedDifficulty;
    private int songsInLevels[];
    private int levelsCount;
    private int levelsPassedCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingsPreferences = getSharedPreferences("Settings",MODE_PRIVATE);
        gameDataPreferences = getSharedPreferences("GameData",MODE_PRIVATE);
        progressPreferences = getSharedPreferences("Progress",MODE_PRIVATE);
        levelPreferences    = getSharedPreferences("Level",MODE_PRIVATE);
        setParameters();
        setViews();
        connectGUI();
    }

    @Override
    protected void onStart(){
        super.onStart();

        applyTheme();
        generateLevels();
        setProgressBar();
    }

    // Assigns value to the private parameters
    private void setParameters(){
        Log.i(TAG,"setParameters");
        // Load number of songs and selected difficulty
        songsCount = gameDataPreferences.getInt("SongsListSize",0);
        selectedDifficulty = progressPreferences.getInt("Difficulty",3);

        // Calculate number of levels in selected difficulty
        levelsCount = songsCount / 5;
        if(songsCount % 5 >= selectedDifficulty ) levelsCount++;

        // Calculate number of evey song for the levels of current difficulty
        songsInLevels = new int[levelsCount+1];
        for(int i = selectedDifficulty,k=1; i <= songsCount; i+=5,k++)
            songsInLevels[k] = i;
    }

    private void setViews(){
        setContentView(R.layout.activity_choose_level);
        cl_selectlevel = (CoordinatorLayout)findViewById(R.id.cl_selectlevel);
        layout = (LinearLayout) findViewById(R.id.ll_chooselevel) ;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        backBtn = (Button) findViewById(R.id.backBtn);
        progressBar = (ProgressBar) findViewById(R.id.levelProgress);
        progressTxt = (TextView) findViewById(R.id.progressTxt);
    }

    // Applies the theme to the GUI
    private void applyTheme(){
        Log.i(TAG,":applyTheme:");
        int rid;
        if(settingsPreferences.getBoolean("DarkTheme",false)){
            toolbar.setBackgroundColor(Color.parseColor("#111111"));
            cl_selectlevel.setBackgroundColor(Color.parseColor("#111111"));
            rid = getResources().getIdentifier( "@drawable/btn_back_dark",null,getPackageName());
            backBtn.setBackgroundResource(rid);
        } else {
            toolbar.setBackgroundColor(Color.parseColor("#89bff0"));
            cl_selectlevel.setBackgroundColor(Color.parseColor("#89bff0"));
            rid = getResources().getIdentifier( "@drawable/btn_back_light",null,getPackageName());
            backBtn.setBackgroundResource(rid);
        }
    }

    // Generates a scrollbar view of buttons for each level in the chosed difficulty
    private void generateLevels(){
        Log.i(TAG,":generateLevels:");
        layout.removeAllViews();
        for (int k = 0; k < levelsCount; k++) {
            layout.addView(generateLevelButton(k));
        }
    }

    // Generates each seperate button.
    private Button generateLevelButton(int num) {
        Log.i(TAG,"generateButton");

        Button btn = new Button(this);
        btn.setId(selectedDifficulty + 5*num);
        btn.setTag(selectedDifficulty+ 5*num);
        btn.setTextColor(Color.parseColor("#ffffff"));
        int songNum = songsInLevels[num+1];

        if(progressPreferences.getBoolean("Song_" + Integer.toString(songNum) + "_Passed",false)){
            levelsPassedCount++;
            btn.setText(gameDataPreferences.getString("Song_" + Integer.toString(songNum) + "_Title",
                                                        "No song for this button"));
            btn.setBackgroundColor(Color.parseColor("#21b017"));
        }
        else {
            btn.setText("Song " + Integer.toString(num+1));
            if(settingsPreferences.getBoolean("DarkTheme",false)) btn.setBackgroundColor(Color.parseColor("#111111"));
            else btn.setBackgroundColor(Color.parseColor("#89bff0"));
        }
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNewLevel(view.getId());
            }
        });
        return btn;
    }

    // Creates the functionality of other GUI elements
    private void connectGUI(){
        Log.i(TAG,"connectGUI");
        setSupportActionBar(toolbar);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    // Assigns values to the progress bar
    private void setProgressBar(){
        Log.i(TAG,"setProgressBar");
        progressBar.setMax(levelsCount);
        progressBar.setProgress(levelsPassedCount);
        progressTxt.setText("Progress: " +
                Integer.toString(levelsPassedCount) + " / " +
                Integer.toString(levelsCount));
    }

    // Saves the chosen level song, downloads lyrics and placemarks and starts the play of the level
    private void openNewLevel(int songNum){
        Log.i(TAG,"Start Song Level: " + Integer.toString(songNum));

        levelPreferences.edit().clear().commit();
        saveLevelSong(songNum);
        downloadLevelLyrics();
    }

    private void saveLevelSong(int songNum){
        editor = levelPreferences.edit();
        editor.putBoolean("LevelInProgress",true);
        editor.putString("LevelSongNumber",gameDataPreferences.getString("Song_" + songNum + "_Number",null ));
        editor.putString("LevelSongArtist",gameDataPreferences.getString("Song_" + songNum + "_Artist",null ));
        editor.putString("LevelSongTitle", gameDataPreferences.getString("Song_" + songNum + "_Title",null ));
        editor.putString("LevelSongLink",  gameDataPreferences.getString("Song_" + songNum + "_Link",null ));
        editor.commit();
    }

    private void downloadLevelLyrics(){
        if(!(getConnectivityStatusString(this).equals( "Not connected to Internet"))){
            String songNumber = levelPreferences.getString("LevelSongNumber","null");
            if(songNumber.equals("null")) Log.w(TAG,"Download Level Lyrics: songNumber = null");
            else new DownloadLyricsTask(this).execute(UrlGenerator.getInstance().songLyricsUrl(songNumber));
        }
    }

}