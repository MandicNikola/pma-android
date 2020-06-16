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


    public long insert(String key, double value, String date, int userId,double reached_value,int notified,Long back_id){

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.KEY, key);
        contentValues.put(DatabaseHelper.VALUE, value);
        contentValues.put(DatabaseHelper.DATE, date);
        contentValues.put(DatabaseHelper.GOAL_USER, userId);
        contentValues.put(DatabaseHelper.PERCENTAGE, 0);
        contentValues.put(DatabaseHelper.REACHED_VALUE, reached_value);
        contentValues.put(DatabaseHelper.NOTIFIED,notified);
        Log.d(TAG," id od goal na frontu je"+back_id);

        contentValues.put(DatabaseHelper.BACK_ID,back_id);
       long i= database.insert(DatabaseHelper.TABLE_GOALS, null, contentValues);
        return i;
    }
    public Cursor fetch(){
        String[] columns  = new String[]{DatabaseHelper._ID,DatabaseHelper.VALUE,DatabaseHelper.REACHED_VALUE, DatabaseHelper.GOAL_USER,DatabaseHelper.PERCENTAGE,DatabaseHelper.NOTIFIED,DatabaseHelper.BACK_ID,DatabaseHelper.KEY,DatabaseHelper.DATE};
        Cursor cursor = database.query(DatabaseHelper.TABLE_GOALS, columns, null,null,null,null,null);
        if(cursor != null){
            cursor.moveToFirst();
        }
        return  cursor;

    }
    public int update(long id, String key, double value, String date,double reached_value,int notified,Long back_id){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.KEY, key);
        contentValues.put(DatabaseHelper.VALUE, value);
        contentValues.put(DatabaseHelper.DATE, date);
        contentValues.put(DatabaseHelper.REACHED_VALUE, reached_value);
        contentValues.put(DatabaseHelper.NOTIFIED,notified);
        contentValues.put(DatabaseHelper.BACK_ID,back_id);

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
                    goal.setCurrentValue(Double.parseDouble(cursor.getString(cursor.getColumnIndex(dbHelper.REACHED_VALUE))));
                    goal.setNotified(Integer.parseInt(cursor.getString(cursor.getColumnIndex(dbHelper.NOTIFIED))));
                    goal.setBackId(Long.parseLong(cursor.getString(cursor.getColumnIndex(DatabaseHelper.BACK_ID))));

                    String dateString = cursor.getString(cursor.getColumnIndex(dbHelper.DATE));
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
