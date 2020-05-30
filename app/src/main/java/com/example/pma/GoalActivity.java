package com.example.pma;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.pma.adapter.GoalAdapter;
import com.example.pma.model.Goal;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Date;

public class GoalActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<Goal> goals;
    private GoalAdapter goalAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GoalActivity.this, CreateGoalActivity.class);
                startActivity(intent);
            }
        });

        /* recycler view init flow */
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if(savedInstanceState != null) {
            goals = savedInstanceState.getParcelableArrayList("goal");
        } else {
            goals = new ArrayList<>();
        }

        goalAdapter = new GoalAdapter( goals,this);

        recyclerView.setAdapter(goalAdapter);

        if(goals.size() == 0){
            initializeData();
        }

    }
    public void initializeData(){

        goals.clear();

        Goal goal1 = new Goal((long) 1,1200,"calories", new Date(2019,10,10));
        Goal goal2 = new Goal((long) 2,1900,"calories", new Date(2020,2,15));
        Goal goal3 = new Goal((long) 3,1600,"calories", new Date(2020,1,18));

        goals.add(goal1);
        goals.add(goal2);
        goals.add(goal3);

        goalAdapter.notifyDataSetChanged();

    }
}
