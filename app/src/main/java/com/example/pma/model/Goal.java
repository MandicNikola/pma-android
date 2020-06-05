package com.example.pma.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Goal implements Parcelable {
    private Long id;
    private double goalValue;
    private String goalKey;
    private Date date;
    private User user;
    private int percentage;

    public Goal(){}



    public Goal(Long id, double goalValue, String goalKey, Date date){
        this.id = id;
        this.goalValue = goalValue;
        this.goalKey = goalKey;
        this.date = date;
    }
    public Goal(Long id, double goalValue, String goalKey, Date date, User user){
        this.id = id;
        this.goalValue = goalValue;
        this.goalKey = goalKey;
        this.date = date;
        this.user = user;
    }

    public Goal(Long id, double goalValue, String goalKey, Date date, User user, int percentage) {
        this.id = id;
        this.goalValue = goalValue;
        this.goalKey = goalKey;
        this.date = date;
        this.user = user;
        this.percentage = percentage;
    }
    protected Goal(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        goalValue = in.readDouble();
        goalKey = in.readString();
    }
    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }


    public static final Creator<Goal> CREATOR = new Creator<Goal>() {
        @Override
        public Goal createFromParcel(Parcel in) {
            return new Goal(in);
        }

        @Override
        public Goal[] newArray(int size) {
            return new Goal[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(getId());
        dest.writeDouble(getGoalValue());
        dest.writeString(getGoalKey());
        dest.writeString(getDate().toString());
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

}
