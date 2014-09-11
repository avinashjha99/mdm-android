package com.avin.devadmin;

import java.util.Date;
import java.util.List;

import com.avin.mdm.R;

import android.support.v7.app.ActionBarActivity;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.TrafficStats;
import android.os.Bundle;
import android.provider.CallLog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        printPackageAnalytics();
        Log.d(" *** Call Analytics ***", printCallRecordAnalytics());
        printInternetUsage();
        
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void  printPackageAnalytics(){
    	PackageManager pManager= getPackageManager();
        List<PackageInfo> pacakages=pManager.getInstalledPackages(PackageManager.GET_ACTIVITIES);
        int pck=0,act=0,ser=0,con=0,rec=0;
        
        for(PackageInfo packageInfo: pacakages){
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
        
        Log.d("**************************", "Final Stats packages "+pck+", activities "+ act+", services "+ser+", content providers "+con+", receivers "+rec);
    }
    
    private String printCallRecordAnalytics(){
    	StringBuffer sb = new StringBuffer();
        Cursor managedCursor = managedQuery(CallLog.Calls.CONTENT_URI, null,
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
        return sb.toString();
    }
    
    public void printInternetUsage(){

        final PackageManager pm = getPackageManager();
        // get a list of installed apps.
        List<ApplicationInfo> packages = pm.getInstalledApplications(0);

        // loop through the list of installed packages and see if the selected
        // app is in the list
        for (ApplicationInfo packageInfo : packages) {
            // get the UID for the selected app
            int UID = packageInfo.uid;
            String package_name = packageInfo.packageName;
            ApplicationInfo app = null;
            try {
                app = pm.getApplicationInfo(package_name, 0);
            } catch (NameNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
//            String name = (String) pm.getApplicationLabel(app);
//            Drawable icon = pm.getApplicationIcon(app);
            // internet usage for particular app(sent and received)
            double received = (double) TrafficStats.getUidRxBytes(UID)

                    / (1024 * 1024);
            double send = (double) TrafficStats.getUidTxBytes(UID)
                    / (1024 * 1024);
            double total = received + send;

            if(true)
            {
            	
            	Log.d("Internet usage@@@@@@@@@@@@22", "Packag "+package_name+" total mb "+String.format( "%.2f", total )+" MB");
//                PackageInformationTotal pi=new PackageInformationTotal();
//                pi.name=name;
//                pi.packageName=package_name;
//                pi.icon=icon;               
//                pi.totalMB=String.format( "%.2f", total )+" MB";
//                pi.individual_mb=String.format( "%.2f", total );
//                totalData+=Double.parseDouble(String.format( "%.2f", total ));
//                dataHash.add(pi);
//            Log.e(name,String.format( "%.2f", total )+" MB");
            }

        }
//        Editor edit=shared.edit();
//        edit.putString("Total",String.format( "%.2f", totalData));
//        edit.commit();
    }
}
