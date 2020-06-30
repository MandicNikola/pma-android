package com.example.pma;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class WaterReceiver extends BroadcastReceiver {

    private NotificationManager mNotificationManager;
    private static final int NOTIFICATION_ID = 1;

    private static final String PRIMARY_CHANNEL_ID =
            "primary_notification_channel";
    private SharedPreferences preferences;
    private static final String TAG = "WaterReceiver";



    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "on Receive ");

        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        preferences = context.getSharedPreferences("user_detail", Context.MODE_PRIVATE);
        if(preferences.getBoolean("waterFlag",false)) {
            Log.d(TAG, "water on");
            deliverNotification(context);
        }else{
            Log.d(TAG, "water off");

        }
    }

    public void deliverNotification(Context context) {
        Intent contentIntent = new Intent(context, MainActivity.class);
        PendingIntent contentPendingIntent = PendingIntent.getActivity
                (context, NOTIFICATION_ID, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_accessibility_black_24dp)
                .setContentTitle("Water")
                .setContentText("Drink some water!")
                .setContentIntent(contentPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL);
        mNotificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
