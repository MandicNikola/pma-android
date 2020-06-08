package com.example.pma;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Placeholder;
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
import com.example.pma.model.Goal;
import com.example.pma.model.GoalRequest;
import com.example.pma.model.GoalResponse;
import com.example.pma.model.UserResponse;
import com.example.pma.services.AuthPlaceholder;
import com.example.pma.services.GoalPlaceholder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    private GoalPlaceholder goalService;

    private int id;
    public static final String GOAL_RESULT = "GOAL_RESULT";
    private static final String TAG = "CreateGoalActivity1";

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
    public void createGoal(View view) throws ParseException {
        EditText valueEditText = (EditText)findViewById(R.id.goal_value);
        value = Integer.parseInt(valueEditText.getText().toString());
        key = (String) spinner.getSelectedItem();
        goalService = retrofit.create(GoalPlaceholder.class);
        Date goalDate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
        // TODO: Just add later id of goal not 1
        Goal goal = new Goal(Long.parseLong("1"), value, key, goalDate);
        String token = "";


        if(preferences.contains("token") ) {
             token = preferences.getString("token",null);
            service = retrofit.create(AuthPlaceholder.class);

            Call<UserResponse> call = service.getLoggedUser("Bearer "+token);
            call.enqueue(new Callback<UserResponse>() {
                @Override
                public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {

                    if (response.isSuccessful()) {
                        if(response.code() == 200){
                            id = response.body().getId();
                            dbManager.insert(key, value, date, id,0);



                        }
                    }
                }
                @Override
                public void onFailure(Call<UserResponse> call, Throwable t) {

                }
            });
        }

        /* Used to parse string just in case for parsing it for backend */
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date parsedDate = simpleDateFormat.parse(date);
        GoalRequest goalReq = new GoalRequest(simpleDateFormat.format(parsedDate),key.toUpperCase(),value,(long)id,0);
        Call<GoalResponse> callGoal = goalService.addGoal(goalReq,"Bearer "+token);
        callGoal.enqueue(new Callback<GoalResponse>() {
            @Override
            public void onResponse(Call<GoalResponse> call, Response<GoalResponse> response) {
                Log.d(TAG," kod je"+response.code());

                if (response.isSuccessful()) {
                    Log.d(TAG," uspjesno  je"+response.body().getId());

                    if(response.code() == 200){
                        Log.d(TAG," vratio se posle dodavanja "+response.code());

                    }
                }
            }
            @Override
            public void onFailure(Call<GoalResponse> call, Throwable t) {
                Log.d(TAG," neuspesno");

            }
        });

        Intent intent  = new Intent();


        intent.putExtra(GOAL_RESULT, goal);
        setResult(RESULT_OK, intent);
        finish();
    }




    public void showDatapicker(View view) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(),"datePicker");
    }

    public void processDatePickerResult(int year, int month, int day) {
        String month_string = Integer.toString(month+1);
        String day_string = Integer.toString(day);
        String year_string = Integer.toString(year);
        Date goalDate = new Date();
        date = year_string+"-"+month_string+"-"+day_string;

    }
}
