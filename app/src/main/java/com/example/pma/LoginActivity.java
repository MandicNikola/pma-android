package com.example.pma;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pma.database.DatabaseManagerGoal;
import com.example.pma.database.DatabaseManagerPoint;
import com.example.pma.database.DatabaseManagerProfile;
import com.example.pma.database.DatabaseManagerRoute;
import com.example.pma.database.DatabaseManagerUser;
import com.example.pma.dialogues.MessageDialogue;

import com.example.pma.model.GoalResponse;
import com.example.pma.model.LoginRequest;
import com.example.pma.model.LoginResponse;
import com.example.pma.model.Profile;
import com.example.pma.model.RouteResponse;
import com.example.pma.model.UserResponse;
import com.example.pma.services.AuthPlaceholder;
import com.example.pma.services.GoalPlaceholder;
import com.example.pma.services.RoutePlaceholder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    Retrofit retrofit;
    private AuthPlaceholder service;
    private GoalPlaceholder goalService;
    private RoutePlaceholder routeService;
    private SharedPreferences preferences;
    private String username = "";
    private String savedUsername = "";
    private String password = "";
    private DatabaseManagerProfile dbManagerProfile;
    private DatabaseManagerUser dbManagerUser;
    private DatabaseManagerGoal dbManagerGoal;
    private DatabaseManagerRoute dbmanagerRoute;
    private DatabaseManagerPoint dbManagerPoint;

    private Integer userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        dbManagerProfile = new DatabaseManagerProfile(this);
        dbManagerProfile.open();
        dbManagerUser = new DatabaseManagerUser(this);
        dbManagerUser.open();

        retrofit = new Retrofit.Builder()
                .baseUrl("https://pma-app-19.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        if(savedInstanceState != null){
            if(savedInstanceState.containsKey("username")){
                ((EditText)findViewById(R.id.username)).setText(savedInstanceState.getString("username"));
            }
            if(savedInstanceState.containsKey("password")){
                ((EditText)findViewById(R.id.password)).setText(savedInstanceState.getString("password"));
            }
        }

    }
    public void checkData(View view){
        username = ((EditText)findViewById(R.id.username)).getText().toString();
        password = ((EditText)findViewById(R.id.password)).getText().toString();
        boolean correct = true;
            if(TextUtils.isEmpty(username)){
                correct = false;
                ((EditText) findViewById(R.id.username)).setError("Username is required");

            }
            if(TextUtils.isEmpty(password)){
                ((EditText) findViewById(R.id.password)).setError("Password is required");
                correct = false;
            }
            if(correct){
                LoginRequest request = new LoginRequest(username, password);
                navigateRoutes(request);
            }

    }
    public void navigateRoutes(LoginRequest request){
        service = retrofit.create(AuthPlaceholder.class);

        Call<LoginResponse> call = service.loginUser(request);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if(response.isSuccessful()){
                    if(response.code() == 200){
                        preferences = getSharedPreferences("user_detail", MODE_PRIVATE);
                        String savedUsername = "";
                        Log.d("Login","Prvi username "+ username);

                        if(preferences.contains("username")){

                            savedUsername = preferences.getString("username",null);

                            Log.d("Login","Sadrzi username "+ savedUsername);
                            if(!savedUsername.equals(username)){
                                Log.d("Login","Razliciti username ");

                                Log.d("Login","FIRST ****** "+username);


                                Log.d("Login","SECOND SAVED ****** "+ savedUsername);
                                dbManagerUser.deleteTables();
                                fillTables(response.body().getAccessToken());
                            }
                        }

                        SharedPreferences.Editor editor = preferences.edit();

                        editor.putString("token", response.body().getAccessToken());
                        editor.putString("username", username);
                        editor.commit();


                        MessageDialogue dialog = new MessageDialogue("You have successfully logged in", "Notification");
                        dialog.show(getSupportFragmentManager(), "logging dialog");
                        new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                // your code to start second activity. Will wait for 3 seconds before calling this method
                                Intent intent = new Intent(LoginActivity.this, RouteActivity.class);
                                startActivity(intent);
                            }
                        }, 2000);

                    }
                 }else{
                    if(response.code() == 406){
                        CharSequence mess = "Login usuccessfull "+response.code();
                        Context context = getApplicationContext();
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, mess, duration);
                        toast.show();
                    }else {
                        MessageDialogue dialog = new MessageDialogue("Sign in unsuccessfully, please try again.", "Notification");
                        dialog.show(getSupportFragmentManager(), "logging dialog");
                    }
                    Log.d(TAG,"Unsuccessfull logged "+response.code());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.d(TAG,"failed "+t.getMessage());
            }
        });
  }
    void fillTables(String token){
        goalService =  retrofit.create(GoalPlaceholder.class);
        routeService = retrofit.create(RoutePlaceholder.class);

        Log.d(TAG," Stigao u fill table");
        Call<UserResponse> callLoggedUser = service.getLoggedUser("Bearer "+token);
        callLoggedUser.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if(response.code() == 200) {
                    userId = response.body().getId();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {

            }
        });
        Call<Profile> call = service.getProfile("Bearer "+token);
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                if (response.isSuccessful()) {
                    if(response.code() == 200){
                        dbManagerProfile.insert(response.body().getHeight(),response.body().getWeight(), userId,0);
                    }
                }
            }
            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                Log.d(TAG,"Unsuccessfull");
            }
        });

        dbManagerGoal = new DatabaseManagerGoal(this);
        dbManagerGoal.open();
        Call<ArrayList<GoalResponse>> callGoal = goalService.getGoals("Bearer "+token);
        callGoal.enqueue(new Callback<ArrayList<GoalResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<GoalResponse>> call, Response<ArrayList<GoalResponse>> response) {
                Log.d(TAG," Kod je u goal "+response.code());
                if (response.isSuccessful()) {


                    Log.d(TAG," uspjesan  ");
                    if(response.code() == 200){
                       ArrayList<GoalResponse> goals = new ArrayList<>();
                       goals = response.body();

                       for(GoalResponse goal:goals) {
                          String key= "Distance";
                           if(goal.getGoalKey().equals("CALORIES")){
                              key = "Calories";
                          }

                           dbManagerGoal.insert(key,goal.getGoalValue(),goal.getLocalDateTime(),userId,goal.getCurrentValue(),goal.getNotified(),goal.getId());
                       }
                    }
                }
            }
            @Override
            public void onFailure(Call<ArrayList<GoalResponse>> call, Throwable t) {
                Log.d(TAG,"Unsuccessfull");
            }
        });

        dbmanagerRoute = new DatabaseManagerRoute(this);
        dbmanagerRoute.open();
        dbManagerPoint = new DatabaseManagerPoint(this);
        dbManagerPoint.open();

        Call<ArrayList<RouteResponse>> callRoute = routeService.getRoutes("Bearer "+token);
        callRoute.enqueue(new Callback<ArrayList<RouteResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<RouteResponse>> call, Response<ArrayList<RouteResponse>> response) {

                if (response.isSuccessful()) {

                    if(response.code() == 200){

                        ArrayList<RouteResponse> routes = new ArrayList<>();
                        routes = (ArrayList<RouteResponse>)response.body();
                        for(RouteResponse route:routes) {

                            long routeId =  dbmanagerRoute.insert(route.getCalories(),route.getDistance(),"m",route.getId(),route.getStartTime(),route.getEndTime());
                            for (Map.Entry<String, List<Double>> entry : route.getPoints().entrySet()) {

                                dbManagerPoint.insert(entry.getValue().get(0),entry.getValue().get(1),routeId,entry.getKey());
                            }
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<ArrayList<RouteResponse>> call, Throwable t) {
                Log.d(TAG,"Unsuccessfull");
            }
        });
    }

    public void navigateRegister(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if(savedInstanceState.containsKey("username")){
            ((EditText)findViewById(R.id.username)).setText(savedInstanceState.getString("username"));
        }
        if(savedInstanceState.containsKey("password")){
            ((EditText)findViewById(R.id.password)).setText(savedInstanceState.getString("password"));
        }
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        username = ((EditText)findViewById(R.id.username)).getText().toString();
        password = ((EditText)findViewById(R.id.password)).getText().toString();

        savedInstanceState.putString("username", username);
        savedInstanceState.putString("password", password);

        super.onSaveInstanceState(savedInstanceState);
    }

}
