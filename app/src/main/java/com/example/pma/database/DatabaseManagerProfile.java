package com.example.pma.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.pma.model.Goal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DatabaseManagerProfile {
    private DatabaseHelper dbHelper;
    private Context context;
    private SQLiteDatabase database;
    private static final String TAG = "DatabaseManagerProfile";

    public DatabaseManagerProfile(Context c){
        this.context = c;
    }
    public  DatabaseManagerProfile open() throws SQLException {
        dbHelper = new DatabaseHelper(context);

        database = dbHelper.getWritableDatabase();
        return  this;
    }
    public void close(){
        dbHelper.close();
    }
    public void insert(double height, double weight){

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.HEIGHT, height);
        contentValues.put(DatabaseHelper.WEIGHT, weight);
        database.insert(DatabaseHelper.TABLE_PROFILE, null, contentValues);
    }
    public Cursor fetch(){
        String[] columns  = new String[]{DatabaseHelper._ID, DatabaseHelper.HEIGHT,DatabaseHelper.WEIGHT};
        Cursor cursor = database.query(DatabaseHelper.TABLE_PROFILE, columns, null,null,null,null,null);
        if(cursor != null){
            cursor.moveToFirst();
        }
        return  cursor;

    }
    public int update(long id, double height, double weight){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.HEIGHT, height);
        contentValues.put(DatabaseHelper.WEIGHT, weight);
        int i = database.update(DatabaseHelper.TABLE_PROFILE, contentValues,DatabaseHelper._ID + " = "+id,null);
        return i ;
    }
    public  void delete(long id){
        database.delete(DatabaseHelper.TABLE_PROFILE,DatabaseHelper._ID+" = "+ id,null);
    }

}
