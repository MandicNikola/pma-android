package com.example.pma.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Route implements Parcelable {
    private Long id;
    private int calories;
    private int distance;
    private String unit;
    private long synchronized_id;


    public Route() {

    }

    public Route(Long id, int calories, int distance, String unit) {
        this.id = id;
        this.calories = calories;
        this.distance = distance;
        this.unit = unit;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
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
        calories = in.readInt();
        distance = in.readInt();
        unit = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public long getSynchronized_id() {
        return synchronized_id;
    }

    public void setSynchronized_id(long synchronized_id) {
        this.synchronized_id = synchronized_id;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeInt(calories);
        dest.writeInt(distance);
        dest.writeString(unit);
    }

    public static final Parcelable.Creator<Route> CREATOR = new Parcelable.Creator<Route>() {
        public Route createFromParcel(Parcel in) {
            return new Route(in);
        }

        public Route[] newArray(int size) {
            return new Route[size];
        }
    };
}
