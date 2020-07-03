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
import android.widget.TextView;
import android.widget.Toast;

import com.example.pma.database.DatabaseManagerGoal;
import com.example.pma.database.DatabaseManagerRoute;
import com.example.pma.model.Goal;
import com.example.pma.model.GoalRequest;
import com.example.pma.model.GoalResponse;
import com.example.pma.model.UserResponse;
import com.example.pma.services.AuthPlaceholder;
import com.example.pma.services.GoalPlaceholder;

import org.w3c.dom.Text;

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
    private TextView dateTextView;
    private EditText valueTextView;
    String[] spinner_array = { "Calories", "Distance"};
    private String date;
    private String key;
    private Double value;
    private DatabaseManagerGoal dbManager;
    private SharedPreferences preferences;
    Retrofit retrofit;
    private AuthPlaceholder service;
    private GoalPlaceholder goalService;

    private  ArrayAdapter adapter;
    private int id;
    public static final String GOAL_RESULT = "GOAL_RESULT";
    private static final String TAG = "CreateGoalActivity1";
    private long idBack;
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
         adapter= new ArrayAdapter(this,android.R.layout.simple_spinner_item, spinner_array);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        dateTextView = (TextView) findViewById(R.id.goal_date);

        valueTextView = (EditText) findViewById(R.id.goal_value);
        if(savedInstanceState != null){
            if(savedInstanceState.containsKey("value")){
                valueTextView.setText(savedInstanceState.getString("value"));
            }
            if(savedInstanceState.containsKey("key")){
                int selectedPosition =  adapter.getPosition(savedInstanceState.get("key"));
                spinner.setSelection(selectedPosition);

            }
            if(savedInstanceState.containsKey("date")){
                dateTextView.setText(savedInstanceState.getString("date"));
                this.date = savedInstanceState.getString("date");
            }
        }
        dbManager = new DatabaseManagerGoal(this);
        dbManager.open();

    }
    public void createGoal(View view) throws ParseException {
        EditText valueEditText = (EditText)findViewById(R.id.goal_value);
        value = Double.parseDouble(valueEditText.getText().toString());
        key = (String) spinner.getSelectedItem();
        goalService = retrofit.create(GoalPlaceholder.class);
        Log.d("bgt","datum je "+date);
        Date goalDate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
        Goal goal = new Goal(Long.parseLong("1"), value, key, goalDate);
        goal.setCurrentValue(0);
        String token = "";


        /* Used to parse string just in case for parsing it for backend */
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date parsedDate = simpleDateFormat.parse(date);
        GoalRequest goalReq = new GoalRequest(simpleDateFormat.format(parsedDate),key.toUpperCase(),value,(long)id,0);
        goalReq.setCurrentValue(0.00);
        if(preferences.contains("token") ) {
            token = preferences.getString("token",null);
        }
        Call<GoalResponse> callGoal = goalService.addGoal(goalReq,"Bearer "+token);
        callGoal.enqueue(new Callback<GoalResponse>() {
            @Override
            public void onResponse(Call<GoalResponse> call, Response<GoalResponse> response) {
                Log.d(TAG," kod je"+response.code());

                if (response.isSuccessful()) {
                    Log.d(TAG," uspjesno  je"+response.body().getId());
                    idBack = response.body().getId();
                    if(response.code() == 200){
                        Log.d(TAG," vratio se posle dodavanja "+response.code());
                        insertInDatabase(idBack);
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
    public void insertInDatabase(long backId){
        String token = "";
        Log.d(TAG," insert in database "+backId);


        if(preferences.contains("token") ) {
            token = preferences.getString("token",null);
            service = retrofit.create(AuthPlaceholder.class);

            Call<UserResponse> call = service.getLoggedUser("Bearer "+token);
            call.enqueue(new Callback<UserResponse>() {
                @Override
                public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {

                    if (response.isSuccessful()) {
                        if(response.code() == 200){
                            id = response.body().getId(); Log.d(TAG," idBack "+idBack);

                            long i =  dbManager.insert(key, value, date, id,0,0,idBack);
                            Log.d(TAG," id od goal na frontu je"+i);

                        }
                    }
                }
                @Override
                public void onFailure(Call<UserResponse> call, Throwable t) {

                }
            });
        }

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if(savedInstanceState.containsKey("value")){
            EditText valueText = (EditText) findViewById(R.id.goal_value);
            Double valueDouble = savedInstanceState.getDouble("value");
            valueText.setText(valueDouble.toString());
        }
        if(savedInstanceState.containsKey("key")){
            Spinner spinner = (Spinner) findViewById(R.id.spinner);
           int selectedPosition =  adapter.getPosition(savedInstanceState.get("key"));
           spinner.setSelection(selectedPosition);

        }
        if(savedInstanceState.containsKey("date")){
            TextView dateText = (TextView) findViewById(R.id.goal_date);

            dateText.setText(savedInstanceState.getString("date"));

            this.date = savedInstanceState.getString("date");
        }
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        EditText valueEditText = (EditText)findViewById(R.id.goal_value);

        Log.d("bgt ",""+valueEditText.getText().toString());
        if(!valueEditText.getText().toString().isEmpty()) {
            value = Double.parseDouble(valueEditText.getText().toString());
        }else{
            Log.d("bgt ","prazno");
        }
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        key = (String) spinner.getSelectedItem();
        savedInstanceState.putDouble("value",value);
        savedInstanceState.putString("key",this.key);
        savedInstanceState.putString("date",this.date);
        super.onSaveInstanceState(savedInstanceState);
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
        dateTextView.setText(date);

    }
}
