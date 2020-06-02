package com.example.pma.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseManagerRoute {
    private DatabaseHelperRoute dbHelper;
    private Context context;
    private SQLiteDatabase database;

    public DatabaseManagerRoute(Context c){
        this.context = c;
    }
    public  DatabaseManagerRoute open() throws SQLException {
        dbHelper = new DatabaseHelperRoute(context);
        database = dbHelper.getWritableDatabase();
        return  this;
    }
    public void close(){
        dbHelper.close();

    }

    public void insert(int calories,int distance,String unit){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelperRoute.CALORIES,calories);
        contentValues.put(DatabaseHelperRoute.DISTANCE,distance);
        contentValues.put(DatabaseHelperRoute.UNIT,unit);
        database.insert(DatabaseHelperRoute.TABLE_NAME, null, contentValues);
    }
    public Cursor fetch(){
        String[] columns  = new String[]{DatabaseHelperRoute._ID, DatabaseHelperRoute.CALORIES,DatabaseHelperRoute.DISTANCE,DatabaseHelperRoute.UNIT};
        Cursor cursor = database.query(DatabaseHelperRoute.TABLE_NAME, columns, null,null,null,null,null);
        if(cursor != null){
            cursor.moveToFirst();
        }
        return  cursor;

    }
    public int update(long id,int calories,int distance,String unit){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelperRoute.CALORIES,calories);
        contentValues.put(DatabaseHelperRoute.DISTANCE,distance);
        contentValues.put(DatabaseHelperRoute.UNIT,unit);
        int i = database.update(DatabaseHelperRoute.TABLE_NAME,contentValues,DatabaseHelperRoute._ID + " = "+id,null);
        return i ;
    }
    public  void delete(long id){
        database.delete(DatabaseHelperRoute.TABLE_NAME,DatabaseHelperRoute._ID+" = "+ id,null);
    }
    public Cursor testQuery(){

        Cursor res = database.rawQuery( "select "+DatabaseHelperRoute._ID+" from "+DatabaseHelperRoute.TABLE_NAME, null );
        res.moveToFirst();


        return res;
    }

}
