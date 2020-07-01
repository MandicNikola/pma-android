package com.example.pma.model;

public class GoalRequest {
    private String dateTime;

    private String goalKey;

    private double goalValue;
    private Long userId;

    private double percentage;

    private Double currentValue;
    public GoalRequest(){
    }

    public GoalRequest(String dateTime, String goalKey, double goalValue, Long userId, double percentage) {
        this.dateTime = dateTime;
        this.goalKey = goalKey;
        this.goalValue = goalValue;
        this.userId = userId;
        this.percentage = percentage;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getGoalKey() {
        return goalKey;
    }

    public void setGoalKey(String goalKey) {
        this.goalKey = goalKey;
    }

    public double getGoalValue() {
        return goalValue;
    }

    public void setGoalValue(double goalValue) {
        this.goalValue = goalValue;
    }

    public Double getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(Double currentValue) {
        this.currentValue = currentValue;
    }
}
