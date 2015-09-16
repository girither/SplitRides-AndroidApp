package com.splitrides;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.splitrides.util.ImageLoadTask;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.widget.ProfilePictureView;
import com.splitrides.android.helper.ConnectionDetector;
import com.splitrides.android.helper.JSONParser;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class notificationfragmentdetails extends ActionBarActivity implements View.OnClickListener {

    private getindividualnotificationdetailsetask mynotificationTask = null;
    JSONParser jsonParser = new JSONParser();
    ExpandableTextView ridefromheader_source_expander,ridefromheader_destination_expander;
    TextView todayortomorrowheader, timeofday, rideownernamevalue, rideowneremailvalue, rideownerphonevalue;
    getindividualnotificationdetailsetask individualnotificationstask;
    ProgressBar bar;
    Button acceptrequest, rejectrequest,estimaterequest,viewyourride;
    LinearLayout detailform;
    CustomerAdapter customerlistadapter;
    static final int PICK_CABPROVIDER_RESULT_FROMESIMATE = 2;
    private ProgressDialog pDialog;
    GridView mGridView;
    notificationdata globalnotificationdataobject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificationfragmentdetails);
        SharedPreferenceManager.setApplicationContext(getApplicationContext());
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ridefromheader_source_expander = (ExpandableTextView) findViewById(R.id.expand_text_view);
        ridefromheader_destination_expander= (ExpandableTextView)findViewById(R.id.expand_text_view_destination);
        todayortomorrowheader = (TextView) findViewById(R.id.rideday);
        timeofday = (TextView) findViewById(R.id.ridetime);
        viewyourride = (Button) findViewById(R.id.view_your_ride);
        rideownernamevalue = (TextView) findViewById(R.id.rideownernamevalue);
        rideowneremailvalue = (TextView) findViewById(R.id.rideowneremailvalue);
        rideownerphonevalue = (TextView) findViewById(R.id.rideownerphonenumbervalue);
        bar = (ProgressBar) findViewById(R.id.individualnotification_progress);
        detailform = (LinearLayout) findViewById(R.id.requestdatashow);
        mGridView = (GridView) findViewById(android.R.id.list);
        acceptrequest = (Button) findViewById(R.id.accept_request);
        rejectrequest = (Button) findViewById(R.id.reject_request);
        estimaterequest = (Button) findViewById(R.id.estimeaterequest);
        acceptrequest.setOnClickListener(this);
        rejectrequest.setOnClickListener(this);
        estimaterequest.setOnClickListener(this);
        viewyourride.setOnClickListener(this);
        Bundle extras = getIntent().getExtras();
        String requestId = (!extras.getString("requestId").isEmpty())?extras.getString("requestId"):SharedPreferenceManager.getPreference("currentrequestId");
        SharedPreferenceManager.setPreference("currentrequestId",requestId);
        new getindividualnotificationdetailsetask(requestId).execute();
    }

    public void onClick(View view) {

        ConnectionDetector cd = new ConnectionDetector(notificationfragmentdetails.this.getApplicationContext());

        // Check if Internet present
        if (!cd.isConnectingToInternet()) {
            // Internet Connection is not present
            Toast.makeText(notificationfragmentdetails.this,
                    "Internet Connection Error Please connect to working Internet connection", Toast.LENGTH_LONG).show();
            // stop executing code by return
            return;
        }
        switch(view.getId()) {
            case R.id.accept_request:
                new acceptorrejecttojoinridetask(SharedPreferenceManager.getPreference("currentrequestId"),"accept").execute();
                break;
            case R.id.reject_request:
                new acceptorrejecttojoinridetask(SharedPreferenceManager.getPreference("currentrequestId"),"reject").execute();
                break;
            case R.id.estimeaterequest:
                Intent selectcabprovider_estimate = new Intent(notificationfragmentdetails.this,cabproviderselction.class);
                startActivityForResult(selectcabprovider_estimate,PICK_CABPROVIDER_RESULT_FROMESIMATE);
                break;
            case R.id.view_your_ride:
                Intent getinduvidualrides = new Intent(this,showupcomingridedetails.class);
                getinduvidualrides.putExtra("rideId", globalnotificationdataobject.getOwnerrideid());
                startActivity(getinduvidualrides);
                overridePendingTransition(R.animator.activity_in, R.animator.activity_out);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == PICK_CABPROVIDER_RESULT_FROMESIMATE){
            if (resultCode == Activity.RESULT_OK) {
                Bundle extras = data.getExtras();
                String cabprovidervalue = extras.getString("cabprovider");
                new estimateridetask(cabprovidervalue,globalnotificationdataobject.getOwnerrideid(),Integer.toString(1),globalnotificationdataobject.getRideId()).execute();
            }
        }
    }


    public class estimateridetask extends AsyncTask<Void, Void,ratecardobject> {

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
            pDialog = new ProgressDialog(notificationfragmentdetails.this);
            pDialog.setMessage("Estimating Request...");
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
                DialogFragment rateFragment = new ratecardfragment(data,false);
                rateFragment.show(getFragmentManager(), "ratepicker");
            }
        }

        @Override
        protected void onCancelled() {

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
                convertView = notificationfragmentdetails.this.getLayoutInflater().inflate(R.layout.customer_detail_list,
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

            //Work History Details
            TextView workHistoryView = (TextView)convertView.findViewById(R.id.workHistory);
            TextView workHistoryLabel = (TextView)convertView.findViewById(R.id.work_label);
            if(mSamples.get(position).getWorkHistory() != null && mSamples.get(position).getWorkHistory().trim().length() > 0) {
                workHistoryView.setText(mSamples.get(position).getWorkHistory());
            } else {
                workHistoryView.setText("No Work Data");
            }

            //Education History Details
            TextView educationHistoryView = (TextView)convertView.findViewById(R.id.educationHistory);
            TextView educationHistoryLabel = (TextView)convertView.findViewById(R.id.education_label);
            if(mSamples.get(position).getEducationHistory() != null && mSamples.get(position).getEducationHistory().trim().length() > 0) {
                educationHistoryView.setText(mSamples.get(position).getEducationHistory());
            } else {
                educationHistoryView.setText("No Education Data");
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
            Integer friendsCount = 0;
            String userProfileId = null;
            String userWorkHistory = "";
            String userEducationHistory = "";

            try {
                JSONObject params = new JSONObject();
                params.put("requestId", mrequestId);

                // getting JSON string from URL
                String json = jsonParser.makeHttpRequest(mainurl.geturl() +"getRequestDetails", "POST",
                        params);



                JSONObject jObj = new JSONObject(json);
                if(jObj != null){
                    JSONObject ride = jObj.getJSONObject("requesterRide");
                    String ownerrideid = jObj.getString("ownerRide");
                    JSONObject customerindividualdata = jObj.getJSONObject("requesterCustomerProfile");
                    StringBuilder latlongbuilder = new StringBuilder();
                    latlongbuilder.append(ride.getString("pickUpLat")).append(",").append(ride.getString("pickUpLng"));
                    StringBuilder latlongbuilder_droppoint = new StringBuilder();
                    latlongbuilder_droppoint.append(ride.getString("dropLat")).append(",").append(ride.getString("dropLng"));


                    userProfileId = customerindividualdata.has("profileId")?customerindividualdata.getString("profileId"):"";
                    if(!userProfileId.equals("") && !userProfileId.startsWith("http")) {
                        friendsCount = getMutualFriendsCount(userProfileId);
                    }

                    userEducationHistory = customerindividualdata.has("educationHistory") ? customerindividualdata.getString("educationHistory") : "";
                    userWorkHistory = customerindividualdata.has("workHistory") ? customerindividualdata.getString("workHistory") : "";

                    List<customer> customerlistdata = new ArrayList<customer>();
                    customer customeradapterdata = new customer(customerindividualdata.getString("name"),customerindividualdata.getString("email"),ride.getString("phoneNumber"),latlongbuilder.toString() ,latlongbuilder_droppoint.toString(),customerindividualdata.has("profileId")?customerindividualdata.getString("profileId"):"",customerindividualdata.getString("customerNumber"), friendsCount, userWorkHistory, userEducationHistory);
                    customerlistdata.add(customeradapterdata);
                    info = new notificationdata(ride.getString("source"), ride.getString("destination"), ride.getString("date"),customerlistdata,ride.getString("rideId"),ownerrideid);
                    SharedPreferenceManager.setPreference("owner_customernumber",ride.getString("customerNumber"));

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
                try {
                    globalnotificationdataobject = notificationdataobject;
                    ridefromheader_source_expander.setText(notificationdataobject.getSource());
                    ridefromheader_destination_expander.setText(notificationdataobject.getDestination());
                    String dateOfRides = notificationdataobject.getDate().split("T")[0];
                    String timeofrides = notificationdataobject.getDate().split("T")[1];
                    SimpleDateFormat inFormat = new SimpleDateFormat("hh:mm aa");
                    SimpleDateFormat outFormat = new SimpleDateFormat("HH:mm");
                    timeofrides = inFormat.format(outFormat.parse(timeofrides));
                    timeofrides = timeofrides.split(".000Z")[0];
                    Calendar cal = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String currentDate = sdf.format(cal.getTime());
                    Calendar nextdaycal = Calendar.getInstance();
                    nextdaycal.add(Calendar.DATE, 1);
                    String todayortomorrow;
                    SimpleDateFormat sdftomorrow = new SimpleDateFormat("yyyy-MM-dd");
                    String tomorrowDate = sdftomorrow.format(nextdaycal.getTime());
                    if (currentDate.equals(dateOfRides)) {
                        todayortomorrow = "Today        ";
                    } else if (tomorrowDate.equals(dateOfRides)) {
                        todayortomorrow = "Tomorrow     ";
                    } else {
                        todayortomorrow = dateOfRides;
                    }
                    if (!(todayortomorrow.trim().equals("Today") || todayortomorrow.trim().equals("Tomorrow"))) {
                        acceptrequest.setVisibility(View.GONE);
                        rejectrequest.setVisibility(View.GONE);
                        estimaterequest.setVisibility(View.GONE);
                    }
                    todayortomorrowheader.setText(todayortomorrow);
                    timeofday.setText(timeofrides);
                    customerlistadapter = new CustomerAdapter(notificationdataobject.getCustomerlistdata());
                    mGridView.setAdapter(customerlistadapter);
                }
                catch (ParseException ex){

                }
            }
        }

        @Override
        protected void onCancelled() {
            individualnotificationstask = null;
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
    public class acceptorrejecttojoinridetask extends AsyncTask<Void, Void, Boolean > {


        private final String mrequestId;
        private final String mstatus;


        acceptorrejecttojoinridetask(String requestId,String status) {
            mrequestId= requestId;
            mstatus=status;
        }



        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(notificationfragmentdetails.this);
            pDialog.setMessage("Sending reponse...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
        @Override
        protected Boolean doInBackground(Void... param) {
            ridedata info = null;

            try {
                JSONObject params = new JSONObject();
                params.put("requestId", mrequestId);
                params.put("status",mstatus);
                String json = jsonParser.makeHttpRequest(mainurl.geturl() +"acceptOrRejectTheRequestToJoin", "POST",
                        params);



                JSONObject jObj = new JSONObject(json);
                if(jObj != null) {
                    String message = jObj.getString("success");
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
            if (success) {
                int count = SharedPreferenceManager.getIntPreference("notificationcount");
                if(count > 0) {
                    SharedPreferenceManager.setPreference("notificationcount", count - 1);
                }
                Intent requesttojoinedride = new Intent("requesttojoinedridenotification");
                LocalBroadcastManager.getInstance(getApplication()).sendBroadcast(requesttojoinedride);
                finish();
            } else {
                Toast.makeText(notificationfragmentdetails.this,
                        "Something went wrong while sending request. Please try again", Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected void onCancelled() {

        }
    }
}
