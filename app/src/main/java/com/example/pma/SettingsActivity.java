package com.example.pma;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class SettingsActivity extends AppCompatActivity {
    private Spinner spinnerGender;
    private Spinner spinnerUnits;
    String[] gender_array = { "Male", "Female"};
    String[] units_array = { "Metric", "Imperial"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        spinnerGender = (Spinner) findViewById(R.id.gender_spinner);
        spinnerUnits = (Spinner) findViewById(R.id.unit_spinner);


        ArrayAdapter adapterGender= new ArrayAdapter(this,android.R.layout.simple_spinner_item, gender_array);
        adapterGender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(adapterGender);
        ArrayAdapter adapterUnit= new ArrayAdapter(this,android.R.layout.simple_spinner_item, units_array);
        adapterUnit.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnits.setAdapter(adapterUnit);
    }
}
