package com.example.pma.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseManagerUser {
    private DatabaseHelper dbHelper;
    private Context context;
    private SQLiteDatabase database;

    public DatabaseManagerUser(Context c){
        this.context = c;
    }

    public  DatabaseManagerUser open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return  this;
    }
    public void deleteTables(){
        database.execSQL("DROP TABLE IF EXISTS " + dbHelper.TABLE_POINTS);
        database.execSQL("DROP TABLE IF EXISTS " + dbHelper.TABLE_ROUTES);
        database.execSQL("DROP TABLE IF EXISTS " + dbHelper.TABLE_PROFILE);
        database.execSQL("DROP TABLE IF EXISTS " + dbHelper.TABLE_GOALS);
        database.execSQL("DROP TABLE IF EXISTS " + dbHelper.TABLE_USERS);
        dbHelper.onCreate(database);
    }
    public void close(){
        dbHelper.close();
    }

    public void insert(String firstname,String lastname,String username,String email,String password ){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.FIRSTNAME,firstname);
        contentValues.put(DatabaseHelper.LASTNAME,lastname);
        contentValues.put(DatabaseHelper.EMAIL,email);
        contentValues.put(DatabaseHelper.USERNAME,username);
        contentValues.put(DatabaseHelper.PASSWORD,password);

        database.insert(DatabaseHelper.TABLE_USERS, null, contentValues);
    }
    public Cursor fetch(){
        String[] columns  = new String[]{DatabaseHelper._ID,DatabaseHelper.FIRSTNAME,DatabaseHelper.LASTNAME,DatabaseHelper.USERNAME,DatabaseHelper.EMAIL,DatabaseHelper.PASSWORD};
        Cursor cursor = database.query(DatabaseHelper.TABLE_USERS, columns, null,null,null,null,null);
        if(cursor != null){
            cursor.moveToFirst();
        }
        return  cursor;

    }
    public int update(long id,String firstname,String lastname,String username,String email,String password){
        ContentValues contentValues = new ContentValues();

        contentValues.put(DatabaseHelper.FIRSTNAME,firstname);
        contentValues.put(DatabaseHelper.LASTNAME,lastname);
        contentValues.put(DatabaseHelper.EMAIL,email);
        contentValues.put(DatabaseHelper.USERNAME,username);
        contentValues.put(DatabaseHelper.PASSWORD,password);
        int i = database.update(DatabaseHelper.TABLE_USERS,contentValues,DatabaseHelper._ID + " = "+id,null);
        return i ;
    }

    public  void delete(long id){
        database.delete(DatabaseHelper.TABLE_USERS,DatabaseHelper._ID+" = "+ id,null);
    }
}
