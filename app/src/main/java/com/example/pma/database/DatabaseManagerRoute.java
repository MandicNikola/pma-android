package com.example.pma.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseManagerRoute {
    private DatabaseHelper dbHelper;
    private Context context;
    private SQLiteDatabase database;

    public DatabaseManagerRoute(Context c){
        this.context = c;
    }
    public  DatabaseManagerRoute open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return  this;
    }
    public void close(){
        dbHelper.close();
    }

    public void insert(int calories,int distance,String unit){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.CALORIES,calories);
        contentValues.put(DatabaseHelper.DISTANCE,distance);
        contentValues.put(DatabaseHelper.UNIT,unit);
        database.insert(DatabaseHelper.TABLE_ROUTES, null, contentValues);
    }
    public Cursor fetch(){
        String[] columns  = new String[]{DatabaseHelper._ID, DatabaseHelper.CALORIES,DatabaseHelper.DISTANCE,DatabaseHelper.UNIT};
        Cursor cursor = database.query(DatabaseHelper.TABLE_ROUTES, columns, null,null,null,null,null);
        if(cursor != null){
            cursor.moveToFirst();
        }
        return  cursor;

    }
    public int update(long id,int calories,int distance,String unit){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.CALORIES,calories);
        contentValues.put(DatabaseHelper.DISTANCE,distance);
        contentValues.put(DatabaseHelper.UNIT,unit);
        int i = database.update(DatabaseHelper.TABLE_ROUTES,contentValues,DatabaseHelper._ID + " = "+id,null);
        return i ;
    }
    public  void delete(long id){
        database.delete(DatabaseHelper.TABLE_ROUTES,DatabaseHelper._ID+" = "+ id,null);
    }
    public Cursor testQuery(){

        Cursor res = database.rawQuery( "select "+DatabaseHelper._ID+" from "+DatabaseHelper.TABLE_ROUTES, null );
        res.moveToFirst();
        return res;
    }

}
