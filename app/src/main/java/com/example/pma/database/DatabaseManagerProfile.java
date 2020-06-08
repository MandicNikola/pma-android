package com.example.pma.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.pma.model.Goal;
import com.example.pma.model.Profile;
import com.example.pma.model.ProfileDB;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DatabaseManagerProfile {
    private DatabaseHelper dbHelper;
    private Context context;
    private SQLiteDatabase database;
    private static final String TAG = "DatabaseManagerProfile";

    public DatabaseManagerProfile(Context c){
        this.context = c;
    }
    public  DatabaseManagerProfile open() throws SQLException {
        dbHelper = new DatabaseHelper(context);

        database = dbHelper.getWritableDatabase();
        return  this;
    }
    public void close(){
        dbHelper.close();
    }
    public void insert(double height, double weight, int user_id){

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.HEIGHT, height);
        contentValues.put(DatabaseHelper.WEIGHT, weight);
        contentValues.put(DatabaseHelper.USER_ID, user_id);
        database.insert(DatabaseHelper.TABLE_PROFILE, null, contentValues);
    }
    public Cursor fetch(){
        String[] columns  = new String[]{DatabaseHelper._ID, DatabaseHelper.HEIGHT,DatabaseHelper.WEIGHT};
        Cursor cursor = database.query(DatabaseHelper.TABLE_PROFILE, columns, null,null,null,null,null);
        if(cursor != null){
            cursor.moveToFirst();
        }
        return  cursor;

    }
    public int getProfileId(int userId){
        Cursor cursor = database.rawQuery("select "+DatabaseHelper._ID+" from "+DatabaseHelper.TABLE_PROFILE + " where " + DatabaseHelper.USER_ID + " = " +userId+";" , null);
       int id=0;
        try {
            if (cursor.moveToFirst()) {
                do {
                    id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(dbHelper._ID)));
                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get id from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return  id;
}
    public int update( double height, double weight, int user_id){
        int idRow = getProfileId(user_id);
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.HEIGHT, height);
        contentValues.put(DatabaseHelper.WEIGHT, weight);
        contentValues.put(DatabaseHelper.USER_ID, user_id);
        int i = database.update(DatabaseHelper.TABLE_PROFILE, contentValues,DatabaseHelper._ID + " = "+idRow,null);
        return i ;
    }

    public  void delete(long id){
        database.delete(DatabaseHelper.TABLE_PROFILE,DatabaseHelper._ID+" = "+ id,null);
    }
    public ProfileDB getProfileByUserId(int id){
        Cursor cursor = database.rawQuery("select * from "+DatabaseHelper.TABLE_PROFILE + " where " + DatabaseHelper.USER_ID + " = " +id+";" , null);
        ProfileDB profile = new ProfileDB();
        try {
            if (cursor.moveToFirst()) {
                do {
                    profile.setWeight(Double.parseDouble(cursor.getString(cursor.getColumnIndex(dbHelper.WEIGHT))));
                    profile.setHeight(Double.parseDouble(cursor.getString(cursor.getColumnIndex(dbHelper.HEIGHT))));
                    profile.setUser_id(cursor.getInt(cursor.getColumnIndex(dbHelper.USER_ID)));
                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get profile from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return profile;
    }

}
