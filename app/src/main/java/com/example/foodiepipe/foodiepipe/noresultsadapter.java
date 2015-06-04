package com.example.foodiepipe.foodiepipe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by gbm on 5/25/15.
 */
public class noresultsadapter extends ArrayAdapter<ridedata> {
    public noresultsadapter(Context context, List<ridedata> noresultsarray) {
        super(context, 0, noresultsarray);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ridedata noresults = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.noresults_searchrides, parent, false);
        }
        TextView noresultsview = (TextView) convertView.findViewById(R.id.text1);
        noresultsview.setText(noresults.getNoresults());
        // Lookup view for data population

        return convertView;
    }

}
