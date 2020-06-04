package com.example.pma.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseManagerPoint {
    private DatabaseHelperPoint dbHelper;
    private Context context;
    private SQLiteDatabase database;
    public DatabaseManagerPoint(Context c){
        this.context = c;
    }

    public  DatabaseManagerPoint open() throws SQLException {
        dbHelper = new DatabaseHelperPoint(context);
        database = dbHelper.getWritableDatabase();
        return  this;
    }
    public void close(){
        dbHelper.close();
    }

    public void insert(float longitude,float latitude,long route_id){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelperPoint.LONGITUDE,longitude);
        contentValues.put(DatabaseHelperPoint.LATITUDE,latitude);
        contentValues.put(DatabaseHelperPoint.ROUTE_ID,route_id);

        database.insert(DatabaseHelperPoint.TABLE_NAME, null, contentValues);
    }
    public Cursor fetch(){
        String[] columns  = new String[]{DatabaseHelperPoint._ID,DatabaseHelperPoint.LONGITUDE,DatabaseHelperPoint.LATITUDE,DatabaseHelperPoint.ROUTE_ID};
        Cursor cursor = database.query(DatabaseHelperPoint.TABLE_NAME, columns, null,null,null,null,null);
        if(cursor != null){
            cursor.moveToFirst();
        }
        return  cursor;

    }
    public int update(long id,float longitude,float latitude,long route_id){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelperPoint.LONGITUDE,longitude);
        contentValues.put(DatabaseHelperPoint.LATITUDE,latitude);
        contentValues.put(DatabaseHelperPoint.ROUTE_ID,route_id);

        int i = database.update(DatabaseHelperPoint.TABLE_NAME,contentValues,DatabaseHelperPoint._ID + " = "+id,null);
        return i ;
    }

    public  void delete(long id){
        database.delete(DatabaseHelperPoint.TABLE_NAME,DatabaseHelperPoint._ID+" = "+ id,null);
    }

}
