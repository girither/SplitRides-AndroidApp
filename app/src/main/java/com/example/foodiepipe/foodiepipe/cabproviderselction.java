package com.example.foodiepipe.foodiepipe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
    }

    @Override
    public void onItemClick(AdapterView<?> container, View view, int position, long id) {
        String cabprovidervalue = (String)container.getItemAtPosition(position);
        Intent intent = new Intent();
        intent.putExtra("cabprovider",cabprovidervalue);
        setResult (Activity.RESULT_OK, intent);
        finish();
    }
}
