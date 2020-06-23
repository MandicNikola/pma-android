package com.example.pma.sync;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class SyncTask extends AsyncTask<Void, Void, Void> {
    private Context context;

    public static String RESULT_CODE = "RESULT_CODE";

    public SyncTask(Context context)
    {
        this.context = context;
    }
    @Override
    protected Void doInBackground(Void... params) {
        Log.i("REZ", "doInBackground");

       /* try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        return null;
    }
}
