package com.example.pma;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;

import com.example.pma.adapter.RouteAdapter;
import com.example.pma.database.DatabaseManagerPoint;
import com.example.pma.database.DatabaseManagerRoute;
import com.example.pma.model.Point;
import com.example.pma.model.Route;
import com.example.pma.model.RouteRequest;
import com.example.pma.model.RouteResponse;
import com.example.pma.services.RoutePlaceholder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RouteActivity extends AppCompatActivity    implements NavigationView.OnNavigationItemSelectedListener{
    private RecyclerView recyclerView;
    private ArrayList<Route> routes;
    private RouteAdapter routeAdapter;
    private static final String TAG = "RouteActivity";
    private SharedPreferences preferences;
    private DatabaseManagerRoute dbManager;
    private RoutePlaceholder routeService;
    Retrofit retrofit;
    private DatabaseManagerPoint managerPoint;
    private ArrayList<Route> routesSync;
    private List<Point> points = new ArrayList<com.example.pma.model.Point>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        preferences = getSharedPreferences("user_detail", MODE_PRIVATE);
        retrofit = new Retrofit.Builder()
                .baseUrl("https://pma-app-19.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        dbManager = new DatabaseManagerRoute(this);
        dbManager.open();
        routes = dbManager.getRoutes();
        routeService = retrofit.create(RoutePlaceholder.class);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RouteActivity.this);

                builder.setTitle("New route");
                builder.setMessage("Do you want to start new route?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    /* start active route for now */
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                        Intent intent = new Intent(RouteActivity.this, ActiveRoute.class);
                        dialog.dismiss();
                        startActivity(intent);
                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Do nothing
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /* recycler view init flow */
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if(savedInstanceState != null) {
            routes = savedInstanceState.getParcelableArrayList("route");
        }

        routeAdapter = new RouteAdapter(this, routes);

        recyclerView.setAdapter(routeAdapter);
        routeAdapter.notifyDataSetChanged();


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(RouteActivity.this, SettingActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if(id == R.id.nav_goals) {
            Intent intent = new Intent(RouteActivity.this, GoalActivity.class);
            startActivity(intent);
        }
        if(id == R.id.nav_profile){
            Intent intent = new Intent(RouteActivity.this, ProfileActivity.class);
            startActivity(intent);
        }

        if(id == R.id.nav_settings){
            Intent intent = new Intent(RouteActivity.this, SettingActivity.class);
            startActivity(intent);
        }
        if(id == R.id.nav_log_out){
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove("token");
            editor.commit();
            doSync();
            Intent intent = new Intent(RouteActivity.this, MainActivity.class);
            startActivity(intent);
       }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("route", routes);
        super.onSaveInstanceState(outState);
    }
    public void doSync(){
        preferences = getSharedPreferences("user_detail", MODE_PRIVATE);
        Log.d(TAG, "Entered alarm_receiver");

        if(preferences.getBoolean("syncFlag",false) && hasInternet()) {
            Log.d(TAG, "sync on");
            managerPoint = new DatabaseManagerPoint(this);
            dbManager.open();
            managerPoint.open();
            routesSync = dbManager.getRoutes();
            for (Route route : routesSync) {
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
}
