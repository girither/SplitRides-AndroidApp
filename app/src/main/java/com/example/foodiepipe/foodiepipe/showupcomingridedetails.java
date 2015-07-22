package com.example.foodiepipe.foodiepipe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.widget.ProfilePictureView;
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
    TextView ridefromheader_source,ridefromheader_destination,todayortomorrowheader, timeofday, rideownernamevalue, rideowneremailvalue, rideownerphonevalue;
    getindividualriddetailsetask individualridestask;
    protected final static String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    protected final static String LOCATION_KEY = "location-key";
    protected final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";
    startridetask startride_task;
    Button startride, endride, estimateride, exitride;
    ProgressBar bar;
    LinearLayout detailform;
    CustomerAdapter customerlistadapter;
    private ProgressDialog pDialog;
    ExpandableHeightGridView mGridView;
    static final int PICK_CABPROVIDER_RESULT = 1;
    static final int PICK_CABPROVIDER_RESULT_FROMESIMATE = 2;
    private PendingIntent pendingIntent;
    Intent startlocationservice;
    MenuItem hideeditmenuitem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showupcomingridedetails);
        SharedPreferenceManager.setApplicationContext(getApplicationContext());
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ridefromheader_source = (TextView) findViewById(R.id.rideFromTextHeader_source);
        ridefromheader_destination = (TextView)findViewById(R.id.rideFromTextHeader_destination);
        todayortomorrowheader = (TextView) findViewById(R.id.rideday);
        timeofday = (TextView) findViewById(R.id.ridetime);
        rideownernamevalue = (TextView) findViewById(R.id.rideownernamevalue);
        rideowneremailvalue = (TextView) findViewById(R.id.rideowneremailvalue);
        rideownerphonevalue = (TextView) findViewById(R.id.rideownerphonenumbervalue);
        bar = (ProgressBar) findViewById(R.id.searchindividualrides_progress);
        detailform = (LinearLayout) findViewById(R.id.ridedatashow);
        mGridView = (ExpandableHeightGridView)findViewById(R.id.customer_list);
        mGridView.setExpanded(true);
        startride = (Button) findViewById(R.id.startride);
        endride = (Button) findViewById(R.id.endride);
        estimateride = (Button) findViewById(R.id.estimeateride);
        exitride = (Button) findViewById(R.id.exitride);
        startride.setOnClickListener(this);
        endride.setOnClickListener(this);
        estimateride.setOnClickListener(this);
        exitride.setOnClickListener(this);

        Bundle extras = getIntent().getExtras();
        String currentrideid = extras.getString("rideId");
        String rideId = (currentrideid!= null && !currentrideid.isEmpty())?extras.getString("rideId"):SharedPreferenceManager.getPreference("currentride_rideid");
        SharedPreferenceManager.setPreference("currentride_rideid",rideId);
        new getindividualriddetailsetask(rideId).execute();

        startlocationservice = new Intent(this,Locationservice.class);
        /*Intent alarmIntent = new Intent(showupcomingridedetails.this, Alarmreciever.class);
        pendingIntent = PendingIntent.getBroadcast(showupcomingridedetails.this, 0, alarmIntent, 0);*/
    }
    //LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

