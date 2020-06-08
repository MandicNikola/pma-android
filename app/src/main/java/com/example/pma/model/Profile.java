package com.example.pma.model;

public class Profile {

    private String username;
    private String firstname;
    private String lastname;
    private String email;
    private double height;
    private double weight;
    private String gender;
    private boolean waterReminder;

    public Profile(){}

    public Profile(String username, String firstname, String lastname, String email, double height, double weight, String gender, boolean waterReminder) {
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.height = height;
        this.weight = weight;
        this.gender = gender;
        this.waterReminder = waterReminder;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public boolean isWaterReminder() {
        return waterReminder;
    }

    public void setWaterReminder(boolean waterReminder) {
        this.waterReminder = waterReminder;
    }
}
