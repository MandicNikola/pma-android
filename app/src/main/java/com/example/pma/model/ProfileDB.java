package com.example.pma.model;

public class ProfileDB {
    double height;
    double weight;
    int user_id;

    public ProfileDB(){}

    public ProfileDB(double height, double weight, int user_id) {
        this.height = height;
        this.weight = weight;
        this.user_id = user_id;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
}
