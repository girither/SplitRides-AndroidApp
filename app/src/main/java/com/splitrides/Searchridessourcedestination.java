package com.splitrides;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.splitrides.android.helper.ConnectionDetector;

public class Searchridessourcedestination extends ActionBarActivity implements View.OnClickListener {

    ShowcaseView sv;
    Button opengoogledirection;
    int clickeditem =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchridessourcedestination);
        ftububble();
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

    public void ftububble(){
        if(!SharedPreferenceManager.getBooleanPreference("showsearchpageftu")) {
            RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            lps.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            int marginbottom = ((Number) (getResources().getDisplayMetrics().density * 50)).intValue();
            int margin = ((Number) (getResources().getDisplayMetrics().density * 12)).intValue();
            lps.setMargins(0, 0, margin, marginbottom);
            ViewTarget target = new ViewTarget(R.id.tool_bar, this);
            sv = new ShowcaseView.Builder(this, true)
                    .setTarget(Target.NONE)
                    .setContentTitle("Welcome to search page")
                    .setContentText("Optimized routing by google and we get rides en-route selected by google")
                    .setStyle(R.style.CustomShowcaseTheme)
                    .setOnClickListener(this)
                    .build();
            sv.setButtonPosition(lps);
            sv.setShouldCentreText(true);
        }
    }

    public void onClick(View view) {
        View targetView;
        ViewTarget target;
        int margin,marginbottom;
        if(!SharedPreferenceManager.getBooleanPreference("showsearchpageftu")) {
            switch (clickeditem) {
                case 0:
                    target = new ViewTarget(R.id.open_google_directions, this);
                    sv.setShowcase(target, true);
                    sv.setContentText("Click to see the optimized route given by google");
                    sv.setContentTitle("Check Directions");
                    RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    lps.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    marginbottom = ((Number) (getResources().getDisplayMetrics().density * 50)).intValue();
                    margin = ((Number) (getResources().getDisplayMetrics().density * 12)).intValue();
                    lps.setMargins(0, 0, margin, marginbottom);
                    sv.setButtonPosition(lps);
                    break;
                case 1:
                    sv.hide();
                    SharedPreferenceManager.setPreference("showsearchpageftu", true);
                    break;
            }
            clickeditem++;
        }
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
