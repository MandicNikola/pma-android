package com.example.pma.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseManagerSettings {
    private DatabaseHelper dbHelper;
    private Context context;
    private SQLiteDatabase database;

    public DatabaseManagerSettings(Context c){
        this.context = c;
    }
    public  DatabaseManagerSettings open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return  this;
    }
    public void close(){
        dbHelper.close();

    }

    public void insert(String reminder){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.WATER_REMINDER, reminder);
        database.insert(DatabaseHelper.TABLE_SETTINGS, null, contentValues);
    }
    public Cursor fetch(){
        String[] columns  = new String[]{DatabaseHelper._ID, DatabaseHelper.WATER_REMINDER};
        Cursor cursor = database.query(DatabaseHelper.TABLE_SETTINGS, columns, null,null,null,null,null);
        if(cursor != null){
            cursor.moveToFirst();
        }
        return  cursor;

    }
    public int update(long id, String reminder){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.WATER_REMINDER, reminder);
        int i = database.update(DatabaseHelper.TABLE_SETTINGS,contentValues,DatabaseHelper._ID + " = "+id,null);
        return i ;
    }
    public  void delete(long id){
        database.delete(DatabaseHelper.TABLE_SETTINGS,DatabaseHelper._ID+" = "+ id,null);
    }
    public Cursor testQuery(){

        Cursor res = database.rawQuery( "select "+DatabaseHelper._ID+" from "+DatabaseHelper.TABLE_SETTINGS, null );
        res.moveToFirst();


        return res;
    }

}
