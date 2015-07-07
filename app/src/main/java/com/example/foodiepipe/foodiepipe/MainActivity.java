package com.example.foodiepipe.foodiepipe;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.foodpipe.android.helper.ConnectionDetector;
import com.foodpipe.android.helper.JSONParser;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

public class MainActivity extends ActionBarActivity
        implements NavigationDrawerCallbacks,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,facebookloginFragment.OnGooglePlusButtonClicked,signupage.OnSignUpsButtonClicked
{

    private static final int LOGINPAGE = 0;
    private static final int SIGNUPPAGE = 1;

    private static final int RC_SIGN_IN = 0;
    private static final int STATE_DEFAULT = 0;
    private static final int STATE_SIGN_IN = 1;
    private static final int STATE_IN_PROGRESS = 2;
    private static final String SAVED_PROGRESS = "sign_in_progress";
    private String personName="";
    private String email="";
    private String gender="";
    private UserLoginTask mAuthTask = null;
    private Retrieveauthtokengoogle mTokenTask = null;
    private ProgressDialog pDialog;
    private EditText mEmailView;
    private EditText mPasswordView;
    private EditText mEmailView_signup;
    private EditText mPasswordView_signup;
    private EditText mNameView_signup;
    private static final int FRAGMENT_COUNT = SIGNUPPAGE+1;
    private static final String TAG = "RetrieveAccessToken";
    private static final int REQUEST_CODE_SIGN_IN = 1;
    private Toolbar toolbar;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private boolean isResumed = false;
    private boolean mIsSignedIn = false;
    private GoogleApiClient mGoogleApiClient;
    private int mSignInError;
    private int mSignInProgress;
    private PendingIntent mSignInIntent;
    private CharSequence mTitle;
    private boolean mIntentInProgress;
    private Fragment[] fragments = new Fragment[FRAGMENT_COUNT];
    JSONParser jsonParser = new JSONParser();
    private static final int REQ_SIGN_IN_REQUIRED = 55664;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    MenuItem hidemenuitem;
    String SENDER_ID = "544261498132";

    private boolean mSignInClicked;
    CallbackManager callbackManager;
    GoogleCloudMessaging gcm;
    //AtomicInteger msgId = new AtomicInteger();

    String regid;


   @Override
   public void onsinuppageButtonClick() {
       attemptSignup();
   }

   @Override
   public void onButtonClick()
   {
       onregister();
       mSignInProgress = STATE_SIGN_IN;
       mGoogleApiClient.connect();
   }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    @Override
    public void onStop()
    {
        super.onStop();
    }

    @Override
    public void onFacebookLoginButtonClicked()
    {
        String token = AccessToken.getCurrentAccessToken().getToken();
        onregister();
        if(token != null){
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends", "email", "user_birthday"));
        }
        else{
            LoginManager.getInstance().logOut();
        }

    }

   @Override
   public void onsignupButtonClicked(){

       showFragment(SIGNUPPAGE, true);
   }

    @Override
    public void onLoginButtonClicked(){
       attemptLogin();
    }
    public void attemptSignup() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView_signup.setError(null);
        mPasswordView_signup.setError(null);
        mNameView_signup.setError(null);

        // Store values at the time of the login attempt.
        String Email = mEmailView_signup.getText().toString();
        String Password = mPasswordView_signup.getText().toString();
        String Name = mNameView_signup.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(Password)) {
            mPasswordView_signup.setError(getString(R.string.error_field_required));
            focusView = mPasswordView_signup;
            cancel = true;
        } else if (Password.length() < 4) {
            mPasswordView_signup.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView_signup;
            cancel = true;
        }
        if (TextUtils.isEmpty(Name)) {
            mNameView_signup.setError(getString(R.string.error_field_required));
            focusView = mNameView_signup;
            cancel = true;
        }
        // Check for a valid email address.
        if (TextUtils.isEmpty(Email)) {
            mEmailView_signup.setError(getString(R.string.error_field_required));
            focusView = mEmailView_signup;
            cancel = true;
        } else if (!Email.contains("@")) {
            mEmailView_signup.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView_signup;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.


            mAuthTask = new UserLoginTask(Email,Password,Name,"local","","male",getRegistrationId(getApplicationContext()));
            mAuthTask.execute((Void) null);
        }
    }
    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String Email = mEmailView.getText().toString();
        String Password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(Password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (Password.length() < 4) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(Email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!Email.contains("@")) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.


            mAuthTask = new UserLoginTask(Email,Password,"","local","","",getRegistrationId(getApplicationContext()));
            mAuthTask.execute((Void) null);
        }
    }

    public void onregister(){
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);

            try {
                regid = getRegistrationId(getApplicationContext());

                if (regid.isEmpty()) {
                    registerInBackground();
                }
            } catch (Throwable e) {
                Toast.makeText(getApplicationContext(),"exception happened: "+e.getClass().getName(),Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }


        } else {
           // Log.i(TAG, "No valid Google Play Services APK found.");
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        onregister();

    FacebookSdk.sdkInitialize(this.getApplicationContext());

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {

                        final Profile profile = Profile.getCurrentProfile();
                        SharedPreferenceManager.setPreference("id", profile.getId());
                        GraphRequest request = GraphRequest.newMeRequest(
                                AccessToken.getCurrentAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(
                                            JSONObject jsonObject,
                                            GraphResponse response) {
                                        try {
                                            email = jsonObject.getString("email");
                                            gender = jsonObject.getString("gender");
                                            if (mAuthTask == null) {
                                                mAuthTask = new UserLoginTask(email, "", profile.getName(), "facebook", loginResult.getAccessToken().getToken(),gender,getRegistrationId(getApplicationContext()));
                                                //Log.v("token", loginResult.getAccessToken().getToken());
                                                mAuthTask.execute((Void) null);
                                            }

                                        } catch (JSONException exe) {

                                        }
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,link,email,gender");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        LoginManager.getInstance().logOut();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        LoginManager.getInstance().logOut();
                    }
                });
        mGoogleApiClient = buildGoogleApiClient();
        if (savedInstanceState != null) {
            mSignInProgress = savedInstanceState
                    .getInt(SAVED_PROGRESS, STATE_DEFAULT);
        }
        setContentView(R.layout.activity_main);
        FragmentManager fm = getSupportFragmentManager();
        fragments[LOGINPAGE] = fm.findFragmentById(R.id.loginpage);
        fragments[SIGNUPPAGE] = fm.findFragmentById(R.id.signuppage);
        FragmentTransaction transaction = fm.beginTransaction();
        for (int i = 0; i < fragments.length; i++) {
            transaction.hide(fragments[i]);
        }
        transaction.commitAllowingStateLoss();
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        //mEmailView = (EditText) findViewById(R.id.enter_email);

        //mPasswordView = (EditText) findViewById(R.id.enter_password);
        mEmailView_signup = (EditText) findViewById(R.id.enter_email_signup);

        mPasswordView_signup = (EditText) findViewById(R.id.enter_password_signup);
        mNameView_signup = (EditText) findViewById(R.id.enter_name_signup);

    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }


    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGcmPreferences(context);
        int appVersion = getAppVersion(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("registrationid", regId);
        editor.putInt("appversion", appVersion);
        editor.commit();
    }

    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGcmPreferences(context);
        String registrationId = prefs.getString("registrationid", "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt("appversion", Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }


    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and the app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    storeRegistrationId(getApplicationContext(), regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {

            }
        }.execute(null, null, null);
    }


    private SharedPreferences getGcmPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    private GoogleApiClient buildGoogleApiClient() {
        // When we build the GoogleApiClient we specify where connected and
        // connection failed callbacks should be returned, which Google APIs our
        // app uses and which OAuth 2.0 scopes our app requests.
        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN);


        return builder.build();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        //Log.d("position to", Integer.toString(position));
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch(position) {

            case 0:

                fragmentManager.beginTransaction()
                    .replace(R.id.containernavigation, new homepageX())
                    .commitAllowingStateLoss();
                break;
            case 1:

                fragmentManager.beginTransaction()
                        .replace(R.id.containernavigation, new myridedetail())
                        .commitAllowingStateLoss();
                break;
            case 2:

                fragmentManager.beginTransaction()
                        .replace(R.id.containernavigation, new ratecard())
                        .commitAllowingStateLoss();
                break;
            case 3:
                fragmentManager.beginTransaction().replace(R.id.containernavigation,new settingsfragment())
                .commitAllowingStateLoss();
                break;
        }

    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d("onconnected", "connected");
        mTitle = getTitle();
        if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
            Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
            personName = currentPerson.getDisplayName();
            if(currentPerson.getGender() == 0)
            {
               gender = "male";
            }
            else if(currentPerson.getGender() == 1)
            {
                gender = "female";
            }
            else
            {
                gender = "other";
            }
            email = Plus.AccountApi.getAccountName(mGoogleApiClient);
        }
        mSignInProgress = STATE_DEFAULT;
        if(mTokenTask == null) {
            mTokenTask = new Retrieveauthtokengoogle(Plus.AccountApi.getAccountName(mGoogleApiClient), personName, email,gender);
            mTokenTask.execute((Void) null);
        }

    }

       @Override
    public void onConnectionSuspended(int cause) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might
        // be returned in onConnectionFailed.


        if (result.getErrorCode() == ConnectionResult.API_UNAVAILABLE) {
            // An API requested for GoogleApiClient is not available. The device's current
            // configuration might not be supported with the requested API or a required component
            // may not be installed, such as the Android Wear application. You may need to use a
            // second GoogleApiClient to manage the application's optional APIs.

        } else if (mSignInProgress != STATE_IN_PROGRESS) {
            // We do not have an intent in progress so we should store the latest
            // error resolution intent for use when the sign in button is clicked.
            mSignInIntent = result.getResolution();
            mSignInError = result.getErrorCode();

            if (mSignInProgress == STATE_SIGN_IN) {
                // STATE_SIGN_IN indicates the user already clicked the sign in button
                // so we should continue processing errors until the user is signed in
                // or they click cancel.
                resolveSignInError();
            }
        }

    }
    private void resolveSignInError() {
        if (mSignInIntent != null) {
            // We have an intent which will allow our user to sign in or
            // resolve an error.  For example if the user needs to
            // select an account to sign in with, or if they need to consent
            // to the permissions your app is requesting.

            try {
                // Send the pending intent that we stored on the most recent
                // OnConnectionFailed callback.  This will allow the user to
                // resolve the error currently preventing our connection to
                // Google Play services.
                mSignInProgress = STATE_IN_PROGRESS;
                startIntentSenderForResult(mSignInIntent.getIntentSender(),
                        RC_SIGN_IN, null, 0, 0, 0);
            } catch (SendIntentException e) {
                // The intent was canceled before it was sent.  Attempt to connect to
                // get an updated ConnectionResult.
                mSignInProgress = STATE_SIGN_IN;
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        hidemenuitem = menu.findItem(R.id.action_signout);
        if(mIsSignedIn)
        {
            hidemenuitem.setVisible(true);
        }
        else {
            hidemenuitem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_signout) {
            singoutfromapp();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showFragment(int fragmentIndex, boolean addToBackStack) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        for (int i = 0; i < fragments.length; i++) {
            if (i == fragmentIndex) {
                transaction.show(fragments[i]);
            } else {
                transaction.hide(fragments[i]);
            }
        }
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commitAllowingStateLoss();
    }
    @Override
    public void onResume() {
        super.onResume();
        SharedPreferenceManager.setApplicationContext(getApplicationContext());
        checkPlayServices();
        isResumed = true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RC_SIGN_IN:
                if (resultCode == RESULT_OK) {
                    // If the error resolution was successful we should continue
                    // processing errors.
                    mSignInProgress = STATE_SIGN_IN;
                } else {
                    // If the error resolution was not successful or the user canceled,
                    // we should stop processing errors.
                    mSignInProgress = STATE_DEFAULT;
                }

                if (!mGoogleApiClient.isConnecting()) {
                    // If Google Play services resolved the issue with a dialog then
                    // onStart is not called so we need to re-attempt connection here.
                    mGoogleApiClient.connect();
                }
                break;
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVED_PROGRESS, mSignInProgress);
    }

    @Override
    public void onPause() {
        super.onPause();
        isResumed = false;
    }
    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        mIsSignedIn = SharedPreferenceManager.getBooleanPreference("mIsSignedIn");
        if ( mIsSignedIn ) {
            // if the session is already open,
            // try to show the selection fragment
            OnLoginAuthenticated();
            ConnectionDetector cd = new ConnectionDetector(getApplicationContext());


            // Check if Internet present
            if (!cd.isConnectingToInternet()) {
                // Internet Connection is not present
                Toast.makeText(MainActivity.this,
                        "Internet Connection Error Please connect to working Internet connection", Toast.LENGTH_LONG).show();
                // stop executing code by return
                return;
            }
        } else {
            // otherwise present the splash screen
            // and ask the person to login.
           // if (getSupportFragmentManager().getBackStackEntryCount() > 0){
             //   showFragment(SIGNUPPAGE, false);
           // }
           // else{

            showFragment(LOGINPAGE, false);
            //}

        }
    }
    public void OnLoginAuthenticated() {
        showFragment(FRAGMENT_COUNT, false);
        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        mNavigationDrawerFragment.setUserDetails(SharedPreferenceManager.getPreference("name"),SharedPreferenceManager.getPreference("email"),SharedPreferenceManager.getPreference("id"));
    }
    public void singoutfromapp()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Do You want to logout?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        showFragment(LOGINPAGE, false);
                        hidemenuitem.setVisible(false);

                        SharedPreferenceManager.setPreference("mIsSignedIn", false);
                        String profile = SharedPreferenceManager.getPreference("profile");

                        if (profile.equals("facebook"))
                        {
                            LoginManager.getInstance().logOut();
                        }
                        else if (profile.equals("google"))
                        {
                            mGoogleApiClient.disconnect();
                        }
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private final String mName;
        private final String mProfile;
        private final String accessToken;
        private final String mgender;
        private final String mgcmid;

        UserLoginTask(String email, String password,String name,String profile,String accesstoken,String gender,String gcmid) {
            mEmail = email;
            mPassword = password;
            mName = name;
            mProfile = profile;
            accessToken = accesstoken;
            mgender = gender;
            mgcmid = gcmid;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Attempting Login...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
        @Override
        protected Boolean doInBackground(Void... param) {
            // Building Parameters
            /*List<NameValuePair> params = new ArrayList<NameValuePair>();

            // post album id, song id as GET parameters
            params.add(new BasicNameValuePair("name", mName));
            params.add(new BasicNameValuePair("email", mEmail));
            params.add(new BasicNameValuePair("password", mPassword));
            params.add(new BasicNameValuePair("profile", mProfile));*/
            try {
            JSONObject params = new JSONObject();
            params.put("name", mName);
            params.put("email", mEmail);
            params.put("profile", mProfile);
                params.put("password", mPassword);
                params.put("token", accessToken);
                params.put("gender",mgender);
                params.put("gcmId",mgcmid);



            // getting JSON string from URL
            String json = jsonParser.makeHttpRequest("http://radiant-peak-3095.herokuapp.com/signuplogin", "POST",
                    params);

            // Check your log cat for JSON reponse
            //Log.d("Single Track JSON: ", json);


                JSONObject jObj = new JSONObject(json);
                if(jObj != null){
                    String auth_token = jObj.getString("token");
                    String data = jObj.getString("data");
                    JSONObject jcustomerObj = new JSONObject(data);
                    String name = jcustomerObj.getString("name");
                    String customerNumber = jcustomerObj.getString("customerNumber");
                    String email = jcustomerObj.getString("email");
                    SharedPreferenceManager.setPreference("auth_token",auth_token);
                    SharedPreferenceManager.setPreference("name",name);
                    SharedPreferenceManager.setPreference("customerNumber",customerNumber);
                    SharedPreferenceManager.setPreference("email",email);
                    SharedPreferenceManager.setPreference("profile",mProfile);
                    return true;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return false;
    }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            pDialog.dismiss();

            if (success) {
                //finish();
                SharedPreferenceManager.setPreference("mIsSignedIn",true);
                hidemenuitem.setVisible(true);
                OnLoginAuthenticated();
            } else {
                if(mProfile.equals("facebook")||mProfile.equals("google"))
                {
                    if (mProfile.equals("facebook"))
                    {
                        LoginManager.getInstance().logOut();
                    }
                    if (mProfile.equals("google"))
                    {
                        mGoogleApiClient.disconnect();
                    }
                    Toast.makeText(MainActivity.this,
                            "failed", Toast.LENGTH_LONG).show();
                }
                else if (mName.isEmpty()) {
                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                    mPasswordView.requestFocus();
                }
                else {
                    mPasswordView_signup.setError(getString(R.string.error_incorrect_password));
                    mPasswordView_signup.requestFocus();
                }
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }
    public class Retrieveauthtokengoogle extends AsyncTask<Void, Void,String> {

        private final String maccountName;
        private final String mEmail;
        private final String mName;
        private final String mGender;


        Retrieveauthtokengoogle(String accountName,String name,String email,String gender) {
          maccountName = accountName;
          mEmail = email;
          mName = name;
          mGender = gender;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Fetching authtoken...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
        @Override
        protected String doInBackground(Void... param) {
            String scopes = "oauth2:profile email";
            String token = null;
            try {
                token = GoogleAuthUtil.getToken(getApplicationContext(), maccountName, scopes);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            } catch (UserRecoverableAuthException e) {
                startActivityForResult(e.getIntent(), REQ_SIGN_IN_REQUIRED);
            } catch (GoogleAuthException e) {
                Log.e(TAG, e.getMessage());
            }
            return token;
        }

        @Override
        protected void onPostExecute(final String result) {
            mTokenTask = null;
            pDialog.dismiss();
            if(result!=null)
            {
                mAuthTask = new UserLoginTask(mEmail,"",mName,"google",result,mGender,getRegistrationId(getApplicationContext()));
                mAuthTask.execute((Void) null);
            }
            else
            {
                mGoogleApiClient.disconnect();
            }
        }
    }
}
