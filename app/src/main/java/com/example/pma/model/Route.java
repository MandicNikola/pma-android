package com.example.pma.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Route implements Parcelable {
    private Long id;
    private double calories;
    private double distance;
    private String unit;
    private Long synchronized_id;
    private String start_time;
    private String end_time;


    public Route() {

    }

    public Route(Long id, double calories, double distance, String unit) {
        this.id = id;
        this.calories = calories;
        this.distance = distance;
        this.unit = unit;
    }

    public Route(Long id, double calories, double distance) {
        this.id = id;
        this.calories = calories;
        this.distance = distance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getCalories() {
        return calories;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    Route(Parcel in) {
        id = in.readLong();
        calories = in.readDouble();
        distance = in.readDouble();
        unit = in.readString();
        start_time = in.readString();
        end_time = in.readString();
        synchronized_id = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public Long getSynchronized_id() {
        return synchronized_id;
    }

    public void setSynchronized_id(Long synchronized_id) {
        this.synchronized_id = synchronized_id;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeDouble(calories);
        dest.writeDouble(distance);
        dest.writeString(unit);
        dest.writeString(start_time);
        dest.writeString(end_time);
        dest.writeLong(synchronized_id);
    }

    public static final Parcelable.Creator<Route> CREATOR = new Parcelable.Creator<Route>() {
        public Route createFromParcel(Parcel in) {
            return new Route(in);
        }

        public Route[] newArray(int size) {
            return new Route[size];
        }
    };

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }
}
