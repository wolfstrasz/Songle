package com.example.boyanyotov.songle.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.boyanyotov.songle.R;

public class MainMenuActivity extends AppCompatActivity {
    static private String TAG = MainMenuActivity.class.getSimpleName();

    private static SharedPreferences settingsPreferences;
    private static SharedPreferences gameDataPreferences;
    private static SharedPreferences progressPreferences;
    private static SharedPreferences levelPreferences;
    private static SharedPreferences.Editor editor;

    private boolean isGameInProgress;

    private ConstraintLayout cl;
    private Button resumeGameBtn;
    private Button startNewGameBtn;
    private Button optionsBtn;
    private Button exitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG,"Created MainMenu Activity");

        settingsPreferences = this.getSharedPreferences("Settings",MODE_PRIVATE);
        gameDataPreferences = this.getSharedPreferences("GameData",MODE_PRIVATE);
        progressPreferences = this.getSharedPreferences("Progress",MODE_PRIVATE);
        levelPreferences    = this.getSharedPreferences("Level",MODE_PRIVATE);

        setViews();
        connectGUI();
    }

    @Override
    protected void onStart(){
        applyTheme();

        // Hide the resume game button if no previous game was on
        if(gameDataPreferences.getBoolean("GameInProgress",false)) {
            resumeGameBtn.setVisibility(View.VISIBLE);
            Log.e(TAG,":onStart: Game is in progress");
        }
        else resumeGameBtn.setVisibility(View.GONE);

        super.onStart();
    }

    // Sets all views
    private void setViews(){
        setContentView(R.layout.activity_main_menu);
        cl = (ConstraintLayout)findViewById(R.id.cl_main_menu);
        resumeGameBtn = (Button) findViewById(R.id.ResumeGameBtn);
        startNewGameBtn = (Button)findViewById(R.id.StartNewGameBtn);
        optionsBtn = (Button)findViewById(R.id.OptionsBtn);
        exitBtn = (Button)findViewById(R.id.ExitBtn);
    }

    // Applies dark/light theme to GUI
    private void applyTheme() {
        int rid;
        if(settingsPreferences.getBoolean("DarkTheme",false)){
            Log.i(TAG,":applyTheme: Dark");
            rid = getResources().getIdentifier( "@drawable/btn_options_dark",null,getPackageName());
            optionsBtn.setBackgroundResource(rid);
            rid = getResources().getIdentifier( "@drawable/btn_start_new_game_dark",null,getPackageName());
            startNewGameBtn.setBackgroundResource(rid);
            rid = getResources().getIdentifier( "@drawable/btn_resume_game_dark",null,getPackageName());
            resumeGameBtn.setBackgroundResource(rid);
            rid = getResources().getIdentifier( "@drawable/btn_exit_dark",null,getPackageName());
            exitBtn.setBackgroundResource(rid);
            cl.setBackgroundColor(Color.parseColor("#111111"));

        }
        else {
            Log.i(TAG,":applyTheme: Light");
            rid = getResources().getIdentifier( "@drawable/btn_options_light",null,getPackageName());
            optionsBtn.setBackgroundResource(rid);
            rid = getResources().getIdentifier( "@drawable/btn_start_new_game_light",null,getPackageName());
            startNewGameBtn.setBackgroundResource(rid);
            rid = getResources().getIdentifier( "@drawable/btn_resume_game_light",null,getPackageName());
            resumeGameBtn.setBackgroundResource(rid);
            rid = getResources().getIdentifier( "@drawable/btn_exit_light",null,getPackageName());
            exitBtn.setBackgroundResource(rid);
            cl.setBackgroundColor(Color.parseColor("#89bff0"));
        }
    }

    // Creates the functionalities of the GUI
    private void connectGUI()
    {
        resumeGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playGame();
            }
        });
        startNewGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNewGame();
            }
        });
        optionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openOptions();
            }
        });
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                exitGame();
            }
        });
    }

    // If no game existed, starts a new one.
    // Else displays msg and if accepted reset everything.
    private void startNewGame(){
        Log.i(TAG,":startNewGame:");

        if (gameDataPreferences.getBoolean("GameInProgress",false)){
            AlertDialog.Builder alertDialogBuilder;
            alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Start new game?");
            alertDialogBuilder.setMessage(
                        "Are you sure you want to start new game. "
                    +   "All data from previous game will be reset. "
                    +   "Also levels will not be the same!");
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    resetGame();
                    playGame();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
        else {
            resetGame();
            playGame();
        }

    }

    // Resets saved data for passed levels
    private void resetGame() {
        progressPreferences.edit().clear().commit();
        levelPreferences.edit().clear().commit();
    }

    // Go to choose Difficulty
    private void playGame() {
        editor = gameDataPreferences.edit();
        editor.putBoolean("GameInProgress",true);
        editor.apply();
        Intent intent = new Intent(this, ChooseDifficultyActivity.class);
        startActivity(intent);
    }

    // Open options menu
    private void openOptions(){
        Log.i(TAG,":openOptions:");
        Intent intent = new Intent(MainMenuActivity.this,OptionsActivity.class);
        startActivity(intent);
    }

    // Exit the game
    private void exitGame(){
        Log.i(TAG,":exitGame:");
        finish();
        System.exit(0);
    }

}
