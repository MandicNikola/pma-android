package com.example.pma.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.Date;

public class DatabaseManagerGoal {
    private DatabaseHelper dbHelper;
    private Context context;
    private SQLiteDatabase database;

    public DatabaseManagerGoal(Context c){
        this.context = c;
    }

    public  DatabaseManagerGoal open() throws SQLException {
        dbHelper = new DatabaseHelper(context);

        database = dbHelper.getWritableDatabase();
        return  this;
    }
    public void close(){
        dbHelper.close();

    }

    public void insert(String key, int value, String date){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.KEY, key);
        contentValues.put(DatabaseHelper.VALUE, value);
        contentValues.put(DatabaseHelper.DATE, date);
        database.insert(DatabaseHelper.TABLE_GOALS, null, contentValues);
    }
    public Cursor fetch(){
        String[] columns  = new String[]{DatabaseHelper._ID, DatabaseHelper.KEY,DatabaseHelper.VALUE,DatabaseHelper.DATE};
        Cursor cursor = database.query(DatabaseHelper.TABLE_GOALS, columns, null,null,null,null,null);
        if(cursor != null){
            cursor.moveToFirst();
        }
        return  cursor;

    }
    public int update(long id, String key, int value, String date){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.KEY, key);
        contentValues.put(DatabaseHelper.VALUE, value);
        contentValues.put(DatabaseHelper.DATE, date);
        int i = database.update(DatabaseHelper.TABLE_GOALS,contentValues,DatabaseHelper._ID + " = "+id,null);
        return i ;
    }
    public  void delete(long id){
        database.delete(DatabaseHelper.TABLE_GOALS,DatabaseHelper._ID+" = "+ id,null);
    }
    public Cursor testQuery(){

        Cursor res = database.rawQuery( "select "+DatabaseHelper._ID+" from "+DatabaseHelper.TABLE_GOALS, null );
        res.moveToFirst();


        return res;
    }

}
