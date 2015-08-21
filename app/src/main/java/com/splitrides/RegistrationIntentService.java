package com.splitrides;

import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.splitrides.android.helper.JSONParser;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by gbm on 8/19/15.
 */
public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};
    JSONParser jsonParser = new JSONParser();
    String SENDER_ID = "544261498132";

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            // In the (unlikely) event that multiple refresh operations occur simultaneously,
            // ensure that they are processed sequentially.
            synchronized (TAG) {
                // [START register_for_gcm]
                // Initially this call goes out to the network to retrieve the token, subsequent calls
                // are local.
                // [START get_token]
                InstanceID instanceID = InstanceID.getInstance(this);
                String token = instanceID.getToken(SENDER_ID,
                        GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                // [END get_token]
                Log.d(TAG, "GCM Registration Token: " + token);

                sendRegistrationToServer(token);

                // Subscribe to topic channels
                //subscribeTopics(token);

                // You should store a boolean that indicates whether the generated token has been
                // sent to your server. If the boolean is false, send the token to your server,
                // otherwise your server should have already received the token.
                //sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, true).apply();
                // [END register_for_gcm]
            }
        } catch (Exception e) {

        }
    }


    private void sendRegistrationToServer(String token) {
        SharedPreferenceManager.setPreference("registrationid", token);
        if(SharedPreferenceManager.getBooleanPreference("mIsSignedIn"))
        {
            new updategcmidtask(token).execute((Void) null);
        }
    }

    public class updategcmidtask extends AsyncTask<Void, Void,Boolean> {
        private String gcmid;

        updategcmidtask(String token) {
            gcmid = token;
        }

        @Override
        protected Boolean doInBackground(Void... param) {
            try {
                JSONObject params = new JSONObject();
                params.put("gcmId",gcmid);
                String json = jsonParser.makeHttpRequest(mainurl.geturl() +"updateGcmId", "POST",
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

}