package com.example.pma.model;

import java.util.Date;

public class GoalResponse {

    private Long id;

    private double goalValue;

    private String goalKey;

    private String date;

    private Double currentValue;

    private int notified;

    public GoalResponse(){
     }

    public GoalResponse(Long id,Double currentValue, int notified){
        this.id = id;
        this.currentValue = currentValue;
        this.notified = notified;
    }
    public GoalResponse(Long id, double goalValue, String goalKey, String date) {
        this.id = id;
        this.goalValue = goalValue;
        this.goalKey = goalKey;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getGoalValue() {
        return goalValue;
    }

    public void setGoalValue(double goalValue) {
        this.goalValue = goalValue;
    }

    public String getGoalKey() {
        return goalKey;
    }

    public void setGoalKey(String goalKey) {
        this.goalKey = goalKey;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
