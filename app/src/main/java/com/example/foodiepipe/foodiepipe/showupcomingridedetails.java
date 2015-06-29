package com.example.foodiepipe.foodiepipe;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.foodpipe.android.helper.ConnectionDetector;
import com.foodpipe.android.helper.JSONParser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.nullwire.trace.ExceptionHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimerTask;


public class showupcomingridedetails extends ActionBarActivity implements View.OnClickListener,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {


    private getindividualriddetailsetask myrideTask = null;
    JSONParser jsonParser = new JSONParser();
    TextView ridefromheader, todayortomorrowheader, timeofday, rideownernamevalue, rideowneremailvalue, rideownerphonevalue;
    getindividualriddetailsetask individualridestask;
    protected final static String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    protected final static String LOCATION_KEY = "location-key";
    protected final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";
    startridetask startride_task;
    Button startride, endride, estimateride, exitride;
    ProgressBar bar;
    LinearLayout detailform;
    CustomerAdapter customerlistadapter;
    GridView mGridView;
    String locationstring;
    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocationRequest;
    protected Boolean mRequestingLocationUpdates;
    protected Location mCurrentLocation;
    TimerTask hourlyTask;
    protected String mLastUpdateTime;
    static final int PICK_CABPROVIDER_RESULT = 1;
    private PendingIntent pendingIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showupcomingridedetails);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ridefromheader = (TextView) findViewById(R.id.rideFromTextHeader);
        todayortomorrowheader = (TextView) findViewById(R.id.rideday);
        timeofday = (TextView) findViewById(R.id.ridetime);
        rideownernamevalue = (TextView) findViewById(R.id.rideownernamevalue);
        rideowneremailvalue = (TextView) findViewById(R.id.rideowneremailvalue);
        rideownerphonevalue = (TextView) findViewById(R.id.rideownerphonenumbervalue);
        bar = (ProgressBar) findViewById(R.id.searchindividualrides_progress);
        detailform = (LinearLayout) findViewById(R.id.ridedatashow);
        mGridView = (GridView) findViewById(android.R.id.list);
        startride = (Button) findViewById(R.id.startride);
        endride = (Button) findViewById(R.id.endride);
        estimateride = (Button) findViewById(R.id.estimeateride);
        exitride = (Button) findViewById(R.id.exitride);
        startride.setOnClickListener(this);
        endride.setOnClickListener(this);
        estimateride.setOnClickListener(this);
        exitride.setOnClickListener(this);
        mRequestingLocationUpdates = false;
        Bundle extras = getIntent().getExtras();
        String rideId = extras.getString("rideId");
        String rideFlag = extras.getString("rideFlag");
        if(rideFlag.equals("ride"))
        {
            exitride.setVisibility(View.GONE);
        }
        String datetimeOfRides = extras.getString("rideDate") , todayortomorrow;
        String dateOfRides = datetimeOfRides.split("T")[0];
        String timeofrides = datetimeOfRides.split("T")[1];
        timeofrides = timeofrides.split(".000Z")[0];
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = sdf.format(cal.getTime());
        Calendar nextdaycal = Calendar.getInstance();
        nextdaycal.add(Calendar.DATE, 1);
        SimpleDateFormat sdftomorrow = new SimpleDateFormat("yyyy-MM-dd");
        String tomorrowDate = sdftomorrow.format(nextdaycal.getTime());
        if(currentDate.equals(dateOfRides))
        {
            todayortomorrow = "Today";
        }
        else if(tomorrowDate.equals(dateOfRides))
        {
            todayortomorrow = "Tomorrow";
        }
        else
        {
            todayortomorrow = dateOfRides;
        }
        if(!(todayortomorrow.equals("Today")||todayortomorrow.equals("Tomorrow")))
        {
            startride.setVisibility(View.GONE);
            endride.setVisibility(View.GONE);
            estimateride.setVisibility(View.GONE);
        }
        new getindividualriddetailsetask(rideId, rideFlag).execute();
        updateValuesFromBundle(savedInstanceState);

        Intent alarmIntent = new Intent(showupcomingridedetails.this, Alarmreciever.class);
        pendingIntent = PendingIntent.getBroadcast(showupcomingridedetails.this, 0, alarmIntent, 0);

        // Kick off the process of building a GoogleApiClient and requesting the LocationServices
        // API.
        buildGoogleApiClient();
        ExceptionHandler.register(this, "http://radiant-peak-3095.herokuapp.com/remoteStackTrace");

    }


    @Override
    public void onConnectionSuspended(int cause) {

        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {

    }


    /**
     * Stores activity data in the Bundle.
     */
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(LOCATION_KEY, mCurrentLocation);
        savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, mLastUpdateTime);
        locationstring = SharedPreferenceManager.getPreference("locationstringdata");
        if (locationstring != null && !locationstring.isEmpty()) {
            savedInstanceState.putString("locationstringdata", locationstring);
        }
        super.onSaveInstanceState(savedInstanceState);
    }

    protected synchronized void buildGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(15000);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(7500);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {

        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.

        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    public void startalarm() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int interval = 1000 * 3 * 60;
        SharedPreferenceManager.setPreference("stoprides",false);
        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
    }

    public void stopalarm() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
        //Toast.makeText(this, "Alarm Canceled", Toast.LENGTH_SHORT).show();
    }


    public void onClick(View view) {
        ConnectionDetector cd = new ConnectionDetector(showupcomingridedetails.this.getApplicationContext());

        // Check if Internet present
        if (!cd.isConnectingToInternet()) {
            // Internet Connection is not present
            Toast.makeText(showupcomingridedetails.this,
                    "Internet Connection Error Please connect to working Internet connection", Toast.LENGTH_LONG).show();
            // stop executing code by return
            return;
        }
        switch(view.getId()) {
            case R.id.startride:
                Intent selectcabprovider = new Intent(showupcomingridedetails.this,cabproviderselction.class);
                startActivityForResult(selectcabprovider, PICK_CABPROVIDER_RESULT);

                break;
            case R.id.endride:
                mRequestingLocationUpdates = false;
                stopLocationUpdates();
                stopalarm();
                if(mGoogleApiClient != null) {
                    mGoogleApiClient.disconnect();
                }
                SharedPreferenceManager.setPreference("stoprides",true);
                locationstring = SharedPreferenceManager.getPreference("locationstringdata");
                if(locationstring != null && !locationstring.isEmpty()) {
                    Intent dailyUpdater = new Intent(this, googleservice.class);
                    this.startService(dailyUpdater);
                }
                Toast.makeText(showupcomingridedetails.this,
                        "Ride has ended", Toast.LENGTH_LONG).show();

                break;
            case R.id.estimeateride:
                break;
            case R.id.exitride:
                break;
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        StringBuilder locationstringbuilder = new StringBuilder();
        locationstring = SharedPreferenceManager.getPreference("locationstringdata");
        if(locationstring != null) {
            locationstringbuilder.append(locationstring).append(mCurrentLocation.getLatitude()).append(",").append(mCurrentLocation.getLongitude()).append("|");
            SharedPreferenceManager.setPreference("locationstringdata", locationstringbuilder.toString());
        }
    }


    @Override
    public void onConnected(Bundle connectionHint) {


        // If the initial location was never previously requested, we use
        // FusedLocationApi.getLastLocation() to get it. If it was previously requested, we store
        // its value in the Bundle and check for it in onCreate(). We
        // do not request it again unless the user specifically requests location updates by pressing
        // the Start Updates button.
        //
        // Because we cache the value of the initial location in the Bundle, it means that if the
        // user launches the activity,
        // moves to a new location, and then changes the device orientation, the original location
        // is displayed as the activity is re-created.
        if (mCurrentLocation == null) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }

        // If the user presses the Start Updates button before GoogleApiClient connects, we set
        // mRequestingLocationUpdates to true (see startUpdatesButtonHandler()). Here, we check
        // the value of mRequestingLocationUpdates and if it is true, we start location updates.
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    private class CustomerAdapter extends BaseAdapter {
        private List<customer> mSamples;
        public CustomerAdapter(List<customer> myDataset) {
            mSamples = myDataset;
        }

        @Override
        public int getCount() {
            return mSamples.size();
        }

        @Override
        public Object getItem(int position) {
            return mSamples.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mSamples.get(position).hashCode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            String todayortomorrow;
            if (convertView == null) {
                convertView = showupcomingridedetails.this.getLayoutInflater().inflate(R.layout.customer_detail_list,
                        container, false);
            }


            ((TextView) convertView.findViewById(R.id.rideowneremailvalue)).setText(mSamples.get(position).getCustomerEmail());
            ((TextView) convertView.findViewById(R.id.rideownernamevalue)).setText(mSamples.get(position).getCustomerName());
            ((TextView) convertView.findViewById(R.id.rideownerphonenumbervalue)).setText(mSamples.get(position).getCustomerPhoneNumber());
            ((Button)convertView.findViewById(R.id.see_pickup_point)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri gmmIntentUri = Uri.parse("geo:37.7749,-122.4194");
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    if (mapIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(mapIntent);
                    }
                }
            });

            return convertView;
        }
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and make sure that
            // the Start Updates and Stop Updates buttons are correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        REQUESTING_LOCATION_UPDATES_KEY);
            }

            // Update the value of mCurrentLocation from the Bundle and update the UI to show the
            // correct latitude and longitude.
            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                // Since LOCATION_KEY was found in the Bundle, we can be sure that mCurrentLocation
                // is not null.
                mCurrentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }
            if (savedInstanceState.keySet().contains("locationstring"))
            {
                String locationstringdata = savedInstanceState.getString("locationstring");
                if(locationstringdata!=null && !locationstringdata.isEmpty()){
                    StringBuilder locationstringbuilder = new StringBuilder();
                    if(mCurrentLocation  != null)
                    {

                        locationstringbuilder.append(locationstringdata).append(mCurrentLocation.getLatitude()).append(",").append(mCurrentLocation.getLongitude()).append("|");
                    }
                    else
                    {
                        locationstringbuilder.append(mCurrentLocation.getLatitude()).append(",").append(mCurrentLocation.getLongitude()).append("|");
                    }
                    SharedPreferenceManager.setPreference("locationstringdata",locationstringbuilder.toString());
                }
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(LAST_UPDATED_TIME_STRING_KEY)) {
                mLastUpdateTime = savedInstanceState.getString(LAST_UPDATED_TIME_STRING_KEY);
            }
        }
    }

    @Override
    public void onBackPressed() {
        // finish() is called in super: we only override this method to be able to override the transition
        super.onBackPressed();

        overridePendingTransition(R.animator.back_in, R.animator.back_out);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == PICK_CABPROVIDER_RESULT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                SharedPreferenceManager.setPreference("totaldistance", 0.0f);
                Toast.makeText(showupcomingridedetails.this,
                        "Ride has started", Toast.LENGTH_LONG).show();
                createLocationRequest();
                startalarm();
                mRequestingLocationUpdates = true;
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Within {@code onPause()}, we pause location updates, but leave the
        // connection to GoogleApiClient intact.  Here, we resume receiving
        // location updates if the user has requested them.

        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
           // startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop location updates to save battery, but don't disconnect the GoogleApiClient object.
        if (mGoogleApiClient.isConnected()) {
           // stopLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();

        super.onStop();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_searchshowinduvidualrides, menu);
        return true;
    }

    public class startridetask extends AsyncTask<Void, Void,String > {

        private final String mRideId;
        private final String mRideFlag;



        startridetask(String RideId, String RideFlag ) {
            mRideId = RideId;
            mRideFlag = RideFlag ;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            bar.setVisibility(View.VISIBLE);

        }
        @Override
        protected String doInBackground(Void... param) {
            String data = null;

            try {
                JSONObject params = new JSONObject();
                params.put("rideId", mRideId);
                params.put("rideFlag", mRideFlag);
                // getting JSON string from URL
                String json = jsonParser.makeHttpRequest("http://radiant-peak-3095.herokuapp.com/startRide", "POST",
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
            individualridestask = null;
            bar.setVisibility(View.GONE);
            detailform.setVisibility(View.VISIBLE);
            if(success != null){

            }
        }

        @Override
        protected void onCancelled() {
            individualridestask = null;
        }
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
            bar.setVisibility(View.VISIBLE);

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
                    if(mRideFlag.equals("jride")) {
                        JSONObject ride = jObj.getJSONObject("ride");
                        JSONArray customerdata = jObj.getJSONArray("customers");
                        List<customer> customerlistdata = new ArrayList<customer>();
                        for(int i=0; i<customerdata.length(); i++){
                            JSONObject customerindividualdata = customerdata.getJSONObject(i);
                            customer customeradapterdata = new customer(customerindividualdata.getString("name"),customerindividualdata.getString("email"),ride.getString("phoneNumber"));
                            customerlistdata.add(customeradapterdata);
                        }
                        info = new ridedata(ride.getString("source"), ride.getString("destination"), ride.getString("date"),customerlistdata,ride.getString("rideId"));
                    }
                    else{
                        JSONObject ride = jObj.getJSONObject("ride");
                        JSONObject customerdata = jObj.getJSONObject("owner");
                        customer customeradapterdata = new customer(customerdata.getString("name"),customerdata.getString("email"),ride.getString("phoneNumber"));
                        List<customer> customerlistdata = new ArrayList<customer>();
                        customerlistdata.add(customeradapterdata);
                        info = new ridedata(ride.getString("source"), ride.getString("destination"), ride.getString("date"),customerlistdata, ride.getString("rideId"));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return info;
        }

        @Override
        protected void onPostExecute(final ridedata ridedataobject) {
            individualridestask = null;
            bar.setVisibility(View.GONE);
            detailform.setVisibility((ridedataobject!=null)?View.VISIBLE:View.GONE);
            if(ridedataobject != null ){
                String Ridefrom = new StringBuilder().append("Ride from ").append(ridedataobject.getSource()).append(" to ").append(ridedataobject.getDestination()).toString();
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
                customerlistadapter = new CustomerAdapter(ridedataobject.getCustomerlistdata());
                mGridView.setAdapter(customerlistadapter);
            }
        }

        @Override
        protected void onCancelled() {
            individualridestask = null;
        }
    }
}
