package com.edbrix.connectbrix.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.edbrix.connectbrix.Application;
import com.edbrix.connectbrix.R;
import com.edbrix.connectbrix.baseclass.BaseActivity;
import com.edbrix.connectbrix.data.UserOrganizationListData;
import com.edbrix.connectbrix.data.UserOrganizationListParentData;
import com.edbrix.connectbrix.utils.AuthConstants;
import com.edbrix.connectbrix.utils.Conditions;
import com.edbrix.connectbrix.utils.Constants;
import com.edbrix.connectbrix.utils.SessionManager;
import com.edbrix.connectbrix.volley.GsonRequest;
import com.edbrix.connectbrix.volley.SettingsMy;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


import org.json.JSONObject;

import java.util.ArrayList;

import us.zoom.sdk.InviteOptions;
import us.zoom.sdk.JoinMeetingOptions;
import us.zoom.sdk.JoinMeetingParams;
import us.zoom.sdk.MeetingService;
import us.zoom.sdk.MeetingServiceListener;
import us.zoom.sdk.MeetingStatus;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKAuthenticationListener;
import us.zoom.sdk.ZoomSDKInitializeListener;

import static com.edbrix.connectbrix.utils.Constants.APP_KEY__;
import static com.edbrix.connectbrix.utils.Constants.APP_SECRET__;

public class LoginActivity extends BaseActivity implements AuthConstants, ZoomSDKInitializeListener, MeetingServiceListener, ZoomSDKAuthenticationListener {

    private static final String TAG = LoginActivity.class.getName();

    private LinearLayout mLoginLinearLayout;
    private ImageView mImgConnectBrix;
    private EditText mEdTxtEmail;
    private EditText mEdTxtPassword;
    private Button mBtnLogin;
    private ImageView mEyeIcon;
    private TextView mTextViewForgotPassword;
    private Button mBtnJoin;
    private SignInButton googleSignIn;
    private EditText mEdTxtMeetingId;

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private boolean isPasswordVisible;
    SessionManager sessionManager;
    boolean isEmailValid = false;

    ArrayList<UserOrganizationListData> userOrganizationListData;
    boolean doubleBackToExitPressedOnce = false;

    final public int CHECK_PERMISSIONS = 123;
    public static final int RESULT_LOGIN = 5;


    //used for google sign in
   /* GoogleSignInOptions gso;
    private GoogleSignInClient mGoogleSignInClient;
   *//* private FirebaseAuth mAuth;*/
    private static final int RC_SIGN_IN = 9001;
    //----------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
        getSupportActionBar().hide(); //hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //show the activity in full screen

        sessionManager = new SessionManager(this);
        ZoomSDK zoomSDK = ZoomSDK.getInstance();

        // Configure Google Sign In
         gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        sessionManager.updateIsPasswordSkip("0");
        /*mAuth = FirebaseAuth.getInstance();*/

        if (savedInstanceState == null) {
            Log.e(TAG, "Call savedInstanceState inside onCreate method.");
            zoomSDK.initialize(LoginActivity.this, "qjDDhSsOzp5Ln0WSP0Z0LoKo86XFR4S2UIUn", "ePR5WENlisNzQVRJ8vrVeG0UGUsPza2iQ3xL", WEB_DOMAIN, this);
        }

        if (validateUser()) {
            finish();
            startActivity(new Intent(LoginActivity.this, SchoolListActivity.class));
        } else {
            setContentView(R.layout.activity_login);
            userOrganizationListData = new ArrayList<>();
            assignViews();
            hideKeyboard();
            init();
        }

        checkPermission();
        Constants.androidDeviceid = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        Log.e(BaseActivity.class.getName(), Constants.androidDeviceid);

