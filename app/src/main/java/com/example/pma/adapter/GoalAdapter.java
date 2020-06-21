package com.example.pma.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pma.R;
import com.example.pma.model.Goal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class GoalAdapter  extends RecyclerView.Adapter<GoalAdapter.ViewHolder>{
private ArrayList<Goal> goalList;
private Context context;

    public GoalAdapter(ArrayList<Goal> goalsData, Context context) {
            this.goalList = goalsData;
            this.context = context;
        }

    @NonNull
    @Override
    public GoalAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new GoalAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.goal_list_item, parent, false));
        }

    @Override
    public void onBindViewHolder(@NonNull GoalAdapter.ViewHolder holder, int position) {
            //binding data to viewholder
            //get current object from the list
            Goal goal = goalList.get(position);
            holder.bindTo(goal);
        }

    @Override
    public int getItemCount() {
        if(goalList == null){
            return 0;
        }
            return goalList.size();
        }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView dateText;
        private TextView goalText;
        private TextView valueText;
        private TextView currentValueText;
        private ProgressBar progressBar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateText = itemView.findViewById(R.id.goal_date);
            goalText = itemView.findViewById(R.id.goal_key);
            valueText = itemView.findViewById(R.id.goal_value);
            progressBar = itemView.findViewById(R.id.simpleProgressBar);
            currentValueText = itemView.findViewById(R.id.currentValue);
        }
        void bindTo(Goal currentGoal) {
            dateText.setText(new SimpleDateFormat("dd/MM/yyyy").format(currentGoal.getDate()));
            goalText.setText(currentGoal.getGoalKey());
            Double val = currentGoal.getGoalValue();
            String valS = val.toString();
            valueText.setText(valS);
            progressBar.setMax((int)currentGoal.getGoalValue());
            progressBar.setProgress((int)currentGoal.getCurrentValue());
            if(currentGoal.getNotified() != 1){
                double percentage = currentGoal.getCurrentValue()/currentGoal.getGoalValue()*100;
                currentValueText.setText(""+ Math.round(percentage*100)/100.0+" %");
             }
        }

    }

}
