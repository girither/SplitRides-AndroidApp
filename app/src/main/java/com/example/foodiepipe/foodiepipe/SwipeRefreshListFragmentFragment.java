package com.example.foodiepipe.foodiepipe;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.foodpipe.android.helper.ConnectionDetector;
import com.foodpipe.android.helper.JSONParser;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SwipeRefreshListFragmentFragment extends SwipeRefreshListFragment {

    private static final String LOG_TAG = SwipeRefreshListFragmentFragment.class.getSimpleName();
    JSONParser jsonParser = new JSONParser();
    private static final int LIST_ITEM_COUNT = 20;
    DummyBackgroundTask searchridetask;
    ListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferenceManager.setApplicationContext(getActivity().getApplicationContext());

        // Notify the system to allow an options menu for this fragment.
        setHasOptionsMenu(true);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        ridedata rideobj = (ridedata)getListAdapter().getItem(position);
        if(rideobj.getNoresults() == null)
        {
            Intent getinduvidualrides = new Intent(getActivity(),searchshowinduvidualrides.class);
            getinduvidualrides.putExtra("rideId", rideobj.getRideId());
            getinduvidualrides.putExtra("ownercustomernumber", rideobj.getRideownercustomernumber());
            startActivity(getinduvidualrides);
        }
        getActivity().overridePendingTransition(R.animator.activity_in, R.animator.activity_out);
    }

    @Override
    public void onResume() {
        super.onResume();
        ConnectionDetector cd = new ConnectionDetector(getActivity().getApplicationContext());
        if (!cd.isConnectingToInternet()) {
            List<ridedata> noresultsarray = new ArrayList<ridedata>();
            ridedata info = new ridedata(null,null,null);
            info.setNoresults("No Internet Available Currently");
            noresultsarray.add(info);
            adapter = new noresultsadapter(getActivity(),noresultsarray);

            // Set the adapter between the ListView and its backing data.
            setListAdapter(adapter);

        }
        else {
            searchridetask = new DummyBackgroundTask();
            searchridetask.execute();
        }
    }

    // BEGIN_INCLUDE (setup_views)
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /**
         * Create an ArrayAdapter to contain the data for the ListView. Each item in the ListView
         * uses the system-defined simple_list_item_1 layout that contains one TextView.
         */
        searchridetask = new DummyBackgroundTask();
        searchridetask.execute();
        // BEGIN_INCLUDE (setup_refreshlistener)
        /**
         * Implement {@link SwipeRefreshLayout.OnRefreshListener}. When users do the "swipe to
         * refresh" gesture, SwipeRefreshLayout invokes
         * {@link SwipeRefreshLayout.OnRefreshListener#onRefresh onRefresh()}. In
         * {@link SwipeRefreshLayout.OnRefreshListener#onRefresh onRefresh()}, call a method that
         * refreshes the content. Call the same method in response to the Refresh action from the
         * action bar.
         */
        setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(LOG_TAG, "onRefresh called from SwipeRefreshLayout");

                initiateRefresh();
            }
        });
        // END_INCLUDE (setup_refreshlistener)
    }
    // END_INCLUDE (setup_views)



    // BEGIN_INCLUDE (setup_refresh_menu_listener)
    /**
     * Respond to the user's selection of the Refresh action item. Start the SwipeRefreshLayout
     * progress bar, then initiate the background task that refreshes the content.
     *
     * <p>A color scheme menu item used for demonstrating the use of SwipeRefreshLayout's color
     * scheme functionality. This kind of menu item should not be incorporated into your app,
     * it just to demonstrate the use of color. Instead you should choose a color scheme based
     * off of your application's branding.
     */

    // END_INCLUDE (setup_refresh_menu_listener)

    // BEGIN_INCLUDE (initiate_refresh)
    /**
     * By abstracting the refresh process to a single method, the app allows both the
     * SwipeGestureLayout onRefresh() method and the Refresh action item to refresh the content.
     */
    private void initiateRefresh() {
        Log.i(LOG_TAG, "initiateRefresh");
        new DummyBackgroundTask().execute();
    }

    private Boolean islatlongonpathofride(LatLng source,LatLng destination,java.util.List<LatLng> polylinelatlong) {
        Boolean ispickpointonpath = PolyUtil.isLocationOnPath(source, polylinelatlong, true, 4000.0);
        Boolean isdroppointonpath = PolyUtil.isLocationOnPath(destination, polylinelatlong, true, 4000.0);
        if(ispickpointonpath && isdroppointonpath)
        {
           return true;
        }
        else{
           return false;
        }
    }

    public LatLng convertStringtoLatlong(String lat,String lng)
    {
        double lati = Double.parseDouble(lat);
        double lngi = Double.parseDouble(lng);
        LatLng newlatlong = new LatLng(lati, lngi);
        return newlatlong;
    }
    /**
     * Dummy {@link AsyncTask} which simulates a long running task to fetch new cheeses.
     */
    private class DummyBackgroundTask extends AsyncTask<Void, Void, List<ridedata>> {

        @Override
        protected List<ridedata> doInBackground(Void... param) {
            List<ridedata> ridedataArray = new ArrayList<ridedata>();
            java.util.List<LatLng> polylinelatlong = PolyUtil.decode(SharedPreferenceManager.getPreference("myrideId_encodedpolyline"));
            // Building Parameters
            /*List<NameValuePair> params = new ArrayList<NameValuePair>();

            // post album id, song id as GET parameters
            params.add(new BasicNameValuePair("name", mName));
            params.add(new BasicNameValuePair("email", mEmail));
            params.add(new BasicNameValuePair("password", mPassword));
            params.add(new BasicNameValuePair("profile", mProfile));*/
            try {
                JSONObject params = new JSONObject();
                params.put("timeChoice",SharedPreferenceManager.getPreference("myrideId_timechoice"));
                params.put("rideId",SharedPreferenceManager.getPreference("myrideId"));
                // getting JSON string from URL
                String json = jsonParser.makeHttpRequest(mainurl.geturl() +"getRides", "POST",
                        params);



                JSONObject jObj = new JSONObject(json);
                if(jObj != null){
                    if(jObj.has("rides")) {
                        JSONArray rides = jObj.getJSONArray("rides");
                        for (int i = 0; i < rides.length(); i++) {
                            JSONObject rideindividualdata = rides.getJSONObject(i);
                            if(islatlongonpathofride(convertStringtoLatlong(rideindividualdata.getString("pickUpLat"),rideindividualdata.getString("pickUpLng")),convertStringtoLatlong(rideindividualdata.getString("dropLat"),rideindividualdata.getString("dropLng")),polylinelatlong)) {
                                ridedata info = new ridedata(rideindividualdata.getString("source"), rideindividualdata.getString("destination"), rideindividualdata.getString("date"), rideindividualdata.getString("rideId"), "ride", rideindividualdata.getString("customerNumber"), (!rideindividualdata.isNull("overview_polyline")?rideindividualdata.getString("overview_polyline"):""));
                                ridedataArray.add(info);
                            }
                            else if(islatlongonpathofride(convertStringtoLatlong(SharedPreferenceManager.getPreference("myrideId_sourcelat"),SharedPreferenceManager.getPreference("myrideId_sourcelong")),convertStringtoLatlong(SharedPreferenceManager.getPreference("myrideId_destinationlat"),SharedPreferenceManager.getPreference("myrideId_destinationlong")),PolyUtil.decode(!rideindividualdata.isNull("overview_polyline")?rideindividualdata.getString("overview_polyline"):""))){
                                ridedata info = new ridedata(rideindividualdata.getString("source"), rideindividualdata.getString("destination"), rideindividualdata.getString("date"), rideindividualdata.getString("rideId"), "ride", rideindividualdata.getString("customerNumber"), (!rideindividualdata.isNull("overview_polyline")?rideindividualdata.getString("overview_polyline"):""));
                                ridedataArray.add(info);
                            }
                        }
                    }
                    if(jObj.has("joinedRides")) {
                        JSONArray jrides = jObj.getJSONArray("joinedRides");
                        for (int i = 0; i < jrides.length(); i++) {
                            JSONObject joinedrideindividualdata = jrides.getJSONObject(i);
                            if(islatlongonpathofride(convertStringtoLatlong(joinedrideindividualdata.getString("pickUpLat"),joinedrideindividualdata.getString("pickUpLng")),convertStringtoLatlong(joinedrideindividualdata.getString("dropLat"),joinedrideindividualdata.getString("dropLng")),polylinelatlong)) {
                                ridedata info = new ridedata(joinedrideindividualdata.getString("source"), joinedrideindividualdata.getString("destination"), joinedrideindividualdata.getString("date"), joinedrideindividualdata.getString("jrId"), "jride", joinedrideindividualdata.getString("ownerCustomerNumber"),(!joinedrideindividualdata.isNull("overview_polyline")?joinedrideindividualdata.getString("overview_polyline"):""));
                                ridedataArray.add(info);
                            }
                            else if(islatlongonpathofride(convertStringtoLatlong(SharedPreferenceManager.getPreference("myrideId_sourcelat"),SharedPreferenceManager.getPreference("myrideId_sourcelong")),convertStringtoLatlong(SharedPreferenceManager.getPreference("myrideId_destinationlat"),SharedPreferenceManager.getPreference("myrideId_destinationlong")),PolyUtil.decode(!joinedrideindividualdata.isNull("overview_polyline")?joinedrideindividualdata.getString("overview_polyline"):""))){
                                ridedata info = new ridedata(joinedrideindividualdata.getString("source"), joinedrideindividualdata.getString("destination"), joinedrideindividualdata.getString("date"), joinedrideindividualdata.getString("jrId"), "jride", joinedrideindividualdata.getString("ownerCustomerNumber"),(!joinedrideindividualdata.isNull("overview_polyline")?joinedrideindividualdata.getString("overview_polyline"):""));
                                ridedataArray.add(info);
                            }
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return ridedataArray;
        }

        @Override
        protected void onPostExecute(List<ridedata> result) {
            super.onPostExecute(result);
            setRefreshing(false);
            try {
                if (getActivity() != null) {
                    if (!result.isEmpty()) {
                        adapter = new customridedataadapter(getActivity(), result);

                        // Set the adapter between the ListView and its backing data.
                        setListAdapter(adapter);
                    } else {
                        List<ridedata> noresultsarray = new ArrayList<ridedata>();
                        ridedata info = new ridedata(null, null, null);
                        info.setNoresults("No Results Available Currently");
                        noresultsarray.add(info);
                        adapter = new noresultsadapter(getActivity(), noresultsarray);

                        // Set the adapter between the ListView and its backing data.
                        setListAdapter(adapter);
                    }
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }

        }

    }

}
