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
import android.widget.Button;
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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link allrides.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link allrides#newInstance} factory method to
 * create an instance of this fragment.
 */
public class allrides extends Fragment implements AdapterView.OnItemClickListener {

    JSONParser jsonParser = new JSONParser();
    private GridView mGridView;
    private getallridetask mallrideTask = null;
    private LinearLayout allridesform;
    private LinearLayout noresultsform;
    SampleAdapter myallridedataadapter;

    private ProgressBar bar;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onItemClick(AdapterView<?> container, View view, int position, long id) {
        ridedata rideobj = (ridedata)myallridedataadapter.getItem(position);
        Intent getinduvidualrides = new Intent(getActivity(),showupcomingridedetails.class);
        getinduvidualrides.putExtra("rideId", rideobj.getRideId());
        getinduvidualrides.putExtra("rideFlag", rideobj.getRideFlag());
        getinduvidualrides.putExtra("rideDate",rideobj.getDate());
        getinduvidualrides.putExtra("ownercustomernumber", rideobj.getRideownercustomernumber());
        startActivity(getinduvidualrides);
        getActivity().overridePendingTransition(R.animator.activity_in, R.animator.activity_out);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_allrides,container, false);
        bar = (ProgressBar) rootView.findViewById(R.id.allrides_progress);
        mallrideTask = new getallridetask();
        mallrideTask.execute((Void) null);
        mGridView = (GridView)rootView.findViewById(android.R.id.list);
        mGridView.setOnItemClickListener(this);
        allridesform = (LinearLayout)rootView.findViewById(R.id.show_all_rides);
        noresultsform = (LinearLayout)rootView.findViewById(R.id.noridestoshow_form);
        return rootView;
    }

    private class SampleAdapter extends BaseAdapter {
        private List<ridedata> mSamples;
        public SampleAdapter(List<ridedata> myDataset) {
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
                convertView = getActivity().getLayoutInflater().inflate(R.layout.myride_details,
                        container, false);
            }
            String datetimeOfRides = mSamples.get(position).getDate();
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
                todayortomorrow = "Today        ";
            }
            else if(tomorrowDate.equals(dateOfRides))
            {
                todayortomorrow = "Tomorrow";
            }
            else
            {
                todayortomorrow = dateOfRides;
            }
            mSamples.get(position).setTodayortomorrow(todayortomorrow);
            ((TextView) convertView.findViewById(android.R.id.content)).setText(timeofrides);
            ((TextView) convertView.findViewById(android.R.id.title)).setText(todayortomorrow);
            ((TextView) convertView.findViewById(android.R.id.text1)).setText(
                    mSamples.get(position).getSource());
            ((TextView) convertView.findViewById(android.R.id.text2)).setText(
                    mSamples.get(position).getDestination());
            return convertView;
        }
    }

    public class getallridetask extends AsyncTask<Void, Void, List<ridedata>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            bar.setVisibility(View.VISIBLE);

        }
        @Override
        protected List<ridedata> doInBackground(Void... param) {
            List<ridedata> ridedataArray = new ArrayList<ridedata>();
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
                String json = jsonParser.makeHttpRequest("http://radiant-peak-3095.herokuapp.com/getride_single", "POST",
                        params);



                JSONObject jObj = new JSONObject(json);
                if(jObj != null){
                    JSONArray rides = jObj.getJSONArray("rides");
                    for(int i=0; i<rides.length(); i++){
                        JSONObject rideindividualdata = rides.getJSONObject(i);
                        ridedata info = new ridedata(rideindividualdata.getString("source"),rideindividualdata.getString("destination"),rideindividualdata.getString("date"),rideindividualdata.getString("rideId"),"ride",rideindividualdata.getString("customerNumber"));
                        ridedataArray.add(info);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return ridedataArray;
        }

        @Override
        protected void onPostExecute(final List<ridedata> ridedataArray) {
            mallrideTask = null;
            bar.setVisibility(View.GONE);
            allridesform.setVisibility(!ridedataArray.isEmpty()?View.VISIBLE:View.GONE);
            noresultsform.setVisibility(!ridedataArray.isEmpty()?View.GONE:View.VISIBLE);
            if(!ridedataArray.isEmpty()){
                myallridedataadapter = new SampleAdapter(ridedataArray);
                mGridView.setAdapter(myallridedataadapter);
            }
        }

        @Override
        protected void onCancelled() {
            mallrideTask = null;
        }
    }

}