package com.example.foodiepipe.foodiepipe;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.nullwire.trace.ExceptionHandler;


public class Searchridessourcedestination extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchridessourcedestination);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            SwipeRefreshListFragmentFragment fragment = new SwipeRefreshListFragmentFragment();
            fragment.setArguments(getIntent().getExtras());
            transaction.replace(R.id.sample_content_fragment, fragment);
            transaction.commitAllowingStateLoss();
        }
        ExceptionHandler.register(this, "http://radiant-peak-3095.herokuapp.com/remoteStackTrace");
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
