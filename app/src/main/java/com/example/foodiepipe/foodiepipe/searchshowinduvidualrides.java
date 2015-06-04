package com.example.foodiepipe.foodiepipe;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.foodpipe.android.helper.ConnectionDetector;
import com.foodpipe.android.helper.JSONParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class searchshowinduvidualrides extends ActionBarActivity implements View.OnClickListener {

    private ProgressDialog pDialog;
    private getindividualriddetailsetask myrideTask = null;
    JSONParser jsonParser = new JSONParser();
    TextView ridefromheader,todayortomorrowheader,timeofday,rideownernamevalue,rideowneremailvalue,rideownerphonevalue;
    getindividualriddetailsetask individualridestask;
    Button seePickupPoint,sendrequesttojoinride;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchshowinduvidualrides);
        ridefromheader = (TextView)findViewById(R.id.rideFromTextHeader);
        todayortomorrowheader = (TextView)findViewById(R.id.rideday);
        timeofday = (TextView)findViewById(R.id.ridetime);
        rideownernamevalue = (TextView)findViewById(R.id.rideownernamevalue);
        rideowneremailvalue = (TextView)findViewById(R.id.rideowneremailvalue);
        rideownerphonevalue = (TextView)findViewById(R.id.rideownerphonenumbervalue);
        seePickupPoint = (Button)findViewById(R.id.see_pickup_point);
        sendrequesttojoinride = (Button)findViewById(R.id.send_request_joinride);
        seePickupPoint.setOnClickListener(this);
        sendrequesttojoinride.setOnClickListener(this);
        Bundle extras = getIntent().getExtras();
        String rideId = extras.getString("rideId");
        String rideFlag = extras.getString("rideFlag");
        new getindividualriddetailsetask(rideId,rideFlag).execute();

    }


    public void onClick(View view) {
        ConnectionDetector cd = new ConnectionDetector(searchshowinduvidualrides.this.getApplicationContext());

        // Check if Internet present
        if (!cd.isConnectingToInternet()) {
            // Internet Connection is not present
            Toast.makeText(searchshowinduvidualrides.this,
                    "Internet Connection Error Please connect to working Internet connection", Toast.LENGTH_LONG).show();
            // stop executing code by return
            return;
        }
        switch(view.getId()) {
            case R.id.see_pickup_point:
                Uri gmmIntentUri = Uri.parse("geo:37.7749,-122.4194");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
                break;
            case R.id.send_request_joinride:
                Bundle extras = getIntent().getExtras();
                String rideId = extras.getString("rideId");
                String rideFlag = extras.getString("rideFlag");
                String rideOwnerCustomerNumber = extras.getString("ownercustomernumber");
                new sendrequesttojoinridetask(rideId,rideFlag,rideOwnerCustomerNumber,SharedPreferenceManager.getPreference("customerNumber"),SharedPreferenceManager.getPreference("myrideId")).execute();
                break;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_searchshowinduvidualrides, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class getindividualriddetailsetask extends AsyncTask<Void, Void, ridedata > {

        private final String mRideId;
        private final String mRideFlag;



        getindividualriddetailsetask(String RideId, String RideFlag ) {
            mRideId = RideId;
            mRideFlag = RideFlag ;
        }



        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(searchshowinduvidualrides.this);
            pDialog.setMessage("fetching ride data...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
        @Override
        protected ridedata doInBackground(Void... param) {
            ridedata info = null;

            try {
                JSONObject params = new JSONObject();
                params.put("rideId", mRideId);
                params.put("rideFlag", mRideFlag);
                // getting JSON string from URL
                String json = jsonParser.makeHttpRequest("http://radiant-peak-3095.herokuapp.com/getRideDetails", "POST",
                        params);



                JSONObject jObj = new JSONObject(json);
                if(jObj != null){
                    JSONObject ride = jObj.getJSONObject("ride");
                    JSONObject customer = jObj.getJSONObject("owner");
                    info = new ridedata(ride.getString("source"),ride.getString("destination"),ride.getString("date"),customer.getString("name"),ride.getString("phoneNumber"),customer.getString("email"),ride.getString("rideId"));
                  }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return info;
        }

        @Override
        protected void onPostExecute(final ridedata ridedataobject) {
            individualridestask = null;
            pDialog.dismiss();
            if(ridedataobject != null ){
                String Ridefrom = new StringBuilder().append("Ride from").append(ridedataobject.getSource()).append("to").append(ridedataobject.getDestination()).toString();
                ridefromheader.setText(Ridefrom);
                String dateOfRides = ridedataobject.getDate().split("T")[0];
                String timeofrides = ridedataobject.getDate().split("T")[1];
                timeofrides = timeofrides.split(".000Z")[0];
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String currentDate = sdf.format(cal.getTime());
                String todayortomorrow = (currentDate.equals(dateOfRides))?"Today":"Tomorrow";
                todayortomorrowheader.setText(todayortomorrow);
                timeofday.setText(timeofrides);
                rideownernamevalue.setText(ridedataobject.getRideownerName());
                rideownerphonevalue.setText(ridedataobject.getRideownerPhoneNumber());
                rideowneremailvalue.setText(ridedataobject.getRideOwneremail());
            }
        }

        @Override
        protected void onCancelled() {
            individualridestask = null;
        }
    }
    public class sendrequesttojoinridetask extends AsyncTask<Void, Void, Boolean > {

        private final String mRideId;
        private final String mRideFlag;
        private final String mownerCustomerNumber;
        private final String mrequestingCustomerNumber;
        private final String mrRideId;

        sendrequesttojoinridetask(String RideId, String RideFlag,String ownerCustomerNumber,String requestingCustomerNumber,String rRideId ) {
            mRideId = RideId;
            mRideFlag = RideFlag ;
            mownerCustomerNumber= ownerCustomerNumber;
            mrequestingCustomerNumber = requestingCustomerNumber;
            mrRideId = rRideId;
        }



        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(searchshowinduvidualrides.this);
            pDialog.setMessage("sending request to join ride...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
        @Override
        protected Boolean doInBackground(Void... param) {
            ridedata info = null;

            try {
                JSONObject params = new JSONObject();
                params.put("rideId", mRideId);
                params.put("rideFlag", mRideFlag);
                params.put("ownerCustomerNumber", mownerCustomerNumber);
                params.put("requestingCustomerNumber", mrequestingCustomerNumber);
                params.put("rRideId", mrRideId);
                // getting JSON string from URL
                String json = jsonParser.makeHttpRequest("http://radiant-peak-3095.herokuapp.com/sendRequestToJoinTheRideOrJoinedRide", "POST",
                        params);



                JSONObject jObj = new JSONObject(json);
                if(jObj != null) {
                    JSONObject message = jObj.getJSONObject("message");
                    return true;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            individualridestask = null;
            pDialog.dismiss();
            if (success) {
                Toast.makeText(searchshowinduvidualrides.this,
                        "Request is sent succesfully", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(searchshowinduvidualrides.this,
                        "Something went wrong while sending request. Please try again", Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected void onCancelled() {

        }
    }
}
