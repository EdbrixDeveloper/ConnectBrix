package com.edbrix.connectbrix.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    private Button mBtnChangePassSubmit;

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
    }

    private void clickListner() {

        mBtnChangePassSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validation(mEdTxtPassword.getText().toString().trim(), mEdTxtConfirmPassword.getText().toString().trim()) == true) {
                    if (mEdTxtPassword.getText().toString().trim().equals(mEdTxtConfirmPassword.getText().toString().trim())) {
                        ChangePassword(mEdTxtPassword.getText().toString().trim(), mEdTxtConfirmPassword.getText().toString().trim());
                    } else {
                        showToast("New Password and Confirm Password are not matched");
                    }
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
}
