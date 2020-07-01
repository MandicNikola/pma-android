package com.example.pma.model;

import java.util.Date;

public class GoalResponse {

    private Long id;

    private double goalValue;

    private String goalKey;

    private String localDateTime;

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
        this.localDateTime = date;
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

    public String getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(String localDateTime) {
        this.localDateTime = localDateTime;
    }

    public Double getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(Double currentValue) {
        this.currentValue = currentValue;
    }

    public int getNotified() {
        return notified;
    }

    public void setNotified(int notified) {
        this.notified = notified;
    }
}
