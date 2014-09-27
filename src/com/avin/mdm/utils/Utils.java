package com.avin.mdm.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.avin.mdm.R;
import com.avin.mdm.config.Prefs;
import com.avin.mdm.models.Account;
import com.avin.mdm.models.AppPackage;
import com.avin.mdm.models.AppPackageRequest;
import com.avin.mdm.models.CallRecord;
import com.avin.mdm.models.CallRecordRequest;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class Utils {

	
	private static final String TAG = "Utils";
	private static final String LOG_TAG = "MDM Utils";

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
		List<AppPackage> packages=new ArrayList<AppPackage>();
		
		//now popoulate the model.
	    	PackageManager pManager= context.getPackageManager();
	        List<PackageInfo> pacakages=pManager.getInstalledPackages(PackageManager.GET_ACTIVITIES);
	        int pck=0,act=0,ser=0,con=0,rec=0;
	        
	        for(PackageInfo packageInfo: pacakages){
	        	AppPackage pckg= new AppPackage();
	        	pckg.setPackageName(packageInfo.packageName);
	        	pckg.setVersion(packageInfo.versionName);
	        	
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
	        	
	        	packages.add(pckg);
	        }
	        
	     return packages;   
	}
	
	public static List<CallRecord> getCallRecordList(Activity activity, int maxCount){
		List<CallRecord> callRecords=new ArrayList<CallRecord>();
    	StringBuffer sb = new StringBuffer();
        Cursor managedCursor = activity.managedQuery(CallLog.Calls.CONTENT_URI, null,
                null, null, null);
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        sb.append("Call Details :");
        int count=0;
        while (managedCursor.moveToNext()) {
        	CallRecord callRecord= new CallRecord();
            String phNumber = managedCursor.getString(number);
            String callType = managedCursor.getString(type);
            String callDate = managedCursor.getString(date);
            Date callDayTime = new Date(Long.valueOf(callDate));
            String callDuration = managedCursor.getString(duration);
            callRecord.setPhNumber(phNumber);
            callRecord.setCallDate(callDate);
            callRecord.setCallDuration(callDuration);
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
            callRecord.setCallType(dir);
            sb.append("\nPhone Number:--- " + phNumber + " \nCall Type:--- "
                    + dir + " \nCall Date:--- " + callDayTime
                    + " \nCall duration in sec :--- " + callDuration);
            sb.append("\n----------------------------------");
            callRecords.add(callRecord);
            count++;
            if(count>maxCount-1){
            	break;
            }
        }
        managedCursor.close();
        
//        return sb.toString();
        return callRecords;
    
	}
	
	public static void showNotification(Context context, String title, String text, String ticker, int r_drawable, int notificationNumber,int notificationId, PendingIntent pendingIntentToBeLaunched ){
		NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//	    Notification notification = new Notification(R.drawable.ic_launcher,
//	            "{'command': 'uninstall -p com.x.x'}", System.currentTimeMillis());
//	    Intent intent2 = new Intent(context, MainActivity.class);
//	    manager.notify(111, notification);
		
		NotificationCompat.Builder  mBuilder = 
			      new NotificationCompat.Builder(context);	
//			      mBuilder.setContentTitle("MDM admin commands");
				mBuilder.setContentTitle(title);
//			      mBuilder.setContentText("Uninstall app com.x.y");
				 mBuilder.setContentText(text);
//			      mBuilder.setTicker("MDM Alert!");
				 mBuilder.setTicker(ticker);
//			      mBuilder.setSmallIcon(R.drawable.ic_launcher);
				 mBuilder.setSmallIcon(r_drawable);
			      /* Increase notification number every time a new notification arrives */
//			      mBuilder.setNumber(5);
				 mBuilder.setNumber(notificationNumber);
			      /* Creates an explicit intent for an Activity in your app */
//			      Intent resultIntent = new Intent(context, MainActivity.class);
//
//			      TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//			      stackBuilder.addParentStack(NotificationView.class);
//
//			      /* Adds the Intent that starts the Activity to the top of the stack */
//			      stackBuilder.addNextIntent(resultIntent);
//			      PendingIntent resultPendingIntent =
//			         stackBuilder.getPendingIntent(
//			            0,
//			            PendingIntent.FLAG_UPDATE_CURRENT
//			         );
//
//			      mBuilder.setContentIntent(resultPendingIntent);

				 
				 //------------------------set intent-----------------------
//			      Intent intent3 = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
//			      intent3.setData(Uri.parse("package:com.ridlr"));
//			      PendingIntent pIntent= PendingIntent.getActivity(context, 0, intent3, 0);
//			      mBuilder.setContentIntent(pIntent);
			      //-------------------------------------------------------------
			      mBuilder.setContentIntent(pendingIntentToBeLaunched);
			      
			      NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

			      /* notificationID allows you to update the notification later on. */
//			      mNotificationManager.notify(5, mBuilder.build());
			      mNotificationManager.notify(notificationId, mBuilder.build());
	}
	
	public static void dumpIntent(Intent i){

	    Bundle bundle = i.getExtras();
	    if (bundle != null) {
	        Set<String> keys = bundle.keySet();
	        Iterator<String> it = keys.iterator();
	        Log.e(LOG_TAG,"Dumping Intent start");
	        while (it.hasNext()) {
	            String key = it.next();
	            Log.e(LOG_TAG,"[" + key + "=" + bundle.get(key)+"]");
	        }
	        Log.e(LOG_TAG,"Dumping Intent end");
	    }
	}
	
	public static void writeStringToSharedPref(Context context, String key, String value){
		SharedPreferences prefs = context.getSharedPreferences(key, Context.MODE_PRIVATE);
		prefs.edit().putString(key, value).commit();
	}
	
	public static String readStringFromSharedPref(Context context, String key, String defValue){
		SharedPreferences prefs = context.getSharedPreferences(key, Context.MODE_PRIVATE);
		return prefs.getString(key, defValue);
	}
	
	public static AppPackageRequest createAppPackageRequestFromAppPackages(Context context, List<AppPackage> pckgs){
		AppPackageRequest request= new AppPackageRequest();
		request.setAppPackages(pckgs);
		Account account= new Account();
		account.setEmailId(readStringFromSharedPref(context, Prefs.PROPERTY_EMAILID	, "default-account@avin123.com"));
		account.setPassword(readStringFromSharedPref(context, Prefs.PROPERTY_PASSWORD, "default-password"));
		request.setAccount(account);
		return request;
	}
	
	public static CallRecordRequest createCallRecordRequestFromAppPackages(Context context, List<CallRecord> calls){
		CallRecordRequest request= new CallRecordRequest();
		request.setCallRecords(calls);
		Account account= new Account();
		account.setEmailId(readStringFromSharedPref(context, Prefs.PROPERTY_EMAILID	, "default-account@avin123.com"));
		account.setPassword(readStringFromSharedPref(context, Prefs.PROPERTY_PASSWORD, "default-password"));
		request.setAccount(account);
		return request;
	}
	
}
