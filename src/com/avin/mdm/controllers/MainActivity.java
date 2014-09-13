package com.avin.mdm.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.avin.mdm.R;
import com.avin.mdm.config.Config;
import com.avin.mdm.config.URLPatterns;
import com.avin.mdm.receivers.MDMDevicePolicyReceiver;
import com.avin.mdm.utils.CloudMessagingHelper;
import com.avin.mdm.utils.DevicePolicyManagerHelper;
import com.avin.mdm.utils.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer.InitiateMatchResult;
import com.google.android.gms.gcm.GoogleCloudMessaging;


import android.support.v7.app.ActionBarActivity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = "Main Activity";
	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    
    String SENDER_ID = Config.ServerProjectNumber;
    private Context context;
    
    EditText emailid;
    EditText password;
    String registrationId;
    Button button,buttonUpdate,lockButton;
    DevicePolicyManager mdmPolicyManager;
    
    TextView textReg;
    
    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context= this;
        initUI();
        initPolicyManger();
        registrationId= CloudMessagingHelper.getRegistrationId(this);
//        mDisplay = (TextView) findViewById(R.id.display);
//        context = getApplicationContext();

        // Check device for Play Services APK.
        if (checkPlayServices()) {
            // If this check succeeds, proceed with normal processing.
            // Otherwise, prompt user to get valid Play Services APK.
        }
    }
	
	private void initPolicyManger(){
		this.mdmPolicyManager= DevicePolicyManagerHelper.getInstance(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		 checkPlayServices();
	}
	
	private void initUI(){
		setContentView(R.layout.activity_main);
		emailid= (EditText) findViewById(R.id.emailid);
		password= (EditText) findViewById(R.id.password);
		button= (Button) findViewById(R.id.button);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sendRegistrationIdToBackend();
			}
		});
		textReg= (TextView) findViewById(R.id.text_reg);
		buttonUpdate= (Button) findViewById(R.id.button_update);
		buttonUpdate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				updateRegInTextView();
			}
		});
		lockButton= (Button) findViewById(R.id.button_lock);
		lockButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				lockPhone();
				
			}
		});
	}
	
	private void lockPhone(){
		if(DevicePolicyManagerHelper.isDevicePolicyManagerReceiverActive(context)){
			if(null!=mdmPolicyManager){
				mdmPolicyManager.lockNow();
			}
		}
		else{
			DevicePolicyManagerHelper.registerDevicePolicyManager(context);
		}
		
	}
	
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		Log.d("-----------------------", "Returned");
		
		super.onActivityResult(arg0, arg1, arg2);
	}
	
	private void updateRegInTextView(){
		String tmp= CloudMessagingHelper.getRegistrationId(this);
		if((tmp==null)||tmp.equals("")){
			registerInBackground();
		}
		else{
			textReg.setText(tmp);
		}
	}
	
	private void updateRegInTextView(String msg){
		textReg.setText(msg);
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
    
    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
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
    
    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    
                    GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
                    String regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    


                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(context, regid);
                    
                    //sendRegistrationIdToBackend();
                    showRegIdAvailable();
                    
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
            	updateRegInTextView(msg);
            }
        }.execute(null,null,null);

			
    }
    
    private void showRegIdAvailable(){
//    	Toast.makeText(context, "Regid is now available!!", Toast.LENGTH_SHORT).show();
    }
    
    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void sendRegistrationIdToBackend() {
    	String[] params= new String[3];
    	params[0]= emailid.getText().toString();
    	params[1]= password.getText().toString();
    	params[2]= CloudMessagingHelper.getRegistrationId(this);
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                String msg = "";
             // Create a new HttpClient and Post Header
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(Config.AppServer+URLPatterns.DOMAIN_REGISTER);

                try {
                    // Add your data
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
                    nameValuePairs.add(new BasicNameValuePair("emailId", params[0]));
                    nameValuePairs.add(new BasicNameValuePair("password", params[1]));
                    nameValuePairs.add(new BasicNameValuePair("registrationId", params[2]));
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    // Execute HTTP Post Request
                    HttpResponse response = httpclient.execute(httppost);
                    
                } catch (ClientProtocolException e) {
                    // TODO Auto-generated catch block
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                }
                
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
//                mDisplay.append(msg + "\n");
            }
        }.execute(params);

			
    }
    
    private void storeRegistrationId(Context context, String regid){
    	CloudMessagingHelper.storeRegistrationId(context, regid);
    }
    
}
