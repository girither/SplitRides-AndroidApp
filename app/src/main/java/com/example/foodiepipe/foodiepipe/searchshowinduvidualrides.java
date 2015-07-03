package com.example.foodiepipe.foodiepipe;

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


public class searchshowinduvidualrides extends ActionBarActivity implements View.OnClickListener {

    private getindividualriddetailsetask myrideTask = null;
    JSONParser jsonParser = new JSONParser();
    TextView ridefromheader,todayortomorrowheader,timeofday,rideownernamevalue,rideowneremailvalue,rideownerphonevalue;
    getindividualriddetailsetask individualridestask;
    Button sendrequesttojoinride,requestalreadysent;
    ProgressBar bar;
    LinearLayout detailform;
    CustomerAdapter customerlistadapter;
    GridView mGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchshowinduvidualrides);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ridefromheader = (TextView)findViewById(R.id.rideFromTextHeader);
        todayortomorrowheader = (TextView)findViewById(R.id.rideday);
        timeofday = (TextView)findViewById(R.id.ridetime);
        rideownernamevalue = (TextView)findViewById(R.id.rideownernamevalue);
        rideowneremailvalue = (TextView)findViewById(R.id.rideowneremailvalue);
        rideownerphonevalue = (TextView)findViewById(R.id.rideownerphonenumbervalue);
        bar = (ProgressBar)findViewById(R.id.searchindividualrides_progress);
        detailform = (LinearLayout)findViewById(R.id.ridedatashow);
        mGridView = (GridView)findViewById(android.R.id.list);
        sendrequesttojoinride = (Button)findViewById(R.id.send_request_joinride);
        sendrequesttojoinride.setOnClickListener(this);
        requestalreadysent =(Button)findViewById(R.id.request_alreadysent);
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
            case R.id.send_request_joinride:
                Bundle extras = getIntent().getExtras();
                String rideId = extras.getString("rideId");
                String rideFlag = extras.getString("rideFlag");
                String rideOwnerCustomerNumber = extras.getString("ownercustomernumber");
                detailform.setVisibility(View.GONE);
                new sendrequesttojoinridetask(rideId,rideFlag,rideOwnerCustomerNumber,SharedPreferenceManager.getPreference("customerNumber"),SharedPreferenceManager.getPreference("myrideId")).execute();
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
                convertView = searchshowinduvidualrides.this.getLayoutInflater().inflate(R.layout.customer_detail_list,
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_searchshowinduvidualrides, menu);
        return true;
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
            individualridestask = null;
            bar.setVisibility(View.GONE);
            detailform.setVisibility(View.VISIBLE);
            if(price != null){

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
                            customer customeradapterdata = new customer(customerindividualdata.getString("name"),customerindividualdata.getString("email"),customerindividualdata.getString("phoneNumber"),customerindividualdata.getString("latLng"));
                            customerlistdata.add(customeradapterdata);
                        }
                        info = new ridedata(ride.getString("source"), ride.getString("destination"), ride.getString("date"),customerlistdata,ride.getString("rideId"));
                    }
                    else{
                        JSONObject ride = jObj.getJSONObject("ride");
                        JSONObject customerdata = jObj.getJSONObject("owner");
                        customer customeradapterdata = new customer(customerdata.getString("name"),customerdata.getString("email"),ride.getString("phoneNumber"),ride.getString("latlong"));
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
            bar.setVisibility(View.VISIBLE);
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
                    String message = jObj.getString("message");
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
            bar.setVisibility(View.GONE);
            detailform.setVisibility(View.VISIBLE);
            if (success) {
                requestalreadysent.setVisibility(View.VISIBLE);
                sendrequesttojoinride.setVisibility(View.GONE);
                Toast.makeText(searchshowinduvidualrides.this,
                        "Request is sent succesfully", Toast.LENGTH_LONG).show();
            } else {
                requestalreadysent.setVisibility(View.GONE);
                sendrequesttojoinride.setVisibility(View.VISIBLE);
                Toast.makeText(searchshowinduvidualrides.this,
                        "Something went wrong while sending request. Please try again", Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected void onCancelled() {

        }
    }
}
