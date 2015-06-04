package com.example.foodiepipe.foodiepipe;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.foodpipe.android.helper.ConnectionDetector;

/**
 * Created by gbm on 4/7/15.
 */
public class signupage extends Fragment implements View.OnClickListener  {

    Activity activity;
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup,
                container, false);
        Button signupButton = (Button)view.findViewById(R.id.signup_button);
        signupButton.setOnClickListener(this);

        return view;
    }
    @Override

    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        this.activity = activity;
    }


    @Override
    public void onClick(View view) {
        ConnectionDetector cd = new ConnectionDetector(getActivity().getApplicationContext());

        // Check if Internet present
        if (!cd.isConnectingToInternet()) {
            // Internet Connection is not present
            Toast.makeText(getActivity(),
                    "Internet Connection Error Please connect to working Internet connection", Toast.LENGTH_LONG).show();
            // stop executing code by return
            return;
        }
        switch(view.getId()) {
            case R.id.signup_button:
                ((OnSignUpsButtonClicked) activity).onsinuppageButtonClick();
                break;
        }
    }
    public interface OnSignUpsButtonClicked {
        public void onsinuppageButtonClick();
    }
}
