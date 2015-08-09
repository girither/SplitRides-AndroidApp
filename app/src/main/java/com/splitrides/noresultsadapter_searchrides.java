package com.splitrides;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.splitrides.Postyourrides;
import com.splitrides.ridedata;

import java.util.List;

/**
 * Created by gbm on 7/1/15.
 */
public class noresultsadapter_searchrides extends ArrayAdapter<ridedata> {
    public noresultsadapter_searchrides(Context context, List<ridedata> noresultsarray) {
        super(context, 0, noresultsarray);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ridedata noresults = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_noresults_searchrides, parent, false);
        }
        TextView noresultsview = (TextView) convertView.findViewById(R.id.text1);
        noresultsview.setText(noresults.getNoresults());
        ((Button)convertView.findViewById(R.id.searchrides_postyourrides)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent postrides = new Intent(getContext(), Postyourrides.class);
                getContext().startActivity(postrides);
                Activity activity = (Activity) getContext();
                activity.overridePendingTransition(R.animator.activity_in, R.animator.activity_out);
            }
        });
        // Lookup view for data population

        return convertView;
    }

}