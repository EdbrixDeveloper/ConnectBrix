package com.edbrix.connectbrix.baseclass;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import com.edbrix.connectbrix.R;
import com.edbrix.connectbrix.activities.SchoolListActivity;
import com.edbrix.connectbrix.app.Config;
import com.edbrix.connectbrix.commons.AlertDialogManager;
import com.edbrix.connectbrix.commons.DialogManager;
import com.edbrix.connectbrix.commons.GlobalMethods;
import com.edbrix.connectbrix.commons.ToastMessage;
import com.edbrix.connectbrix.utils.NotificationUtils;
import com.edbrix.connectbrix.utils.SessionManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.calendar.CalendarScopes;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Random;

import pub.devrel.easypermissions.EasyPermissions;


public class BaseActivity extends AppCompatActivity {

    //    ConnectivityMonitor connectivityMonitor;
    protected Context mContext;
    protected OnFragmentBackPressedListener onFragmentBackPressedListener;
    DialogManager dialogManager;
    GlobalMethods globalMethods;
    private ToastMessage toastMessage;
    private SessionManager sessionManager;

    public static String TicketID = "TKT_0";

    public static String checkOutBatteryData = "0";
    public static String checkInBatteryData = "0";

    public static boolean isDuplicateQRcode = false;


    //used for google sign in
    public GoogleSignInOptions gso;
    public GoogleSignInClient mGoogleSignInClient;
    /* private FirebaseAuth mAuth;*/
    /*public SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
    public SharedPreferences.Editor editor = settings.edit();*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(this);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        dialogManager = new DialogManager(this);
        globalMethods = new GlobalMethods();
        toastMessage = new ToastMessage(this);
//        connectivityMonitor = new ConnectivityMonitor(this, erisConnectionListener);
        mContext = this;

        // Configure Google Sign In
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        displayFirebaseRegId();


    }

    @Override
    protected void onResume() {
        super.onResume();
        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
//        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
//                new IntentFilter(Config.PUSH_NOTIFICATION));
        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    protected void showToast(String msg, int timeDuration) {
        toastMessage.showToastMsg(msg, timeDuration);
    }

    protected void showToast(String msg) {
        toastMessage.showToastMsg(msg, Toast.LENGTH_LONG);
    }

    protected void cancelToast() {
        toastMessage.cancelToast();
    }

    protected AlertDialogManager getAlertDialogManager() {
        return dialogManager.getAlertDialogManager();
    }

    protected GlobalMethods getGlobalMethods() {
        return globalMethods;
    }

    protected void showBusyProgress() {
        dialogManager.showBusyProgress();
    }

    protected void showBusyProgress(String message) {
        dialogManager.showBusyProgress(message);
    }

    protected void hideBusyProgress() {
        dialogManager.hideBusyProgress();
    }

    protected void onNetworkStatusChanged(boolean status) {

    }

    public void setOnFragmentBackPressedListener(OnFragmentBackPressedListener onFragmentBackPressedListener) {
        this.onFragmentBackPressedListener = onFragmentBackPressedListener;
    }

    protected boolean isTablet() {
        boolean xlarge = ((this.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >=
                Configuration.SCREENLAYOUT_SIZE_XLARGE);
        boolean large = ((this.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >=
                Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large);
    }

    protected int getScreenOrientation() {
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        return display.getRotation();
    }

    public interface OnFragmentBackPressedListener {
        public void doBack();
    }

    protected BroadcastReceiver mRegistrationBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // checking for type intent filter
            if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                // gcm successfully registered
                // now subscribe to `global` topic to receive app wide notifications
                FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                displayFirebaseRegId();

            } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                // new push notification is received

                String message = intent.getStringExtra("message");

                showToast("Push notification: " + message);

            }
        }
    };

    // Fetches reg id from shared preferences
    // and displays on the screen
    private void displayFirebaseRegId() {

        String regId = sessionManager.getSessionFCMToken();

        Log.e("BaseActivity", "Firebase reg id: " + regId);

       /*if (!TextUtils.isEmpty(regId))
           showToast("Firebase Reg Id: " + regId);
        else
            showToast("Firebase Reg Id is not received yet!");*/

    }

    public Bitmap decodeFromBase64ToBitmap(String encodedImage) {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;

    }

    public static int generateRandomIntIntRange(int min, int max) {
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }


}
