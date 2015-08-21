package com.splitrides;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by gbm on 5/23/15.
 */
public class customridedataadapter extends ArrayAdapter<ridedata> {
    public customridedataadapter(Context context, List<ridedata> ridedatasarray) {
        super(context, 0, ridedatasarray);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ridedata Ridedata = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_cardview_item, parent, false);
        }
        try {
            // Lookup view for data population
            TextView ridetodayortomo = (TextView) convertView.findViewById(R.id.todayortomo_title);
            TextView timeofride = (TextView) convertView.findViewById(R.id.time_title);
            TextView source = (TextView) convertView.findViewById(R.id.source_data);
            TextView destination = (TextView) convertView.findViewById(R.id.destination_data);
            TextView joinedride_label = (TextView) convertView.findViewById(R.id.joinedride_title);
            if (Ridedata.getRideFlag().equals("jride")) {
                if (Ridedata.getJoinedridestatus().equals("started")) {
                    joinedride_label.setText("RIDE STARTED");
                } else {
                    joinedride_label.setText("JOINED RIDE");
                }
            } else {
                joinedride_label.setText("SINGLE RIDE");
            }
            // Populate the data into the template view using the data object
            source.setText(Ridedata.getSource());
            destination.setText(Ridedata.getDestination());
            String datetimeOfRides = Ridedata.getDate(), todayortomorrow;
            String dateOfRides = datetimeOfRides.split("T")[0];
            String timeofrides = datetimeOfRides.split("T")[1];
            timeofrides = timeofrides.split(".000Z")[0];
            SimpleDateFormat inFormat = new SimpleDateFormat("hh:mm aa");
            SimpleDateFormat outFormat = new SimpleDateFormat("HH:mm");
            timeofrides = inFormat.format(outFormat.parse(timeofrides));
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String currentDate = sdf.format(cal.getTime());
            Calendar nextdaycal = Calendar.getInstance();
            nextdaycal.add(Calendar.DATE, 1);
            SimpleDateFormat sdftomorrow = new SimpleDateFormat("yyyy-MM-dd");
            String tomorrowDate = sdftomorrow.format(nextdaycal.getTime());
            if (currentDate.equals(dateOfRides)) {
                todayortomorrow = "Today        ";
            } else if (tomorrowDate.equals(dateOfRides)) {
                todayortomorrow = "Tomorrow";
            } else {
                todayortomorrow = dateOfRides;
            }
            Ridedata.setTodayortomorrow(todayortomorrow);
            ridetodayortomo.setText(todayortomorrow);
            timeofride.setText(timeofrides);
        }
        catch (ParseException ex){
        }
        // Return the completed view to render on screen
        return convertView;
    }

}
