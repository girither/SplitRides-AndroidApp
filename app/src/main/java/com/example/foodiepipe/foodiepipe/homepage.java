package com.example.foodiepipe.foodiepipe;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link homepage.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link homepage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class homepage extends Fragment implements AdapterView.OnItemClickListener {
    private Sample[] mSamples;
    private GridView mGridView;

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
        };
        mGridView = (GridView)view.findViewById(android.R.id.list);
        mGridView.setAdapter(new SampleAdapter());
        mGridView.setOnItemClickListener(this);
        if (savedInstanceState == null) {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            Showupcomingridesfragment fragment = new Showupcomingridesfragment();
            transaction.replace(R.id.show_upcomingrides_fragment, fragment);
            transaction.commitAllowingStateLoss();
        }
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onItemClick(AdapterView<?> container, View view, int position, long id) {
        startActivity(mSamples[position].intent);
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