package com.example.pma.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseManagerSettings {
    private DatabaseHelperSettings dbHelper;
    private Context context;
    private SQLiteDatabase database;

    public DatabaseManagerSettings(Context c){
        this.context = c;
    }
    public  DatabaseManagerSettings open() throws SQLException {
        dbHelper = new DatabaseHelperSettings(context);
        database = dbHelper.getWritableDatabase();
        return  this;
    }
    public void close(){
        dbHelper.close();

    }

    public void insert(String reminder){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelperSettings.WATER_REMINDER, reminder);
        database.insert(DatabaseHelperSettings.TABLE_NAME, null, contentValues);
    }
    public Cursor fetch(){
        String[] columns  = new String[]{DatabaseHelperSettings._ID, DatabaseHelperSettings.WATER_REMINDER};
        Cursor cursor = database.query(DatabaseHelperRoute.TABLE_NAME, columns, null,null,null,null,null);
        if(cursor != null){
            cursor.moveToFirst();
        }
        return  cursor;

    }
    public int update(long id, String reminder){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelperSettings.WATER_REMINDER, reminder);
        int i = database.update(DatabaseHelperSettings.TABLE_NAME,contentValues,DatabaseHelperSettings._ID + " = "+id,null);
        return i ;
    }
    public  void delete(long id){
        database.delete(DatabaseHelperSettings.TABLE_NAME,DatabaseHelperSettings._ID+" = "+ id,null);
    }
    public Cursor testQuery(){

        Cursor res = database.rawQuery( "select "+DatabaseHelperSettings._ID+" from "+DatabaseHelperSettings.TABLE_NAME, null );
        res.moveToFirst();


        return res;
    }

}
