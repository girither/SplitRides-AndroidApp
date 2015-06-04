package com.example.foodiepipe.foodiepipe;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
/**
 * Created by gbm on 4/9/15.
 */
public class SharedPreferenceManager {
    private static Context applicationContext;
    private static SharedPreferences sparrowPreferences;

    public static void setApplicationContext(Context context) {
        applicationContext = context;
        setPreferences();
    }

    private static void setPreferences() {
        if (sparrowPreferences == null) {
            sparrowPreferences = applicationContext.getSharedPreferences("naturalForms",
                    Context.MODE_WORLD_READABLE);
        }
    }

    public static String getPreference(String key) {
        try {
            return sparrowPreferences.getString(key, null);
        } catch (NullPointerException npe) {
            Log.e("Exception in getPreferences", "Context not set properly");
        }
        return null;
    }

    public static boolean getBooleanPreference(String key) {
        try {
            return sparrowPreferences.getBoolean(key, false);
        } catch (NullPointerException npe) {
            Log.e("Exception in getPreferences", "Context not set properly");
        }
        return false;
    }

    public static int getIntPreference(String key) {
        try {
            return sparrowPreferences.getInt(key, 0);
        } catch (NullPointerException npe) {
            Log.e("Exception in getPreferences", "Context not set properly");
        }
        return 0;
    }

    public static Long getLongPreference(String key) {
        try {
            return sparrowPreferences.getLong(key, 0);
        } catch (NullPointerException npe) {
            Log.e("Exception in getPreferences", "Context not set properly");
        }
        return (long) 0;
    }

    public static void setPreference(String key, String value) {
        SharedPreferences.Editor prefsEditor = sparrowPreferences.edit();
        prefsEditor.putString(key, value);
        prefsEditor.commit();
    }

    public static void setPreference(String key, boolean value) {
        SharedPreferences.Editor prefsEditor = sparrowPreferences.edit();
        prefsEditor.putBoolean(key, value);
        prefsEditor.commit();
    }

    public static void setPreference(String key, int value) {
        SharedPreferences.Editor prefsEditor = sparrowPreferences.edit();
        prefsEditor.putInt(key, value);
        prefsEditor.commit();
    }

    public static void setPreference(String key, Long value) {
        SharedPreferences.Editor prefsEditor = sparrowPreferences.edit();
        prefsEditor.putLong(key, value);
        prefsEditor.commit();
    }
}