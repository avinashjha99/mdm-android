package com.avin.mdm.receivers;

import com.avin.mdm.R;
import com.avin.mdm.controllers.MainActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

public class MDMBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
//		NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
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

}
