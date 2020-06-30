package com.example.pma;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;

import com.example.pma.database.DatabaseManagerProfile;
import com.example.pma.dialogues.MessageDialogue;
import com.example.pma.model.ProfileDB;
import com.example.pma.model.UserResponse;
import com.example.pma.model.UserSettings;
import com.example.pma.services.AuthPlaceholder;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SettingsActivity extends AppCompatActivity {
    Switch switchReminder;
    Switch switchSync;
    boolean reminder;
    boolean sync = false;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        switchReminder = (Switch) findViewById(R.id.reminder);
        switchSync = (Switch) findViewById(R.id.sync);

        if (savedInstanceState != null) {
            if(savedInstanceState.getBoolean("syncFlag")){
                sync = true;
            }
            if(savedInstanceState.getBoolean("reminder")){
                reminder = true;
            }
            switchReminder.setChecked(savedInstanceState.getBoolean("reminder"));
            switchSync.setChecked(savedInstanceState.getBoolean("reminder"));

        }

        preferences = getSharedPreferences("user_detail", MODE_PRIVATE);
        if(preferences.contains("syncFlag")){
            switchSync.setChecked(preferences.getBoolean("syncFlag",false));
        }
        if(preferences.contains("waterFlag")){
            switchReminder.setChecked(preferences.getBoolean("waterFlag",false));
        }else{
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("waterFlag", reminder);
            editor.commit();
        }


        switchReminder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled

                    reminder = true;

                } else {
                    // The toggle is disabled
                    reminder = false;
                }
                changeWater(reminder);
            }
        });
        switchSync.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    changeSync(true);
                    sync = true;
                } else {
                    // The toggle is disabled
                    changeSync(false);
                    sync = false;
                }
            }
        });
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if(savedInstanceState.getBoolean("reminder")){
           switchReminder.setChecked(true);
       }
        if(savedInstanceState.getBoolean("syncFlag")){
            switchSync.setChecked(true);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("reminder",reminder);
        savedInstanceState.putBoolean("syncFlag",sync);
        super.onSaveInstanceState(savedInstanceState);
    }

    public void changeSync(boolean syncFlag){
        preferences = getSharedPreferences("user_detail", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("syncFlag", syncFlag);
        editor.commit();
    }
    public void changeWater(boolean waterFlag){
        preferences = getSharedPreferences("user_detail", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("waterFlag", waterFlag);
        editor.commit();
    }
}