package com.splitrides;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.splitrides.android.helper.JSONParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class completedridedetails extends ActionBarActivity {


    private getindividualcompletedridetask mycompletedrideTask = null;
    TextView base_fare,fare_distance,fare_time,total_fare,ridestart_time,rideend_time;
    JSONParser jsonParser = new JSONParser();
    ProgressBar bar;
    LinearLayout detailform;
    SplitRideAdapter splitridelistadapter;
    GridView mGridView;
    GridView mGridView_noresults;
    SampleAdapter_noresults  rideshare_noresults;
    getindividualcompletedridetask individualcompletedridestask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completedridedetails);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        base_fare = (TextView) findViewById(R.id.base_fare_value);
        fare_distance = (TextView)findViewById(R.id.fare_distance_value);
        fare_time = (TextView) findViewById(R.id.fare_time_value);
        total_fare = (TextView) findViewById(R.id.total_fare_value);
        ridestart_time = (TextView) findViewById(R.id.ridestart_time);
        rideend_time = (TextView) findViewById(R.id.rideend_time);
        bar = (ProgressBar) findViewById(R.id.completedridedetails_progress);
        detailform = (LinearLayout) findViewById(R.id.completedridedatashow);
        mGridView = (GridView)findViewById(android.R.id.list);
        mGridView_noresults = (GridView) findViewById(R.id.no_results_return_list);
        Bundle extras = getIntent().getExtras();
        String currentuniqueid = extras.getString("uniqueId");
        String uniqueId = (currentuniqueid!= null && !currentuniqueid.isEmpty())?extras.getString("uniqueId"): SharedPreferenceManager.getPreference("currentride_uniqueid");
        SharedPreferenceManager.setPreference("currentride_uniqueid", uniqueId);
        new getindividualcompletedridetask(uniqueId).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_completedridedetails, menu);
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

    private class SplitRideAdapter extends BaseAdapter {
        private List<splitfaredataobject> mSamples;
        public SplitRideAdapter(List<splitfaredataobject> myDataset) {
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
                convertView = getLayoutInflater().inflate(R.layout.splitfare_detail_list,
                        container, false);
            }
            ((TextView) convertView.findViewById(R.id.leg_value)).setText(Integer.toString(position+1));
            ((TextView) convertView.findViewById(R.id.fare_for_distance_value)).setText(getResources().getString(R.string.Rs) + " " + mSamples.get(position).getFareForThisLeg());
            ((TextView) convertView.findViewById(R.id.fare_for_time_value)).setText(getResources().getString(R.string.Rs)+" "+mSamples.get(position).getFareForTimeSpentInThisLeg());
            ((TextView) convertView.findViewById(R.id.partners_value)).setText(TextUtils.join(",",mSamples.get(position).getPartners()));
            return convertView;
        }
    }

    private class SampleAdapter_noresults extends BaseAdapter {
        private List<ridedata> mSamples;
        public SampleAdapter_noresults(List<ridedata> myDataset) {
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
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.noresults_searchrides,
                        container, false);
            }
            TextView noresultsview = (TextView) convertView.findViewById(R.id.text1);
            noresultsview.setText(mSamples.get(position).getNoresults());
            // Lookup view for data population

            return convertView;
        }
    }


    public class getindividualcompletedridetask extends AsyncTask<Void, Void, completedrideobject > {

        private final String mUniqueId;

        getindividualcompletedridetask(String UniqueId) {
            mUniqueId = UniqueId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            bar.setVisibility(View.VISIBLE);

        }
        @Override
        protected completedrideobject doInBackground(Void... param) {
            completedrideobject info = null;
            List<splitfaredataobject> splitrideobjectlist = new ArrayList<splitfaredataobject>();
            String status = null;

            try {
                JSONObject params = new JSONObject();
                params.put("uniqueId", mUniqueId);
                // getting JSON string from URL
                String json = jsonParser.makeHttpRequest(mainurl.geturl() +"getCompletedRideDetails", "POST",
                        params);



                JSONObject jObj = new JSONObject(json);
                if(jObj != null){
                    if (jObj.has("completedRides")) {
                        JSONObject completedride = jObj.getJSONObject("completedRides");
                        if(completedride.has("someMatrix")) {
                            JSONArray somematrix = completedride.getJSONArray("someMatrix");
                            for (int i = 0; i < somematrix.length(); i++) {
                                JSONObject splitfaredetails = somematrix.getJSONObject(i);
                                JSONArray partnerarray = splitfaredetails.getJSONArray("partners");
                                List<String> array = new ArrayList<String>();
                                for (int j = 0; j < partnerarray.length(); j++) {
                                     JSONObject partners = partnerarray.getJSONObject(j);
                                     array.add(partners.getString("name"));
                                }
                                splitfaredataobject dataobject = new splitfaredataobject(splitfaredetails.getString("fareForTimeSpentInThisLeg"), array, splitfaredetails.getString("fareForThisLeg"));
                                splitrideobjectlist.add(dataobject);
                            }
                        }
                        info = new completedrideobject(completedride.getString("uniqueId"),completedride.getString("totalFare"),completedride.getString("fareForTimeSpent"),completedride.getString("fareForDistanceTravelled"),completedride.getString("baseFare"),completedride.getString("rideEndedAt"),completedride.getString("rideStartedAt"),splitrideobjectlist);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            /*List<String> array = new ArrayList<String>();
            array.add("giri");
            splitfaredataobject dataobject = new splitfaredataobject("300", array, "400");
            splitrideobjectlist.add(dataobject);
            info = new completedrideobject("11111111","300","100","100","75","2015-07-18 20:22:32","2015-07-18 20:28:34",splitrideobjectlist);*/
            return info;
        }

        @Override
        protected void onPostExecute(final completedrideobject completedridedataobject) {
            bar.setVisibility(View.GONE);
            detailform.setVisibility((completedridedataobject!=null)?View.VISIBLE:View.GONE);
            if(completedridedataobject != null ){
                base_fare.setText(getResources().getString(R.string.Rs)+" "+completedridedataobject.getBaseFare());
                fare_distance.setText(getResources().getString(R.string.Rs)+" "+completedridedataobject.getPfareForDistanceTravelled());
                fare_time.setText(getResources().getString(R.string.Rs)+" "+completedridedataobject.getFareForTimeSpent());
                total_fare.setText(getResources().getString(R.string.Rs)+" "+completedridedataobject.getTotalFare());
                String timeofrides_start = completedridedataobject.getRideStartedAt().split(" ")[1];
                String timeofrides_end = completedridedataobject.getRideEndedAt().split(" ")[1];
                ridestart_time.setText(timeofrides_start);
                rideend_time.setText(timeofrides_end);
                mGridView.setVisibility(!completedridedataobject.getListofsplitfare().isEmpty() ? View.VISIBLE : View.GONE);
                mGridView_noresults.setVisibility(!completedridedataobject.getListofsplitfare().isEmpty() ? View.GONE : View.VISIBLE);
                if(!completedridedataobject.getListofsplitfare().isEmpty()) {
                    splitridelistadapter = new SplitRideAdapter(completedridedataobject.getListofsplitfare());
                    mGridView.setAdapter(splitridelistadapter);
                }
                else{
                    List<ridedata> noresultsarray = new ArrayList<ridedata>();
                    ridedata info = new ridedata(null,null,null);
                    info.setNoresults("No rides shared currently.");
                    noresultsarray.add(info);
                    rideshare_noresults = new SampleAdapter_noresults(noresultsarray);
                    mGridView_noresults.setAdapter(rideshare_noresults);
                }
            }
        }

        @Override
        protected void onCancelled() {
        }
    }

}
