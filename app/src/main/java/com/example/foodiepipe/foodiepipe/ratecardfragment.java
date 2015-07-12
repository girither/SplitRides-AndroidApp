package com.example.foodiepipe.foodiepipe;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * Created by gbm on 7/4/15.
 */
public class ratecardfragment extends DialogFragment {

    String mRateText;

    public ratecardfragment(String textView) {
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
        TextView rateText = (TextView)dialogView.findViewById(R.id.textView3);
        rateText.setText(Html.fromHtml("<big><b>"+"&#8377;"+ " " +mRateText+"</b></big>"));
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        return builder.create();
    }
}
