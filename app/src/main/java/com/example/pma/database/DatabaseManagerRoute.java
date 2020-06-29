package com.example.pma.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.pma.model.Route;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DatabaseManagerRoute {
    private DatabaseHelper dbHelper;
    private Context context;
    private SQLiteDatabase database;
    private static final String TAG = "DatabaseManagerRoute";

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

    public long insert(double calories,double distance,String unit,Long synchronized_id,String start_date,String end_date){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.CALORIES,calories);
        contentValues.put(DatabaseHelper.DISTANCE,distance);
        contentValues.put(DatabaseHelper.UNIT,unit);
        contentValues.put(DatabaseHelper.SYNCHRONIZED_ID,synchronized_id);
        contentValues.put(DatabaseHelper.START_DATE,start_date);
        contentValues.put(DatabaseHelper.END_DATE,end_date);
        long i =  database.insert(DatabaseHelper.TABLE_ROUTES, null, contentValues);
       return i;
    }
    public Cursor fetch(){
        String[] columns  = new String[]{DatabaseHelper._ID, DatabaseHelper.CALORIES,DatabaseHelper.DISTANCE,DatabaseHelper.UNIT,DatabaseHelper.SYNCHRONIZED_ID,DatabaseHelper.START_DATE,DatabaseHelper.END_DATE};
        Cursor cursor = database.query(DatabaseHelper.TABLE_ROUTES, columns, null,null,null,null,null);
        if(cursor != null){
            cursor.moveToFirst();
        }
        return  cursor;

    }
   public int update(long id,double calories,double distance,String unit,Long synchronized_id,String start_date,String end_date){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.CALORIES,calories);
        contentValues.put(DatabaseHelper.DISTANCE,distance);
        contentValues.put(DatabaseHelper.UNIT,unit);
        contentValues.put(DatabaseHelper.SYNCHRONIZED_ID,synchronized_id);
        contentValues.put(DatabaseHelper.START_DATE,start_date);
        contentValues.put(DatabaseHelper.END_DATE,end_date);


        int i = database.update(DatabaseHelper.TABLE_ROUTES,contentValues,DatabaseHelper._ID + " = "+id,null);
        return i ;
    }

    public int updateSynchronized(long id, Long synchronized_id){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.SYNCHRONIZED_ID,synchronized_id);

        int i = database.update(DatabaseHelper.TABLE_ROUTES,contentValues,DatabaseHelper._ID + " = "+id,null);
        return i ;
    }
    public void delete(long id){
        database.delete(DatabaseHelper.TABLE_ROUTES,DatabaseHelper._ID+" = "+ id,null);
    }

    public ArrayList<Route> getRoutes(){
        ArrayList<Route> routes = new ArrayList<>();

        String ROUTE_SELECT_QUERY = String.format("SELECT * FROM %s ;", DatabaseHelper.TABLE_ROUTES);
        Cursor cursor = database.rawQuery(ROUTE_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Route route = new Route();
                    route.setId(Long.parseLong(cursor.getString(cursor.getColumnIndex(dbHelper._ID))));
                    Log.d(TAG, cursor.getString(cursor.getColumnIndex(dbHelper._ID)));

                    route.setCalories(Double.parseDouble(cursor.getString(cursor.getColumnIndex(dbHelper.CALORIES))));
                    Log.d(TAG, cursor.getString(cursor.getColumnIndex(dbHelper.CALORIES)));

                    route.setDistance(Double.parseDouble(cursor.getString(cursor.getColumnIndex(dbHelper.DISTANCE))));
                    Log.d(TAG, cursor.getString(cursor.getColumnIndex(dbHelper.DISTANCE)));

                     route.setEnd_time(cursor.getString(cursor.getColumnIndex(dbHelper.END_DATE)));
                    route.setStart_time(cursor.getString(cursor.getColumnIndex(dbHelper.START_DATE)));
                     route.setSynchronized_id(cursor.getLong(cursor.getColumnIndex(dbHelper.SYNCHRONIZED_ID)));

                   // String dateStringEnd = cursor.getString(cursor.getColumnIndex(dbHelper.END_DATE));
                    //String dateStringStart = cursor.getString(cursor.getColumnIndex(dbHelper.START_DATE));

                    //Log.d(TAG, " date "+dateStringEnd + " - " + dateStringStart);
                  //  Date dateEnd = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(dateStringEnd);
                   // Date dateStart = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(dateStringStart);
                    routes.add(route);
                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get routes from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return routes;
    }

    public Route getRoute(Long id) {
        Cursor cursor = database.rawQuery("select * from " + DatabaseHelper.TABLE_ROUTES + " where " + DatabaseHelper._ID + "=" +id + ";", null);
        Route route = new Route();
        try {
            if (cursor.moveToFirst()) {
                do {
                    route.setId(Long.parseLong(cursor.getString(cursor.getColumnIndex(dbHelper._ID))));
                    Log.d(TAG, cursor.getString(cursor.getColumnIndex(dbHelper._ID)));

                    route.setCalories(Double.parseDouble(cursor.getString(cursor.getColumnIndex(dbHelper.CALORIES))));
                    Log.d(TAG, cursor.getString(cursor.getColumnIndex(dbHelper.CALORIES)));

                    route.setDistance(Double.parseDouble(cursor.getString(cursor.getColumnIndex(dbHelper.DISTANCE))));
                    Log.d(TAG, cursor.getString(cursor.getColumnIndex(dbHelper.DISTANCE)));

                    route.setEnd_time(cursor.getString(cursor.getColumnIndex(dbHelper.END_DATE)));
                    route.setStart_time(cursor.getString(cursor.getColumnIndex(dbHelper.START_DATE)));
                    route.setSynchronized_id(cursor.getLong(cursor.getColumnIndex(dbHelper.SYNCHRONIZED_ID)));

                    // String dateStringEnd = cursor.getString(cursor.getColumnIndex(dbHelper.END_DATE));
                    //String dateStringStart = cursor.getString(cursor.getColumnIndex(dbHelper.START_DATE));

                    //Log.d(TAG, " date "+dateStringEnd + " - " + dateStringStart);
                    //  Date dateEnd = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(dateStringEnd);
                    // Date dateStart = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(dateStringStart);
                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get routes from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return route;
    }

}
