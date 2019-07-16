package com.edbrix.connectbrix.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edbrix.connectbrix.Application;
import com.edbrix.connectbrix.R;
import com.edbrix.connectbrix.baseclass.BaseActivity;
import com.edbrix.connectbrix.data.UserOrganizationListParentData;
import com.edbrix.connectbrix.utils.Constants;
import com.edbrix.connectbrix.utils.SessionManager;
import com.edbrix.connectbrix.volley.GsonRequest;
import com.edbrix.connectbrix.volley.SettingsMy;

import org.json.JSONObject;

import static com.edbrix.connectbrix.utils.Constants.APP_KEY__;
import static com.edbrix.connectbrix.utils.Constants.APP_SECRET__;

public class ChangePasswordActivity extends BaseActivity {

    private static final String TAG = ChangePasswordActivity.class.getName();
    private TextView mTextViewPassword;
    private EditText mEdTxtPassword;
    private TextView mTextViewConfirmPassword;
    private EditText mEdTxtConfirmPassword;
    private ImageView mEyeIconPassword;
    private ImageView mEyeIconConfirmPassword;
    private Button mBtnChangePassSubmit;
    private boolean isPasswordVisible;
    private boolean isConfirmPasswordVisible;
    private boolean isPasswordLengthFulfill = true;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        getSupportActionBar().setTitle("Change Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        assignViews();
        sessionManager = new SessionManager(this);
        clickListner();

        if (savedInstanceState != null) {
            mEdTxtPassword.setText(savedInstanceState.getString("newPwd", ""));
            mEdTxtConfirmPassword.setText(savedInstanceState.getString("confirmPwd", ""));
        }

    }

    private void assignViews() {
        mTextViewPassword = (TextView) findViewById(R.id.textViewPassword);
        mEdTxtPassword = (EditText) findViewById(R.id.edTxtPassword);
        mTextViewConfirmPassword = (TextView) findViewById(R.id.textViewConfirmPassword);
        mEdTxtConfirmPassword = (EditText) findViewById(R.id.edTxtConfirmPassword);
        mBtnChangePassSubmit = (Button) findViewById(R.id.btnChangePassSubmit);
        mEyeIconPassword = (ImageView) findViewById(R.id.eyeIconPassword);
        mEyeIconConfirmPassword = (ImageView) findViewById(R.id.eyeIconConfirmPassword);
    }

    private void clickListner() {

        mBtnChangePassSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validation(mEdTxtPassword.getText().toString().trim(), mEdTxtConfirmPassword.getText().toString().trim()) == true) {
                    if (isPasswordLengthFulfill == true) {
                        if (mEdTxtPassword.getText().toString().trim().equals(mEdTxtConfirmPassword.getText().toString().trim())) {

                            ChangePassword(mEdTxtPassword.getText().toString().trim(), mEdTxtConfirmPassword.getText().toString().trim());
                        } else {
                            showToast("Password must be same.");
                        }

                    } else {
                        showToast("Password must in between mininum 6 charactor or maximum 10 charactor.");
                    }
                }
            }
        });

        mEyeIconPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPasswordVisible(isPasswordVisible, mEyeIconPassword, mEdTxtPassword);
                isPasswordVisible = !isPasswordVisible;
            }
        });

        mEyeIconConfirmPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPasswordVisible(isConfirmPasswordVisible, mEyeIconConfirmPassword, mEdTxtConfirmPassword);
                isConfirmPasswordVisible = !isConfirmPasswordVisible;
            }
        });

        mEdTxtPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    checkPasswordLength(mEdTxtPassword, "Password");
                }
            }
        });

        mEdTxtConfirmPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    checkPasswordLength(mEdTxtConfirmPassword, "Confirm Password");
                }
            }
        });

    }

    private boolean validation(String NewPassword, String ConfirmPassword) {
        if (NewPassword.isEmpty() || NewPassword == null) {
            showToast("Enter New Password");
            return false;
        } else if (ConfirmPassword.isEmpty() || ConfirmPassword == null) {
            showToast("Enter Confirm Password");
            return false;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void ChangePassword(final String NewPassword, final String ConfirmPassword) {
        try {

            showBusyProgress();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("UserId", sessionManager.getSessionUserId());
            jsonObject.put("AccessToken", sessionManager.getPrefsSessionAccessToken());
            jsonObject.put("NewPassword", NewPassword);
            jsonObject.put("ConfirmPassword", ConfirmPassword);

            /*"UserId":"1",
                    "AccessToken":"MjUyLTg1REEyUzMtQURTUzVELUVJNUI0QTIyMTE=",
                    "NewPassword": "123456",
                    "ConfirmPassword": "123456"*/


            GsonRequest<UserOrganizationListParentData> userOrganizationListRequest = new GsonRequest<>(Request.Method.POST, Constants.changePassword, jsonObject.toString(), UserOrganizationListParentData.class,
                    new Response.Listener<UserOrganizationListParentData>() {
                        @Override
                        public void onResponse(@NonNull UserOrganizationListParentData response) {
                            hideBusyProgress();
                            if (response.getError() != null) {
                                String error = response.getError().getErrorMessage();
                                showToast(error);
                            } else {

                                if (response.getSuccess() == 1) {

                                    showToast(response.getMessage());
                                    sessionManager.updateSessionPassword(NewPassword);
                                    finish();
                                    /*Intent intent = new Intent(ChangePasswordActivity.this, UserProfileActivity.class);
                                    startActivity(intent);*/
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("newPwd", mEdTxtPassword.getText().toString());
        outState.putString("confirmPwd", mEdTxtConfirmPassword.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //Model model = savedInstanceState.getParcelable("parcelable");
        mEdTxtPassword.setText(savedInstanceState.getString("newPwd", ""));
        mEdTxtConfirmPassword.setText(savedInstanceState.getString("confirmPwd", ""));
    }

    private void setPasswordVisible(boolean isVisible, ImageView imageView, EditText editText) {
        if (isVisible) {
            imageView.setImageDrawable(ContextCompat.getDrawable(ChangePasswordActivity.this, R.drawable.ic_visibility_off_black_24dp));
            editText.setTransformationMethod(new PasswordTransformationMethod());
        } else {
            imageView.setImageDrawable(ContextCompat.getDrawable(ChangePasswordActivity.this, R.drawable.ic_visibility_black_24dp));
            editText.setTransformationMethod(null);
        }
    }

    private void checkPasswordLength(EditText editText, String value) {

        int length = editText.getText().toString().length();
        if (length < 6 || length > 10) {
            isPasswordLengthFulfill = false;
            showToast(value + " must in between mininum 6 charactor or maximum 10 charactor.");
        } else {
            isPasswordLengthFulfill = true;
        }
    }
}
