package com.example.pma.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.pma.model.Point;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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

    public long insert(double longitude,double latitude,long route_id, String currentTime){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.LONGITUDE,longitude);
        contentValues.put(DatabaseHelper.LATITUDE,latitude);
        contentValues.put(DatabaseHelper.ROUTE_ID,route_id);
        contentValues.put(DatabaseHelper.CURRENT_TIME,currentTime);

       return database.insert(DatabaseHelper.TABLE_POINTS, null, contentValues);
    }
    public Cursor fetch(){
        String[] columns  = new String[]{DatabaseHelper._ID,DatabaseHelper.LONGITUDE,DatabaseHelper.LATITUDE,DatabaseHelper.ROUTE_ID,DatabaseHelper.CURRENT_TIME};
        Cursor cursor = database.query(DatabaseHelper.TABLE_POINTS, columns, null,null,null,null,null);
        if(cursor != null){
            cursor.moveToFirst();
        }
        return  cursor;

    }
    public int update(long id,float longitude,float latitude,long route_id,String currentTime){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.LONGITUDE,longitude);
        contentValues.put(DatabaseHelper.LATITUDE,latitude);
        contentValues.put(DatabaseHelper.ROUTE_ID,route_id);

        contentValues.put(DatabaseHelper.CURRENT_TIME,currentTime);

        int i = database.update(DatabaseHelper.TABLE_POINTS,contentValues,DatabaseHelper._ID + " = "+id,null);
        return i ;
    }

    public  void delete(long id){
        database.delete(DatabaseHelper.TABLE_POINTS,DatabaseHelper._ID+" = "+ id,null);
    }

    public List getRoutePoints(Long id) {
        Cursor cursor = database.rawQuery("select * from " + DatabaseHelper.TABLE_POINTS + " where " + DatabaseHelper.ROUTE_ID + "=" +id + ";", null);
        List points = new ArrayList<Point>();
        try {
            if (cursor.moveToFirst()) {
                do {
                    Point point = new Point();

                    point.setId(Long.parseLong(cursor.getString(cursor.getColumnIndex(dbHelper._ID))));
                    point.setLongitude(cursor.getDouble(cursor.getColumnIndex(dbHelper.LONGITUDE)));
                    point.setLatitude((cursor.getDouble(cursor.getColumnIndex(dbHelper.LATITUDE))));
                    point.setDateTime(cursor.getString(cursor.getColumnIndex(dbHelper.CURRENT_TIME)));
                    points.add(point);
                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d("AAAA", "Error while trying to get points from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return points;
    }
}
