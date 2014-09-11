package com.avin.mdm.utils;

import java.util.Date;
import java.util.List;

import com.avin.mdm.config.Prefs;
import com.avin.mdm.models.AppPackage;
import com.avin.mdm.models.CallRecord;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.TrafficStats;
import android.provider.CallLog;
import android.util.Log;

public class Utils {

	
	private static final String TAG = "Utils";

	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	public static int getAppVersion(Context context) {
	    try {
	        PackageInfo packageInfo = context.getPackageManager()
	                .getPackageInfo(context.getPackageName(), 0);
	        return packageInfo.versionCode;
	    } catch (NameNotFoundException e) {
	        // should never happen
	        throw new RuntimeException("Could not get package name: " + e);
	    }
	}

	public static List<AppPackage> getAppPackageList(Context context){
	    	PackageManager pManager= context.getPackageManager();
	        List<PackageInfo> pacakages=pManager.getInstalledPackages(PackageManager.GET_ACTIVITIES);
	        int pck=0,act=0,ser=0,con=0,rec=0;
	        
	        for(PackageInfo packageInfo: pacakages){
	        	//save uid and get internet usage immediately
	        	int UID = packageInfo.applicationInfo.uid;
	        	double received = (double) TrafficStats.getUidRxBytes(UID)

	                    / (1024 * 1024);
	            double send = (double) TrafficStats.getUidTxBytes(UID)
	                    / (1024 * 1024);
	            double total = received + send;
	        	
	        	
	        	Log.d("Packages-->", packageInfo.packageName);
	        	 //activities
	        	if(null!=packageInfo.activities){
	        		for(ActivityInfo aInfo: packageInfo.activities){
	            		if(null!=aInfo){
	            			Log.d("\tActivities are-->`", aInfo.name);
	            			act++;
	            		}
	            	}
	        	}
	        	 //services
	        	if(null!=packageInfo.services){
	        		for(ServiceInfo aInfo: packageInfo.services){
	            		if(null!=aInfo){
	            			Log.d("Services are-->`", aInfo.name);
	            			ser++;
	            		}
	            	}
	        	}
	        	 //contentproviders
	        	if(null!=packageInfo.providers){
	        		for(ProviderInfo aInfo: packageInfo.providers){
	            		if(null!=aInfo){
	            			Log.d("Content are-->`", aInfo.name);
	            			con++;
	            		}
	            	}
	        	}
	        	 //receivers
	        	if(null!=packageInfo.receivers){
	        		for( ActivityInfo aInfo: packageInfo.receivers){
	            		if(null!=aInfo){
	            			Log.d("Receivers are-->", aInfo.name);
	            			rec++;
	            		}
	            	}
	        	}
	        	pck++;
	        }
	        
	     return null;   
	}
	
	public static List<CallRecord> getCallRecordList(Activity activity){

    	StringBuffer sb = new StringBuffer();
        Cursor managedCursor = activity.managedQuery(CallLog.Calls.CONTENT_URI, null,
                null, null, null);
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        sb.append("Call Details :");
        while (managedCursor.moveToNext()) {
            String phNumber = managedCursor.getString(number);
            String callType = managedCursor.getString(type);
            String callDate = managedCursor.getString(date);
            Date callDayTime = new Date(Long.valueOf(callDate));
            String callDuration = managedCursor.getString(duration);
            String dir = null;
            int dircode = Integer.parseInt(callType);
            switch (dircode) {
            case CallLog.Calls.OUTGOING_TYPE:
                dir = "OUTGOING";
                break;

            case CallLog.Calls.INCOMING_TYPE:
                dir = "INCOMING";
                break;

            case CallLog.Calls.MISSED_TYPE:
                dir = "MISSED";
                break;
            }
            sb.append("\nPhone Number:--- " + phNumber + " \nCall Type:--- "
                    + dir + " \nCall Date:--- " + callDayTime
                    + " \nCall duration in sec :--- " + callDuration);
            sb.append("\n----------------------------------");
        }
        managedCursor.close();
//        return sb.toString();
        return null;
    
	}
	
}
