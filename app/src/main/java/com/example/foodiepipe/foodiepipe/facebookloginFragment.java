package com.example.foodiepipe.foodiepipe;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.Activity;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodiepipe.foodiepipe.R;
import com.foodpipe.android.helper.ConnectionDetector;
import com.google.android.gms.common.SignInButton;

import java.util.Arrays;



/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link facebookloginFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link facebookloginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class facebookloginFragment extends Fragment implements View.OnClickListener {

    Activity activity;
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_facebooklogin,
                container, false);
        Button authButton = (Button)view.findViewById(R.id.facebook_button);
        SignInButton mSignInButton = (SignInButton)view.findViewById(R.id.sign_in_button);
        TextView signUplink = (TextView)view.findViewById(R.id.signup_link);
        Button loginButton = (Button)view.findViewById(R.id.login_button);
        loginButton.setOnClickListener(this);
        authButton.setOnClickListener(this);
        signUplink.setOnClickListener(this);
        mSignInButton.setOnClickListener(this);
        return view;
    }
    @Override

    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
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
            case R.id.sign_in_button:
            ((OnGooglePlusButtonClicked) activity).onButtonClick();
            break;
            case R.id.signup_link:
            ((OnGooglePlusButtonClicked) activity).onsignupButtonClicked();
            break;
            case R.id.login_button:
                ((OnGooglePlusButtonClicked) activity).onLoginButtonClicked();
                break;
            case R.id.facebook_button:
                ((OnGooglePlusButtonClicked) activity).onFacebookLoginButtonClicked();
                break;
        }
    }
    public interface OnGooglePlusButtonClicked {
        public void onButtonClick();
        public void onsignupButtonClicked();
        public void onLoginButtonClicked();
        public void onFacebookLoginButtonClicked();
    }


}