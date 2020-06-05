package com.example.pma.model;

public class Point {
    private long id;
    private float longitude;
    private float latitude;
    private long route_id;

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

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public long getRoute_id() {
        return route_id;
    }

    public void setRoute_id(long route_id) {
        this.route_id = route_id;
    }

}
