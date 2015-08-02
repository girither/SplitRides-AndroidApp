package com.example.foodiepipe.foodiepipe;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.foodpipe.android.helper.ConnectionDetector;

/**
 * Created by gbm on 7/24/15.
 */
public class billcardfragment extends ActionBarActivity implements View.OnClickListener {


    private Toolbar toolbar;
    private Button okcool;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rate_billfragment);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        Bundle extras = getIntent().getExtras();
        String price = extras.getString("price");
        String distance = extras.getString("distance");
        String time = extras.getString("time");
        TextView rateText = (TextView)findViewById(R.id.totalcost_value);
        rateText.setText(getResources().getString(R.string.Rs) + " " + price);
        TextView ratedistance = (TextView)findViewById(R.id.distance_value);
        ratedistance.setText(distance+" Km");
        TextView ratetime = (TextView)findViewById(R.id.trip_time_value);
        ratetime.setText(time + " min");
        okcool = (Button) findViewById(R.id.startride);
        okcool.setOnClickListener(this);
    }

    public void onClick(View view) {
        ConnectionDetector cd = new ConnectionDetector(billcardfragment.this.getApplicationContext());

        // Check if Internet present
        if (!cd.isConnectingToInternet()) {
            // Internet Connection is not present
            Toast.makeText(billcardfragment.this,
                    "Internet Connection Error Please connect to working Internet connection", Toast.LENGTH_LONG).show();
            // stop executing code by return
            return;
        }
        switch(view.getId()) {
            case R.id.ok_cool:
                finish();
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
    }

}