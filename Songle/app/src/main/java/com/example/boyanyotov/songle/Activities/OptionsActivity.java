package com.example.boyanyotov.songle.Activities;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.boyanyotov.songle.R;

public class OptionsActivity extends AppCompatActivity {
    static private String TAG = OptionsActivity.class.getSimpleName();

    private static SharedPreferences settingsPreferences;
    private static SharedPreferences.Editor editor;

    private boolean isVibrationOn = false;
    private boolean isDarkThemeOn = false;

    private Switch vibrationSwitch;
    private Switch darkThemeSwitch;
    private Button backBtn;
    private ConstraintLayout cl_options;
    private String themeStr;
    private int rid;
    private String btnStr = "@drawable/btn_";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingsPreferences = this.getSharedPreferences("Settings",MODE_PRIVATE);

        loadSettings();
        setViews();
        connectGUI();
    }

    @Override
    protected void onStart(){

        loadSettings();
        applyTheme();


        super.onStart();
    }

    // TODO : add themes
    private void applyTheme(){
        if(isDarkThemeOn){
            themeStr = "_dark";
            cl_options.setBackgroundColor(Color.parseColor("#111111"));
        } else {
            themeStr = "_light";
            cl_options.setBackgroundColor(Color.parseColor("#89bff0"));
        }

        rid = getResources().getIdentifier( btnStr + "back" + themeStr,null,getPackageName());
        backBtn.setBackgroundResource(rid);
    }

    private void setViews(){
        setContentView(R.layout.activity_options);
        cl_options = (ConstraintLayout)findViewById(R.id.cl_options);
        vibrationSwitch = (Switch)findViewById(R.id.vibrationSwitch);
        darkThemeSwitch = (Switch)findViewById(R.id.darkThemeSwitch);
        backBtn = (Button)findViewById(R.id.optionsBackBtn);
    }

    private void connectGUI() {

        vibrationSwitch.setChecked(isVibrationOn);
        vibrationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.i(TAG,"VIBRATION: New boolean is " + Boolean.toString(b));
                isVibrationOn = b;
                if (b) setSettings("Vibration",true);
                else   setSettings("Vibration",false);
            }
        });

        darkThemeSwitch.setChecked(isDarkThemeOn);
        darkThemeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.i(TAG,"DARKTHEME: New boolean is " + Boolean.toString(b));
                isDarkThemeOn = b;
                if (b) setSettings("DarkTheme",true);
                else   setSettings("DarkTheme",false);
                applyTheme();
                connectGUI();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                onBackPressed();
            }
        });
    }

    private void loadSettings() {
        isVibrationOn = settingsPreferences.getBoolean("Vibration",false);
        isDarkThemeOn = settingsPreferences.getBoolean("DarkTheme",false);

        Log.i(TAG,Boolean.toString(isVibrationOn));
        Log.i(TAG,Boolean.toString(isDarkThemeOn));
    }

    private void setSettings(String setting, boolean val) {
        editor = settingsPreferences.edit();
        editor.putBoolean(setting,val);
        editor.apply();
    }


}
