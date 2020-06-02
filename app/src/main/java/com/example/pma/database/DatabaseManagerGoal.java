package com.example.pma.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.Date;

public class DatabaseManagerGoal {
    private DatabaseHelperGoal dbHelper;
    private Context context;
    private SQLiteDatabase database;

    public DatabaseManagerGoal(Context c){
        this.context = c;
    }
    public  DatabaseManagerGoal open() throws SQLException {
        dbHelper = new DatabaseHelperGoal(context);
        database = dbHelper.getWritableDatabase();
        return  this;
    }
    public void close(){
        dbHelper.close();

    }

    public void insert(String key, double value, String date){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelperGoal.KEY, key);
        contentValues.put(DatabaseHelperGoal.VALUE, value);
        contentValues.put(DatabaseHelperGoal.DATE, date);
        database.insert(DatabaseHelperGoal.TABLE_NAME, null, contentValues);
    }
    public Cursor fetch(){
        String[] columns  = new String[]{DatabaseHelperGoal._ID, DatabaseHelperGoal.KEY,DatabaseHelperGoal.VALUE,DatabaseHelperGoal.DATE};
        Cursor cursor = database.query(DatabaseHelperGoal.TABLE_NAME, columns, null,null,null,null,null);
        if(cursor != null){
            cursor.moveToFirst();
        }
        return  cursor;

    }
    public int update(long id, String key, double value, String date){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelperGoal.KEY, key);
        contentValues.put(DatabaseHelperGoal.VALUE, value);
        contentValues.put(DatabaseHelperGoal.DATE, date);
        int i = database.update(DatabaseHelperGoal.TABLE_NAME,contentValues,DatabaseHelperGoal._ID + " = "+id,null);
        return i ;
    }
    public  void delete(long id){
        database.delete(DatabaseHelperGoal.TABLE_NAME,DatabaseHelperGoal._ID+" = "+ id,null);
    }
    public Cursor testQuery(){

        Cursor res = database.rawQuery( "select "+DatabaseHelperGoal._ID+" from "+DatabaseHelperGoal.TABLE_NAME, null );
        res.moveToFirst();


        return res;
    }

}
