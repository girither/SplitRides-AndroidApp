package com.splitrides;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.splitrides.android.helper.JSONParser;

import org.json.JSONException;
import org.json.JSONObject;

public class MyGcmListenerService extends GcmListenerService {
    private NotificationManager mNotificationManager;
    JSONParser jsonParser = new JSONParser();

    @Override
    public void onMessageReceived(String from, Bundle data) {
        //String message = data.getString("message");

        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
        final int notificationID = (int) (Math.random() * 100000000);
        sendNotification(data,notificationID);
        new updatenotificationtask(data.toString()).execute((Void) null);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(Bundle msg,final int aNotificationID) {
        String data ="";
        if(msg != null) {
            data = msg.getString("NotificationType");
        }
        if(data == null){
            data = "";
        }
        if(data.equals("StartRideDistanceTravelled")){
            new startridewithdistancetask(SharedPreferenceManager.getPreference("started_jrride"),SharedPreferenceManager.getPreference("locationstringdata")).execute((Void) null);
            Log.e("Error occured :", "StartRideDistanceTravelled");
        }
        else if(data.equals("EndRideDistanceTravelled")){
            new endridewithdistancetask(SharedPreferenceManager.getPreference("started_jrride"),SharedPreferenceManager.getPreference("locationstringdata")).execute((Void) null);
            Log.e("Error occured :", "EndRideDistanceTravelled");
        }
        else if(data.equals("OwnerHasEndedTheRide")){
            SharedPreferenceManager.setPreference("startrides", false);
            SharedPreferenceManager.setPreference("stoprides", true);
            Intent startlocationservice = new Intent(getApplicationContext(),Locationservice.class);
            this.stopService(startlocationservice);
            new endridewhenownerends(msg.getString("rideid"),SharedPreferenceManager.getPreference("locationstringdata")).execute((Void) null);
        }
        else if(data.equals("requestToJoinTheRide")){
            String requestID = msg.getString("requestId");
            int count = SharedPreferenceManager.getIntPreference("notificationcount");
            SharedPreferenceManager.setPreference("notificationcount", count+1);
            mNotificationManager = (NotificationManager)
                    this.getSystemService(Context.NOTIFICATION_SERVICE);

            Intent getnotificationdetails = new Intent(this,notificationfragmentdetails.class);
            getnotificationdetails.putExtra("requestId", requestID);
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0,getnotificationdetails, PendingIntent.FLAG_CANCEL_CURRENT);
            String source = msg.getString("source");
            String destination =msg.getString("destination");
            String customername = msg.getString("requesterCustomerName");
            String phonenumber = msg.getString("requesterCustomerPhoneNumber");
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setContentTitle("Request To Join Ride From " + customername)
                            .setContentText("source :" + source + " destination :" + destination)
                            .setStyle(new NotificationCompat.InboxStyle()
                                    .addLine("source :" + source)
                                    .addLine("destination :" + destination)
                                    .setBigContentTitle("Request To Join Ride From " + customername)
                                    .setSummaryText("Phone Number :" + phonenumber));


            mBuilder.setContentIntent(contentIntent);
            mBuilder.setAutoCancel(true);


            mNotificationManager.notify(aNotificationID, mBuilder.build());
            Intent requesttojoinedride = new Intent("requesttojoinedridenotification");
            LocalBroadcastManager.getInstance(this).sendBroadcast(requesttojoinedride);
        }
        else if(data.equals("acceptedByTheOwner")){
            String ownerrideid = msg.getString("ownerrideid");
            mNotificationManager = (NotificationManager)
                    this.getSystemService(Context.NOTIFICATION_SERVICE);

            Intent getridedetails = new Intent(this,showupcomingridedetails.class);
            getridedetails.putExtra("rideId",ownerrideid);
            //getridedetails.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
            // Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0,getridedetails, PendingIntent.FLAG_CANCEL_CURRENT);
            String emailId = msg.getString("ownerCustomerEmail");
            String customername = msg.getString("ownerCustomerName");
            String phonenumber = msg.getString("ownerPhoneNumber");
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setContentTitle("Ride Request accepted by " + customername)
                            .setContentText("email Id :" + emailId)
                            .setStyle(new NotificationCompat.InboxStyle()
                                    .addLine("email Id :" + emailId)
                                    .setBigContentTitle("Ride Request accepted by " + customername)
                                    .setSummaryText("Phone Number :" + phonenumber));


            mBuilder.setContentIntent(contentIntent);
            mBuilder.setAutoCancel(true);

            mNotificationManager.notify(aNotificationID, mBuilder.build());
        }
        else if(data.equals("YouAreTheNewOwner") || data.equals("OwnerHasExitedTheRide") ||data.equals("fellowRiderHasLeft")){
            String ownerrideid = msg.getString("jrId");
            mNotificationManager = (NotificationManager)
                    this.getSystemService(Context.NOTIFICATION_SERVICE);

            Intent getridedetails = new Intent(this,showupcomingridedetails.class);
            getridedetails.putExtra("rideId",ownerrideid);
            //getridedetails.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
            //      Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, getridedetails, PendingIntent.FLAG_CANCEL_CURRENT);
            String message = msg.getString("message");
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setContentTitle(message)
                            .setStyle(new NotificationCompat.InboxStyle()
                                    .addLine(message)
                                    .setBigContentTitle(message));


            mBuilder.setContentIntent(contentIntent);
            mBuilder.setAutoCancel(true);
            mNotificationManager.notify(aNotificationID, mBuilder.build());
        }
        else if(data.equals("fellowRiderHasLeftWithJoinedRideRemoved")){
            mNotificationManager = (NotificationManager)
                    this.getSystemService(Context.NOTIFICATION_SERVICE);
            String message = msg.getString("message");
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setContentTitle(message)
                            .setStyle(new NotificationCompat.InboxStyle()
                                    .addLine(message)
                                    .setBigContentTitle(message));
            mBuilder.setAutoCancel(true);
            mNotificationManager.notify(aNotificationID, mBuilder.build());
        }
        else if(data.equals("OwnerHasEndedTheRideDontPostEndRide")){
            String ownerrideid = msg.getString("rideid");
            mNotificationManager = (NotificationManager)
                    this.getSystemService(Context.NOTIFICATION_SERVICE);
            Intent getridedetails = new Intent(this,showupcomingridedetails.class);
            getridedetails.putExtra("rideId",ownerrideid);
            //getridedetails.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
            // Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0,getridedetails, PendingIntent.FLAG_CANCEL_CURRENT);
            String message = msg.getString("message");
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setContentTitle(message)
                            .setStyle(new NotificationCompat.InboxStyle()
                                    .addLine(message)
                                    .setBigContentTitle(message));
            mBuilder.setAutoCancel(true);
            mNotificationManager.notify(aNotificationID, mBuilder.build());
        }
        else if(data.equals("rejectedByTheOwner")){
            String ownerrideid = msg.getString("ownerrideid");
            mNotificationManager = (NotificationManager)
                    this.getSystemService(Context.NOTIFICATION_SERVICE);

            Intent getridedetails = new Intent(this,searchshowinduvidualrides.class);
            getridedetails.putExtra("rideId",ownerrideid);
            //getridedetails.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
            //      Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0,getridedetails, PendingIntent.FLAG_CANCEL_CURRENT);
            String customername = msg.getString("ownerCustomerName");
            String emailId = msg.getString("ownerCustomerEmail");
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setContentTitle("Ride Request rejected by " + customername)
                            .setContentText("email Id :" + emailId)
                            .setStyle(new NotificationCompat.InboxStyle()
                                    .addLine("email Id :" + emailId)
                                    .setBigContentTitle("Ride Request rejected by " + customername));


            mBuilder.setContentIntent(contentIntent);
            mBuilder.setAutoCancel(true);
            mNotificationManager.notify(aNotificationID, mBuilder.build());
        }
    }

