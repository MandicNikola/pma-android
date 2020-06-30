package com.example.pma;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;

import com.example.pma.adapter.RouteAdapter;
import com.example.pma.database.DatabaseManagerRoute;
import com.example.pma.model.Route;
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

import java.util.ArrayList;

public class RouteActivity extends AppCompatActivity    implements NavigationView.OnNavigationItemSelectedListener{
    private RecyclerView recyclerView;
    private ArrayList<Route> routes;
    private RouteAdapter routeAdapter;
    private static final String TAG = "RouteActivity";
    private SharedPreferences preferences;
    private DatabaseManagerRoute dbManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        preferences = getSharedPreferences("user_detail", MODE_PRIVATE);
        dbManager = new DatabaseManagerRoute(this);
        dbManager.open();
        routes = dbManager.getRoutes();

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
        getMenuInflater().inflate(R.menu.route, menu);
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
            Intent intent = new Intent(RouteActivity.this, SettingsActivity.class);
            startActivity(intent);
        }
        if(id == R.id.nav_log_out){
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove("token");
            editor.commit();
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
}
