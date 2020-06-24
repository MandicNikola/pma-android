package com.example.pma;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.example.pma.database.DatabaseManagerPoint;
import com.example.pma.database.DatabaseManagerRoute;
import com.example.pma.model.GoalResponse;
import com.example.pma.model.LoginRequest;
import com.example.pma.model.LoginResponse;
import com.example.pma.model.Point;
import com.example.pma.model.Profile;
import com.example.pma.model.Route;
import com.example.pma.model.RouteRequest;
import com.example.pma.model.RouteResponse;
import com.example.pma.model.UserResponse;
import com.example.pma.services.AuthPlaceholder;
import com.example.pma.services.GoalPlaceholder;
import com.example.pma.services.RoutePlaceholder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import hossamscott.com.github.backgroundservice.RunService;
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
    private RoutePlaceholder routeService;
    private static final int NOTIFICATION_ID = 1;
    private DatabaseManagerRoute dbManager;
    private DatabaseManagerPoint managerPoint;
    private ArrayList<Route> routes;
    private List<Point> points = new ArrayList<com.example.pma.model.Point>();


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
        dbManager = new DatabaseManagerRoute(this);
        managerPoint = new DatabaseManagerPoint(this);
        routeService = retrofit.create(RoutePlaceholder.class);

        RunService repeat = new RunService(this);
        repeat.call(120, true);
        IntentFilter intentFilter = new IntentFilter("alaram_received");
        registerReceiver(alarm_receiver, intentFilter);


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

                                Call<Profile> call2 = service.getProfile("Bearer "+token);
                                call2.enqueue(new Callback<Profile>() {
                                    @Override
                                    public void onResponse(Call<Profile> call, Response<Profile> response) {
                                        Log.d(TAG,"Code is "+response.code());
                                        if (response.isSuccessful()) {
                                            if(response.code() == 200){
                                                SharedPreferences.Editor editor = preferences.edit();
                                                if(response.body().isWaterReminder()){
                                                    editor.putString("reminder", "true");
                                                }else{
                                                    editor.putString("reminder", "false");
                                                }
                                                editor.commit();
                                            }
                                        }
                                    }
                                    @Override
                                    public void onFailure(Call<Profile> call, Throwable t) {
                                        Log.d(TAG,"Unsuccessfull");
                                    }
                                });
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
    BroadcastReceiver alarm_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // your logic here

            dbManager.open();
            managerPoint.open();
            routes = dbManager.getRoutes();
            //sinhr. id je -1
             for(Route route:routes){
                 if(route.getSynchronized_id() == -1){
                     if(preferences.contains("token") ) {
                         String token = preferences.getString("token",null);

                         points = managerPoint.getRoutePoints(route.getId());
                    HashMap<String, List<Double>> pointsMap = new HashMap<String, List<Double>>();
                          for(Point point:points){
                                List<Double> values = new ArrayList<>();
                                values.add(point.getLatitude());
                                values.add(point.getLongitude());
                                pointsMap.put(point.getDateTime(),values);
                          }

                    RouteRequest routeRequest = new RouteRequest(route.getStart_time(),route.getEnd_time(),pointsMap,route.getDistance());
                     Call<RouteResponse> call = routeService.saveRoute(routeRequest,"Bearer "+token);
                     call.enqueue(new Callback<RouteResponse>() {
                         @Override
                         public void onResponse(Call<RouteResponse> call, Response<RouteResponse> response) {
                             Log.d(TAG," kod je"+response.code());

                             if (response.isSuccessful()) {
                                 Log.d(TAG," uspjesno  je"+response.body().getId());
                                 if(response.code() == 200){
                                     Log.d(TAG," vratio se posle dodavanja "+response.code());
                                    //dbManager.updateSynchronized(route.getId(), response.body().getId());
                                 }
                             }
                         }
                         @Override
                         public void onFailure(Call<RouteResponse> call, Throwable t) {
                             Log.d(TAG," neuspesno");

                         }
                     });


                    }
                 }
             }


        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(alarm_receiver);
    }

}
