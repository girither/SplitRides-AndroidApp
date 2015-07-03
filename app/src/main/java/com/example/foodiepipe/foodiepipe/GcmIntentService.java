package com.example.foodiepipe.foodiepipe;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by gbm on 6/30/15.
 */
public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    public GcmIntentService() {
        super("GcmIntentService");
    }
    public static final String TAG = "GcmIntentService";

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "inside the onHandleIntent method");
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM will be
             * extended in the future with new message types, just ignore any message types you're
             * not interested in, or that you don't recognize.
             */
            Log.i(TAG,"inside the extras.isEmpty clause");
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                Log.i(TAG,"inside the first if clause");
                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                Log.i(TAG,"inside the first else if clause");
                sendNotification("Deleted messages on server: " + extras.toString());
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // This loop represents the service doing some work.
                Log.i(TAG,"inside the second else if clause");
                for (int i = 0; i < 5; i++) {
                    Log.i(TAG, "Working... " + (i + 1)
                            + "/5 @ " + SystemClock.elapsedRealtime());
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                    }
                }
                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                // Post notification of received message.
                sendNotification("Received: " + extras.toString());
                Log.i(TAG, "Received: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        Gcmbroadcastreciever.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg) {

        try {
        JSONObject jObj = new JSONObject(msg);
        if(jObj != null){
            String data = jObj.getString("NotificationType");

            if(data.equals("StartRideDistanceTravelled")){

            }
            else if(data.equals("EndRideDistanceTravelled")){

            }
            else if(data.equals("requestToJoinTheRide")){
                String requestID = jObj.getString("requestId");
                mNotificationManager = (NotificationManager)
                        this.getSystemService(Context.NOTIFICATION_SERVICE);

                Intent getnotificationdetails = new Intent(this,notificationfragmentdetails.class);
                getnotificationdetails.putExtra("requestId", requestID);
                PendingIntent contentIntent = PendingIntent.getActivity(this, 0,getnotificationdetails, 0);

                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(this)
                                .setSmallIcon(R.drawable.ic_launcher)
                                .setContentTitle("Request To Join Ride")
                                .setStyle(new NotificationCompat.BigTextStyle()
                                        .bigText(msg))
                                .setContentText(msg);

                mBuilder.setContentIntent(contentIntent);
                Log.i(TAG,"in the penultimate line");
                mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
            }
            else if(data.equals("acceptedByTheOwner")){

            }
            else if(data.equals("rejectedByTheOwner")){

            }
        }

      } catch (JSONException e) {
        e.printStackTrace();
     }
    }
}