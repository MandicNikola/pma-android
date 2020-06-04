package com.example.pma.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelperPoint extends SQLiteOpenHelper {
    public  static final String TABLE_NAME = "POINTS";
    public static final String _ID = "_id";
    public static final String LONGITUDE = "longitude";
    public  static final String LATITUDE = "latitude";
    public static final String ROUTE_ID = "route_id";
    //Database information
    static final String DB_NAME = "MASTER_ANDROID_APP.DB";
    static final int DB_VERSION = 1;
    public static final String CREATE_QUERY = "create table "+TABLE_NAME + "(" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + LONGITUDE+
            " REAL, " + LATITUDE+
            " REAL, " + ROUTE_ID+
            " INTEGER);";
    public DatabaseHelperPoint(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DB_NAME);
        onCreate(db);
    }
}
