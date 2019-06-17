package com.edbrix.connectbrix.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
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

public class ForgotPasswordActivity extends BaseActivity {

    private static final String TAG = ForgotPasswordActivity.class.getName();

    private ImageView mImgConnectBrix;
    private TextView mTextView;
    private EditText mEdTxtEmail;
    private Button mButton;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    boolean isEmailValid = false;
    ArrayList<UserOrganizationListData> userOrganizationListData = new ArrayList<>();
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_forgot_password);
        getSupportActionBar().setTitle("Reset your Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        userOrganizationListData = new ArrayList<>();
        sessionManager = new SessionManager(this);
        assignViews();

        checkEmailIsValidate();
        clickListner();

    }

    private void clickListner() {

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkFieldValidation() == true) {
                    //if (isEmailValid == true) {

                    getOrganizationList(mEdTxtEmail.getText().toString());
                    //}
                }
            }
        });
    }

    private void getOrganizationList(final String userName) {

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
                            } else {

                                if (response.getSuccess() == 1) {
                                    sessionManager.updateSessionUsername(userName);

                                    for (int i = 0; i < response.getUserOrganizationList().size(); i++) {
                                        userOrganizationListData = new ArrayList<>();
                                        userOrganizationListData.add(response.getUserOrganizationList().get(i));
                                    }

                                    Intent intent = new Intent(ForgotPasswordActivity.this, OrgnizationListActivity.class);
                                    intent.putExtra("organizationList", userOrganizationListData);
                                    intent.putExtra("email", mEdTxtEmail.getText().toString());
                                    intent.putExtra("comesFrom", "forgotPasswordActivity");
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

    private void checkEmailIsValidate() {

        /*mEdTxtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String email = mEdTxtEmail.getText().toString();
                if (!email.equals("")) {
                    if (email.matches(emailPattern) && s.length() > 0) {
                        mEdTxtEmail.setBackgroundResource(R.drawable.flash_screen_background);
                        isEmailValid = true;
                    } else {
                        mEdTxtEmail.setError("Enter valid email address");
                        mEdTxtEmail.setBackgroundResource(R.drawable.error_background);
                        isEmailValid = false;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/
    }

    private void assignViews() {
        //  mImgConnectBrix = (ImageView) findViewById(R.id.imgConnectBrixInForgotPass);
        mTextView = (TextView) findViewById(R.id.textViewForEnterEmail);
        mEdTxtEmail = (EditText) findViewById(R.id.edTxtEmailInForgotPass);
        mButton = (Button) findViewById(R.id.btnSubmit);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private boolean checkFieldValidation() {

        String email = mEdTxtEmail.getText().toString().trim();

        if (email.isEmpty() || email == null) {
            mEdTxtEmail.setError("Email can not be blank");
            return false;
        } else {
            return true;
        }
    }
}
