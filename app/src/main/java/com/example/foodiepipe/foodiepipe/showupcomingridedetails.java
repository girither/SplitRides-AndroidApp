package com.example.foodiepipe.foodiepipe;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class showupcomingridedetails extends ActionBarActivity implements View.OnClickListener {


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
    static final int PICK_CABPROVIDER_RESULT = 1;
    static final int PICK_CABPROVIDER_RESULT_FROMESIMATE = 2;
    private PendingIntent pendingIntent;
    Intent startlocationservice;


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
        Bundle extras = getIntent().getExtras();
        String rideId = extras.getString("rideId");
        String rideFlag = extras.getString("rideFlag");
        SharedPreferenceManager.setPreference("currentride_rideid",rideId);
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

        startlocationservice = new Intent(this,Locationservice.class);
        Intent alarmIntent = new Intent(showupcomingridedetails.this, Alarmreciever.class);
        pendingIntent = PendingIntent.getBroadcast(showupcomingridedetails.this, 0, alarmIntent, 0);
    }
    //LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);


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

    public void startlocationservice(){
        this.startService(startlocationservice);
    }

    public void stoplocationservice(){
        this.stopService(startlocationservice);
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
                if(SharedPreferenceManager.getBooleanPreference("stoprides")) {
                    Intent selectcabprovider = new Intent(showupcomingridedetails.this, cabproviderselction.class);
                    startActivityForResult(selectcabprovider, PICK_CABPROVIDER_RESULT);
                }

                break;
            case R.id.endride:
                if(SharedPreferenceManager.getBooleanPreference("startrides")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(showupcomingridedetails.this);
                    builder.setMessage("Do You want to end the ride?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    SharedPreferenceManager.setPreference("startrides", false);
                                    stopalarm();
                                    SharedPreferenceManager.setPreference("stoprides", true);
                                    String locationstring = SharedPreferenceManager.getPreference("locationstringdata");
                                    if (locationstring != null && !locationstring.isEmpty()) {
                                        Intent dailyUpdater = new Intent(getApplicationContext(), googleservice.class);
                                        startService(dailyUpdater);
                                    }
                                    stoplocationservice();
                                    Toast.makeText(showupcomingridedetails.this,
                                            "Ride has ended", Toast.LENGTH_LONG).show();

                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                break;
            case R.id.estimeateride:
                Intent selectcabprovider_estimate = new Intent(showupcomingridedetails.this,cabproviderselction.class);
                startActivityForResult(selectcabprovider_estimate,PICK_CABPROVIDER_RESULT_FROMESIMATE);
                break;
            case R.id.exitride:
                break;
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
                SharedPreferenceManager.setPreference("startrides",true);
                SharedPreferenceManager.setPreference("totaldistance", 0.0f);
                Toast.makeText(showupcomingridedetails.this,
                        "Ride has started", Toast.LENGTH_LONG).show();
                startalarm();
                startlocationservice();
            }
        }
        else if(requestCode == PICK_CABPROVIDER_RESULT_FROMESIMATE){
            new estimateridetask(SharedPreferenceManager.getPreference("currentride_rideid"),Integer.toString(0),"").execute();
        }
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

            bar.setVisibility(View.GONE);
            detailform.setVisibility(View.VISIBLE);
            if(success != null){

            }
        }

        @Override
        protected void onCancelled() {

        }
    }

    public class estimateridetask extends AsyncTask<Void, Void,String > {

        private final String jrId;
        private final String mestimateBeforeJoining;
        private final String mrrideId;



        estimateridetask(String joinedrideId, String estimateBeforeJoining,String rRideId) {
            jrId=joinedrideId;
            mestimateBeforeJoining =estimateBeforeJoining;
            mrrideId = rRideId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            bar.setVisibility(View.VISIBLE);

        }
        @Override
        protected String doInBackground(Void... param) {
            String price = null;

            try {
                JSONObject params = new JSONObject();
                params.put("jrId", jrId);
                params.put("estimateBeforeJoining", mestimateBeforeJoining);
                params.put("rrideId", mrrideId);
                // getting JSON string from URL
                String json = jsonParser.makeHttpRequest("http://radiant-peak-3095.herokuapp.com/estimateRide", "POST",
                        params);



                JSONObject jObj = new JSONObject(json);
                if(jObj != null){
                    price = jObj.getString("price");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return price;
        }

        @Override
        protected void onPostExecute(final String price) {
            bar.setVisibility(View.GONE);
            detailform.setVisibility(View.VISIBLE);
            if(price != null){

            }
        }

        @Override
        protected void onCancelled() {

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
