package com.example.pma.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Date;

public class DatabaseHelperGoal extends SQLiteOpenHelper {

    //Table name
    public static final String TABLE_NAME = "GOALS";
    //Table Columns
    public static final String _ID = "_id";
    public static final String VALUE = "value";
    public static final String KEY = "goal key";
    public static final String DATE = "date";
    //Database information
    static final String DB_NAME = "MASTER_ANDROID_APP.DB";
    static final int DB_VERSION = 1;

    //Creating table query
    private static final String CREATE_TABLE = " create table " + TABLE_NAME+"("+ _ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+ VALUE+
            " TEXT, " + KEY+
            " TEXT, " + DATE + " TEXT);";

    public DatabaseHelperGoal(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DB_NAME);
        onCreate(db);
    }
}
