package com.example.boyanyotov.songle.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.boyanyotov.songle.R;

public class ChooseDifficultyActivity extends AppCompatActivity {
    static private String TAG = ChooseDifficultyActivity.class.getSimpleName();

    private static SharedPreferences settingsPreferences;
    private static SharedPreferences progressPreferences;
    private static SharedPreferences.Editor editor;
    private int selectedDifficulty;

    // GUI Elements
    private TextView chosenDifficultyTxt;
    private TextView difficultyStatisticsTxt;
    private ImageButton increaseBtn;
    private ImageButton decreaseBtn;
    private Button backBtn;
    private Button selectDifficultyBtn;
    private ConstraintLayout cl;

    // Holds all pre-assign texts
    private String[] allDifficultyTexts = new String [6];
    private String[] allStatisticsTexts = new String [6];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingsPreferences = this.getSharedPreferences("Settings",MODE_PRIVATE);
        progressPreferences = this.getSharedPreferences("Progress",MODE_PRIVATE);
        selectedDifficulty = progressPreferences.getInt("Difficulty",3);
        setViews();
        initTexts();
    }

    @Override
    protected void onStart() {
        super.onStart();
        selectedDifficulty = progressPreferences.getInt("Difficulty",3);
        connectGUI();
        applyTheme();
    }

    private void setViews(){
        setContentView(R.layout.activity_choose_difficulty);
        cl = (ConstraintLayout) findViewById(R.id.cl_choose_difficulty);
        chosenDifficultyTxt = (TextView) findViewById(R.id.ChosenDifficultyTxt);
        difficultyStatisticsTxt = (TextView) findViewById(R.id.DifficultyStatisticsTxt);

        // Buttons
        increaseBtn = (ImageButton) findViewById(R.id.IncreaseImageBtn);
        decreaseBtn = (ImageButton) findViewById(R.id.DecreaseImageBtn);
        selectDifficultyBtn = (Button) findViewById(R.id.SelectDifficultyBtn);
        backBtn = (Button) findViewById(R.id.BackToMainMenuBtn);
    }

    private void applyTheme(){
        int rid;
        if(settingsPreferences.getBoolean("DarkTheme",false)){
            cl.setBackgroundColor(Color.parseColor("#111111"));
            rid = getResources().getIdentifier("@drawable/btn_back_dark",null,getPackageName());
            backBtn.setBackgroundResource(rid);
            rid = getResources().getIdentifier("@drawable/btn_select_dark",null,getPackageName());
            selectDifficultyBtn.setBackgroundResource(rid);

        } else {
            cl.setBackgroundColor(Color.parseColor("#89bff0"));
            rid = getResources().getIdentifier("@drawable/btn_back_light",null,getPackageName());
            backBtn.setBackgroundResource(rid);
            rid = getResources().getIdentifier("@drawable/btn_select_light",null,getPackageName());
            selectDifficultyBtn.setBackgroundResource(rid);
        }
    }

    // Creates the functionality of he GUI
    private void connectGUI() {
        setTexts();

        // Assign increase and decrease difficulty buttons
        increaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                increaseDifficultyLevel();
            }
        });
        decreaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                decreaseDifficultyLevel();
            }
        });

        // Assign other buttons
        selectDifficultyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                openChooseLevel();
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                onBackPressed();
            }
        });
    }

    // Saves difficulty and calls Choose Level Activity
    private void openChooseLevel(){
        Log.i(TAG,":openChooseLevel:");

        editor = progressPreferences.edit();
        editor.putInt("Difficulty", selectedDifficulty);
        editor.commit();

        Intent intent = new Intent(this, ChooseLevelActivity.class);
        startActivity(intent);
    }

    // Decreases difficulty
    private void decreaseDifficultyLevel() {
        // Ensures that the interval stays from 1 to 5
        selectedDifficulty = (selectedDifficulty + 3) % 5 + 1;

        setTexts();
        Log.i(TAG,"Decreasing Difficulty to " + Integer.toString(selectedDifficulty));
    }

    // Increases difficulty
    private void increaseDifficultyLevel(){
        // Ensures that the interval stays from 1 to 5
        selectedDifficulty = selectedDifficulty % 5 + 1;

        setTexts();
        Log.i(TAG,"Increasing Difficulty to " + Integer.toString(selectedDifficulty));
    }

    // Assigns GUI Text to the text of the corresponding difficulty
    private void setTexts(){
        chosenDifficultyTxt.setText(allDifficultyTexts[selectedDifficulty]);
        difficultyStatisticsTxt.setText(allStatisticsTexts[selectedDifficulty]);
    }

    // Initialises texts to corresponding difficulties
    private void initTexts(){
        Log.i(TAG,"Init Texts: Start");
        allDifficultyTexts[1] = "Newbie";
        allDifficultyTexts[2] = "Easy,Peasy, Lemon squeezy";
        allDifficultyTexts[3] = "Meh Normal";
        allDifficultyTexts[4] = "Hard as a rock";
        allDifficultyTexts[5] = "ImpossiBro";

        allStatisticsTexts[1] = "INFO:" +
                "\nObtainable lyrics: All"+
                "\nClassification of lyrics:" +
                "\n\t1) Boring" +
                "\n\t2) Not boring" +
                "\n\t3) Interesting" +
                "\n\t4) Very interesting";

        allStatisticsTexts[2] = "INFO:" +
                "\nObtainable lyrics: All"+
                "\nClassification of lyrics:" +
                "\n\t1) Boring" +
                "\n\t2) Not boring" +
                "\n\t3) Interesting";
        allStatisticsTexts[3] = "INFO:" +
                "\nObtainable lyrics: 75%"+
                "\nClassification of lyrics:" +
                "\n\t1) Boring" +
                "\n\t2) Not boring" +
                "\n\t3) Interesting";
        allStatisticsTexts[4] = "INFO:" +
                "\nObtainable lyrics: 50%"+
                "\nClassification of lyrics:" +
                "\n\t1) Boring" +
                "\n\t2) Not boring";
        allStatisticsTexts[5] = "INFO:" +
                "\nObtainable lyrics: 25%"+
                "\nClassification of lyrics:" +
                "\n\t1) Unclassified";
    }
}