/*
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
*/
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
                                    SharedPreferenceManager.setPreference("stoprides", true);
                                    stoplocationservice();
                                    String locationstring = SharedPreferenceManager.getPreference("locationstringdata");
                                    if (locationstring != null && !locationstring.isEmpty()) {
                                        new endridetask(SharedPreferenceManager.getPreference("started_jrride"),SharedPreferenceManager.getPreference("locationstringdata")).execute((Void) null);
                                        SharedPreferenceManager.setPreference("locationstringdata","");
                                    }

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
                new exitjoinedride(SharedPreferenceManager.getPreference("currentride_rideid"),SharedPreferenceManager.getPreference("customerNumber")).execute();
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
                convertView = getLayoutInflater().inflate(R.layout.customer_detail_list,
                        container, false);
            }


            ((TextView) convertView.findViewById(R.id.rideowneremailvalue)).setText(mSamples.get(position).getCustomerEmail());
            ((TextView) convertView.findViewById(R.id.rideownernamevalue)).setText(mSamples.get(position).getCustomerName());
            ((TextView) convertView.findViewById(R.id.rideownerphonenumbervalue)).setText(mSamples.get(position).getCustomerPhoneNumber());
            ((ProfilePictureView)convertView.findViewById(R.id.profilePicture)).setProfileId(mSamples.get(position).getProfileId());
            final String latlongposition = mSamples.get(position).getLatLong();
            final String droplatlongposition = mSamples.get(position).getDropLatlong();
            ((Button)convertView.findViewById(R.id.see_pickup_point)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StringBuilder latlongbuilder = new StringBuilder();
                    latlongbuilder.append("geo:").append(latlongposition).append("?q=").append(latlongposition).append("(Pickuppoint)");
                    Uri gmmIntentUri = Uri.parse(latlongbuilder.toString());
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    if (mapIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(mapIntent);
                    }
                }
            });
            ((Button)convertView.findViewById(R.id.see_drop_point)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StringBuilder latlongbuilder = new StringBuilder();
                    latlongbuilder.append("geo:").append(droplatlongposition).append("?q=").append(droplatlongposition).append("(Pickuppoint)");
                    Uri gmmIntentUri = Uri.parse(latlongbuilder.toString());
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
                if(!SharedPreferenceManager.getBooleanPreference("startrides")) {
                    SharedPreferenceManager.setPreference("startrides", true);
                    SharedPreferenceManager.setPreference("started_jrride", SharedPreferenceManager.getPreference("currentride_rideid"));
                    startlocationservice();
                    new startridetask(SharedPreferenceManager.getPreference("started_jrride")).execute();
                }
                else{
                    Toast.makeText(showupcomingridedetails.this,
                            "You have already started a Ride. PLease end it to start another one", Toast.LENGTH_LONG).show();
                }
            }
        }
        else if(requestCode == PICK_CABPROVIDER_RESULT_FROMESIMATE){
            if (resultCode == Activity.RESULT_OK) {
                Bundle extras = data.getExtras();
                String cabprovidervalue = extras.getString("cabprovider");
                new estimateridetask(cabprovidervalue, SharedPreferenceManager.getPreference("currentride_rideid"), Integer.toString(0), "").execute();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_showupcomingridedetails, menu);
        hideeditmenuitem = menu.findItem(R.id.action_edit);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Integer itemId = item.getItemId();
        switch (itemId) {
            case R.id.action_edit:
                Intent editYourRide = new Intent(showupcomingridedetails.this, Postyourrides.class);
                Bundle editRideBundle = new Bundle();
                editRideBundle.putString("activityName", "editRide");
                editRideBundle.putString("editRideId", getIntent().getExtras().getString("rideId"));
                editYourRide.putExtras(editRideBundle);
                startActivity(editYourRide);
                break;
            default:
                break;
        }
        return true;
    }

    public class startridetask extends AsyncTask<Void, Void,String > {

        private final String mRideId;



        startridetask(String RideId) {
            mRideId = RideId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(showupcomingridedetails.this);
            pDialog.setMessage("Starting Ride...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }
        @Override
        protected String doInBackground(Void... param) {
            String data = null;

            try {
                JSONObject params = new JSONObject();
                params.put("rideId", mRideId);
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

            pDialog.dismiss();
            if(success != null){
                startride.setVisibility(View.GONE);
                exitride.setVisibility(View.GONE);
            }
            else{
                Toast.makeText(showupcomingridedetails.this,
                        "Something went wrong while sending request. Please try again", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {

        }
    }

    public class endridetask extends AsyncTask<Void, Void,String > {

        private final String mRideId;
        private  final  String mlatlongstring;



        endridetask(String RideId,String latlongstring) {
            mRideId = RideId;
            mlatlongstring = latlongstring;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(showupcomingridedetails.this);
            pDialog.setMessage("Ending Ride...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }
        @Override
        protected String doInBackground(Void... param) {
            String data = null;

            try {
                JSONObject params = new JSONObject();
                params.put("rideId", mRideId);
                params.put("latLngString",mlatlongstring);
                // getting JSON string from URL
                String json = jsonParser.makeHttpRequest("http://radiant-peak-3095.herokuapp.com/endRide", "POST",
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

            pDialog.dismiss();
            if(success != null){
               finish();
            }
            else{
                Toast.makeText(showupcomingridedetails.this,
                        "Something went wrong while sending request. Please try again", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {

        }
    }

    public class exitjoinedride extends AsyncTask<Void, Void,Boolean> {

        private final String jrId;
        private final String mcustomerNumber;


        exitjoinedride(String joinedrideId, String customerNumber) {
            jrId=joinedrideId;
            mcustomerNumber = customerNumber;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(showupcomingridedetails.this);
            pDialog.setMessage("Exiting Ride...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
        @Override
        protected Boolean doInBackground(Void... param) {
            String success = null;

            try {
                JSONObject params = new JSONObject();
                params.put("jrId", jrId);
                params.put("customerNumber",mcustomerNumber);
                // getting JSON string from URL
                String json = jsonParser.makeHttpRequest("http://radiant-peak-3095.herokuapp.com/removeFromTheJoinedRide", "POST",
                        params);



                JSONObject jObj = new JSONObject(json);
                if(jObj != null){
                    success = jObj.getString("success");
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            pDialog.dismiss();
            if(success){
                finish();
            }
            else{
                Toast.makeText(showupcomingridedetails.this,
                        "Something went wrong while sending request. Please try again", Toast.LENGTH_LONG).show();
            }
        }


        @Override
        protected void onCancelled() {

        }
    }

    public class estimateridetask extends AsyncTask<Void, Void,ratecardobject > {

        private final String mcabProvider;
        private final String jrId;
        private final String mestimateBeforeJoining;
        private final String mrrideId;



        estimateridetask(String cabProvider,String joinedrideId, String estimateBeforeJoining,String rRideId) {
            mcabProvider = cabProvider;
            jrId=joinedrideId;
            mestimateBeforeJoining =estimateBeforeJoining;
            mrrideId = rRideId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(showupcomingridedetails.this);
            pDialog.setMessage("Estimating Ride...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
        @Override
        protected ratecardobject doInBackground(Void... param) {
            ratecardobject data = null;

            try {
                JSONObject params = new JSONObject();
                params.put("jrId", jrId);
                params.put("estimateBeforeJoining", mestimateBeforeJoining);
                params.put("rrideId", mrrideId);
                params.put("serviceProvider",mcabProvider.toLowerCase());
                params.put("city","bengaluru");
                // getting JSON string from URL
                String json = jsonParser.makeHttpRequest("http://radiant-peak-3095.herokuapp.com/estimateRide", "POST",
                        params);



                JSONObject jObj = new JSONObject(json);
                if(jObj != null){
                    data = new ratecardobject(jObj.getString("price"),jObj.getString("distance"),jObj.getString("time"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return data;
        }

        @Override
        protected void onPostExecute(final ratecardobject data) {
            pDialog.dismiss();
            if(data != null){
                DialogFragment rateFragment = new ratecardfragment(data);
                rateFragment.show(getFragmentManager(), "ratepicker");
            }
        }

        @Override
        protected void onCancelled() {

        }
    }


    public class getindividualriddetailsetask extends AsyncTask<Void, Void, ridedata > {

        private final String mRideId;



        getindividualriddetailsetask(String RideId) {
            mRideId = RideId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            bar.setVisibility(View.VISIBLE);

        }
        @Override
        protected ridedata doInBackground(Void... param) {
            ridedata info = null;
            String status = null;

            try {
                JSONObject params = new JSONObject();
                params.put("rideId", mRideId);
                // getting JSON string from URL
                String json = jsonParser.makeHttpRequest("http://radiant-peak-3095.herokuapp.com/getRideDetails", "POST",
                        params);



                JSONObject jObj = new JSONObject(json);
                if(jObj != null){
                    if (jObj.has("jride")) {
                        JSONObject ride = jObj.getJSONObject("jride");
                        JSONArray customerdata = jObj.getJSONArray("builtDetails");
                        List<customer> customerlistdata = new ArrayList<customer>();
                        if(ride.has("statusMatrix")) {
                            if(!ride.isNull("statusMatrix")) {
                                JSONObject statusmatrix = ride.getJSONObject("statusMatrix");
                                if (statusmatrix.has(SharedPreferenceManager.getPreference("customerNumber"))) {
                                    JSONObject requestobject = statusmatrix.getJSONObject(SharedPreferenceManager.getPreference("customerNumber"));
                                    status = requestobject.getString("status");
                                }
                            }
                        }
                        for (int i = 0; i < customerdata.length(); i++) {
                            JSONObject customerindividualdata = customerdata.getJSONObject(i);
                            customer customeradapterdata = new customer(customerindividualdata.getString("name"), customerindividualdata.getString("email"), customerindividualdata.getString("phoneNumber"), customerindividualdata.getString("pickUplatLng"),customerindividualdata.getString("customersDropLatLngMatrix"),customerindividualdata.has("profileId")?customerindividualdata.getString("profileId"):"");
                            customerlistdata.add(customeradapterdata);
                        }
                        info = new ridedata(ride.getString("source"), ride.getString("destination"), ride.getString("date"), customerlistdata,"jride",ride.getString("jrId"),status);
                    }
                    else if(jObj.has("ride")) {
                        JSONObject ride = jObj.getJSONObject("ride");
                        JSONObject customerdata = jObj.getJSONObject("owner");
                        StringBuilder latlongbuilder = new StringBuilder();
                        latlongbuilder.append(ride.getString("pickUpLat")).append(",").append(ride.getString("pickUpLng"));
                        StringBuilder latlongbuilder_droppoint = new StringBuilder();
                        latlongbuilder_droppoint.append(ride.getString("dropLat")).append(",").append(ride.getString("dropLng"));
                        customer customeradapterdata = new customer(customerdata.getString("name"), customerdata.getString("email"), ride.getString("phoneNumber"),latlongbuilder.toString() ,latlongbuilder_droppoint.toString(),customerdata.has("profileId")?customerdata.getString("profileId"):"");
                        List<customer> customerlistdata = new ArrayList<customer>();
                        customerlistdata.add(customeradapterdata);
                        info = new ridedata(ride.getString("source"), ride.getString("destination"), ride.getString("date"), customerlistdata,"ride",ride.getString("rideId"));
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
                ridefromheader_source.setText(ridedataobject.getSource());
                ridefromheader_destination.setText(ridedataobject.getDestination());
                String dateOfRides = ridedataobject.getDate().split("T")[0];
                String timeofrides = ridedataobject.getDate().split("T")[1];
                timeofrides = timeofrides.split(".000Z")[0];
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String currentDate = sdf.format(cal.getTime());
                Calendar nextdaycal = Calendar.getInstance();
                nextdaycal.add(Calendar.DATE, 1);
                String todayortomorrow;
                SimpleDateFormat sdftomorrow = new SimpleDateFormat("yyyy-MM-dd");
                String tomorrowDate = sdftomorrow.format(nextdaycal.getTime());
                if(currentDate.equals(dateOfRides))
                {
                    todayortomorrow = "Today        ";
                }
                else if(tomorrowDate.equals(dateOfRides))
                {
                    todayortomorrow = "Tomorrow     ";
                }
                else
                {
                    todayortomorrow = dateOfRides;
                }
                if(!(todayortomorrow.trim().equals("Today") || todayortomorrow.trim().equals("Tomorrow")))
                {
                    startride.setVisibility(View.GONE);
                    endride.setVisibility(View.GONE);
                    estimateride.setVisibility(View.GONE);
                    exitride.setVisibility(View.GONE);
                    hideeditmenuitem.setVisible(false);
                }
                if(ridedataobject.getRidestatus() != null && ridedataobject.getRidestatus().equals("notstarted")){
                    endride.setVisibility(View.GONE);
                }
                else if(ridedataobject.getRidestatus() != null && ridedataobject.getRidestatus().equals("started")){
                    startride.setVisibility(View.GONE);
                    exitride.setVisibility(View.GONE);
                }
                todayortomorrowheader.setText(todayortomorrow);
                timeofday.setText(timeofrides);
                if(ridedataobject.getRideFlag().equals("ride"))
                {
                    startride.setVisibility(View.GONE);
                    endride.setVisibility(View.GONE);
                    estimateride.setVisibility(View.GONE);
                    exitride.setVisibility(View.GONE);
                }
                customerlistadapter = new CustomerAdapter(ridedataobject.getCustomerlistdata());
                mGridView.setAdapter(customerlistadapter);
                customerlistadapter.notifyDataSetChanged();
            }
        }

        @Override
        protected void onCancelled() {
            individualridestask = null;
        }
    }
}
