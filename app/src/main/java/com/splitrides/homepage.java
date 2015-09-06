package com.splitrides;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;


public class homepage extends Fragment implements AdapterView.OnItemClickListener,AdapterView.OnItemSelectedListener {
    private Sample[] mSamples;
    private GridView mGridView;
    static final int  SWITCH_SEARCH_RIDES = 23;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_homepage,
                container, false);
        mSamples = new Sample[]{
                new Sample(R.string.navigationdraweractivity_title, R.string.navigationdraweractivity_description,
                        Postyourrides.class),
                new Sample(R.string.searchyourrides_title, R.string.searchyourrides_description,
                        Searchyourrides.class),
                new Sample(R.string.showupcoming_title, R.string.showupcomingrides_description,
                        showupcomingridesactivity.class)
        };
        mGridView = (GridView)view.findViewById(R.id.listgridview);
        mGridView.setAdapter(new SampleAdapter());
        mGridView.setOnItemClickListener(this);
        return view;
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        String optionSelected = parent.getItemAtPosition(pos).toString();
        switch(optionSelected) {
            case "bengaluru":
                break;
            case "delhi":
                break;
            case "hyderabad":
                break;
            case "chennai":
                break;
            case "mumbai":
                break;
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

      /*  new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                View targetView = mGridView.getChildAt(0);
                ViewTarget target = new ViewTarget(targetView);
                ShowcaseView sv  = new ShowcaseView.Builder(getActivity(), true)
                        .setTarget(target)
                        .setContentTitle("YOUR MESSAGE")
                        .setStyle(R.style.CustomShowcaseTheme3)
                        .build();
            }
        }, 5000);*/
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onItemClick(AdapterView<?> container, View view, int position, long id) {
        getActivity().startActivityForResult(mSamples[position].intent,SWITCH_SEARCH_RIDES);
        getActivity().overridePendingTransition(R.animator.activity_in, R.animator.activity_out);
    }

    private class SampleAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mSamples.length;
        }

        @Override
        public Object getItem(int position) {
            return mSamples[position];
        }

        @Override
        public long getItemId(int position) {
            return mSamples[position].hashCode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.sample_dashboard_item,
                        container, false);
            }

            ((TextView) convertView.findViewById(android.R.id.text1)).setText(
                    mSamples[position].titleResId);
            ((TextView) convertView.findViewById(android.R.id.text2)).setText(
                    mSamples[position].descriptionResId);

            return convertView;
        }
    }

    private class Sample {
        int titleResId;
        int descriptionResId;
        Intent intent;

        private Sample(int titleResId, int descriptionResId, Intent intent) {
            this.intent = intent;
            this.titleResId = titleResId;
            this.descriptionResId = descriptionResId;
        }

        private Sample(int titleResId, int descriptionResId,
                       Class<? extends Activity> activityClass) {
            this(titleResId, descriptionResId,
                    new Intent(getActivity(), activityClass));
        }
    }
}