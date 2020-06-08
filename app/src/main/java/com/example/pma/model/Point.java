package com.example.pma.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Point  implements  Comparable<Point>{
    private long id;
    private double longitude;
    private double latitude;
    private long route_id;
    private String dateTime;

    public  Point(){
        super();
    }

    public Point(float longitude, float latitude, long route_id) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.route_id = route_id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public long getRoute_id() {
        return route_id;
    }

    public void setRoute_id(long route_id) {
        this.route_id = route_id;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public int compareTo(Point o) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        try {
            return simpleDateFormat.parse(getDateTime()).compareTo(simpleDateFormat.parse(o.getDateTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