    public class updatenotificationtask extends AsyncTask<Void, Void,Boolean> {
        private String mnotification;

        updatenotificationtask(String notification) {
            mnotification = notification;
        }

        @Override
        protected Boolean doInBackground(Void... param) {
            try {
                JSONObject params = new JSONObject();
                params.put("notification",mnotification);
                String json = jsonParser.makeHttpRequest(mainurl.geturl() +"writeNotificationToDB", "POST",
                        params);



                JSONObject jObj = new JSONObject(json);
                if(jObj != null){
                    String data = jObj.getString("success");
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if(success){

            }
        }

        @Override
        protected void onCancelled() {

        }
    }

    public class startridewithdistancetask extends AsyncTask<Void, Void,String > {

        private final String mjrId;
        private final String mdistanceTravelled;


        startridewithdistancetask(String jRideId,String distancetravelled) {

            mjrId = jRideId;
            mdistanceTravelled = distancetravelled;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(Void... param) {
            String data = null;

            try {
                //Thread.sleep((long)(Math.random() * 3000));
                JSONObject params = new JSONObject();
                params.put("jrId", mjrId);
                params.put("latLngString",mdistanceTravelled);

                String json = jsonParser.makeHttpRequest(mainurl.geturl() +"startRideDistanceTravelled", "POST",
                        params);



                JSONObject jObj = new JSONObject(json);
                if(jObj != null){
                    data = jObj.getString("success");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(final String success) {
            if(success != null){
                SharedPreferenceManager.setPreference("locationstringdata","");
            }
        }

        @Override
        protected void onCancelled() {

        }
    }
    public class endridewithdistancetask extends AsyncTask<Void, Void,String > {

        private final String mjrId;
        private final String mdistanceTravelled;


        endridewithdistancetask(String jRideId,String distancetravelled) {

            mjrId = jRideId;
            mdistanceTravelled = distancetravelled;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(Void... param) {
            String data = null;

            try {
                //Thread.sleep((long)(Math.random() * 3000));
                JSONObject params = new JSONObject();
                params.put("jrId", mjrId);
                params.put("latLngString",mdistanceTravelled);

                // getting JSON string from URL
                String json = jsonParser.makeHttpRequest(mainurl.geturl() +"endRideDistanceTravelled", "POST",
                        params);



                JSONObject jObj = new JSONObject(json);
                if(jObj != null){
                    data = jObj.getString("success");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return data;
        }

        @Override
        protected void onPostExecute(final String success) {
            if(success != null){
                SharedPreferenceManager.setPreference("locationstringdata","");
            }
        }

        @Override
        protected void onCancelled() {

        }
    }
    public class endridewhenownerends extends AsyncTask<Void, Void,ratecardobject> {

        private final String mjrId;
        private final String mdistanceTravelled;


        endridewhenownerends(String jRideId,String distancetravelled) {

            mjrId = jRideId;
            mdistanceTravelled = distancetravelled;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ratecardobject doInBackground(Void... param) {
            ratecardobject data = null;

            try {
                JSONObject params = new JSONObject();
                params.put("jrId", mjrId);
                params.put("latLngString",mdistanceTravelled);

                // getting JSON string from URL
                String json = jsonParser.makeHttpRequest(mainurl.geturl() +"endRide", "POST",
                        params);



                JSONObject jObj = new JSONObject(json);
                if(jObj != null){
                    data = new ratecardobject(jObj.getString("totalSharedFare"),jObj.getString("distanceTravelled"),jObj.getString("totalTimeSpent"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return data;
        }

        @Override
        protected void onPostExecute(final ratecardobject data) {
            if(data != null){
                SharedPreferenceManager.setPreference("locationstringdata", "");
                Intent billfragmentactivity = new Intent(getApplicationContext(),billcardfragment.class);
                billfragmentactivity.putExtra("price", data.getPrice());
                billfragmentactivity.putExtra("distance", data.getDistance());
                billfragmentactivity.putExtra("time", data.getTime());
                billfragmentactivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(billfragmentactivity);
            }
        }

        @Override
        protected void onCancelled() {

        }
    }
}
