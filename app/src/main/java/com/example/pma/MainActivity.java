package com.example.pma;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

import com.example.pma.model.LoginRequest;
import com.example.pma.model.LoginResponse;
import com.example.pma.model.UserResponse;
import com.example.pma.services.AuthPlaceholder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private SharedPreferences preferences;
    Retrofit retrofit;
    private AuthPlaceholder service;

    private static final int NOTIFICATION_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        retrofit = new Retrofit.Builder()
                .baseUrl("https://pma-app-19.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        preferences = getSharedPreferences("user_detail", MODE_PRIVATE);

        Intent notifyIntent = new Intent(this, WaterReceiver.class);

        PendingIntent notifyPendingIntent = PendingIntent.getBroadcast
                (this, NOTIFICATION_ID, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // TODO: Need to implement initialization using shared preferences for water reminder
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        long repeatInterval = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
        long triggerTime = SystemClock.elapsedRealtime()
                + repeatInterval;

        if (alarmManager != null) {
            alarmManager.setInexactRepeating
                    (AlarmManager.ELAPSED_REALTIME_WAKEUP,
                            triggerTime, repeatInterval, notifyPendingIntent);
        }

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // your code to start second activity. Will wait for 3 seconds before calling this method
                if(preferences.contains("token") ) {
                    String token = preferences.getString("token",null);
                    service = retrofit.create(AuthPlaceholder.class);

                    Call<UserResponse> call = service.getLoggedUser("Bearer "+token);
                    call.enqueue(new Callback<UserResponse>() {
                        @Override
                        public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                            if(response.code() == 403){
                                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            }
                            if(response.code() == 200){
                                startActivity(new Intent(MainActivity.this, RouteActivity.class));
                            }
                        }
                        @Override
                        public void onFailure(Call<UserResponse> call, Throwable t) {
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        }
                    });
                } else {
                    //nobody is logged
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
                }
        }, 2000);

    }
}
