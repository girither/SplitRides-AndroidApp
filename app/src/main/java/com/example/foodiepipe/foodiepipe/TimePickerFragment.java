package com.example.foodiepipe.foodiepipe;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by gbm on 4/26/15.
 */
public  class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    TextView mResultText;
    TextView mResultTextHidden;
    String mCurrentDate;

    public TimePickerFragment(TextView textView,TextView TextHidden,String currentdate) {
        mResultText = textView;
        mResultTextHidden = TextHidden;
        mCurrentDate = currentdate;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int seconds = c.get(Calendar.SECOND);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        int seconds = 0;
        try {
            String timefragmentsettime = new StringBuilder().append(pad(hourOfDay))
                    .append(":").append(pad(minute)).append(":").append(pad(seconds)).toString();
            String timefragmentsetdatetime = new StringBuilder().append(mCurrentDate).append(" ").append(timefragmentsettime).toString();
            Date time1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(timefragmentsetdatetime);
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
            String showTime = sdf.format(time1);
            Calendar cal = Calendar.getInstance();
            if(time1.before(cal.getTime())) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Invalid Ride Time! Make Sure your pickuptime is atleast 15 minutes from now!")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //do things
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
            else {
                mResultTextHidden.setText(timefragmentsettime);
                mResultText.setText(showTime);
            }
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
    }
    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }
}