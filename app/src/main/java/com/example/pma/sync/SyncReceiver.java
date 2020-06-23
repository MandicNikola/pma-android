package com.example.pma.sync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SyncReceiver extends BroadcastReceiver {

    private static final String TAG = "SyncReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG,"Receiver");

    }
}