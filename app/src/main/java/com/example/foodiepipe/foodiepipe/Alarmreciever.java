package com.example.foodiepipe.foodiepipe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by gbm on 6/29/15.
 */
public class Alarmreciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String locationstring = SharedPreferenceManager.getPreference("locationstringdata");
        if(locationstring != null && !locationstring.isEmpty()) {
            Intent dailyUpdater = new Intent(context, googleservice.class);
            context.startService(dailyUpdater);
        }
    }
}
