package com.splitrides.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.splitrides.appcallback.AppCallback;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This task has been used for validating phone number
 * of user using one time password(OTP) sms.
 * Created by abhishek purwar on 9/17/15.
 */
public class SMSOTPValidation extends AsyncTask<Object, Void, Boolean> {
    private static final int digitOTPSize = 4;

    Boolean isValidated = false;
    Activity parentActivity = null;
    Integer generatedOTP = null;
    Long dateTime = null;
    ProgressDialog pDialog = null;

    public SMSOTPValidation(Activity parentActivity) {
        this.parentActivity = parentActivity;
    }

    @Override
    protected void onPreExecute() {
        if(pDialog == null) {
            pDialog = new ProgressDialog(parentActivity);
            pDialog.setMessage("Validating phone number");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if(this.pDialog != null) {
            this.pDialog.dismiss();
        }

        if(result) {
            Toast.makeText(parentActivity.getApplicationContext(), "Phone number validated for user", Toast.LENGTH_LONG).show();
            ((AppCallback)parentActivity).successActivityCallback();
        } else {
            Toast.makeText(parentActivity.getApplicationContext(), "Please provide phone number from which user is trying to post a ride", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        if(this.pDialog != null) {
            this.pDialog.dismiss();
            this.pDialog = null;
            this.parentActivity = null;
        }
    }

    @Override
    protected Boolean doInBackground(Object... params) {
        String tFactorApiKey = "16800ac2-260a-11e5-88de-5600000c6b13";
        //String tFactorApiKey = "99bb54ed-5dc6-11e5-9a14-00163ef91450";
        String tFactorURL = "https://2factor.p.mashape.com/API/V1/" + tFactorApiKey + "/SMS/" + params[0];

        generatedOTP = ((Double)(Math.random() * 10000)).intValue();

        if(digitOTPSize - countDigit(generatedOTP) != 0) {
            generatedOTP = ((4 - countDigit(generatedOTP)) * 1000) + generatedOTP;
        }

        tFactorURL = tFactorURL + "/" + generatedOTP;

        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse httpResponse = null;
        HttpGet httpGetRequest = new HttpGet(tFactorURL);
        httpGetRequest.setHeader("X-Mashape-Key", "pApkshFbXfmshOBlHOOdnmAtrQp4p1nxVFDjsnyw3WFMwW0psw");
        httpGetRequest.setHeader("Accept", "application/json");

        try {
            dateTime = System.currentTimeMillis();
            httpResponse = httpClient.execute(httpGetRequest);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Error occurred", "Error occured on making HTTP call to OTP server");
        }

        if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

            try {
                String test = EntityUtils.toString(httpResponse.getEntity());
                System.out.println(test);
                Boolean isOTPValidated = null;
                while(isOTPValidated == null) {
                    isOTPValidated = validateSMSFromInbox(parentActivity, generatedOTP);
                }
                isValidated = isOTPValidated;
            } catch (IOException ie) {
                ie.printStackTrace();
                Log.e("Error occurred", "Error occured on reading OTP sms from message inbox");
            }
        }
        return isValidated;
    }

    /*
    * This method has been used for reading OTP message from
    * inbox and validate against given OTP
    * */
    Boolean validateSMSFromInbox(Activity activity, Integer sentOTP) {
        ContentResolver contentResolver;
        Cursor queryCursor;
        String smsBody = null;
        Integer smsOTP = null;
        Pattern pattern = Pattern.compile("(\\s(\\d{4}))$");
        Matcher matches = null;
        String[] params = new String[]{dateTime.toString()};
        Integer retryCounter = 0;

        Uri inboxURI = Uri.parse("content://sms/");
        String[] colIds = {"_id", "address", "date", "body", "type"};

        contentResolver = activity.getContentResolver();
        queryCursor = contentResolver.query(inboxURI, colIds, "address LIKE '%TFCTOR' AND type = '1' AND date > ?", params, "date desc");

        while(queryCursor.getCount() < 1 && retryCounter <= 500) {
            retryCounter++;
            queryCursor.close();
            queryCursor = contentResolver.query(inboxURI, colIds, "address LIKE '%TFCTOR' AND type = '1' AND date > ?", params, "date desc");
        }

        if (queryCursor.moveToFirst()) {
            String address = queryCursor.getString(queryCursor.getColumnIndex("address"));
            String id = queryCursor.getString(queryCursor.getColumnIndex("_id"));
            Long date = queryCursor.getLong(queryCursor.getColumnIndex("date"));
            String type = queryCursor.getString(queryCursor.getColumnIndex("type"));
            smsBody = queryCursor.getString(queryCursor.getColumnIndex("body"));
            queryCursor.close();
        } else {
            return false;
        }

        if(smsBody != null && smsBody.trim().length() > 0) {
            matches = pattern.matcher(smsBody);
            if(matches.find()) {
                smsOTP = Integer.parseInt(matches.group(matches.groupCount()));
            }
        }

        if(smsOTP.intValue() == sentOTP.intValue()) {
            return true;
        }

        return false;
    }

    /*
    * This method has been used for counting number of digits in given number
    * */
    private int countDigit(Integer generatedNum) {
        int digitCount = 0;
        while (generatedNum > 0) {
            digitCount++;
            generatedNum = generatedNum / 10;
        }
        return  digitCount;
    }

    /*
    * This method has been used for getting isValidated parameter value
    * */
    public Boolean getIsValidated() {
        return isValidated;
    }
}
