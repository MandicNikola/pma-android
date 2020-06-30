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
    Retrofit retrofit;
    private AuthPlaceholder service;
    private SharedPreferences preferences;
    private String token = "";
    private DatabaseManagerProfile dbManager;
    private Integer userId;
    private int reminderFlag;
    private static final String TAG = "SettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        switchReminder = (Switch) findViewById(R.id.reminder);
        switchSync = (Switch) findViewById(R.id.sync);

        if (savedInstanceState != null) {
            userId = savedInstanceState.getInt("userId");
            if(savedInstanceState.getBoolean("syncFlag")){
                sync = true;
            }
            switchSync.setChecked(savedInstanceState.getBoolean("syncFlag"));

        }

        retrofit = new Retrofit.Builder()
                .baseUrl("https://pma-app-19.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        dbManager = new DatabaseManagerProfile(this);
        dbManager.open();


        service = retrofit.create(AuthPlaceholder.class);
        preferences = getSharedPreferences("user_detail", MODE_PRIVATE);
        if(preferences.contains("syncFlag")){
            switchSync.setChecked(preferences.getBoolean("syncFlag",false));
        }

        if (preferences.contains("token")) {
            token = preferences.getString("token", null);

            Call<UserResponse> callLoggedUser = service.getLoggedUser("Bearer " + token);
            callLoggedUser.enqueue(new Callback<UserResponse>() {
                @Override
                public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {

                    if (response.code() == 200) {
                        userId = response.body().getId();
                        reminderFlag = dbManager.getReminder(userId);
                        if (reminderFlag == 1) {
                            switchReminder.setChecked(true);
                        }
                    }
                }

                @Override
                public void onFailure(Call<UserResponse> call, Throwable t) {

                }
            });
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
                updateSettingsBack(reminder);
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

    public void updateSettingsBack(boolean reminder) {
        service = retrofit.create(AuthPlaceholder.class);

        if (preferences.contains("token")) {
            token = preferences.getString("token", null);
            UserSettings settings = new UserSettings(reminder);
            Call<HashMap<String, String>> updateSettings = service.updateReminder(settings, "Bearer " + token);
            dbManager = new DatabaseManagerProfile(this);
            dbManager.open();
            updateSettings.enqueue(new Callback<HashMap<String, String>>() {
                @Override
                public void onResponse(Call<HashMap<String, String>> call, Response<HashMap<String, String>> response) {

                    if (response.code() == 200) {
                        ProfileDB profileDB = dbManager.getProfileByUserId(userId);

                        if (reminder) {
                            dbManager.updateReminder(1, userId);
                         } else {
                            dbManager.updateReminder(0, userId);
                        }
                    } else {
                        MessageDialogue dialog = new MessageDialogue("There was a problem with updating water reminder, please try again", "Notification");
                        dialog.show(getSupportFragmentManager(), "Reminder");
                    }
                }

                @Override
                public void onFailure(Call<HashMap<String, String>> call, Throwable t) {
                    MessageDialogue dialog = new MessageDialogue("There was a problem with updating water reminder, please try again", "Notification");
                    dialog.show(getSupportFragmentManager(), "Reminder");
                }
            });

        }


    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if(savedInstanceState.getBoolean("reminder")){
           switchReminder.setChecked(true);
       }
        if(savedInstanceState.getBoolean("syncFlag")){
            switchSync.setChecked(true);
        }
        userId = savedInstanceState.getInt("userId");
        dbManager = new DatabaseManagerProfile(this);
        dbManager.open();

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("reminder",reminder);
        savedInstanceState.putBoolean("syncFlag",sync);
        savedInstanceState.putInt("userId",userId);
        super.onSaveInstanceState(savedInstanceState);
    }

    public void changeSync(boolean syncFlag){
        preferences = getSharedPreferences("user_detail", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("syncFlag", syncFlag);
        editor.commit();
    }
}