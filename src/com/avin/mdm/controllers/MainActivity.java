package com.avin.mdm.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.avin.mdm.R;
import com.avin.mdm.config.Config;
import com.avin.mdm.config.Prefs;
import com.avin.mdm.config.URLPatterns;
import com.avin.mdm.models.Account;
import com.avin.mdm.models.AppPackage;
import com.avin.mdm.models.AppPackageRequest;
import com.avin.mdm.models.CallRecord;
import com.avin.mdm.models.CallRecordRequest;
import com.avin.mdm.receivers.MDMDevicePolicyReceiver;
import com.avin.mdm.utils.CloudMessagingHelper;
import com.avin.mdm.utils.DevicePolicyManagerHelper;
import com.avin.mdm.utils.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer.InitiateMatchResult;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;


import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
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
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {

    private static final String TAG = "Main Activity";
	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    
    String SENDER_ID = Config.ServerProjectNumber;
    private Context context;
    
    EditText emailid;
    EditText password;
    EditText fName;
    EditText lName;
    String registrationId;
    Button button,buttonUpdate,lockButton,updateAppAnalyticsButton,button_call_analytics;
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
		if(!this.mdmPolicyManager.isAdminActive(this.getComponentName())){
			DevicePolicyManagerHelper.registerDevicePolicyManager(this);
		}
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
		fName= (EditText) findViewById(R.id.fName);
		lName= (EditText) findViewById(R.id.lName);
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
		updateAppAnalyticsButton= (Button) findViewById(R.id.button_app_analytics);
		updateAppAnalyticsButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				sendAppAnalyticsToBackend();				
			}
		});
		button_call_analytics= (Button) findViewById(R.id.button_call_analytics);
		button_call_analytics.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				sendCallAnalyticsToBackend();				
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
    	if(!Utils.readStringFromSharedPref(context, Prefs.PROPERTY_EMAILID, "").equals("")){
    		Toast.makeText(context, "Already Registered!", Toast.LENGTH_SHORT).show();
    		return;
    	}
    	String[] params= new String[5];
    	params[0]= emailid.getText().toString();
    	Utils.writeStringToSharedPref(this, Prefs.PROPERTY_EMAILID, params[0]);
    	params[1]= password.getText().toString();
    	Utils.writeStringToSharedPref(this, Prefs.PROPERTY_PASSWORD, params[1]);
    	params[2]= CloudMessagingHelper.getRegistrationId(this);
    	params[3]= fName.getText().toString();
    	params[4]= lName.getText().toString();
    	if(params[3]==null||params[4]==null||params[0]==null||params[1]==null||params[3].equals("")||params[4].equals("")||params[0].equals("")||params[1].equals("")){
    		Toast.makeText(context, "All fields are compulsory! Please fill them all before registering.", Toast.LENGTH_SHORT).show();
    	}
    	//write these values to Prefs aswell for future uses
    	
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                String msg = "";
             // Create a new HttpClient and Post Header
                HttpClient httpclient = new DefaultHttpClient();
                Log.d("URL is ----------->", Config.AppServer+URLPatterns.DOMAIN_REGISTER);
                HttpPost httppost = new HttpPost(Config.AppServer+URLPatterns.DOMAIN_REGISTER);

                try {
                	Account account= new Account();
                	account.setEmailId(params[0]);
                	account.setPassword(params[1]);
                	account.setCloudId(params[2]);
                	account.setFirstName(params[3]);
                	account.setLastName(params[4]);
                	Gson gson= new Gson();
                    // Add your data
//                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
//                    nameValuePairs.add(new BasicNameValuePair("emailId", params[0]));
//                    nameValuePairs.add(new BasicNameValuePair("password", params[1]));
//                    nameValuePairs.add(new BasicNameValuePair("cloudId", params[2]));
                	StringEntity entity= new StringEntity(gson.toJson(account));
                	entity.setContentType("application/json");
                	httppost.setEntity(entity);
//                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    // Execute HTTP Post Request
                    HttpResponse response = httpclient.execute(httppost);
                    msg= response.getEntity().toString();
                    
                } catch (ClientProtocolException e) {
                    msg= "ClientProtocolException";
                } catch (IOException e) {
                	 msg= "IOException";
                }
                
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
            	Log.d("Sent the request", "response is"+ msg);
//                mDisplay.append(msg + "\n");
            }
        }.execute(params);

			
    }
    
    private void sendAppAnalyticsToBackend(){
    	if(Utils.readStringFromSharedPref(context, Prefs.PROPERTY_EMAILID, "").equals("")){
    		Toast.makeText(context, "Not Registered yet!!", Toast.LENGTH_SHORT).show();
    		return;
    	}
    	
    	Gson gson= new Gson();
    	List<AppPackage> pckgs= Utils.getAppPackageList(this);
    	AppPackageRequest request= Utils.createAppPackageRequestFromAppPackages(this, pckgs);
    	String postBody= gson.toJson(request);
    	String params[]= new String[1];
    	params[0]= postBody;
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                String msg = "";
             // Create a new HttpClient and Post Header
                HttpClient httpclient = new DefaultHttpClient();
                Log.d("URL is ----------->", Config.AppServer+URLPatterns.DOMAIN_APP_ANALYTICS);
                HttpPost httppost = new HttpPost(Config.AppServer+URLPatterns.DOMAIN_APP_ANALYTICS);

                try {
                	StringEntity entity= new StringEntity(params[0]);
                	entity.setContentType("application/json");
                	httppost.setEntity(entity);
//                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    // Execute HTTP Post Request
                    HttpResponse response = httpclient.execute(httppost);
                    msg= response.getEntity().toString();
                    
                } catch (ClientProtocolException e) {
                    msg= "ClientProtocolException";
                } catch (IOException e) {
                	 msg= "IOException";
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
            	Log.d("Sent the request", "response is"+ msg);
//                mDisplay.append(msg + "\n");
            }
        }.execute(params);
    }
    
    private void sendCallAnalyticsToBackend(){
    	if(Utils.readStringFromSharedPref(context, Prefs.PROPERTY_EMAILID, "").equals("")){
    		Toast.makeText(context, "Not Registered yet!!", Toast.LENGTH_SHORT).show();
    		return;
    	}
    	
    	Gson gson= new Gson();
    	List<CallRecord> calls= Utils.getCallRecordList(this, 20);
    	CallRecordRequest request= Utils.createCallRecordRequestFromAppPackages(this, calls);
    	String postBody= gson.toJson(request);
    	String params[]= new String[1];
    	params[0]= postBody;
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                String msg = "";
             // Create a new HttpClient and Post Header
                HttpClient httpclient = new DefaultHttpClient();
                Log.d("URL is ----------->", Config.AppServer+URLPatterns.DOMAIN_CALL_ANALYTICS);
                HttpPost httppost = new HttpPost(Config.AppServer+URLPatterns.DOMAIN_CALL_ANALYTICS);
                try {
                	StringEntity entity= new StringEntity(params[0]);
                	entity.setContentType("application/json");
                	httppost.setEntity(entity);
//                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    // Execute HTTP Post Request
                    HttpResponse response = httpclient.execute(httppost);
                    msg= response.getEntity().toString();
                    
                } catch (ClientProtocolException e) {
                    msg= "ClientProtocolException";
                } catch (IOException e) {
                	 msg= "IOException";
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
            	Log.d("Sent the request", "response is"+ msg);
//                mDisplay.append(msg + "\n");
            }
        }.execute(params);
    }
    
    private void storeRegistrationId(Context context, String regid){
    	CloudMessagingHelper.storeRegistrationId(context, regid);
    }
    
}
