package com.example.foodiepipe.foodiepipe;

import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.foodpipe.android.helper.JSONParser;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by gbm on 6/29/15.
 */
public class googleservice  extends IntentService {
    JSONParser jsonParser = new JSONParser();


    public googleservice() {
        super("MyServiceName");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("MyService", "About to execute MyTask");
        new googledistancematrixapitask(SharedPreferenceManager.getPreference("locationstringdata"),SharedPreferenceManager.getBooleanPreference("stoprides")).execute();
        SharedPreferenceManager.setPreference("locationstringdata","");
    }

    public class googledistancematrixapitask extends AsyncTask<Void, Void, String> {
        private String mlocationstring;
        private Boolean mstopRides;


        googledistancematrixapitask(String locationstring,Boolean stopRide) {
            mlocationstring = locationstring;
            mstopRides = stopRide;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(Void... param) {
            String distance = null;
            // Building Parameters
            /*List<NameValuePair> params = new ArrayList<NameValuePair>();

            // post album id, song id as GET parameters
            params.add(new BasicNameValuePair("name", mName));
            params.add(new BasicNameValuePair("email", mEmail));
            params.add(new BasicNameValuePair("password", mPassword));
            params.add(new BasicNameValuePair("profile", mProfile));*/
            try {
                JSONObject params = new JSONObject();
                params.put("sourcelatlong", mlocationstring);
                params.put("destinationlatlong", mlocationstring);
                // getting JSON string from URL
                String json = jsonParser.makeHttpRequest(mainurl.geturl() +"calculatedistance", "POST",
                        params);

                // Check your log cat for JSON reponse
                Log.d("response from post rides ", json);

                JSONObject jObj = new JSONObject(json);
                if(jObj != null){
                    distance = jObj.getString("totaldistance");

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return distance;
        }

        @Override
        protected void onPostExecute(final String distance) {
            float totaldistance = SharedPreferenceManager.getFloatPreference("totaldistance");
            if(distance != null) {
                totaldistance = totaldistance + Float.parseFloat(distance);
            }

            if(mstopRides){
                Toast.makeText(getApplicationContext(),
                        "The total distance travelled is " + Float.toString(totaldistance) + "Km", Toast.LENGTH_LONG).show();
                SharedPreferenceManager.setPreference("totaldistance", 0.0f);

            }
            else{
                SharedPreferenceManager.setPreference("totaldistance", totaldistance);
            }


        }

        @Override
        protected void onCancelled() {

        }
    }
}