        if (savedInstanceState != null) {
            mEdTxtEmail.setText(savedInstanceState.getString("email"));
            mEdTxtPassword.setText(savedInstanceState.getString("password"));
            mEdTxtMeetingId.setText(savedInstanceState.getString("meetingId"));
        }

    }

    private void init() {

        mTextViewForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkValidation() == true) {

                    if (isEmailValid == true) {
                        String userName = mEdTxtEmail.getText().toString().trim();
                        String password = mEdTxtPassword.getText().toString().trim();
                        doLogin(userName, password);
                    }
                }
            }
        });


        // check email field is correct
        mEdTxtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // nothing TODO
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                /*String email = mEdTxtEmail.getText().toString();
                if (!email.equals("")) {
                    if (email.matches(emailPattern) && s.length() > 0) {
                        mEdTxtEmail.setBackgroundResource(R.drawable.flash_screen_background);
                        isEmailValid = true;
                    } else {
                        mEdTxtEmail.setError("Enter valid email address");
                        mEdTxtEmail.setBackgroundResource(R.drawable.error_background);
                        isEmailValid = false;
                    }

                }*/
            }

            @Override
            public void afterTextChanged(Editable s) {
                // nothing TODO

                /*String email = mEdTxtEmail.getText().toString();
                if (!email.equals("")) {
                    if (email.matches(emailPattern) && s.length() > 0) {
                        mEdTxtEmail.setBackgroundResource(R.drawable.flash_screen_background);
                        isEmailValid = true;
                    } else {
                        mEdTxtEmail.setError("Enter valid email address");
                        mEdTxtEmail.setBackgroundResource(R.drawable.error_background);
                        isEmailValid = false;
                    }

                }*/
            }
        });

        mEdTxtEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // code to execute when EditText loses focus
                    String email = mEdTxtEmail.getText().toString();
                    if (!email.equals("")) {
                        if (email.matches(emailPattern)) {
                            mEdTxtEmail.setBackgroundResource(R.drawable.flash_screen_background);
                            isEmailValid = true;
                        } else {
                            mEdTxtEmail.setError("Enter valid email address");
                            mEdTxtEmail.setBackgroundResource(R.drawable.error_background);
                            isEmailValid = false;
                        }

                    }
                }
            }
        });

        mEyeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPasswordVisible(isPasswordVisible);
                isPasswordVisible = !isPasswordVisible;
            }
        });

        //meeting join using meeting id
        mBtnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkMeetingId() == true) {

                    String meetingId = mEdTxtMeetingId.getText().toString().trim();
                    isValidMeetingId(meetingId);
                }
            }
        });

        googleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

    }

    private void assignViews() {
        mLoginLinearLayout = (LinearLayout) findViewById(R.id.loginLinearLayout);
        mImgConnectBrix = (ImageView) findViewById(R.id.imgConnectBrix);
        mEdTxtEmail = (EditText) findViewById(R.id.edTxtEmail);
        mEdTxtPassword = (EditText) findViewById(R.id.edTxtPassword);
        mBtnLogin = (Button) findViewById(R.id.btnLogin);
        mTextViewForgotPassword = (TextView) findViewById(R.id.textViewForgotPassword);
        mEyeIcon = (ImageView) findViewById(R.id.eyeIcon);
        mBtnJoin = (Button) findViewById(R.id.btnJoin);
        mEdTxtMeetingId = (EditText) findViewById(R.id.edTxtMeetingId);
        googleSignIn = findViewById(R.id.sign_in_button);
        isPasswordVisible = false;//

        mEdTxtPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // hide virtual keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mEdTxtPassword.getWindowToken(), 0);
                    checkValidation();
                    return true;
                }
                return false;
            }
        });


        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEdTxtEmail.getWindowToken(), 0);
    }

    private boolean validateUser() {
        //sessionManager = new SessionManager(LoginActivity.this);
        if (!sessionManager.getSessionUsername().equals("") && !sessionManager.getSessionUserId().equals("") && !sessionManager.getPrefsOrganizationApiKey().equals("") && !sessionManager.getPrefsOrganizationSecretKey().equals("")) {
            return true;////user available
        } else {
            return false;////user not available
        }
    }


    private void setPasswordVisible(boolean isVisible) {
        if (isVisible) {
            mEyeIcon.setImageDrawable(ContextCompat.getDrawable(LoginActivity.this, R.drawable.ic_visibility_off_black_24dp));
            mEdTxtPassword.setTransformationMethod(new PasswordTransformationMethod());
        } else {
            mEyeIcon.setImageDrawable(ContextCompat.getDrawable(LoginActivity.this, R.drawable.ic_visibility_black_24dp));
            mEdTxtPassword.setTransformationMethod(null);
        }
    }

    private void doLogin(final String userName, final String password) {

        try {

            showBusyProgress();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("APIKEY", APP_KEY__);
            jsonObject.put("SECRETKEY", APP_SECRET__);
            jsonObject.put("Email", userName);


            GsonRequest<UserOrganizationListParentData> userOrganizationListRequest = new GsonRequest<>(Request.Method.POST, Constants.organizationList, jsonObject.toString(), UserOrganizationListParentData.class,
                    new Response.Listener<UserOrganizationListParentData>() {
                        @Override
                        public void onResponse(@NonNull UserOrganizationListParentData response) {
                            hideBusyProgress();
                            if (response.getError() != null) {
                                signOut();
                                String error = response.getError().getErrorMessage();
                                showToast(error);
                                //Toast.makeText(getApplicationContext(),error,Toast.LENGTH_LONG).show();
                            } else {

                                if (response.getSuccess() == 1) {
                                    sessionManager.updateSessionUsername(userName);
                                    sessionManager.updateSessionPassword(password);

                                    for (int i = 0; i < response.getUserOrganizationList().size(); i++) {
                                        userOrganizationListData.add(response.getUserOrganizationList().get(i));
                                    }
                                    finish();
                                    Intent intent = new Intent(LoginActivity.this, OrgnizationListActivity.class);
                                    intent.putExtra("organizationList", userOrganizationListData);
                                    intent.putExtra("comesFrom", "loginActivity");
                                    startActivityForResult(intent, RESULT_LOGIN);
                                }
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideBusyProgress();
                    showToast(SettingsMy.getErrorMessage(error));
                }
            });
            userOrganizationListRequest.setRetryPolicy(Application.getDefaultRetryPolice());
            userOrganizationListRequest.setShouldCache(false);
            Application.getInstance().addToRequestQueue(userOrganizationListRequest, "userOrganizationListRequest");

        } catch (Exception e) {

            hideBusyProgress();
            Log.e(TAG, e.getMessage());
        }

    }

    private boolean checkMeetingId() {

        String meetingId = mEdTxtMeetingId.getText().toString().trim();

        Conditions.hideKeyboard(LoginActivity.this);

        if (meetingId.isEmpty() || meetingId == null) {
            mEdTxtMeetingId.setError("Meeting ID can not be blank");
            return false;
        }
        if (meetingId.length() < 9) {
            mEdTxtMeetingId.setError("Meeting id must be more than 9 character's");
            return false;
        } else {
            return true;
        }
    }

    private void isValidMeetingId(final String meetingId) {

        try {

            showBusyProgress();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ZoomMeetingId", meetingId);


            GsonRequest<UserOrganizationListParentData> userOrganizationListRequest = new GsonRequest<>(Request.Method.POST, Constants.meetingIdValidOrNot, jsonObject.toString(), UserOrganizationListParentData.class,
                    new Response.Listener<UserOrganizationListParentData>() {
                        @Override
                        public void onResponse(@NonNull UserOrganizationListParentData response) {
                            hideBusyProgress();
                            if (response.getError() != null) {
                                String error = response.getError().getErrorMessage();
                                showToast(error);
                            } else {
                                if (response.getSuccess() == 1) {
                                    mEdTxtMeetingId.setText("");
                                    joinMeeting(meetingId);
                                }
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideBusyProgress();
                    showToast(SettingsMy.getErrorMessage(error));
                }
            });
            userOrganizationListRequest.setRetryPolicy(Application.getDefaultRetryPolice());
            userOrganizationListRequest.setShouldCache(false);
            Application.getInstance().addToRequestQueue(userOrganizationListRequest, "userOrganizationListRequest");

        } catch (Exception e) {

            hideBusyProgress();
            Log.e(TAG, e.getMessage());
        }

    }

    private boolean checkValidation() {

        String userEmail = mEdTxtEmail.getText().toString().trim();
        String userPassword = mEdTxtPassword.getText().toString().trim();

        Conditions.hideKeyboard(LoginActivity.this);

        if (userEmail.isEmpty() || userEmail == null) {
            mEdTxtEmail.setError("Email can not be blank");
            return false;
        } else if (userPassword.isEmpty() || userPassword == null) {
            mEdTxtPassword.setError("Password can not be blank");
            return false;

        } else {
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        //Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        showToast("Click back again to exit.");

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.CAMERA) +
                ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) +
                ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) +
                ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.GET_ACCOUNTS)+
                ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_NETWORK_STATE)+
                ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.WRITE_CALENDAR) /*+
                ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)*/
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(LoginActivity.this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.GET_ACCOUNTS,Manifest.permission.ACCESS_NETWORK_STATE,Manifest.permission.WRITE_CALENDAR},
                    CHECK_PERMISSIONS);
        } else {
            //Toast.makeText(LoginActivity.this,"Already Granted",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case CHECK_PERMISSIONS: {
                //boolean isPerpermissionForAllGranted = false;
                if (grantResults.length > 0) {
                    boolean CAMERA = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean READ_EXTERNAL_STORAGE = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean WRITE_EXTERNAL_STORAGE = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean GET_ACCOUNTS = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                    boolean ACCESS_NETWORK_STATE = grantResults[4] == PackageManager.PERMISSION_GRANTED;
                    boolean WRITE_CALENDAR = grantResults[5] == PackageManager.PERMISSION_GRANTED;
                   /* boolean ACCESS_COARSE_LOCATION = grantResults[4] == PackageManager.PERMISSION_GRANTED;*/

                    if (CAMERA && READ_EXTERNAL_STORAGE && WRITE_EXTERNAL_STORAGE && GET_ACCOUNTS && ACCESS_NETWORK_STATE && WRITE_CALENDAR/*&& ACCESS_FINE_LOCATION && ACCESS_COARSE_LOCATION*/) {
                        //Toast.makeText(LoginActivity.this,"all permission granted",Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "some permission not granted", Toast.LENGTH_LONG).show();
                        /*if (!ACCESS_COARSE_LOCATION || !ACCESS_COARSE_LOCATION) {*/
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
                        /*}*/
                        return;
                    }
                }
                break;

                //DEFAULT CODE BY ANDROID
                // If request is cancelled, the result arrays are empty.
                /*if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;*/
            }
        }
    }

    /////////////zoom sdk metods for join methods/////////

    private void joinMeeting(final String meetingNo) {
        // Step 1: Get meeting number from input field.
        //String meetingNo = "";//"200395093";

        // Check if the meeting number is empty.
        if (meetingNo.length() == 0) {
            Toast.makeText(LoginActivity.this, "You need to enter a meeting number/ vanity id which you want to join.", Toast.LENGTH_LONG).show();
            return;
        }
        // Step 2: Get Zoom SDK instance.
        ZoomSDK zoomSDK = ZoomSDK.getInstance();

        // Check if the zoom SDK is initialized
        if (!zoomSDK.isInitialized()) {
            Toast.makeText(LoginActivity.this, "ZoomSDK has not been initialized successfully", Toast.LENGTH_LONG).show();
            return;
        }

        // Step 3: Get meeting service from zoom SDK instance.
        MeetingService meetingService = zoomSDK.getMeetingService();

        // Step 4: Configure meeting options.
        JoinMeetingOptions opts = new JoinMeetingOptions();


        // Some available options
        opts.no_driving_mode = false;
        opts.no_invite = false;
        opts.no_meeting_end_message = false;
        opts.no_titlebar = false;
        opts.no_bottom_toolbar = false;
        opts.no_dial_in_via_phone = true;
        opts.no_dial_out_to_phone = true;
        opts.no_disconnect_audio = false;
        opts.no_share = false;
        opts.invite_options = InviteOptions.INVITE_VIA_EMAIL + InviteOptions.INVITE_VIA_SMS + InviteOptions.INVITE_COPY_URL + InviteOptions.INVITE_ENABLE_ALL;
        opts.no_audio = true;
        opts.no_video = false;
        //  opts.meeting_views_options = MeetingViewsOptions.NO_BUTTON_SHARE + MeetingViewsOptions.NO_BUTTON_VIDEO;
        opts.no_meeting_error_message = true;
        opts.participant_id = "participant id";

        // Step 5: Setup join meeting parameters
        JoinMeetingParams params = new JoinMeetingParams();

        params.displayName = "Anonymous user";//"Hello World From Zoom SDK"
        params.meetingNo = meetingNo;

        // Step 6: Call meeting service to join meeting
        meetingService.joinMeetingWithParams(LoginActivity.this, params, opts);
    }

    @Override
    public void onMeetingStatusChanged(MeetingStatus meetingStatus, int errorCode, int internalErrorCode) {

    }

    @Override
    public void onZoomSDKLoginResult(long result) {

    }

    @Override
    public void onZoomSDKLogoutResult(long result) {

    }

    @Override
    public void onZoomIdentityExpired() {

    }

    @Override
    public void onZoomSDKInitializeResult(int errorCode, int internalErrorCode) {

        Log.i(TAG, "onZoomSDKInitializeResult, errorCode=" + errorCode + ", internalErrorCode=" + internalErrorCode);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("email", mEdTxtEmail.getText().toString());
        outState.putString("password", mEdTxtPassword.getText().toString());
        outState.putString("meetingId", mEdTxtMeetingId.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mEdTxtEmail.setText(savedInstanceState.getString("email"));
        mEdTxtPassword.setText(savedInstanceState.getString("password"));
        mEdTxtMeetingId.setText(savedInstanceState.getString("meetingId"));
    }

    @Override
    protected void onStart() {
        super.onStart();
        /*FirebaseUser currentUser = mAuth.getCurrentUser();*/
        /*updateUI(currentUser);*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                /*firebaseAuthWithGoogle(account);*/
                sessionManager.updateIsPasswordSkip("1");
                sessionManager.updateGoogleAccount(account.getEmail());
                doLogin(account.getEmail(),"");
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    private void signOut() {
        // Firebase sign out
        /* mAuth.signOut();*/

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        /* updateUI(null);*/
                        sessionManager.updateIsPasswordSkip("0");
                        sessionManager.updateGoogleAccount("");
                    }
                });
    }
}
