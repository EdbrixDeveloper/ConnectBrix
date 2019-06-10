package com.edbrix.connectbrix.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edbrix.connectbrix.Application;
import com.edbrix.connectbrix.R;
import com.edbrix.connectbrix.baseclass.BaseActivity;
import com.edbrix.connectbrix.data.UserOrganizationListData;
import com.edbrix.connectbrix.data.UserOrganizationListParentData;
import com.edbrix.connectbrix.utils.Constants;
import com.edbrix.connectbrix.utils.SessionManager;
import com.edbrix.connectbrix.volley.GsonRequest;
import com.edbrix.connectbrix.volley.SettingsMy;

import org.json.JSONObject;

import java.util.ArrayList;

import static com.edbrix.connectbrix.utils.Constants.APP_KEY__;
import static com.edbrix.connectbrix.utils.Constants.APP_SECRET__;

public class LoginActivity extends BaseActivity {

    private static final String TAG = LoginActivity.class.getName();

    private LinearLayout mLoginLinearLayout;
    private ImageView mImgConnectBrix;
    private EditText mEdTxtEmail;
    private EditText mEdTxtPassword;
    private Button mBtnLogin;
    private ImageView mEyeIcon;
    private TextView mTextViewForgotPassword;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private boolean isPasswordVisible;
    SessionManager sessionManager;
    boolean isEmailValid = false;

    ArrayList<UserOrganizationListData> userOrganizationListData;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
        getSupportActionBar().hide(); //hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //show the activity in full screen
        setContentView(R.layout.activity_login);
        sessionManager = new SessionManager(this);
        userOrganizationListData = new ArrayList<>();
        assignViews();
        init();
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
                String email = mEdTxtEmail.getText().toString();
                if (!email.equals("")) {
                    if (email.matches(emailPattern) && s.length() > 0) {
                        mEdTxtEmail.setBackgroundResource(R.drawable.flash_screen_background);
                        isEmailValid = true;
                    } else {
                        mEdTxtEmail.setError("Email not valid");
                        mEdTxtEmail.setBackgroundResource(R.drawable.error_background);
                        isEmailValid = false;
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // nothing TODO
            }
        });

        mEyeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPasswordVisible(isPasswordVisible);
                isPasswordVisible = !isPasswordVisible;
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
        isPasswordVisible = false;//
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
                                String error = response.getError().getErrorMessage();
                                showToast(error);
                                //Toast.makeText(getApplicationContext(),error,Toast.LENGTH_LONG).show();
                            } else {

                                if (response.getSuccess() == 1) {
                                    sessionManager.updateSessionUsername(userName);
                                    sessionManager.updateSessionPassword(password);

                                    for (int i = 0; i < response.getUserOrganizationList().size(); i++) {
                                        userOrganizationListData = new ArrayList<>();
                                        userOrganizationListData.add(response.getUserOrganizationList().get(i));
                                    }
                                    finish();
                                    Intent intent = new Intent(LoginActivity.this, OrgnizationListActivity.class);
                                    intent.putExtra("organizationList", userOrganizationListData);
                                    intent.putExtra("comesFrom","loginActivity");
                                    startActivity(intent);
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

        if (userEmail.isEmpty() || userEmail == null) {
            mEdTxtEmail.setError("Field can not be empty");
            return false;
        } else if (userPassword.isEmpty() || userPassword == null) {
            mEdTxtPassword.setError("Field can not be empty");
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
}
