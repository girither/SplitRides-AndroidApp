package com.example.foodiepipe.foodiepipe;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.foodpipe.android.helper.ConnectionDetector;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;


public class ratecard  extends Fragment implements GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks,View.OnClickListener {

    static final int PICK_CABPROVIDER_RESULT = 1;
    private AutoCompleteTextView mAutocompleteView;
    private AutoCompleteTextView mAutocompleteView_destination;
    protected GoogleApiClient mGoogleApiClient;
    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(12.89201, 77.58905), new LatLng(12.97232, 77.59480));
    private PlaceAutocompleteAdapter mAdapter;
    private ProgressDialog pDialog;
    private validateplacesapi mvalidateplacesTask = null;
    private double sourcelat,sourcelong,destinationlat,destinationlong;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_ratecard,container, false);
        Button estimateridebutton = (Button)rootView.findViewById(R.id.estimate_ride_button);
        estimateridebutton.setOnClickListener(this);
        mAutocompleteView = (AutoCompleteTextView)rootView.
                findViewById(R.id.autocomplete_places_source);
        mAutocompleteView_destination = (AutoCompleteTextView)rootView.
                findViewById(R.id.autocomplete_places_destination);
        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);
        mAutocompleteView_destination.setOnItemClickListener(mAutocompleteClickListener_destination);
        mAdapter = new PlaceAutocompleteAdapter(getActivity(), android.R.layout.simple_list_item_1,
                BOUNDS_GREATER_SYDNEY, null);
        mAutocompleteView.setAdapter(mAdapter);
        mAutocompleteView_destination.setAdapter(mAdapter);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mGoogleApiClient == null) {
            rebuildGoogleApiClient();
        }
    }

    protected synchronized void rebuildGoogleApiClient() {
        // When we build the GoogleApiClient we specify where connected and connection failed
        // callbacks should be returned, which Google APIs our app uses and which OAuth 2.0
        // scopes our app requests.
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addApi(Places.GEO_DATA_API)
                .build();
        mGoogleApiClient.connect();
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a PlaceAutocomplete object from which we
             read the place ID.
              */
            final PlaceAutocompleteAdapter.PlaceAutocomplete item = mAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
              details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

            Toast.makeText(getActivity(), "Clicked: " + item.description,
                  Toast.LENGTH_SHORT).show();

        }
    };

    private AdapterView.OnItemClickListener mAutocompleteClickListener_destination
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a PlaceAutocomplete object from which we
             read the place ID.
              */
            final PlaceAutocompleteAdapter.PlaceAutocomplete item = mAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
              details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback_destination);

            //Toast.makeText(getApplicationContext(), "Clicked: " + item.description,
            //      Toast.LENGTH_SHORT).show();

        }
    };

    @Override
    public void onConnectionSuspended(int i) {
        // Connection to the API client has been suspended. Disable API access in the client.
        mAdapter.setGoogleApiClient(null);

    }

    /**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     */
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully

                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);
            LatLng latlongcordsource = place.getLatLng();
            sourcelat =  latlongcordsource.latitude;
            sourcelong = latlongcordsource.longitude;
            // Format details of the place for display and show it in a TextView.
            //mPlaceDetailsText.setText(formatPlaceDetails(getResources(), place.getName(),
            //  place.getId(), place.getAddress(), place.getPhoneNumber(), place.getWebsiteUri()));


        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback_destination
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully

                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);
            LatLng latlongcorddestination = place.getLatLng();
            destinationlat =  latlongcorddestination.latitude;
            destinationlong = latlongcorddestination.longitude;
            // Format details of the place for display and show it in a TextView.
            //mPlaceDetailsText.setText(formatPlaceDetails(getResources(), place.getName(),
            //  place.getId(), place.getAddress(), place.getPhoneNumber(), place.getWebsiteUri()));


        }
    };

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {



        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(getActivity(),
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();

        // Disable API access in the adapter because the client was not initialised correctly.
        mAdapter.setGoogleApiClient(null);

    }


    @Override
    public void onConnected(Bundle bundle) {
        // Successfully connected to the API client. Pass it to the adapter to enable API access.
        mAdapter.setGoogleApiClient(mGoogleApiClient);


    }

    @Override
    public void onClick(View view) {
        ConnectionDetector cd = new ConnectionDetector(getActivity().getApplicationContext());

        // Check if Internet present
        if (!cd.isConnectingToInternet()) {
            // Internet Connection is not present
            Toast.makeText(getActivity(),
                    "Internet Connection Error Please connect to working Internet connection", Toast.LENGTH_LONG).show();
            // stop executing code by return
            return;
        }
        switch(view.getId()) {
            case R.id.estimate_ride_button:
                String source = mAutocompleteView.getText().toString();
                String destination = mAutocompleteView_destination.getText().toString();
                View focusView = null;
                boolean cancel = false;
                if (TextUtils.isEmpty(destination)) {
                    mAutocompleteView_destination.setError(getString(R.string.error_field_required));
                    focusView = mAutocompleteView_destination;
                    cancel = true;
                }

                // Check for a valid email address.
                if (TextUtils.isEmpty(source)) {
                    mAutocompleteView.setError(getString(R.string.error_field_required));
                    focusView = mAutocompleteView;
                    cancel = true;
                }
                if (cancel) {
                    // There was an error; don't attempt login and focus the first
                    // form field with an error.
                    focusView.requestFocus();
                } else {
                    // Show a progress spinner, and kick off a background task to
                    // perform the user login attempt.
                    mvalidateplacesTask = new validateplacesapi(source,destination,Double.toString(sourcelat),Double.toString(sourcelong),Double.toString(destinationlat),Double.toString(destinationlong));
                    mvalidateplacesTask.execute((Void) null);

                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == PICK_CABPROVIDER_RESULT) {
            // Make sure the request was successful
            if (resultCode == Activity.RESULT_OK) {
               Bundle extras = data.getExtras();
               String cabprovidervalue = extras.getString("cabprovider");

            }
        }
    }

    public class validateplacesapi extends AsyncTask<Void, Void, Boolean> {

        private final String msourcelat;
        private final String msourcelong;
        private final String mdestinationlat;
        private final String mdestinationlong;
        private final String mSource;
        private final String mDestination;

        String validityflag;

        validateplacesapi(String source, String destination,String sourcelat,String sourcelong,String destinationlat,String destinationlong) {
            mSource = source;
            mDestination = destination;
            msourcelat =  sourcelat;
            msourcelong = sourcelong;
            mdestinationlat = destinationlat;
            mdestinationlong = destinationlong;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Validating source and destination");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
        @Override
        protected Boolean doInBackground(Void... param) {
            ArrayList<PlaceAutocompleteAdapter.PlaceAutocomplete> resultlistsource = mAdapter.getAutocomplete(mSource);
            ArrayList<PlaceAutocompleteAdapter.PlaceAutocomplete> resultlistdestination = mAdapter.getAutocomplete(mDestination);
            if(resultlistsource != null && resultlistsource.size() > 0 && resultlistsource.get(0).description.equals(mSource))
            {
                if(resultlistdestination != null && resultlistdestination.size() > 0 && resultlistdestination.get(0).description.equals(mDestination)){
                    return true;
                }
                else
                {
                    validityflag = "destination";
                    return false;
                }
            }
            else{
                validityflag = "source";
                return false;
            }

        }

        @Override
        protected void onPostExecute(final Boolean success) {
            pDialog.dismiss();
            View focusView = null;
            if (success) {
                Intent selectcabprovider = new Intent(getActivity(),cabproviderselction.class);
                startActivityForResult(selectcabprovider, PICK_CABPROVIDER_RESULT);
            } else {
                if(validityflag.equals("source")){
                    mAutocompleteView.setError(getString(R.string.valid_field_required));
                    focusView = mAutocompleteView;
                }
                else {
                    mAutocompleteView_destination.setError(getString(R.string.valid_field_required));
                    focusView = mAutocompleteView_destination;
                }
            }
        }

        @Override
        protected void onCancelled() {

        }
    }


}
