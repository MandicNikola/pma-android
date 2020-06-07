package com.example.pma.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.pma.model.Goal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseManagerGoal {
    private DatabaseHelper dbHelper;
    private Context context;
    private SQLiteDatabase database;
    private static final String TAG = "DatabaseManagerGoal";


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


    public void insert(String key, int value, String date, int userId){

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.KEY, key);
        contentValues.put(DatabaseHelper.VALUE, value);
        contentValues.put(DatabaseHelper.DATE, date);
        contentValues.put(DatabaseHelper.GOAL_USER, userId);
        contentValues.put(DatabaseHelper.PERCENTAGE, 0);
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

    public ArrayList<Goal> getGoals(){
        ArrayList<Goal> goals = new ArrayList<>();
        String GOAL_SELECT_QUERY = String.format("SELECT * FROM %s ;", DatabaseHelper.TABLE_GOALS);
        Cursor cursor = database.rawQuery(GOAL_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Goal goal = new Goal();
                    goal.setId(Long.parseLong(cursor.getString(cursor.getColumnIndex(dbHelper._ID))));
                    goal.setGoalKey(cursor.getString(cursor.getColumnIndex(dbHelper.KEY)));
                    goal.setGoalValue(Double.parseDouble(cursor.getString(cursor.getColumnIndex(dbHelper.VALUE))));
                    String dateString = cursor.getString(cursor.getColumnIndex(dbHelper.DATE));
                    Log.d(TAG, " date "+dateString);
                    Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
                    goal.setDate(date);
                    goals.add(goal);
                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get goals from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return goals;
    }

}
