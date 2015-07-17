package com.example.foodiepipe.foodiepipe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.acra.ACRA;

import java.util.ArrayList;

public class cabproviderselction extends ActionBarActivity implements AdapterView.OnItemClickListener {

    private Toolbar toolbar;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cabproviderselction);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        ArrayList<String> arrayStrings = new ArrayList<String>();
        //arrayStrings.add("Ola MINI");
        //arrayStrings.add("Ola SEDAN");
        //arrayStrings.add("Ola PRIME");
        arrayStrings.add("uberX");
        arrayStrings.add("uberGO");
        arrayStrings.add("UberBLACK");
        mListView = (ListView) findViewById(R.id.listView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1,arrayStrings);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(this);
        ACRA.init(getApplication());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cabproviderselction, menu);
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> container, View view, int position, long id) {
        String cabprovidervalue = (String)container.getItemAtPosition(position);
        Intent intent = new Intent();
        intent.putExtra("cabprovider",cabprovidervalue);
        setResult (Activity.RESULT_OK, intent);
        finish();
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
}
