package com.example.pma;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragmentCompat {
    private static final String TAG = "SettingsFragment";
    private SharedPreferences preferences;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         SwitchPreferenceCompat syncPreference = (SwitchPreferenceCompat) findPreference("keySync");
        SwitchPreferenceCompat reminderPreference = (SwitchPreferenceCompat) findPreference("keyReminder");

        preferences = this.getActivity().getSharedPreferences("user_detail", Context.MODE_PRIVATE);

        syncPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean syncFlag = (boolean) newValue;
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("syncFlag", syncFlag);
                editor.commit();
                syncPreference.setChecked(syncFlag);
                return true;
            }
        });

        reminderPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean waterFlag = (boolean) newValue;
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("waterFlag", waterFlag);
                editor.commit();
                syncPreference.setChecked(waterFlag);
                return true;
            }
        });
    }
}
