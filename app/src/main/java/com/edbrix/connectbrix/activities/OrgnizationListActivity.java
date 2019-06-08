package com.edbrix.connectbrix.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edbrix.connectbrix.Application;
import com.edbrix.connectbrix.R;
import com.edbrix.connectbrix.adapters.OrgnizationListAdapter;
import com.edbrix.connectbrix.baseclass.BaseActivity;
import com.edbrix.connectbrix.data.ForgotPasswordResponseData;
import com.edbrix.connectbrix.data.UserLoginResponseData;
import com.edbrix.connectbrix.data.UserOrganizationListData;
import com.edbrix.connectbrix.utils.Constants;
import com.edbrix.connectbrix.utils.SessionManager;
import com.edbrix.connectbrix.volley.GsonRequest;
import com.edbrix.connectbrix.volley.SettingsMy;

import org.json.JSONObject;

import java.util.ArrayList;

public class OrgnizationListActivity extends BaseActivity {

    private static final String TAG = OrgnizationListActivity.class.getName();
    private ListView mOrgnizationList;
    OrgnizationListAdapter orgnizationListAdapter;
    ArrayList<UserOrganizationListData> userOrganizationListData;
    SessionManager sessionManager;

    String userComesFrom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orgnization_list);
        getSupportActionBar().setTitle("Select your School");
        assignViews();

        sessionManager = new SessionManager(this);
        Intent intent = getIntent();
        userComesFrom = intent.getStringExtra("comesFrom");
        userOrganizationListData = new ArrayList<>();
        userOrganizationListData = (ArrayList<UserOrganizationListData>) intent.getSerializableExtra("organizationList");
        setOrgnizationListAdapter(userOrganizationListData);
        clickListners();

    }

    private void clickListners() {

        mOrgnizationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                if (userComesFrom.equals("loginActivity")) {
                    doLogin(position);
                } else if (userComesFrom.equals("forgotPasswordActivity")) {
                    resetPassword(position);
                }


            }
        });
    }

    private void assignViews() {
        mOrgnizationList = (ListView) findViewById(R.id.orgnizationList);
    }

    private void setOrgnizationListAdapter(ArrayList<UserOrganizationListData> userOrganizationListData) {
        orgnizationListAdapter = new OrgnizationListAdapter(OrgnizationListActivity.this, userOrganizationListData);
        mOrgnizationList.setAdapter(orgnizationListAdapter);
    }


    /*Intent intent = new Intent(OrgnizationListActivity.this,SchoolListActivity.class);
                startActivity(intent);*/

    private void doLogin(final int position) {
        try {

            showBusyProgress();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("APIKEY", userOrganizationListData.get(position).getApiKey());
            jsonObject.put("SECRETKEY", userOrganizationListData.get(position).getSecretekey());
            jsonObject.put("Email", sessionManager.getSessionUsername());
            jsonObject.put("Password", sessionManager.getSessionPassword());

            GsonRequest<UserLoginResponseData> userLoginRequest = new GsonRequest<>(Request.Method.POST, Constants.userLogin, jsonObject.toString(), UserLoginResponseData.class,
                    new Response.Listener<UserLoginResponseData>() {
                        @Override
                        public void onResponse(@NonNull UserLoginResponseData response) {
                            hideBusyProgress();
                            if (response.getError() != null) {
                                showToast(response.getError().getErrorMessage());
                            } else {

                                if (response.getSuccess() == 1) {
                                    showToast("Login Successfully");

                                    sessionManager.updateAccessToken(response.getAccessToken());
                                    sessionManager.updateSessionUserID(response.getUser().getId());
                                    sessionManager.updateSessionUserFirstName(response.getUser().getFirstName());
                                    sessionManager.updateSessionUserLastName(response.getUser().getLastName());
                                    sessionManager.updateSessionUserEmail(response.getUser().getEmail());
                                    sessionManager.updateUserType(response.getUser().getUserType());
                                    sessionManager.updateSchoolDisplayName(response.getUser().getSchoolDisplayName());
                                    sessionManager.updateSessionProfileImageUrl(response.getUser().getImageUrl());
                                    sessionManager.updateSchoolLoginUrl(response.getUser().getSchoolLoginUrl());
                                    sessionManager.updateOrganizationApiKey(userOrganizationListData.get(position).getApiKey());
                                    sessionManager.updateOrganizationSecretKey(userOrganizationListData.get(position).getSecretekey());

                                    Intent intent = new Intent(OrgnizationListActivity.this, SchoolListActivity.class);
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
            userLoginRequest.setRetryPolicy(Application.getDefaultRetryPolice());
            userLoginRequest.setShouldCache(false);
            Application.getInstance().addToRequestQueue(userLoginRequest, "userLoginRequest");

        } catch (Exception e) {
            hideBusyProgress();
            Log.e(TAG, e.getMessage());
        }
    }

    private void resetPassword(int position) {
        try{
            showBusyProgress();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("APIKEY", userOrganizationListData.get(position).getApiKey());
            jsonObject.put("SECRETKEY", userOrganizationListData.get(position).getSecretekey());
            jsonObject.put("Email", sessionManager.getSessionUsername());

            GsonRequest<ForgotPasswordResponseData> userLoginRequest = new GsonRequest<>(Request.Method.POST, Constants.resetPassword, jsonObject.toString(), ForgotPasswordResponseData.class,
                    new Response.Listener<ForgotPasswordResponseData>() {
                        @Override
                        public void onResponse(@NonNull ForgotPasswordResponseData response) {
                            hideBusyProgress();
                            if (response.getError() != null) {
                                showToast(response.getError().getErrorMessage());
                            } else {

                                if (response.getSuccess() == 1) {
                                    showToast(response.getMessage());

                                    Intent intent = new Intent(OrgnizationListActivity.this, ForgotPasswordActivity.class);
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
            userLoginRequest.setRetryPolicy(Application.getDefaultRetryPolice());
            userLoginRequest.setShouldCache(false);
            Application.getInstance().addToRequestQueue(userLoginRequest, "userLoginRequest");

        }catch (Exception e){
            hideBusyProgress();
            Log.e(TAG, e.getMessage());
        }

    }
}
