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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    BroadcastReceiver alarm_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            preferences = getSharedPreferences("user_detail", MODE_PRIVATE);
            Log.d(TAG, "Entered alarm_receiver");

            if(preferences.getBoolean("syncFlag",false) && hasInternet()) {
                Log.d(TAG, "sync on");

                dbManager.open();
                managerPoint.open();
                routes = dbManager.getRoutes();
                for (Route route : routes) {
                    if (route.getSynchronized_id() == -1) {
                        if (preferences.contains("token")) {
                            String token = preferences.getString("token", null);

                            points = managerPoint.getRoutePoints(route.getId());
                            HashMap<String, List<Double>> pointsMap = new HashMap<String, List<Double>>();
                            for (Point point : points) {
                                List<Double> values = new ArrayList<>();
                                String formattedDate = "";
                                try {
                                    Date date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(point.getDateTime());
                                    formattedDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").format(date);

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                values.add(point.getLatitude());
                                values.add(point.getLongitude());
                                pointsMap.put(formattedDate, values);

                            }
                            String formattedDateEndRoute = "";
                            String formattedDateStartRoute = "";
                            try {
                                Date date = new SimpleDateFormat("yyyy-MM-dd").parse(route.getEnd_time());
                                formattedDateEndRoute = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").format(date);
                                date = new SimpleDateFormat("yyyy-MM-dd").parse(route.getStart_time());
                                formattedDateStartRoute = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").format(date);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            RouteRequest routeRequest = new RouteRequest(formattedDateStartRoute, formattedDateEndRoute, pointsMap, route.getDistance());
                            routeRequest.setCalories(route.getCalories());
                            Call<RouteResponse> call = routeService.saveRoute(routeRequest, "Bearer " + token);
                            call.enqueue(new Callback<RouteResponse>() {
                                @Override
                                public void onResponse(Call<RouteResponse> call, Response<RouteResponse> response) {

                                    if (response.isSuccessful()) {
                                        if (response.code() == 200) {
                                            dbManager.updateSynchronized(route.getId(), response.body().getId());
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<RouteResponse> call, Throwable t) {
                                    Log.d(TAG, " neuspesno");

                                }
                            });


                        }
                    }
                }
            }

        }
    };

    public boolean hasInternet() {
        Log.d(TAG, "In hasInternet");

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        boolean connected = false;
        if(netInfo != null) {
            if (netInfo.getType() == ConnectivityManager.TYPE_WIFI || netInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                if (netInfo.isConnected()) {
                    Log.d(TAG, "Internet connected");
                    connected = true;
                } else {
                    Log.d(TAG, "Internet not connected 1");
                }
            }else{
                Log.d(TAG, "Internet not connected 2");
            }
         }else{
            Log.d(TAG, "No network");

        }
        return connected;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(alarm_receiver);
    }

}
