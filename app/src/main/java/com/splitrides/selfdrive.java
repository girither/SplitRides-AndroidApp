package com.splitrides;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class selfdrive extends Fragment {

    private GridView mGridView_comingsoon;
    SampleAdapter_noresults  selfdriveadapter_comingsoon;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_selfdrive,container, false);
        mGridView_comingsoon = (GridView)rootView.findViewById(R.id.coming_soon_list_selfdrive);
        List<ridedata> noresultsarray = new ArrayList<ridedata>();
        ridedata info = new ridedata(null,null,null);
        info.setNoresults("Coming Soon");
        noresultsarray.add(info);
        selfdriveadapter_comingsoon = new SampleAdapter_noresults(noresultsarray);
        mGridView_comingsoon.setAdapter(selfdriveadapter_comingsoon);
        return rootView;
    }


    private class SampleAdapter_noresults extends BaseAdapter {
        private List<ridedata> mSamples;
        public SampleAdapter_noresults(List<ridedata> myDataset) {
            mSamples = myDataset;
        }

        @Override
        public int getCount() {
            return mSamples.size();
        }

        @Override
        public Object getItem(int position) {
            return mSamples.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mSamples.get(position).hashCode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.noresults_searchrides,
                        container, false);
            }
            TextView noresultsview = (TextView) convertView.findViewById(R.id.text1);
            noresultsview.setText(mSamples.get(position).getNoresults());
            // Lookup view for data population

            return convertView;
        }
    }

}
