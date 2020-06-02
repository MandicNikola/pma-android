package com.example.pma.model;

public class UserSettings {
    private boolean waterReminder;

    public UserSettings(){}

    public UserSettings(boolean waterReminder){
        this.waterReminder = waterReminder;
    }

    public boolean isWaterReminder() {
        return waterReminder;
    }

    public void setWaterReminder(boolean waterReminder) {
        this.waterReminder = waterReminder;
    }
}
