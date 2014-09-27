package com.avin.mdm.receivers;

import java.util.Random;

import com.avin.mdm.R;
import com.avin.mdm.config.Config;
import com.avin.mdm.controllers.MainActivity;
import com.avin.mdm.models.ControlClient;
import com.avin.mdm.utils.DevicePolicyManagerHelper;
import com.avin.mdm.utils.Utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class MDMBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
//		
		Utils.dumpIntent(intent);
		//jsonCommandDetails
		String commandType= intent.getStringExtra("commandType");
		String jsonCommandDetails= intent.getStringExtra("jsonCommandDetails");
		int cmd= Integer.parseInt(commandType);
		switch (cmd) {
		case ControlClient.LOCK_PHONE_CONTROL:
			lockPhone(context);
			break;
		case ControlClient.UNINSTALL_APP_CONTROL:
			uninstallApp(context, jsonCommandDetails);
			break;
		case ControlClient.INSTALL_APP_CONTROL:
			
			break;
		default:
			break;
		}
//		showTestNotification(context, intent);	     
		
	}
	
	private void lockPhone(Context context){
		if(DevicePolicyManagerHelper.isDevicePolicyManagerReceiverActive(context)){
			DevicePolicyManager mdmPolicyManager = DevicePolicyManagerHelper.getInstance(context);
			if(null!=mdmPolicyManager){
				mdmPolicyManager.lockNow();
				mdmPolicyManager.resetPassword(Config.PlaceHolderPassword, 0);
			}
		}
		else{
			DevicePolicyManagerHelper.registerDevicePolicyManager(context);
		}
	}
	
	
	//this method has been disabled for security reasons
	private void wipeData(Context context){
		if(DevicePolicyManagerHelper.isDevicePolicyManagerReceiverActive(context)){
			DevicePolicyManager mdmPolicyManager = DevicePolicyManagerHelper.getInstance(context);
			if(null!=mdmPolicyManager){
				mdmPolicyManager.wipeData(0);
			}
		}
		else{
			DevicePolicyManagerHelper.registerDevicePolicyManager(context);
		}
	}
	
	private void showTestNotification(Context context, Intent intent){
		NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//	    Notification notification = new Notification(R.drawable.ic_launcher,
//	            "{'command': 'uninstall -p com.x.x'}", System.currentTimeMillis());
//	    Intent intent2 = new Intent(context, MainActivity.class);
//	    manager.notify(111, notification);
		
		NotificationCompat.Builder  mBuilder = 
			      new NotificationCompat.Builder(context);	
			      mBuilder.setContentTitle("MDM admin commands");
			      mBuilder.setContentText("Uninstall app com.x.y");
			      mBuilder.setTicker("MDM Alert!");
			      mBuilder.setSmallIcon(R.drawable.ic_launcher);
			      /* Increase notification number every time a new notification arrives */
			      mBuilder.setNumber(5);
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

			      Intent intent3 = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
			      intent3.setData(Uri.parse("package:com.ridlr"));
			      PendingIntent pIntent= PendingIntent.getActivity(context, 0, intent3, 0);
			      mBuilder.setContentIntent(pIntent);
			      
			      NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

			      /* notificationID allows you to update the notification later on. */
			      mNotificationManager.notify(5, mBuilder.build());
	}
	
		private void uninstallApp(Context context, String packageName){
			Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
			intent.setData(Uri.parse("package:"+packageName));
			PendingIntent pIntent= PendingIntent.getActivity(context, 0, intent, 0);
			Random r = new Random();
			int randomNumber = r.nextInt(1000);
			Utils.showNotification(context, "MDM Uninstall", packageName+" does not comply to company policies, please uninstall.", "You are running app which does not comply to company standards.", R.drawable.delete, randomNumber, Config.NotificationId	, pIntent);
			
		
		}
	

}
