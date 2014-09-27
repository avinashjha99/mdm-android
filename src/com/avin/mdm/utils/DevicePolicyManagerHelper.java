package com.avin.mdm.utils;

import com.avin.mdm.R;
import com.avin.mdm.receivers.MDMBroadcastReceiver;
import com.avin.mdm.receivers.MDMDevicePolicyReceiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class DevicePolicyManagerHelper {
	
	
	public static DevicePolicyManager getInstance(Context context){
		Object obj=context.getSystemService(Context.DEVICE_POLICY_SERVICE);
		if(obj instanceof DevicePolicyManager){
			return (DevicePolicyManager) obj;
		}
		else{
			return null;  
		}
	}
	
	
	//call only from activity
	public static boolean registerDevicePolicyManager(Context context){
		Intent intent = new Intent(
				DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
		ComponentName mdmPolicyReceiver= new ComponentName(context, MDMDevicePolicyReceiver.class);
		intent.putExtra(
				DevicePolicyManager.EXTRA_DEVICE_ADMIN,
				mdmPolicyReceiver);
		intent.putExtra(
				DevicePolicyManager.EXTRA_ADD_EXPLANATION,
				context.getString(R.string.device_admin_instructions));
		context.startActivity(intent);
		return true;
	}
	
	public static boolean isDevicePolicyManagerReceiverActive(Context context){
		return getInstance(context).isAdminActive(new ComponentName(context, MDMDevicePolicyReceiver.class));
	}
	

}
