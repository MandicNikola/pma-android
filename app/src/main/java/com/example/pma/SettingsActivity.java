package com.example.pma;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;

public class SettingsActivity extends AppCompatActivity {
    Switch switchReminder;
    Switch switchSync;
    boolean reminder;
    boolean sync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        switchReminder = (Switch) findViewById(R.id.reminder);
        switchSync = (Switch) findViewById(R.id.sync);

        switchReminder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    reminder = true;
                } else {
                    // The toggle is disabled
                    reminder = false;
                }
            }
        });
        switchSync.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    sync = true;
                } else {
                    // The toggle is disabled
                    sync = false;
                }
            }
        });
    }

    public void checkData(View view) {
    }
}
