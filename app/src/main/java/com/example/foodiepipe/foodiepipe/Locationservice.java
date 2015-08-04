package com.example.foodiepipe.foodiepipe;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by gbm on 6/29/15.
 */
public class Locationservice extends Service implements LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    public Locationservice() {

    }

    @Override
    public void onCreate() {
        super.onCreate();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API).
                        addConnectionCallbacks(this).
                        addOnConnectionFailedListener(this)
                .build();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mGoogleApiClient.connect();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onLocationChanged(Location mCurrentLocation) {
        StringBuilder locationstringbuilder = new StringBuilder();
        String locationstring = SharedPreferenceManager.getPreference("locationstringdata");
        if(locationstring != null && mCurrentLocation !=null) {
            locationstringbuilder.append(locationstring).append(mCurrentLocation.getLatitude()).append(",").append(mCurrentLocation.getLongitude()).append("|");
            SharedPreferenceManager.setPreference("locationstringdata", locationstringbuilder.toString());
        }
        else if(mCurrentLocation !=null){
            locationstringbuilder.append(mCurrentLocation.getLatitude()).append(",").append(mCurrentLocation.getLongitude()).append("|");
            SharedPreferenceManager.setPreference("locationstringdata", locationstringbuilder.toString());
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(15000);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(15000);

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        Location loc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    @Override
    public void onDestroy() {

        Toast.makeText(this, "Location services stopped", Toast.LENGTH_LONG).show();

        Location loc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        mGoogleApiClient.disconnect();
        super.onDestroy();
    }

}