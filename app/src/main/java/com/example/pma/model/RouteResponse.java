package com.example.pma.model;

import java.util.HashMap;
import java.util.List;

public class RouteResponse {
    private Long id;
    private String startTime;

    private String endTime;

    private HashMap<String, List<Double>> points;

    private Double distance;

    private Double calories;


    public RouteResponse(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public HashMap<String, List<Double>> getPoints() {
        return points;
    }

    public void setPoints(HashMap<String, List<Double>> points) {
        this.points = points;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Double getCalories() {
        return calories;
    }

    public void setCalories(Double calories) {
        this.calories = calories;
    }

    public RouteResponse(Long id, String startTime, String endTime, Double distance, Double calories) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.distance = distance;
        this.calories = calories;
    }
}
