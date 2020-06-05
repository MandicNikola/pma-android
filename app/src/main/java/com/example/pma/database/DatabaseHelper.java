package com.example.pma.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    static final String DB_NAME = "ANDROID_DATABASE.DB";
    static final int DB_VERSION = 1;
    public static final String TABLE_GOALS = "goals";
    public static final String TABLE_USERS = "users";
    public static final String TABLE_ROUTES = "routes";
    public static final String TABLE_POINTS = "points";
    public static final String TABLE_SETTINGS = "settings";

    public static final String FIRSTNAME = "firstname";
    public static final String LASTNAME = "lastname";
    public static final String USERNAME = "username";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";

    public static final String _ID = "_id";
    public static final String VALUE = "value";
    public static final String KEY = "goalKey";
    public static final String DATE = "date";
    public static final String WATER_REMINDER = "waterReminder";
    public static final String CALORIES = "calories";
    public static final String DISTANCE = "distance";
    public static final String UNIT = "unit";
    public static final String LONGITUDE = "longitude";
    public  static final String LATITUDE = "latitude";
    public static final String ROUTE_ID = "route_id";


    private static final String CREATE_GOALS = " create table " + TABLE_GOALS+"("+ _ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+ VALUE+
            " INTEGER, " + KEY+
            " TEXT, " + DATE + " TEXT);";

    private static final String CREATE_USERS = " create table " + TABLE_USERS+"("+ _ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+ FIRSTNAME+
            " TEXT, " + LASTNAME+
            " TEXT NOT NULL, " + USERNAME+
            " TEXT NOT NULL, " + EMAIL+
            " TEXT NOT NULL, " + PASSWORD + " TEXT);";

    private static final String CREATE_SETTINGS = " create table " + TABLE_SETTINGS+"("+ _ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+ WATER_REMINDER+
            " ,TEXT);";

    private static final String CREATE_ROUTES = " create table " + TABLE_ROUTES+"("+ _ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+ CALORIES+
            " REAL, " + DISTANCE+
            " REAL, " + UNIT + " TEXT);";

    public static final String CREATE_POINTS = "create table "+TABLE_POINTS + "(" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + LONGITUDE+
            " REAL, " + LATITUDE+
            " REAL, " + ROUTE_ID+
            " INTEGER);";
    public DatabaseHelper(Context context){
        super(context,DB_NAME,null,1);

    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_GOALS);
        db.execSQL(CREATE_POINTS);
        db.execSQL(CREATE_ROUTES);
        db.execSQL(CREATE_SETTINGS);
        db.execSQL(CREATE_USERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_POINTS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROUTES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTINGS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_GOALS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            onCreate(db);
        }
        onCreate(db);
    }
}