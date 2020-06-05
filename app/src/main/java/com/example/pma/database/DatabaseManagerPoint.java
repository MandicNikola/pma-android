package com.example.pma.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseManagerPoint {
    private DatabaseHelper dbHelper;
    private Context context;
    private SQLiteDatabase database;
    public DatabaseManagerPoint(Context c){
        this.context = c;
    }

    public  DatabaseManagerPoint open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return  this;
    }
    public void close(){
        dbHelper.close();
    }

    public void insert(float longitude,float latitude,long route_id){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.LONGITUDE,longitude);
        contentValues.put(DatabaseHelper.LATITUDE,latitude);
        contentValues.put(DatabaseHelper.ROUTE_ID,route_id);

        database.insert(DatabaseHelper.TABLE_POINTS, null, contentValues);
    }
    public Cursor fetch(){
        String[] columns  = new String[]{DatabaseHelper._ID,DatabaseHelper.LONGITUDE,DatabaseHelper.LATITUDE,DatabaseHelper.ROUTE_ID};
        Cursor cursor = database.query(DatabaseHelper.TABLE_POINTS, columns, null,null,null,null,null);
        if(cursor != null){
            cursor.moveToFirst();
        }
        return  cursor;

    }
    public int update(long id,float longitude,float latitude,long route_id){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.LONGITUDE,longitude);
        contentValues.put(DatabaseHelper.LATITUDE,latitude);
        contentValues.put(DatabaseHelper.ROUTE_ID,route_id);

        int i = database.update(DatabaseHelper.TABLE_POINTS,contentValues,DatabaseHelper._ID + " = "+id,null);
        return i ;
    }

    public  void delete(long id){
        database.delete(DatabaseHelper.TABLE_POINTS,DatabaseHelper._ID+" = "+ id,null);
    }

}
