package com.example.foodiepipe.foodiepipe;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.foodpipe.android.helper.ConnectionDetector;
import com.foodpipe.android.helper.JSONParser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.SphericalUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class Postyourrides extends ActionBarActivity
        implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks,AdapterView.OnItemSelectedListener,View.OnClickListener {

    /**
     * GoogleApiClient wraps our service connection to Google Play Services and provides access
     * to the user's sign in state as well as the Google's APIs.
     */
    protected GoogleApiClient mGoogleApiClient;

    private PlaceAutocompleteAdapter mAdapter;
    /**
     * Action to launch the PlacePicker from a card. Identifies the card action.
     */
    private static final int ACTION_PICK_PLACE = 1;

    /**
     * Request code passed to the PlacePicker intent to identify its result when it returns.
     */
    private static final int REQUEST_PLACE_PICKER = 1;
    private static final int REQUEST_PLACE_PICKER_DROPPOINT = 2;
    TextView settimetextView;
    TextView settimetextViewhidden;
    TextView setlatlongtextView,setlatlongtextView_droppoint;
    private AutoCompleteTextView mAutocompleteView;
    private AutoCompleteTextView mAutocompleteView_destination;
    private Postyouridetask mAuthTask = null;
    private validateplacesapi mvalidateplacesTask = null;
    private ProgressDialog pDialog;
    private EditText mPhonenumber;
    JSONParser jsonParser = new JSONParser();
    private String currentDate;
    LatLng latlongcordsource,latlongcorddestination;
    private String hiddenRideID;

    private double sourcelat,sourcelong,destinationlat,destinationlong;
    //private TextView mPlaceDetailsText;

    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(12.89201, 77.58905), new LatLng(12.97232, 77.59480));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up the Google API Client if it has not been initialised yet.
        if (mGoogleApiClient == null) {
            rebuildGoogleApiClient();
        }

        setContentView(R.layout.activity_postyourrides);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SharedPreferenceManager.setApplicationContext(getApplicationContext());
        // Retrieve the AutoCompleteTextView that will display Place suggestions.
        mAutocompleteView = (AutoCompleteTextView)
                findViewById(R.id.autocomplete_places_source);
        mPhonenumber = (EditText)
                findViewById(R.id.enter_phone);
        mAutocompleteView_destination = (AutoCompleteTextView)
                findViewById(R.id.autocomplete_places_destination);
        Button postyourridebutton = (Button)findViewById(R.id.post_ride_button);
        postyourridebutton.setOnClickListener(this);
        Button choosepickuppoint = (Button)findViewById(R.id.choose_pickup_point);
        choosepickuppoint.setOnClickListener(this);
        Button choosedroppoint = (Button)findViewById(R.id.choose_drop_point);
        choosedroppoint.setOnClickListener(this);
        // Register a listener that receives callbacks when a suggestion has been selected
        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);
        mAutocompleteView_destination.setOnItemClickListener(mAutocompleteClickListener_destination);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.days_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 15);
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = sdfdate.format(cal.getTime());
        Calendar nextdaycal = Calendar.getInstance();
        nextdaycal.add(Calendar.DATE, 1);
        SimpleDateFormat sdftomorrow = new SimpleDateFormat("yyyy-MM-dd");
        String tomorrowDate = sdftomorrow.format(nextdaycal.getTime());
        if(tomorrowDate.equals(currentDate))
        {
            spinner.setSelection(1);
        }
        settimetextView = (TextView)
                findViewById(R.id.time_data);
        setlatlongtextView = (TextView)
                findViewById(R.id.latlong_data);
        setlatlongtextView_droppoint = (TextView)findViewById(R.id.latlong_data_droppoint);
        String currenttime = sdf.format(cal.getTime());
        settimetextView.setText(currenttime);
        SimpleDateFormat sdfhidden = new SimpleDateFormat("HH:mm:ss");
        String currenttimehidden = sdfhidden.format(cal.getTime());
        settimetextViewhidden = new TextView(getApplicationContext());
        settimetextViewhidden.setText(currenttimehidden);
        // Set up the adapter that will retrieve suggestions from the Places Geo Data API that cover
        // the entire world.
        mAdapter = new PlaceAutocompleteAdapter(this, android.R.layout.simple_list_item_1,
                BOUNDS_GREATER_SYDNEY, null);

        mAutocompleteView.setAdapter(mAdapter);
        mAutocompleteView_destination.setAdapter(mAdapter);

        //After creation
        if(getIntent() != null && getIntent().getExtras() != null) {
            String activityName = getIntent().getExtras().getString("activityName");
            if(activityName != null && activityName.equals("editRide")) {
                String rideId = getIntent().getExtras().getString("editRideId");

                GetRideDetailTask task = new GetRideDetailTask(rideId);
                task.execute();
                ((Button)findViewById(R.id.post_ride_button)).setText("EDIT YOUR RIDE");
            }
        }
    }

    /**
     * Listener that handles selections from suggestions from the AutoCompleteTextView that
     * displays Place suggestions.
     * Gets the place id of the selected item and issues a request to the Places Geo Data API
     * to retrieve more details about the place.
     *
     * @see com.google.android.gms.location.places.GeoDataApi#getPlaceById(com.google.android.gms.common.api.GoogleApiClient,
     * String...)
     */
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

            //Toast.makeText(getApplicationContext(), "Clicked: " + item.description,
              //      Toast.LENGTH_SHORT).show();

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
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {

        if (requestCode == REQUEST_PLACE_PICKER
                && resultCode == Activity.RESULT_OK) {

            // The user has selected a place. Extract the name and address.
            final Place place = PlacePicker.getPlace(data, this);

            final CharSequence name = place.getName();
            final CharSequence address = place.getAddress();
            final LatLng latlongcord = place.getLatLng();

            String attributions = PlacePicker.getAttributions(data);
            if (attributions == null) {
                attributions = "";
            }
            StringBuilder locationstringbuilder = new StringBuilder();
            locationstringbuilder.append(Double.toString(latlongcord.latitude)).append(",").append(Double.toString(latlongcord.longitude));
            setlatlongtextView.setText(locationstringbuilder.toString());
        }
        else if (requestCode == REQUEST_PLACE_PICKER_DROPPOINT
                && resultCode == Activity.RESULT_OK){
            final Place place = PlacePicker.getPlace(data, this);

            final CharSequence name = place.getName();
            final CharSequence address = place.getAddress();
            final LatLng latlongcord = place.getLatLng();

            String attributions = PlacePicker.getAttributions(data);
            if (attributions == null) {
                attributions = "";
            }
            StringBuilder locationstringbuilder = new StringBuilder();
            locationstringbuilder.append(Double.toString(latlongcord.latitude)).append(",").append(Double.toString(latlongcord.longitude));
            setlatlongtextView_droppoint.setText(locationstringbuilder.toString());

        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
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
            latlongcordsource = place.getLatLng();
            sourcelat =  latlongcordsource.latitude;
            sourcelong = latlongcordsource.longitude;
            // Format details of the place for display and show it in a TextView.
            //mPlaceDetailsText.setText(formatPlaceDetails(getResources(), place.getName(),
                //  place.getId(), place.getAddress(), place.getPhoneNumber(), place.getWebsiteUri()));


        }
    };

    public LatLng convertStringtoLatlong(String locationdata)
    {
        int index = locationdata.indexOf(",");
        String lat = locationdata.substring(0, index).trim();
        String lng = locationdata.substring(index+1).trim();
        double lati = Double.parseDouble(lat);
        double lngi = Double.parseDouble(lng);
        LatLng newlatlong = new LatLng(lati, lngi);
        return newlatlong;
    }

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

            latlongcorddestination = place.getLatLng();
            destinationlat =  latlongcorddestination.latitude;
            destinationlong = latlongcorddestination.longitude;
            // Format details of the place for display and show it in a TextView.
            //mPlaceDetailsText.setText(formatPlaceDetails(getResources(), place.getName(),
            //  place.getId(), place.getAddress(), place.getPhoneNumber(), place.getWebsiteUri()));


        }
    };

    @Override
    public void onBackPressed() {
        // finish() is called in super: we only override this method to be able to override the transition
        super.onBackPressed();

        overridePendingTransition(R.animator.back_in, R.animator.back_out);
    }


    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        String optionSelected = parent.getItemAtPosition(pos).toString();
        switch(optionSelected) {
            case "Today":
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                currentDate = sdf.format(cal.getTime());
                break;
            case "Tomorrow":

                Calendar caltomorrow = Calendar.getInstance();
                caltomorrow.add(Calendar.DATE, 1);
                SimpleDateFormat sdftomorrow = new SimpleDateFormat("yyyy-MM-dd");
                currentDate = sdftomorrow.format(caltomorrow.getTime());
                break;

        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    /**
     * Construct a GoogleApiClient for the {@link Places#GEO_DATA_API} using AutoManage
     * functionality.
     * This automatically sets up the API client to handle Activity lifecycle events.
     */
    protected synchronized void rebuildGoogleApiClient() {
        // When we build the GoogleApiClient we specify where connected and connection failed
        // callbacks should be returned, which Google APIs our app uses and which OAuth 2.0
        // scopes our app requests.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addConnectionCallbacks(this)
                .addApi(Places.GEO_DATA_API)
                .build();
    }

    /**
     * Called when the Activity could not connect to Google Play services and the auto manager
     * could resolve the error automatically.
     * In this case the API is not available and notify the user.
     *
     * @param connectionResult can be inspected to determine the cause of the failure
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {



        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(this,
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
    public void onClick(View view) {
        ConnectionDetector cd = new ConnectionDetector(Postyourrides.this.getApplicationContext());

        // Check if Internet present
        if (!cd.isConnectingToInternet()) {
            // Internet Connection is not present
            Toast.makeText(Postyourrides.this,
                    "Internet Connection Error Please connect to working Internet connection", Toast.LENGTH_LONG).show();
            // stop executing code by return
            return;
        }
        switch(view.getId()) {
            case R.id.post_ride_button:
                attempttopostrides();
                break;
            case R.id.choose_pickup_point:
                onPickButtonClick();
                break;
            case R.id.choose_drop_point:
                ondropbuttonclick();
                break;
        }
    }

    public LatLngBounds convertCenterAndRadiusToBounds(LatLng center, double radius) {
        LatLng southwest = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 225);
        LatLng northeast = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 45);

        return new LatLngBounds(southwest, northeast);
    }

    public void onPickButtonClick() {
        // Construct an intent for the place picker
        try {
            PlacePicker.IntentBuilder intentBuilder;
            if(latlongcordsource != null) {
                    intentBuilder         =
                        new PlacePicker.IntentBuilder().setLatLngBounds(convertCenterAndRadiusToBounds(latlongcordsource,100));
            }
            else{
                intentBuilder =
                        new PlacePicker.IntentBuilder();
            }
            Intent intent = intentBuilder.build(this);
            // Start the intent by requesting a result,
            // identified by a request code.
            startActivityForResult(intent, REQUEST_PLACE_PICKER);

        } catch (GooglePlayServicesRepairableException e) {
            // ...
        } catch (GooglePlayServicesNotAvailableException e) {
            // ...
        }
    }

    public void ondropbuttonclick() {
        // Construct an intent for the place picker
        try {
            PlacePicker.IntentBuilder intentBuilder;
            if(latlongcordsource != null) {
                intentBuilder         =
                        new PlacePicker.IntentBuilder().setLatLngBounds(convertCenterAndRadiusToBounds(latlongcorddestination, 100));
            }
            else{
                intentBuilder =
                        new PlacePicker.IntentBuilder();
            }
            Intent intent = intentBuilder.build(this);
            // Start the intent by requesting a result,
            // identified by a request code.
            startActivityForResult(intent, REQUEST_PLACE_PICKER_DROPPOINT);

        } catch (GooglePlayServicesRepairableException e) {
            // ...
        } catch (GooglePlayServicesNotAvailableException e) {
            // ...
        }
    }

    public void attempttopostrides() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mAutocompleteView.setError(null);
        mAutocompleteView_destination.setError(null);
        mPhonenumber.setError(null);

        // Store values at the time of the login attempt.
        String source = mAutocompleteView.getText().toString();
        String destination = mAutocompleteView_destination.getText().toString();
        String phonenumber = mPhonenumber.getText().toString();
        String timeclock = settimetextViewhidden.getText().toString();
        String latlong = setlatlongtextView.getText().toString();
        String latlong_droppoint = setlatlongtextView_droppoint.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid destination.
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
        if (TextUtils.isEmpty(phonenumber)) {
            mPhonenumber.setError(getString(R.string.error_field_required));
            focusView = mPhonenumber;
            cancel = true;
        }
        if(latlong.isEmpty())
        {
            Toast.makeText(Postyourrides.this,
                    "Please select a pickup point", Toast.LENGTH_LONG).show();
            cancel = true;
            focusView = setlatlongtextView;
        }
        if(latlong_droppoint.isEmpty())
        {
            Toast.makeText(Postyourrides.this,
                    "Please select a droppoint point", Toast.LENGTH_LONG).show();
            cancel = true;
            focusView = setlatlongtextView_droppoint;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mvalidateplacesTask = new validateplacesapi(hiddenRideID,source,destination,phonenumber,currentDate,timeclock,Double.toString(sourcelat),Double.toString(sourcelong),Double.toString(destinationlat),Double.toString(destinationlong),latlong,latlong_droppoint);
            mvalidateplacesTask.execute((Void) null);

        }
    }


    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment(settimetextView,settimetextViewhidden,currentDate);
        newFragment.show(getFragmentManager(), "timePicker");
    }

    @Override
    public void onConnectionSuspended(int i) {
        // Connection to the API client has been suspended. Disable API access in the client.
        mAdapter.setGoogleApiClient(null);

    }
    public class Postyouridetask extends AsyncTask<Void, Void, Boolean> {

        private final String mRideId;
        private final String mSource;
        private final String mDestination;
        private final String mPhonenumber;
        private final String mDate;
        private final String mTime;
        private final String msourcelat;
        private final String msourcelong;
        private final String mdestinationlat;
        private final String mdestinationlong;
        private final String mpickuppoint;
        private final String mdroppoint;

        Postyouridetask(String rideId, String source, String destination,String phonenumber,String date,String time,String pickuppoint,String droppoint,String sourcelat,String sourcelong,String destinationlat,String destinationlong) {
            mRideId = rideId;
            mSource = source;
            mDestination = destination;
            mPhonenumber = phonenumber;
            mDate = date;
            mTime =time;
            msourcelat =  sourcelat;
            msourcelong = sourcelong;
            mdestinationlat = destinationlat;
            mdestinationlong = destinationlong;
            mpickuppoint = pickuppoint;
            mdroppoint = droppoint;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Postyourrides.this);
            pDialog.setMessage("Posting your Ride...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
        @Override
        protected Boolean doInBackground(Void... param) {
            String apiAction = null;
            try {
                JSONObject params = new JSONObject();
                params.put("source", mSource);
                params.put("destination", mDestination);
                params.put("phoneNumber", mPhonenumber);
                params.put("date", mDate);
                params.put("time", mTime);
                params.put("sourceLat", msourcelat);
                params.put("sourceLng", msourcelong);
                params.put("destinationLat", mdestinationlat);
                params.put("destinationLng", mdestinationlong);
                params.put("pickUpLatLng", mpickuppoint);
                params.put("dropLatLng",mdroppoint);

                Log.d("token data ", SharedPreferenceManager.getPreference("auth_token"));
                // getting JSON string from URL
                String json = null;

                if(mRideId != null && !mRideId.trim().equals("")) {
                    params.put("rideId", mRideId);
                    apiAction = "updateRide";
                } else {
                    apiAction = "postRide";
                }

                json = jsonParser.makeHttpRequest(mainurl.geturl()  + apiAction, "POST",
                        params);

                // Check your log cat for JSON reponse
                Log.d("response from post rides ", json);


                JSONObject jObj = new JSONObject(json);
                if(jObj != null){
                    String data = jObj.getString("success");
                    return true;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            pDialog.dismiss();
            String userMessage = null;
            if (success) {
                finish();
                if(mRideId != null && !mRideId.trim().equals("")) {
                    userMessage = "Your Ride was updated succesfully";
                } else {
                    userMessage = "Your Ride was posted succesfully";
                }
            } else {
                userMessage = "Something went wrong while posting your ride. Please try again";
            }

            Toast.makeText(Postyourrides.this,
                    userMessage, Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }
    public class validateplacesapi extends AsyncTask<Void, Void, Boolean> {

        private final String mRideId;
        private final String mSource;
        private final String mDestination;
        private final String mPhonenumber;
        private final String mDate;
        private final String mTime;
        private final String msourcelat;
        private final String msourcelong;
        private final String mdestinationlat;
        private final String mdestinationlong;
        private final String mpickuppoint;
        private final String mdroppoint;

        String validityflag;

        validateplacesapi(String rideId, String source, String destination,String phonenumber,String date,String time,String sourcelat,String sourcelong,String destinationlat,String destinationlong,String pickuppoint,String droppoint) {
            mRideId = rideId;
            mSource = source;
            mDestination = destination;
            mPhonenumber = phonenumber;
            mDate = date;
            mTime =time;
            msourcelat =  sourcelat;
            msourcelong = sourcelong;
            mdestinationlat = destinationlat;
            mdestinationlong = destinationlong;
            mpickuppoint = pickuppoint;
            mdroppoint = droppoint;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Postyourrides.this);
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
            mvalidateplacesTask = null;
            pDialog.dismiss();
            View focusView = null;
            if (success) {
                mAuthTask = new Postyouridetask(mRideId,mSource,mDestination,mPhonenumber,mDate,mTime,mpickuppoint,mdroppoint,msourcelat,msourcelong,mdestinationlat,mdestinationlong);
                mAuthTask.execute((Void) null);
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
            mvalidateplacesTask = null;
        }
    }


    private class GetRideDetailTask extends AsyncTask<Void, Void, ridedata> {
        private String rideId = null;

        GetRideDetailTask(String rideId) {
            this.rideId = rideId;
        }

        @Override
        protected ridedata doInBackground(Void ...param) {
            ridedata rideDetails = null;

            try {
                JSONObject params = new JSONObject();
                params.put("rideId", this.rideId);
                // getting JSON string from URL
                String json = jsonParser.makeHttpRequest(mainurl.geturl()+"getRideDetails", "POST",
                        params);

                JSONObject responseJSON = new JSONObject(json);

                if(responseJSON.has("ride")) {
                    JSONObject ride = responseJSON.getJSONObject("ride");
                    JSONObject customerdata = responseJSON.getJSONObject("owner");
                    StringBuilder customerLatLongString = new StringBuilder();
                    customerLatLongString.append(ride.getString("pickUpLat")).append(",").append(ride.getString("pickUpLng"));

                    StringBuilder customerDropLatLong = new StringBuilder();
                    customerDropLatLong.append(ride.getString("dropLat")).append(",").append(ride.getString("dropLng"));
                    customer customeradapterdata = new customer(customerdata.getString("name"), customerdata.getString("email"),
                                                                ride.getString("phoneNumber"), customerLatLongString.toString(),
                                                                customerDropLatLong.toString(),customerdata.getString("profileId"),customerdata.getString("ownerCustomerNumber"));
                    List<customer> customerlistdata = new ArrayList<customer>();
                    customerlistdata.add(customeradapterdata);
                    rideDetails = new ridedata(ride.getString("source"), ride.getString("destination"), ride.getString("date"), customerlistdata,"ride",ride.getString("rideId"));
                }
            } catch (JSONException je) {
                je.printStackTrace();
            }

            return rideDetails;
        }

        @Override
        protected void onPostExecute(final ridedata ridedataobject) {
            Date rideDate = null, currentDate = new Date();
            String dayDate = null;

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                rideDate = sdf.parse(ridedataobject.getDate());

                if(rideDate.getDate() == currentDate.getDate()) {
                    dayDate = "Today";
                } else if(rideDate.after(currentDate)) {
                    dayDate = "Tomorrow";
                }
            } catch (ParseException pe) {
                pe.printStackTrace();
            }

            mAutocompleteView.setText(ridedataobject.getSource());
            mAutocompleteView_destination.setText(ridedataobject.getDestination());
            mPhonenumber.setText(ridedataobject.getCustomerlistdata().get(0).getCustomerPhoneNumber());
            settimetextView.setText(rideDate.getHours() + ":" + rideDate.getMinutes() + ":" + rideDate.getSeconds());
            setlatlongtextView.setText(ridedataobject.getCustomerlistdata().get(0).getLatLong());
            setlatlongtextView_droppoint.setText(ridedataobject.getCustomerlistdata().get(0).getDropLatlong());
            latlongcordsource = convertStringtoLatlong(ridedataobject.getCustomerlistdata().get(0).getLatLong());
            latlongcorddestination = convertStringtoLatlong(ridedataobject.getCustomerlistdata().get(0).getDropLatlong());
            hiddenRideID = ridedataobject.getRideId();

            Spinner spinner = (Spinner) findViewById(R.id.spinner);
            if(dayDate.equals("Tomorrow")) {
                spinner.setSelection(1);
            } else {
                spinner.setSelection(0);
            }
        }
    }

}
