package com.example.foodiepipe.foodiepipe;

import android.app.Service;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import java.util.Timer;
import java.util.TimerTask;


public class showupcomingridedetails extends ActionBarActivity implements View.OnClickListener {


    private getindividualriddetailsetask myrideTask = null;
    JSONParser jsonParser = new JSONParser();
    TextView ridefromheader, todayortomorrowheader, timeofday, rideownernamevalue, rideowneremailvalue, rideownerphonevalue;
    getindividualriddetailsetask individualridestask;
    Button startride, endride, estimateride, exitride;
    ProgressBar bar;
    LinearLayout detailform;
    CustomerAdapter customerlistadapter;
    GridView mGridView;
    private Location previousLocation = null;
    StringBuilder locationstring = new StringBuilder();
    LocationManager locationManager;
    TimerTask hourlyTask;
    static final int PICK_CABPROVIDER_RESULT = 1;


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
        locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);

    }
    public void timerstart()
    {
    Timer timer = new Timer();
    hourlyTask = new TimerTask() {
        @Override
        public void run() {
            ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
            if (!cd.isConnectingToInternet()) {

            } else {
                if (!locationstring.toString().isEmpty()) {
                    locationstring.deleteCharAt(locationstring.length() - 1);
                    new googledistancematrixapitask(locationstring.toString(),false).execute();
                    locationstring = new StringBuilder();
                }
            }
        }
    };
    timer.schedule(hourlyTask, 0l, 1000 * 3 * 60);
    }

    public void  timerstop()
    {
        if(hourlyTask !=null) {
            hourlyTask.cancel();
            new googledistancematrixapitask(locationstring.toString(),true).execute();
        }
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
                startActivityForResult(selectcabprovider,PICK_CABPROVIDER_RESULT);
                SharedPreferenceManager.setPreference("totaldistance", 0);
                Toast.makeText(showupcomingridedetails.this,
                        "Ride has started", Toast.LENGTH_LONG).show();
                Boolean isGPSEnabled = locationManager
                        .isProviderEnabled(LocationManager.GPS_PROVIDER);

                Boolean isNetworkEnabled = locationManager
                        .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                timerstart();
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_FINE);
                locationManager.requestLocationUpdates(locationManager.getBestProvider(criteria,true), 15000, 0, locationListener);
                break;
            case R.id.endride:
                /*int totaldistance = SharedPreferenceManager.getIntPreference("totaldistance");
                if(totaldistance != 0)
                {
                    locationstring.deleteCharAt(locationstring.length() - 1);
                    new googledistancematrixapitask(locationstring.toString()).execute();
                }*/

                locationManager.removeUpdates(locationListener);
                timerstop();
                Toast.makeText(showupcomingridedetails.this,
                        "Ride has ended", Toast.LENGTH_LONG).show();

                break;
            case R.id.estimeateride:
                break;
            case R.id.exitride:
                break;
        }
    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location newLocation)
        {
            locationstring.append(newLocation.getLatitude()).append(",").append(newLocation.getLongitude()).append("|");
            Log.v("location", "IN ON LOCATION CHANGE, lat=" + newLocation.getLatitude() + ", lon=" + newLocation.getLatitude());
        }

        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };


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

            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_searchshowinduvidualrides, menu);
        return true;
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
                String json = jsonParser.makeHttpRequest("http://radiant-peak-3095.herokuapp.com/googledistancematrixapicalculation", "POST",
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
            int totaldistance = SharedPreferenceManager.getIntPreference("totaldistance");
            totaldistance = totaldistance + Integer.parseInt(distance);
            SharedPreferenceManager.setPreference("totaldistance", totaldistance);
            if(mstopRides){
                Toast.makeText(showupcomingridedetails.this,
                   Integer.toString(totaldistance/1000), Toast.LENGTH_LONG).show();
            }


        }

        @Override
        protected void onCancelled() {

        }
    }
}
