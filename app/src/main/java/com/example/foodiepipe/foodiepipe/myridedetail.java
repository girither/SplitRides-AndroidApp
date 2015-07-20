package com.example.foodiepipe.foodiepipe;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link myridedetail.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link myridedetail#newInstance} factory method to
 * create an instance of this fragment.
 */
public class myridedetail extends Fragment {
    private FragmentTabHost mTabHost;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_myridedetail,container, false);


        mTabHost = (FragmentTabHost)rootView.findViewById(android.R.id.tabhost);
        mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.realtabcontent);

        mTabHost.addTab(mTabHost.newTabSpec("SINGLE RIDES").setIndicator("SINGLE RIDES "),
                allrides.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("JOINED RIDES").setIndicator("JOINED RIDES "),
                joinedrides.class, null);
        //mTabHost.addTab(mTabHost.newTabSpec("COMPLETED").setIndicator("COMPLETED"),
          //      completedrides.class, null);
        return rootView;

    }
}
