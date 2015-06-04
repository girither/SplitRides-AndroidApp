package com.example.foodiepipe.foodiepipe;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
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


public class Searchyourrides extends ActionBarActivity implements AdapterView.OnItemClickListener, View.OnClickListener {
    private ProgressDialog pDialog;
    private getmyridetask myrideTask = null;
    JSONParser jsonParser = new JSONParser();
    private GridView mGridView;
    private getmyridetask mMyrideTask = null;
    private LinearLayout searchresultsform;
    private LinearLayout noresultsform;
    private Button postridebutton;
    SampleAdapter myridedataadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchyourrides);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        mGridView = (GridView)findViewById(android.R.id.list);
        mMyrideTask = new getmyridetask();
        mMyrideTask.execute((Void) null);
        searchresultsform = (LinearLayout)findViewById(R.id.searchresults_form);
        noresultsform = (LinearLayout)findViewById(R.id.noresults_form);
        postridebutton = (Button)findViewById(R.id.postride_button);
        postridebutton.setOnClickListener(this);
        mGridView.setOnItemClickListener(this);


        // specify an adapter (see also next example)
    }

    @Override
    public void onClick(View view) {
        ConnectionDetector cd = new ConnectionDetector(Searchyourrides.this.getApplicationContext());

        // Check if Internet present
        if (!cd.isConnectingToInternet()) {
            // Internet Connection is not present
            Toast.makeText(Searchyourrides.this,
                    "Internet Connection Error Please connect to working Internet connection", Toast.LENGTH_LONG).show();
            // stop executing code by return
            return;
        }
        switch(view.getId()) {
            case R.id.postride_button:
                Intent postrides = new Intent(Searchyourrides.this,Postyourrides.class);
                startActivity(postrides);
                break;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_searchyourrides, menu);
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> container, View view, int position, long id) {
        Intent searchrides = new Intent(Searchyourrides.this,Searchridessourcedestination.class);
        ridedata Ridedata = (ridedata)myridedataadapter.getItem(position);
        searchrides.putExtra("source",Ridedata.getSource());
        searchrides.putExtra("destination",Ridedata.getDestination());
        searchrides.putExtra("timeChoice",Ridedata.getTodayortomorrow());
        SharedPreferenceManager.setPreference("myrideId", Ridedata.getRideId());
        startActivity(searchrides);
        overridePendingTransition(R.animator.activity_in, R.animator.activity_out);
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
                convertView = Searchyourrides.this.getLayoutInflater().inflate(R.layout.myride_details,
                        container, false);
            }
            String datetimeOfRides = mSamples.get(position).getDate();
            String dateOfRides = datetimeOfRides.split("T")[0];
            String timeofrides = datetimeOfRides.split("T")[1];
            timeofrides = timeofrides.split(".000Z")[0];
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String currentDate = sdf.format(cal.getTime());
            if(currentDate.equals(dateOfRides))
            {
                todayortomorrow = "Today        ";
            }
            else
            {
                todayortomorrow = "Tomorrow";
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

    @Override
    public void onBackPressed() {
        // finish() is called in super: we only override this method to be able to override the transition
        super.onBackPressed();

        overridePendingTransition(R.animator.back_in, R.animator.back_out);
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
    public class getmyridetask extends AsyncTask<Void, Void, List<ridedata>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Searchyourrides.this);
            pDialog.setMessage("fetching your rides...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
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
                String json = jsonParser.makeHttpRequest("http://radiant-peak-3095.herokuapp.com/getMyRides", "POST",
                        params);



                JSONObject jObj = new JSONObject(json);
                if(jObj != null){
                    JSONArray rides = jObj.getJSONArray("rides");
                    for(int i=0; i<rides.length(); i++){
                        JSONObject rideindividualdata = rides.getJSONObject(i);
                        ridedata info = new ridedata(rideindividualdata.getString("source"),rideindividualdata.getString("destination"),rideindividualdata.getString("date"));
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
            myrideTask = null;
            pDialog.dismiss();
            searchresultsform.setVisibility(!ridedataArray.isEmpty()?View.VISIBLE:View.GONE);
            noresultsform.setVisibility(!ridedataArray.isEmpty()?View.GONE:View.VISIBLE);
            if(!ridedataArray.isEmpty()){
                myridedataadapter = new SampleAdapter(ridedataArray);
               mGridView.setAdapter(myridedataadapter);
            }
        }

        @Override
        protected void onCancelled() {
            myrideTask = null;
        }
    }
}
