package com.splitrides;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/**
 * Created by gbm on 6/29/15.
 */
public class Gcmbroadcastreciever extends WakefulBroadcastReceiver {

    public static final String TAG = "GcmBroadcastReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG, "inside the onReceive function");
        // Explicitly specify that GcmIntentService will handle the intent.
        ComponentName comp = new ComponentName(context.getPackageName(),
                GcmIntentService.class.getName());
        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }
}