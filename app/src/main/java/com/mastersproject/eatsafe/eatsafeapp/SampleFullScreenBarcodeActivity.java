package com.mastersproject.eatsafe.eatsafeapp;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.mastersproject.eatsafe.connectivity.ServerConnectivity;
import com.mastersproject.eatsafe.connectivity.StringValues;
import com.mirasense.scanditsdk.ScanditSDKAutoAdjustingBarcodePicker;
import com.mirasense.scanditsdk.ScanditSDKBarcodePicker;
import com.mirasense.scanditsdk.ScanditSDKScanSettings;
import com.mirasense.scanditsdk.interfaces.ScanditSDKCaptureListener;
import com.mirasense.scanditsdk.interfaces.ScanditSDKCode;
import com.mirasense.scanditsdk.interfaces.ScanditSDKOnScanListener;
import com.mirasense.scanditsdk.interfaces.ScanditSDKScanSession;
import com.mirasense.scanditsdk.interfaces.ScanditSDKSearchBarListener;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Example for a full-screen barcode scanning activity using the Scandit
 * Barcode picker.
 *
 * The activity does the following:
 *
 *  - starting the picker full-screen mode
 *  - configuring the barcode picker with the settings defined in the
 *    SettingsActivity.
 *  - registering a listener to get notified whenever a barcode gets
 *    scanned. Upon a successful scan, the barcode scanner is paused and
 *    the recognized barcode is displayed in an overlay. When the user
 *    taps the screen, barcode recognition is resumed.
 *
 * For non-fullscreen barcode scanning take a look at the ScanditSDKDemo
 * class.
 *
 * Copyright 2014 Scandit AG
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class SampleFullScreenBarcodeActivity
		extends Activity
		implements ScanditSDKOnScanListener,
		           ScanditSDKSearchBarListener,
		           ScanditSDKCaptureListener {

    // The main object for recognizing a displaying barcodes.
    private ScanditSDKBarcodePicker mBarcodePicker;
	private Button mBarcodeSplash = null;
    private UIHandler mHandler = null;
    private static ServerConnectivity sr;
    private static String jsonString;
    // Enter your Scandit SDK App key here.
    // Your Scandit SDK App key is available via your Scandit SDK web account.
    public static final String sScanditSdkAppKey = "bGay3Izt7KzoUW3y/no9Kk9+CYMTHqiBfe3jiuHlACA";


    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sr = new ServerConnectivity();
		super.onCreate(savedInstanceState);
        mHandler = new UIHandler(this);
        // We keep the screen on while the scanner is running.
        getWindow().addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);


        // Initialize and start the bar code recognition.
        initializeAndStartBarcodeScanning();
    }
    
    @Override
    protected void onPause() {
		super.onPause();
        // When the activity is in the background immediately stop the 
        // scanning to save resources and free the camera.
        mBarcodePicker.stopScanning();

    }
    
    @Override
    protected void onResume() {
		super.onResume();
        // Once the activity is in the foreground again, restart scanning.
    	mBarcodePicker.startScanning();
    }

    /**
     * Initializes and starts the bar code scanning.
     */
    public void initializeAndStartBarcodeScanning() {
        // Switch to full screen.
        getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN,
                             LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        SettingsActivity.setActivityOrientation(this);

		// Set all scan settings according to the settings activity. Typically
		// you will hard-code the settings for your app and do not need a settings
		// activity.
		ScanditSDKScanSettings settings = SettingsActivity.getSettings(this);
        
        //settings.setScanningHotSpot(0.25f, 0.25f);


		// the following code caching and duplicate filter values are the
		// defaults, they are nevertheless listed here to introduce them.

		// keep codes forever
		settings.setCodeCachingDuration(-1);
		// classify codes as duplicates if the same data/symbology is scanned
		// within 500ms.
		settings.setCodeDuplicateFilter(500);

        // We instantiate the automatically adjusting barcode picker that will
        // choose the correct picker to instantiate. Be aware that this picker
        // should only be instantiated if the picker is shown full screen as the
        // legacy picker will rotate the orientation and not properly work in
        // a view hierarchy.
        mBarcodePicker = new ScanditSDKAutoAdjustingBarcodePicker(this, sScanditSdkAppKey,
											                      settings);
		// Apply UI settings to barcode picker
		SettingsActivity.applyUISettings(this, mBarcodePicker.getOverlayView());
		
		// Add listeners for scan events and search bar events (only needed if the bar is visible).
        mBarcodePicker.addOnScanListener(this);
        mBarcodePicker.getOverlayView().addSearchBarListener(this);

        // Add both views to activity, with the scan GUI on top.
        setContentView(mBarcodePicker);

        // If you want to process the individual frames yourself, add a capture
        // listener.
        // mBarcodePicker.setCaptureListener(this);
    }

	@Override
	public void didScan(ScanditSDKScanSession session) {
		List<ScanditSDKCode> newlyDecoded = session.getNewlyDecodedCodes();
        // because the callback is invoked inside the thread running the barcode
        // recognition, any UI update must be posted to the UI thread.
        // In this example, we want to show the first decoded barcode in a
        // splash screen covering the full display.
        Message msg = mHandler.obtainMessage(UIHandler.SHOW_BARCODE,
                                             newlyDecoded.get(0));

        mHandler.sendMessage(msg);
        System.out.println("In didscan method of full scan --> message generated : "+ msg.toString());
        // pause scanning and clear the session. The scanning itself is resumed
        // when the user taps the screen.

        session.pauseScanning();
        session.clear();
	}
	
	@Override
	public void didEnter(String entry) {
        Message msg = mHandler.obtainMessage(UIHandler.SHOW_SEARCH_BAR_ENTRY, entry);
        mHandler.sendMessage(msg);
        
        mBarcodePicker.pauseScanning();
	}



	@Override
    public void didCaptureImage(byte[] data, int width, int height) {
        // Here you can process the image frame contained in data. Frames are
        // always in a landscape right orientation and in YCrCb format.
    }
    
    public void didCancel() {
        mBarcodePicker.stopScanning();
        finish();
    }
    
    @Override
    public void onBackPressed() {
        mBarcodePicker.stopScanning();
        mBarcodeSplash = null;
        finish();
    }

    /*public static void showNotification(String notificationMessage, Activity mActivity){

        System.out.println("inside show notifi - ");
        // define sound URI, the sound to be played when there's a notification
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // intent triggered, you can add other intent for other actions
        Intent intent = new Intent(mActivity, NotificationReceiver.class);
        PendingIntent pIntent = PendingIntent.getActivity(mActivity, 0, intent, 0);
        // this is it, we'll build the notification!
        // in the addAction method, if you don't want any icon, just set the first param to 0
        System.out.println("intent - "+intent);
        System.out.println("pintent - "+pIntent);

        Notification mNotification = new Notification.Builder(this)

                .setContentTitle("Scan Result")
                .setContentText("Here's the result of your scan!")
                .setSmallIcon(R.drawable.ic_restaurant)
                .setContentIntent(pIntent)
                .setSound(soundUri)
                .addAction(R.drawable.ic_restaurant, "View", pIntent)
                .addAction(0, "Remind", pIntent)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // If you want to hide the notification after it was selected, do the code below
        // myNotification.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(0, mNotification);
    }*/

    static private class UIHandler extends Handler {
        public static final int SHOW_BARCODE = 0;
        public static final int SHOW_SEARCH_BAR_ENTRY = 1;
        public WeakReference<SampleFullScreenBarcodeActivity> mActivity;
        UIHandler(SampleFullScreenBarcodeActivity activity) {
            mActivity = new WeakReference<SampleFullScreenBarcodeActivity>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            System.out.println("In handle message : msg is : "+msg.toString());
            switch (msg.what) {
                case SHOW_BARCODE:
                    //showSplash(createMessage((ScanditSDKCode)msg.obj));
                    sendBarcode(createMessage((ScanditSDKCode)msg.obj));
                     break;
                case SHOW_SEARCH_BAR_ENTRY:
                    showSplash((String)msg.obj);
                    //sendBarcode((String) msg.obj);
                    break;
            }

        }

        private String createMessage(ScanditSDKCode code) {
            String message = "";
            String data = code.getData();
            // cleanup the data somewhat by replacing control characters contained in
            // some of the barcodes with hash signs and truncating long barcodes
            // to reasonable lengths.
            String cleanData = new String();
            for (int i = 0; i < data.length(); ++i) {
                char c = data.charAt(i);
                cleanData += Character.isISOControl(c) ? '#' : c;
            }
            if (cleanData.length() > 30) {
                cleanData = cleanData.substring(0, 25)+"[...]";
            }
            message += cleanData;
            message += "\n\n(" + code.getSymbologyString()+")";
            System.out.println("in create message method, msg here is "+message);
            return message;
        }

        public void sendBarcode(String msg){
           System.out.println("in send barcode func , msg is"+ msg);
           List<NameValuePair> params = new ArrayList<NameValuePair>();
            String[] upcCode = msg.split("\\n");
            System.out.println("username - "+LoginActivity.username);
           params.add(new BasicNameValuePair("upc_code", upcCode[0]));
           params.add(new BasicNameValuePair("username", LoginActivity.username));
           JSONObject json = sr.getJSON(StringValues.GET_ITEM_INFO_URL, params);
           System.out.println("Json object in sendbarcode func is "+json);
           SampleFullScreenBarcodeActivity activity = mActivity.get();
           if (json != null) {
               try {
                   jsonString = json.getString("response");
                   if (json.getBoolean("res")) {
                       Toast.makeText(activity, jsonString, Toast.LENGTH_LONG).show();
                       //showNotification(jsonString, activity);
                       showSplash(jsonString);
                   }
               } catch (JSONException e) {
                   e.printStackTrace();
               }
           }
        }

        private void showSplash(String msg) {
            System.out.println("in show splash method, msg here is "+msg);
            SampleFullScreenBarcodeActivity activity = mActivity.get();
            activity.mBarcodeSplash = new Button(activity);
            activity.mBarcodeSplash.setTextColor(Color.WHITE);
            activity.mBarcodeSplash.setTextSize(30);
            activity.mBarcodeSplash.setGravity(Gravity.CENTER);
            activity.mBarcodeSplash.setBackgroundColor(0xFF39C1CC);
            activity.mBarcodeSplash.setText(msg);
            RelativeLayout layout = (RelativeLayout)activity.mBarcodePicker;
            layout.addView(activity.mBarcodeSplash, LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT);
            activity.mBarcodePicker.pauseScanning();

            activity.mBarcodeSplash.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SampleFullScreenBarcodeActivity activity = mActivity.get();
                    activity.mBarcodePicker.resumeScanning();
                    ((RelativeLayout) activity.mBarcodePicker).removeView(activity.mBarcodeSplash);
                    activity.mBarcodeSplash = null;
                }
            });
            activity.mBarcodeSplash.requestFocus();
        }

    }
}
