package com.example.foodiepipe.foodiepipe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.Window;

import com.nullwire.trace.ExceptionHandler;


public class Splashactivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isTaskRoot()) {
            final Intent intent = getIntent();
            final String intentAction = intent.getAction();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER)
                    && intentAction != null
                    && intentAction.equals(Intent.ACTION_MAIN)) {
                finish();
                return;
            }
        }
        SharedPreferenceManager.setApplicationContext(getApplicationContext());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splashactivity);
        load();
        ExceptionHandler.register(this, "http://radiant-peak-3095.herokuapp.com/remoteStackTrace");
    }

    private void load() {
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(2000);
                    // Starts the next Activity - Login Screen
                    finishedLoading();
                } catch (InterruptedException e) {
                }
            }// end run
        }.start();
    }// end load

    private void finishedLoading() {
        // Decide which screen to go next based on whether the user has already
        // logged in or should login.

        // For now always send to Login Screen.

        Intent startLoginIntent = new Intent(getApplicationContext(),
                MainActivity.class);
        startActivity(startLoginIntent);
        startLoginIntent = null;
        finish();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splashactivity, menu);
        return true;
    }

}
