package com.example.pma;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.pma.database.DatabaseManagerGoal;
import com.example.pma.database.DatabaseManagerRoute;
import com.example.pma.model.UserResponse;
import com.example.pma.services.AuthPlaceholder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CreateGoalActivity extends AppCompatActivity {
    private Spinner spinner;
    String[] spinner_array = { "Calories", "Distance"};
    private String date;
    private String key;
    private int value;
    private DatabaseManagerGoal dbManager;
    private SharedPreferences preferences;
    Retrofit retrofit;
    private AuthPlaceholder service;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_goal);
        retrofit = new Retrofit.Builder()
                .baseUrl("https://pma-app-19.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        preferences = getSharedPreferences("user_detail", MODE_PRIVATE);

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

        if(preferences.contains("token") ) {
            String token = preferences.getString("token",null);
            service = retrofit.create(AuthPlaceholder.class);

            Call<UserResponse> call = service.getLoggedUser("Bearer "+token);
            call.enqueue(new Callback<UserResponse>() {
                @Override
                public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {

                    if (response.isSuccessful()) {
                        if(response.code() == 200){
                            id = response.body().getId();
                            dbManager.insert(key, value, date, id);

                        }
                    }
                }
                @Override
                public void onFailure(Call<UserResponse> call, Throwable t) {

                }
            });
        }
        Intent intent = new Intent(CreateGoalActivity.this, GoalActivity.class);
        startActivity(intent);
    }

    /*public void testInsert(){

        Cursor cursor = dbManager.fetch();
        cursor.moveToFirst();
        //index krece od 1
        CharSequence mess = "Unos goal "+cursor.getString(2);
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, mess, duration);
        toast.show();
    }
*/


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
