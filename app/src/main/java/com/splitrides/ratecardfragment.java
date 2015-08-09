package com.splitrides;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.splitrides.ratecardobject;

/**
 * Created by gbm on 7/4/15.
 */
public class ratecardfragment extends DialogFragment {

    ratecardobject mRateText;

    public ratecardfragment(ratecardobject textView) {
        mRateText = textView;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View dialogView = inflater.inflate(R.layout.fragment_ratefragment, null);

        builder.setView(dialogView);
        TextView rateText = (TextView)dialogView.findViewById(R.id.estimatecost_value);
        rateText.setText(getResources().getString(R.string.Rs)+" "+mRateText.getPrice());
        TextView ratedistance = (TextView)dialogView.findViewById(R.id.distance_value);
        ratedistance.setText(mRateText.getDistance() +" Km");
        TextView ratetime = (TextView)dialogView.findViewById(R.id.trip_time_value);
        ratetime.setText(mRateText.getTime() +" min");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        return builder.create();
    }
}
