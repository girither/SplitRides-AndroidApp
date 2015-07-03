package com.example.foodiepipe.foodiepipe;

import android.app.Application;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

/**
 * Created by gbm on 7/2/15.
 */

@ReportsCrashes(
        formUri = "http://radiant-peak-3095.herokuapp.com/remoteStackTrace",
        reportType = org.acra.sender.HttpSender.Type.JSON
)
public class Splitrideapplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ACRA.init(this);
    }

}
