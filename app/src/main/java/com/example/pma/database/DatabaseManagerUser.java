package com.example.pma.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseManagerUser {
    private DatabaseHelperUser dbHelper;
    private Context context;
    private SQLiteDatabase database;

    public DatabaseManagerUser(Context c){
        this.context = c;
    }

    public  DatabaseManagerUser open() throws SQLException {
        dbHelper = new DatabaseHelperUser(context);
        database = dbHelper.getWritableDatabase();
        return  this;
    }
    public void close(){
        dbHelper.close();
    }

    public void insert(String firstname,String lastname,String username,String email,String password ){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelperUser.FIRSTNAME,firstname);
        contentValues.put(DatabaseHelperUser.LASTNAME,lastname);
        contentValues.put(DatabaseHelperUser.EMAIL,email);
        contentValues.put(DatabaseHelperUser.USERNAME,username);
        contentValues.put(DatabaseHelperUser.PASSWORD,password);

        database.insert(DatabaseHelperUser.TABLE_NAME, null, contentValues);
    }
    public Cursor fetch(){
        String[] columns  = new String[]{DatabaseHelperUser._ID,DatabaseHelperUser.FIRSTNAME,DatabaseHelperUser.LASTNAME,DatabaseHelperUser.USERNAME,DatabaseHelperUser.EMAIL,DatabaseHelperUser.PASSWORD};
        Cursor cursor = database.query(DatabaseHelperUser.TABLE_NAME, columns, null,null,null,null,null);
        if(cursor != null){
            cursor.moveToFirst();
        }
        return  cursor;

    }
    public int update(long id,String firstname,String lastname,String username,String email,String password){
        ContentValues contentValues = new ContentValues();

        contentValues.put(DatabaseHelperUser.FIRSTNAME,firstname);
        contentValues.put(DatabaseHelperUser.LASTNAME,lastname);
        contentValues.put(DatabaseHelperUser.EMAIL,email);
        contentValues.put(DatabaseHelperUser.USERNAME,username);
        contentValues.put(DatabaseHelperUser.PASSWORD,password);
        int i = database.update(DatabaseHelperUser.TABLE_NAME,contentValues,DatabaseHelperUser._ID + " = "+id,null);
        return i ;
    }

    public  void delete(long id){
        database.delete(DatabaseHelperUser.TABLE_NAME,DatabaseHelperUser._ID+" = "+ id,null);
    }
}
