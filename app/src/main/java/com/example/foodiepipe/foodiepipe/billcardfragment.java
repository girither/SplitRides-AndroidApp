package com.example.foodiepipe.foodiepipe;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * Created by gbm on 7/24/15.
 */
public class billcardfragment extends DialogFragment {

    ratecardobject mRateText;

    public billcardfragment(ratecardobject textView) {
        mRateText = textView;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View dialogView = inflater.inflate(R.layout.rate_billfragment, null);

        builder.setView(dialogView);
        TextView rateText = (TextView)dialogView.findViewById(R.id.totalcost_value);
        rateText.setText(getResources().getString(R.string.Rs)+" "+mRateText.getPrice());
        TextView ratedistance = (TextView)dialogView.findViewById(R.id.distance_value);
        ratedistance.setText(mRateText.getDistance()+" Km");
        TextView ratetime = (TextView)dialogView.findViewById(R.id.trip_time_value);
        ratetime.setText(mRateText.getTime() +" min");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                  getActivity().finish();
            }
        });
        return builder.create();
    }
}