package com.example.pma;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.pma.adapter.GoalAdapter;
import com.example.pma.database.DatabaseManagerGoal;
import com.example.pma.model.Goal;
import com.example.pma.model.UserResponse;
import com.example.pma.services.AuthPlaceholder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GoalActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<Goal> goals;
    private GoalAdapter goalAdapter;
    private SharedPreferences preferences;
    Retrofit retrofit;
    private DatabaseManagerGoal dbManager;
    public static final int CREATE_GOAL = 1;
    private static final String TAG = "GoalActivity";


    //saljemo token on vraca ciljeve
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal);

        retrofit = new Retrofit.Builder()
                .baseUrl("https://pma-app-19.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        preferences = getSharedPreferences("user_detail", MODE_PRIVATE);

        dbManager = new DatabaseManagerGoal(this);
        dbManager.open();
        goals = dbManager.getGoals();


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GoalActivity.this, CreateGoalActivity.class);
               startActivityForResult(intent,CREATE_GOAL);
            }
        });

        /* recycler view init flow */
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if(savedInstanceState != null) {
            goals = savedInstanceState.getParcelableArrayList("goal");
        }
        goalAdapter = new GoalAdapter( goals,this);
        recyclerView.setAdapter(goalAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CREATE_GOAL){
            if(resultCode == RESULT_OK && data.hasExtra(CreateGoalActivity.GOAL_RESULT)) {
                Goal goal = data.getParcelableExtra(CreateGoalActivity.GOAL_RESULT);

                goals.add(goal);
                goalAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        goals = savedInstanceState.getParcelableArrayList("goal");

    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList("goal",goals);
        super.onSaveInstanceState(savedInstanceState);
    }
    }
