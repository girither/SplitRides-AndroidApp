package com.splitrides;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.readystatesoftware.viewbadger.BadgeView;


public class homepageX extends Fragment implements notificationfragment.OnDataChangedListener {
    private FragmentTabHost mTabHost;
    BadgeView badge;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onDataChanged(int number){
        updatebadge(number);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_homepage_x,container, false);


        mTabHost = (FragmentTabHost)rootView.findViewById(android.R.id.tabhost);
        mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.realtabcontent);

        mTabHost.addTab(mTabHost.newTabSpec("HOME").setIndicator(getTabIndicatorhome(mTabHost.getContext(), "HOME")),
                homepage.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("NOTIFICATIONS").setIndicator(getTabIndicator(mTabHost.getContext(), "NOTIFICATIONS")),
                notificationfragment.class, null);
        return rootView;

    }

    private View getTabIndicator(Context context, String title) {
        View view = LayoutInflater.from(context).inflate(R.layout.tab_layout, null);
        TextView tv = (TextView) view.findViewById(R.id.tab_text);
        tv.setText(title);
        TextView tv_counter = (TextView) view.findViewById(R.id.tab_counter);
        badge = new BadgeView(getActivity(), tv_counter);
        updatebadge(SharedPreferenceManager.getIntPreference("notificationcount"));

        return view;
    }
    private View getTabIndicatorhome(Context context, String title) {
        View view = LayoutInflater.from(context).inflate(R.layout.tab_layout_home, null);
        TextView tv = (TextView) view.findViewById(R.id.tab_text_home);
        tv.setText(title);
        return view;
    }

    private void updatebadge(int number){
        badge.setText(Integer.toString(number));
        badge.show();
    }

}
