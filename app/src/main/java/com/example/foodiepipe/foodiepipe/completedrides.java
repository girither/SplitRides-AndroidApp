package com.example.foodiepipe.foodiepipe;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.foodpipe.android.helper.JSONParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class completedrides extends Fragment implements AdapterView.OnItemClickListener {

    JSONParser jsonParser = new JSONParser();
    private GridView mGridView,mGridView_noresults;
    private getcompletedridetask mcompletedrideTask = null;
    private LinearLayout completedridesform;
    private LinearLayout noresultsform;
    private ProgressBar bar;
    SampleAdapter mycompletedridedataadapter;
    SampleAdapter_noresults  myallridedataadapter_noresults;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onItemClick(AdapterView<?> container, View view, int position, long id) {
        completedrideobject completedrideobj = (completedrideobject)mycompletedridedataadapter.getItem(position);
        Intent getinduvidualcompletedrides = new Intent(getActivity(),completedridedetails.class);
        getinduvidualcompletedrides.putExtra("uniqueId", completedrideobj.getUniqueId());
        startActivity(getinduvidualcompletedrides);
        getActivity().overridePendingTransition(R.animator.activity_in, R.animator.activity_out);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_completedrides,container, false);
        bar = (ProgressBar) rootView.findViewById(R.id.completedrides_progress);
        mcompletedrideTask = new getcompletedridetask();
        mcompletedrideTask.execute((Void) null);
        mGridView = (GridView)rootView.findViewById(android.R.id.list);
        mGridView.setOnItemClickListener(this);
        mGridView_noresults = (GridView)rootView.findViewById(R.id.no_results_return_list);
        completedridesform = (LinearLayout)rootView.findViewById(R.id.show_completed_rides);
        noresultsform = (LinearLayout)rootView.findViewById(R.id.noridestoshow_form);
        return rootView;
    }

    private class SampleAdapter extends BaseAdapter {
        private List<completedrideobject> mSamples;
        public SampleAdapter(List<completedrideobject> myDataset) {
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
                convertView = getActivity().getLayoutInflater().inflate(R.layout.completedride_card_list,
                        container, false);
            }
            String datetimeOfRides_start = mSamples.get(position).getRideStartedAt();
            String dateOfRides_start = datetimeOfRides_start.split(" ")[0];
            String timeofrides_start = datetimeOfRides_start.split(" ")[1];
            String datetimeOfRides_end = mSamples.get(position).getRideStartedAt();
            String timeofrides_end = datetimeOfRides_end.split(" ")[1];
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String currentDate = sdf.format(cal.getTime());
            Calendar nextdaycal = Calendar.getInstance();
            nextdaycal.add(Calendar.DATE, 1);
            SimpleDateFormat sdftomorrow = new SimpleDateFormat("yyyy-MM-dd");
            String tomorrowDate = sdftomorrow.format(nextdaycal.getTime());
            if(currentDate.equals(dateOfRides_start))
            {
                todayortomorrow = "Today        ";
            }
            else if(tomorrowDate.equals(dateOfRides_start))
            {
                todayortomorrow = "Tomorrow";
            }
            else
            {
                todayortomorrow = dateOfRides_start;
            }
            mSamples.get(position).setTodayortomorrow(todayortomorrow);
            ((TextView) convertView.findViewById(R.id.day_header)).setText(todayortomorrow);
            ((TextView) convertView.findViewById(R.id.start_time_value)).setText(
                    timeofrides_start);
            ((TextView) convertView.findViewById(R.id.base_fare_value)).setText(
                    mSamples.get(position).getBaseFare());
            ((TextView) convertView.findViewById(R.id.fare_distance_value)).setText(
                    mSamples.get(position).getPfareForDistanceTravelled());
            ((TextView) convertView.findViewById(R.id.fare_time_value)).setText(
                    mSamples.get(position).getFareForTimeSpent());
            ((TextView) convertView.findViewById(R.id.total_fare_value)).setText(
                    mSamples.get(position).getTotalFare());

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
                convertView = getActivity().getLayoutInflater().inflate(R.layout.noresults_searchrides,
                        container, false);
            }
            TextView noresultsview = (TextView) convertView.findViewById(R.id.text1);
            noresultsview.setText(mSamples.get(position).getNoresults());
            // Lookup view for data population

            return convertView;
        }
    }

    public class getcompletedridetask extends AsyncTask<Void, Void, List<completedrideobject>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            bar.setVisibility(View.VISIBLE);

        }
        @Override
        protected List<completedrideobject> doInBackground(Void... param) {
            List<completedrideobject> completedridedataArray = new ArrayList<completedrideobject>();
            // Building Parameters
            /*List<NameValuePair> params = new ArrayList<NameValuePair>();

            // post album id, song id as GET parameters
            params.add(new BasicNameValuePair("name", mName));
            params.add(new BasicNameValuePair("email", mEmail));
            params.add(new BasicNameValuePair("password", mPassword));
            params.add(new BasicNameValuePair("profile", mProfile));*/
            try {
                JSONObject params = new JSONObject();

                // getting JSON string from URL
                String json = jsonParser.makeHttpRequest("http://radiant-peak-3095.herokuapp.com/getride_completedrides", "POST",
                        params);


                // completedrideobject info = new completedrideobject("112345678","300","34","239","60","2015-07-18 20:28:34","2015-07-18 20:22:32");
                // completedridedataArray.add(info);
                JSONObject jObj = new JSONObject(json);
                if(jObj != null){
                    JSONArray completedrides = jObj.getJSONArray("completedRides");
                    for(int i=0; i<completedrides.length(); i++){
                        JSONObject completedrideindividualdata = completedrides.getJSONObject(i);
                        completedrideobject info = new completedrideobject(completedrideindividualdata.getString("uniqueId"),completedrideindividualdata.getString("totalFare"),completedrideindividualdata.getString("fareForTimeSpent"),completedrideindividualdata.getString("fareForDistanceTravelled"),completedrideindividualdata.getString("baseFare"),completedrideindividualdata.getString("rideEndedAt"),completedrideindividualdata.getString("rideStartedAt"));
                        completedridedataArray.add(info);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return completedridedataArray;
        }

        @Override
        protected void onPostExecute(final List<completedrideobject> ridedataArray) {
            mcompletedrideTask = null;
            bar.setVisibility(View.GONE);
            completedridesform.setVisibility(!ridedataArray.isEmpty()?View.VISIBLE:View.GONE);
            noresultsform.setVisibility(!ridedataArray.isEmpty()?View.GONE:View.VISIBLE);
            if(!ridedataArray.isEmpty()){
                mycompletedridedataadapter = new SampleAdapter(ridedataArray);
                mGridView.setAdapter(mycompletedridedataadapter);
            }
            else
            {
                List<ridedata> noresultsarray = new ArrayList<ridedata>();
                ridedata info = new ridedata(null,null,null);
                info.setNoresults("No rides to show currently.");
                noresultsarray.add(info);
                myallridedataadapter_noresults = new SampleAdapter_noresults(noresultsarray);
                mGridView_noresults.setAdapter(myallridedataadapter_noresults);
            }
        }

        @Override
        protected void onCancelled() {
            mcompletedrideTask = null;
        }
    }




}