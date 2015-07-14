package com.example.foodiepipe.foodiepipe;

import android.app.Activity;
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


public class notificationfragment extends Fragment implements AdapterView.OnItemClickListener {

        JSONParser jsonParser = new JSONParser();
        private GridView mGridView,mGridView_noresults;
        private LinearLayout allnotificationform;
        OnDataChangedListener mCallback;
        private LinearLayout noresultsform;
        SampleAdapter myallnotificationdataadapter;
        SampleAdapter_noresults  myallnotificationdataadapter_noresults;
        private getallnotificationtask mnotificationTask = null;

        private ProgressBar bar;
        public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);

        }

        // Container Activity must implement this interface
        public interface OnDataChangedListener {
            public void onDataChanged(int number);
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);

            // This makes sure that the container activity has implemented
            // the callback interface. If not, it throws an exception
            try {
                mCallback = (OnDataChangedListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString()
                        + " must implement OnDataChangedListener");
            }
        }

        @Override
        public void onItemClick(AdapterView<?> container, View view, int position, long id) {
               notificationdata notificationobj = (notificationdata)myallnotificationdataadapter.getItem(position);
               Intent getnotificationdetails = new Intent(getActivity(),notificationfragmentdetails.class);
               getnotificationdetails.putExtra("requestId", notificationobj.getRequestId());
               startActivity(getnotificationdetails);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {

                View rootView = inflater.inflate(R.layout.fragment_notificationfragment,container, false);
                bar = (ProgressBar) rootView.findViewById(R.id.notification_progress);

                mGridView = (GridView)rootView.findViewById(android.R.id.list);
                mGridView.setOnItemClickListener(this);
                mGridView_noresults = (GridView)rootView.findViewById(R.id.no_results_return_list);
                allnotificationform = (LinearLayout)rootView.findViewById(R.id.show_all_notification);
                noresultsform = (LinearLayout)rootView.findViewById(R.id.noridestoshow_form);
                mnotificationTask = new getallnotificationtask();
                mnotificationTask.execute((Void) null);
                return rootView;
                }

        private class SampleAdapter extends BaseAdapter {
            private List<notificationdata> mSamples;
            public SampleAdapter(List<notificationdata> myDataset) {
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

        public class getallnotificationtask extends AsyncTask<Void, Void, List<notificationdata>> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                bar.setVisibility(View.VISIBLE);

            }
            @Override
            protected List<notificationdata> doInBackground(Void... param) {
                List<notificationdata> notificationdataArray = new ArrayList<notificationdata>();
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
                    String json = jsonParser.makeHttpRequest("http://radiant-peak-3095.herokuapp.com/getRequests", "POST",
                            params);



                    JSONObject jObj = new JSONObject(json);
                    if(jObj != null){
                        JSONArray notification = jObj.getJSONArray("requests");
                        for(int i=0; i<notification.length(); i++){
                            JSONObject notificationJSONObject = notification.getJSONObject(i);
                            notificationdata info = new notificationdata(notificationJSONObject.getString("requesterSource"),notificationJSONObject.getString("requesterDestination"),notificationJSONObject.getString("requesterDate"),notificationJSONObject.getString("requestId"));
                            notificationdataArray.add(info);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return notificationdataArray;
            }

            @Override
            protected void onPostExecute(final List<notificationdata> notificationdataArray) {
                mnotificationTask = null;
                bar.setVisibility(View.GONE);
                allnotificationform.setVisibility(!notificationdataArray.isEmpty()?View.VISIBLE:View.GONE);
                noresultsform.setVisibility(!notificationdataArray.isEmpty()?View.GONE:View.VISIBLE);
                if(!notificationdataArray.isEmpty()){
                    myallnotificationdataadapter = new SampleAdapter(notificationdataArray);
                    mGridView.setAdapter(myallnotificationdataadapter);
                    mCallback.onDataChanged(notificationdataArray.size());
                }
                else
                {
                    List<ridedata> noresultsarray = new ArrayList<ridedata>();
                    ridedata info = new ridedata(null,null,null);
                    info.setNoresults("No Notifications to show currently.");
                    noresultsarray.add(info);
                    myallnotificationdataadapter_noresults = new SampleAdapter_noresults(noresultsarray);
                    mGridView_noresults.setAdapter(myallnotificationdataadapter_noresults);
                    mCallback.onDataChanged(0);
                }
            }

            @Override
            protected void onCancelled() {
                mnotificationTask = null;
            }
        }
        }
