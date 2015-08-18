package com.splitrides;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.splitrides.android.helper.ConnectionDetector;

public class Searchridessourcedestination extends ActionBarActivity implements View.OnClickListener {


    Button opengoogledirection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchridessourcedestination);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        EditText populatesource = (EditText)findViewById(R.id.populate_source);
        EditText populatedestination = (EditText)findViewById(R.id.populate_destination);
        opengoogledirection = (Button)findViewById(R.id.open_google_directions);
        opengoogledirection.setOnClickListener(this);
        populatesource.setText(SharedPreferenceManager.getPreference("myride_source"));
        populatedestination.setText(SharedPreferenceManager.getPreference("myride_destination"));
        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            SwipeRefreshListFragmentFragment fragment = new SwipeRefreshListFragmentFragment();
            transaction.replace(R.id.sample_content_fragment, fragment);
            transaction.commitAllowingStateLoss();
        }
    }

    public void onClick(View view) {
        ConnectionDetector cd = new ConnectionDetector(Searchridessourcedestination.this.getApplicationContext());

        // Check if Internet present
        if (!cd.isConnectingToInternet()) {
            // Internet Connection is not present
            Toast.makeText(Searchridessourcedestination.this,
                    "Internet Connection Error Please connect to working Internet connection", Toast.LENGTH_LONG).show();
            // stop executing code by return
            return;
        }
        switch(view.getId()) {
            case R.id.open_google_directions:
                StringBuilder latlongbuilder = new StringBuilder();
                latlongbuilder.append(SharedPreferenceManager.getPreference("myrideId_sourcelat")).append(",").append(SharedPreferenceManager.getPreference("myrideId_sourcelong"));
                StringBuilder latlongbuilder_droppoint = new StringBuilder();
                latlongbuilder_droppoint.append(SharedPreferenceManager.getPreference("myrideId_destinationlat")).append(",").append(SharedPreferenceManager.getPreference("myrideId_destinationlong"));
                StringBuilder latlongstringbuilder = new StringBuilder();
                latlongstringbuilder.append("http://maps.google.com/maps?saddr=").append(latlongbuilder.toString()).append("&daddr=").append(latlongbuilder_droppoint.toString());
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse(latlongstringbuilder.toString()));
                startActivity(intent);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_searchridessourcedestination, menu);
        return true;
    }


    @Override
    public void onBackPressed() {
        // finish() is called in super: we only override this method to be able to override the transition
        super.onBackPressed();

        overridePendingTransition(R.animator.back_in, R.animator.back_out);
    }

}
