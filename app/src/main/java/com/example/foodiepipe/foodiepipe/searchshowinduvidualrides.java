package com.example.foodiepipe.foodiepipe;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodiepipe.foodiepipe.util.ImageLoadTask;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.widget.ProfilePictureView;
import com.foodpipe.android.helper.ConnectionDetector;
import com.foodpipe.android.helper.JSONParser;
import com.ms.square.android.expandabletextview.ExpandableTextView;

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
    ExpandableTextView ridefromheader_source_expander,ridefromheader_destination_expander;
    TextView todayortomorrowheader,timeofday,rideownernamevalue,rideowneremailvalue,rideownerphonevalue;
    getindividualriddetailsetask individualridestask;
    TextView requestreject;
    Button sendrequesttojoinride,requestalreadysent,estimaterideindividual;
    ProgressBar bar;
    LinearLayout detailform;
    CustomerAdapter customerlistadapter;
    private ProgressDialog pDialog;
    static final int PICK_CABPROVIDER_RESULT_FROMESIMATE = 2;
    ExpandableHeightGridView mGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchshowinduvidualrides);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SharedPreferenceManager.setApplicationContext(getApplicationContext());
        ridefromheader_source_expander = (ExpandableTextView) findViewById(R.id.expand_text_view);
        ridefromheader_destination_expander= (ExpandableTextView)findViewById(R.id.expand_text_view_destination);
        todayortomorrowheader = (TextView)findViewById(R.id.rideday);
        timeofday = (TextView)findViewById(R.id.ridetime);
        rideownernamevalue = (TextView)findViewById(R.id.rideownernamevalue);
        rideowneremailvalue = (TextView)findViewById(R.id.rideowneremailvalue);
        rideownerphonevalue = (TextView)findViewById(R.id.rideownerphonenumbervalue);
        bar = (ProgressBar)findViewById(R.id.searchindividualrides_progress);
        detailform = (LinearLayout)findViewById(R.id.ridedatashow);
        mGridView = (ExpandableHeightGridView)findViewById(R.id.customer_list);
        mGridView.setExpanded(true);
        sendrequesttojoinride = (Button)findViewById(R.id.send_request_joinride);
        estimaterideindividual = (Button)findViewById(R.id.estimate_ride_searchindividual);
        estimaterideindividual.setOnClickListener(this);
        sendrequesttojoinride.setOnClickListener(this);
        requestalreadysent =(Button)findViewById(R.id.request_alreadysent);
        requestreject = (TextView)findViewById(R.id.request_rejected_text);
        Bundle extras = getIntent().getExtras();
        String rideId = (!extras.getString("rideId").isEmpty())?extras.getString("rideId"):SharedPreferenceManager.getPreference("owner_rideid");
        SharedPreferenceManager.setPreference("owner_rideid",rideId);
        new getindividualriddetailsetask(rideId).execute();


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
            case R.id.estimate_ride_searchindividual:
                Intent selectcabprovider_estimate = new Intent(searchshowinduvidualrides.this,cabproviderselction.class);
                startActivityForResult(selectcabprovider_estimate,PICK_CABPROVIDER_RESULT_FROMESIMATE);
                break;
            case R.id.send_request_joinride:
                new sendrequesttojoinridetask(SharedPreferenceManager.getPreference("owner_rideid"),SharedPreferenceManager.getPreference("owner_customernumber"),SharedPreferenceManager.getPreference("customerNumber"),SharedPreferenceManager.getPreference("myrideId")).execute();
                break;
        }
    }

    private class CustomerAdapter extends BaseAdapter {
        private List<customer> mSamples;
        private StringBuilder friendsCountMsg = new StringBuilder();
        private Integer friendsCount = 0;
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
            ((ProfilePictureView)convertView.findViewById(R.id.profilePicture)).setProfileId(mSamples.get(position).getProfileId());
            if(mSamples.get(position).getCustomernumber().equals(SharedPreferenceManager.getPreference("owner_customernumber"))) {
                ((TextView) convertView.findViewById(R.id.role_value)).setText("Owner");
            }
            else{
                ((TextView) convertView.findViewById(R.id.role_value)).setText("Partner");
            }

            //Show profile picture from user social profile.
            ImageView profileImg = (ImageView)convertView.findViewById(R.id.googleProfilePicture);
            ProfilePictureView fbProfileImg = (ProfilePictureView) convertView.findViewById(R.id.profilePicture);
            if(mSamples.get(position).getProfileId().startsWith("http")) {
                ImageLoadTask loadImg = new ImageLoadTask(mSamples.get(position).getProfileId(), profileImg);
                loadImg.execute();
                profileImg.setVisibility(View.VISIBLE);
                fbProfileImg.setVisibility(View.INVISIBLE);
            } else {
                fbProfileImg.setProfileId(mSamples.get(position).getProfileId());
                profileImg.setVisibility(View.INVISIBLE);
                fbProfileImg.setVisibility(View.VISIBLE);
            }

            //Setting mutual friends details
            if(mSamples.get(position).getMutualFriendsCount() != null) {
                TextView friendsMsgView = (TextView)convertView.findViewById(R.id.commonFriendsCount);

                if(friendsMsgView != null && mSamples != null && mSamples.get(position) != null) {
                    friendsCount = mSamples.get(position).getMutualFriendsCount();

                    friendsCountMsg.append(friendsCount.toString());
                    friendsCountMsg.append(" mutual friend");
                    if(friendsCount > 1 || friendsCount == 0) {
                        friendsCountMsg.append("s");
                    }

                    friendsCountMsg.append(" on facebook");

                    friendsMsgView.setText(friendsCountMsg.toString());
                    friendsCountMsg.delete(0, friendsCountMsg.length());
                    friendsCount = 0;
                }
            }

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
                    latlongbuilder.append("geo:").append(droplatlongposition).append("?q=").append(droplatlongposition).append("(Droppoint)");
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_searchshowinduvidualrides, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if(requestCode == PICK_CABPROVIDER_RESULT_FROMESIMATE){
            if (resultCode == Activity.RESULT_OK) {
                Bundle extras = data.getExtras();
                String cabprovidervalue = extras.getString("cabprovider");
                new estimateridetask(cabprovidervalue,SharedPreferenceManager.getPreference("owner_rideid"),Integer.toString(1),SharedPreferenceManager.getPreference("myrideId")).execute();

            }
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
            pDialog = new ProgressDialog(searchshowinduvidualrides.this);
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
                params.put("rRideId", mrrideId);
                params.put("serviceProvider",mcabProvider.toLowerCase());
                params.put("city","bengaluru");
                // getting JSON string from URL
                String json = jsonParser.makeHttpRequest(mainurl.geturl() +"estimateRide", "POST",
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
            Integer friendsCount = 0;
            String userProfileId = null;

            try {
                JSONObject params = new JSONObject();
                params.put("rideId", mRideId);
                // getting JSON string from URL
                String json = jsonParser.makeHttpRequest(mainurl.geturl() +"getRideDetails", "POST",
                        params);



                JSONObject jObj = new JSONObject(json);
                if(jObj != null){
                    if(jObj.has("jride")) {
                        JSONObject ride = jObj.getJSONObject("jride");
                        JSONArray customerdata = jObj.getJSONArray("builtDetails");
                        List<customer> customerlistdata = new ArrayList<customer>();
                        status = "";
                        if(ride.has("requestMatrix")) {
                            if(!ride.isNull("requestMatrix")) {
                                JSONObject requestmatrix = ride.getJSONObject("requestMatrix");
                                if (requestmatrix.has(SharedPreferenceManager.getPreference("customerNumber"))) {
                                    JSONObject requestobject = requestmatrix.getJSONObject(SharedPreferenceManager.getPreference("customerNumber"));
                                    status = requestobject.getString("status");
                                }
                            }
                        }
                        for(int i=0; i<customerdata.length(); i++){
                            JSONObject customerindividualdata = customerdata.getJSONObject(i);

                            userProfileId = customerindividualdata.has("profileId")?customerindividualdata.getString("profileId"):"";
                            if(!userProfileId.equals("")) {
                                friendsCount = getMutualFriendsCount(userProfileId);
                            }

                            customer customeradapterdata = new customer(customerindividualdata.getString("name"),customerindividualdata.getString("email"),customerindividualdata.getString("phoneNumber"),customerindividualdata.getString("pickUplatLng"),customerindividualdata.getString("customersDropLatLngMatrix"),customerindividualdata.has("profileId")?customerindividualdata.getString("profileId"):"",customerindividualdata.getString("customerNumber"), friendsCount);
                            customerlistdata.add(customeradapterdata);
                        }
                        info = new ridedata(ride.getString("source"), ride.getString("destination"), ride.getString("date"),status,customerlistdata,ride.getString("jrId"));
                        SharedPreferenceManager.setPreference("owner_customernumber",ride.getString("ownerCustomerNumber"));
                    }
                    else if(jObj.has("ride")){
                        JSONObject ride = jObj.getJSONObject("ride");
                        JSONObject customerdata = jObj.getJSONObject("owner");
                        StringBuilder latlongbuilder = new StringBuilder();
                        latlongbuilder.append(ride.getString("pickUpLat")).append(",").append(ride.getString("pickUpLng"));
                        StringBuilder latlongbuilder_droppoint = new StringBuilder();
                        latlongbuilder_droppoint.append(ride.getString("dropLat")).append(",").append(ride.getString("dropLng"));

                        userProfileId = customerdata.has("profileId")?customerdata.getString("profileId"):"";
                        if(!userProfileId.equals("")) {
                            friendsCount = getMutualFriendsCount(userProfileId);
                        }

                        customer customeradapterdata = new customer(customerdata.getString("name"),customerdata.getString("email"),ride.getString("phoneNumber"),latlongbuilder.toString(),latlongbuilder_droppoint.toString(),customerdata.has("profileId")?customerdata.getString("profileId"):"",customerdata.getString("customerNumber"), friendsCount);
                        List<customer> customerlistdata = new ArrayList<customer>();
                        status = "";
                        if(ride.has("requestMatrix")) {
                            if(!ride.isNull("requestMatrix")) {
                                JSONObject requestmatrix = ride.getJSONObject("requestMatrix");
                                if (requestmatrix.has(SharedPreferenceManager.getPreference("customerNumber")) && !requestmatrix.isNull(SharedPreferenceManager.getPreference("customerNumber"))) {
                                    JSONObject requestobject = requestmatrix.getJSONObject(SharedPreferenceManager.getPreference("customerNumber"));
                                    status = requestobject.getString("status");
                                }
                            }
                        }
                        customerlistdata.add(customeradapterdata);
                        info = new ridedata(ride.getString("source"), ride.getString("destination"), ride.getString("date"),status,customerlistdata, ride.getString("jrId"));
                        SharedPreferenceManager.setPreference("owner_customernumber",ride.getString("customerNumber"));
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
                ridefromheader_source_expander.setText(ridedataobject.getSource());
                ridefromheader_destination_expander.setText(ridedataobject.getDestination());
                String dateOfRides = ridedataobject.getDate().split("T")[0];
                String timeofrides = ridedataobject.getDate().split("T")[1];
                timeofrides = timeofrides.split(".000Z")[0];
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String currentDate = sdf.format(cal.getTime());
                String todayortomorrow = (currentDate.equals(dateOfRides))?"Today":"Tomorrow";
                todayortomorrowheader.setText(todayortomorrow);
                timeofday.setText(timeofrides);
                if (ridedataobject.getStatus() != null && (ridedataobject.getStatus().equals("requestsent") || ridedataobject.getStatus().equals("accepted")||ridedataobject.getStatus().equals("rejected"))) {
                    requestalreadysent.setVisibility(View.VISIBLE);
                    sendrequesttojoinride.setVisibility(View.GONE);
                } else {
                    requestalreadysent.setVisibility(View.GONE);
                    sendrequesttojoinride.setVisibility(View.VISIBLE);
                }
                /*if(ridedataobject.getStatus() != null )
                {
                    // requestreject.setVisibility(View.VISIBLE);
                }*/
                customerlistadapter = new CustomerAdapter(ridedataobject.getCustomerlistdata());
                mGridView.setAdapter(customerlistadapter);
                customerlistadapter.notifyDataSetChanged();
            }
        }

        @Override
        protected void onCancelled() {
            individualridestask = null;
        }

        private Integer getMutualFriendsCount(String profileId) {
            Integer mutualCount = null;

            Bundle paramsObject = new Bundle();
            paramsObject.putString("fields", "context.fields(mutual_friends)");
            JSONObject responseJSON = null;
            AccessToken fbAccessToken = AccessToken.getCurrentAccessToken();

            if(fbAccessToken != null) {
                String fbUserId = fbAccessToken.getUserId();

                if(!fbUserId.equals(profileId)) {
                    GraphResponse gr = new GraphRequest(
                            fbAccessToken,
                            "/" + profileId,
                            paramsObject,
                            HttpMethod.GET,
                            new GraphRequest.Callback() {
                                public void onCompleted(GraphResponse graphResponse) {
                                    Log.d("Mutual Friend Data : ", graphResponse.getJSONObject().toString());
                                }
                            }
                    ).executeAndWait();

                    try {
                        responseJSON = gr.getJSONObject().getJSONObject("context");
                        responseJSON = responseJSON.getJSONObject("mutual_friends");
                        responseJSON = responseJSON.getJSONObject("summary");
                        mutualCount = Integer.parseInt(responseJSON.getString("total_count"));
                    } catch (Exception e) {
                        Log.e("Error occured :", e.getMessage());
                    }
                }
            }

            return mutualCount;
        }
    }
    public class sendrequesttojoinridetask extends AsyncTask<Void, Void, Boolean > {

        private final String mRideId;
        private final String mownerCustomerNumber;
        private final String mrequestingCustomerNumber;
        private final String mrRideId;
        String data = "";

        sendrequesttojoinridetask(String RideId,String ownerCustomerNumber,String requestingCustomerNumber,String rRideId ) {
            mRideId = RideId;
            mownerCustomerNumber= ownerCustomerNumber;
            mrequestingCustomerNumber = requestingCustomerNumber;
            mrRideId = rRideId;
        }



        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(searchshowinduvidualrides.this);
            pDialog.setMessage("Sending request to join ride...");
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
                params.put("ownerCustomerNumber", mownerCustomerNumber);
                params.put("requestingCustomerNumber", mrequestingCustomerNumber);
                params.put("rRideId", mrRideId);
                // getting JSON string from URL
                String json = jsonParser.makeHttpRequest(mainurl.geturl() +"sendRequestToJoinTheRideOrJoinedRide", "POST",
                        params);



                JSONObject jObj = new JSONObject(json);
                if(jObj != null) {
                    if(jObj.has("success")) {
                        String message = jObj.getString("success");
                        return true;
                    }
                    else if(jObj.has("failure")) {
                        data = jObj.getString("message");
                        return false;
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            data =  "Something went wrong while sending request. Please try again";
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            pDialog.dismiss();
            if (success) {
                requestalreadysent.setVisibility(View.VISIBLE);
                sendrequesttojoinride.setVisibility(View.GONE);
                Toast.makeText(searchshowinduvidualrides.this,
                        "Request is sent succesfully", Toast.LENGTH_LONG).show();
            } else {
                requestalreadysent.setVisibility(View.GONE);
                sendrequesttojoinride.setVisibility(View.VISIBLE);
                Toast.makeText(searchshowinduvidualrides.this,
                        data, Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected void onCancelled() {

        }
    }
}
