package com.avin.mdm.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.avin.mdm.config.Prefs;

public class CloudMessagingHelper {

	private static final String TAG = "Cloud Messaging Helper";

	/**
	 * Gets the current registration ID for application on GCM service.
	 * <p>
	 * If result is empty, the app needs to register.
	 *
	 * @return registration ID, or empty string if there is no existing
	 *         registration ID.
	 */
	public static String getRegistrationId(Context context) {
	    final SharedPreferences prefs = getGCMPreferences(context);
	    String registrationId = prefs.getString(Prefs.PROPERTY_REG_ID, "");
	    if (registrationId.isEmpty()) {
	        Log.i(TAG, "Registration not found.");
	        return "";
	    }
	    // Check if app was updated; if so, it must clear the registration ID
	    // since the existing regID is not guaranteed to work with the new
	    // app version.
	    int registeredVersion = prefs.getInt(Prefs.PROPERTY_APP_VERSION, Integer.MIN_VALUE);
	    int currentVersion = Utils.getAppVersion(context);
	    if (registeredVersion != currentVersion) {
	        Log.i(TAG, "App version changed.");
	        return "";
	    }
	    return registrationId;
	}
	
	public static void storeRegistrationId(Context context, String regId){
		 final SharedPreferences prefs = getGCMPreferences(context);
		    int appVersion = Utils.getAppVersion(context);
		    Log.i(TAG, "Saving regId on app version " + appVersion);
		    SharedPreferences.Editor editor = prefs.edit();
		    editor.putString(Prefs.PROPERTY_REG_ID, regId);
		    editor.putInt(Prefs.PROPERTY_APP_VERSION, appVersion);
		    editor.commit();
		
	}
	
	/**
	 * @return Application's {@code SharedPreferences}.
	 */
	public static SharedPreferences getGCMPreferences(Context context) {
	    // This sample app persists the registration ID in shared preferences, but
	    // how you store the regID in your app is up to you.
	    return context.getSharedPreferences(Prefs.PROPERTY_SHARED_PREF,
	            Context.MODE_PRIVATE);
	}
	
	
}
