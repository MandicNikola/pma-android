package com.example.pma;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.pma.database.DatabaseManagerGoal;
import com.example.pma.database.DatabaseManagerRoute;

public class CreateGoalActivity extends AppCompatActivity {
    private Spinner spinner;
    String[] spinner_array = { "Calories", "Distance"};
    private String date;
    private String key;
    private int value;
    private DatabaseManagerGoal dbManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_goal);
        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter adapter= new ArrayAdapter(this,android.R.layout.simple_spinner_item, spinner_array);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        dbManager = new DatabaseManagerGoal(this);
        dbManager.open();

    }
    public void createGoal(View view) {
        EditText valueEditText = (EditText)findViewById(R.id.goal_value);
        value = Integer.parseInt(valueEditText.getText().toString());
        key = (String) spinner.getSelectedItem();

        dbManager.insert(key,value,date);
        Intent intent = new Intent(CreateGoalActivity.this, GoalActivity.class);
        startActivity(intent);
        //testInsert();

    }

    public void testInsert(){

        Cursor cursor = dbManager.fetch();
        cursor.moveToFirst();
        //index krece od 1
        CharSequence mess = "Unos goal "+cursor.getString(2);
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, mess, duration);
        toast.show();
    }



    public void showDatapicker(View view) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(),"datePicker");
    }
    public void processDatePickerResult(int year, int month, int day) {
        String month_string = Integer.toString(month+1);
        String day_string = Integer.toString(day);
        String year_string = Integer.toString(year);
        date = year_string+"-"+month_string+"-"+day_string;
        String dateMessage = (month_string +
                "/" + day_string + "/" + year_string);
        Toast.makeText(this, "Date: " + dateMessage,
                Toast.LENGTH_SHORT).show();
    }
}
