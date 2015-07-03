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

import com.foodpipe.android.helper.JSONParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class notificationfragmentdetails extends ActionBarActivity implements View.OnClickListener {

    private getindividualnotificationdetailsetask mynotificationTask = null;
    JSONParser jsonParser = new JSONParser();
    TextView ridefromheader, todayortomorrowheader, timeofday, rideownernamevalue, rideowneremailvalue, rideownerphonevalue;
    getindividualnotificationdetailsetask individualnotificationstask;
    ProgressBar bar;
    Button acceptrequest, rejectrequest;
    LinearLayout detailform;
    CustomerAdapter customerlistadapter;
    GridView mGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificationfragmentdetails);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ridefromheader = (TextView) findViewById(R.id.rideFromTextHeader);
        todayortomorrowheader = (TextView) findViewById(R.id.rideday);
        timeofday = (TextView) findViewById(R.id.ridetime);
        rideownernamevalue = (TextView) findViewById(R.id.rideownernamevalue);
        rideowneremailvalue = (TextView) findViewById(R.id.rideowneremailvalue);
        rideownerphonevalue = (TextView) findViewById(R.id.rideownerphonenumbervalue);
        bar = (ProgressBar) findViewById(R.id.individualnotification_progress);
        detailform = (LinearLayout) findViewById(R.id.ridedatashow);
        mGridView = (GridView) findViewById(android.R.id.list);
        acceptrequest = (Button) findViewById(R.id.accept_request);
        rejectrequest = (Button) findViewById(R.id.reject_request);
        acceptrequest.setOnClickListener(this);
        rejectrequest.setOnClickListener(this);
        Bundle extras = getIntent().getExtras();
        String requestId = extras.getString("requestId");
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
        new getindividualnotificationdetailsetask(requestId).execute();
    }

    public void onClick(View view) {

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
                convertView = notificationfragmentdetails.this.getLayoutInflater().inflate(R.layout.customer_detail_list,
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notificationfragmentdetails, menu);
        return true;
    }


    public class getindividualnotificationdetailsetask extends AsyncTask<Void, Void,notificationdata > {

        private final String mrequestId;



        getindividualnotificationdetailsetask(String requestId) {
            mrequestId = requestId;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            bar.setVisibility(View.VISIBLE);

        }
        @Override
        protected notificationdata doInBackground(Void... param) {
            notificationdata info = null;

            try {
                JSONObject params = new JSONObject();
                params.put("requestId", mrequestId);

                // getting JSON string from URL
                String json = jsonParser.makeHttpRequest("http://radiant-peak-3095.herokuapp.com/getRideDetails", "POST",
                        params);



                JSONObject jObj = new JSONObject(json);
                if(jObj != null){
                    JSONObject ride = jObj.getJSONObject("requesterRide");
                    JSONObject customerindividualdata = jObj.getJSONObject("requesterCustomerProfile");
                    List<customer> customerlistdata = new ArrayList<customer>();
                    customer customeradapterdata = new customer(customerindividualdata.getString("name"),customerindividualdata.getString("email"),ride.getString("phoneNumber"));
                    customerlistdata.add(customeradapterdata);
                    info = new notificationdata(ride.getString("source"), ride.getString("destination"), ride.getString("date"),customerlistdata,ride.getString("rideId"));

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return info;
        }

        @Override
        protected void onPostExecute(final notificationdata notificationdataobject) {
            individualnotificationstask = null;
            bar.setVisibility(View.GONE);
            detailform.setVisibility((notificationdataobject!=null)?View.VISIBLE:View.GONE);
            if(notificationdataobject != null ){
                String Ridefrom = new StringBuilder().append("Ride Request from ").append(notificationdataobject.getSource()).append(" to ").append(notificationdataobject.getDestination()).toString();
                ridefromheader.setText(Ridefrom);
                String dateOfRides = notificationdataobject.getDate().split("T")[0];
                String timeofrides = notificationdataobject.getDate().split("T")[1];
                timeofrides = timeofrides.split(".000Z")[0];
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String currentDate = sdf.format(cal.getTime());
                String todayortomorrow = (currentDate.equals(dateOfRides))?"Today":"Tomorrow";
                todayortomorrowheader.setText(todayortomorrow);
                timeofday.setText(timeofrides);
                customerlistadapter = new CustomerAdapter(notificationdataobject.getCustomerlistdata());
                mGridView.setAdapter(customerlistadapter);
            }
        }

        @Override
        protected void onCancelled() {
            individualnotificationstask = null;
        }
    }
}
