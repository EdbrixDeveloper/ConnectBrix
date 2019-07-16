package com.edbrix.connectbrix.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edbrix.connectbrix.Application;
import com.edbrix.connectbrix.R;
import com.edbrix.connectbrix.baseclass.BaseActivity;
import com.edbrix.connectbrix.data.UserData;
import com.edbrix.connectbrix.utils.Constants;
import com.edbrix.connectbrix.utils.SessionManager;
import com.edbrix.connectbrix.volley.GsonRequest;
import com.edbrix.connectbrix.volley.SettingsMy;
/*import com.vikktorn.picker.State;*/

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;


public class EditProfileActivity extends BaseActivity {

    private static final String TAG = EditProfileActivity.class.getName();
    private CircleImageView mImgProfile;
    private TextView mEmail;
    private TextView mEmailVal;
    private TextView mFirstName;
    private EditText mFirstNameVal;
    private TextView mLastName;
    private EditText mLastNameVal;
    private TextView mAddress;
    private EditText mAddressVal;
    private TextView mCity;
    private EditText mCityVal;
    private TextView mState;
    private TextView mStateVal;
    private TextView mZip;
    private EditText mZipVal;
    private TextView mPhone1;
    private EditText mPhone1Val;
    private TextView mPhone2;
    private EditText mPhone2Val;
    private TextView timeZone;
    private TextView timeZoneVal;
    private Button mBtnUpdateProfile;

    SessionManager sessionManager;
    public static int countryID, stateID, timeZoneID;
    // arrays of state object
    //public static List<State> stateObject;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        getSupportActionBar().setTitle("Update Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        assignViews();
        sessionManager = new SessionManager(this);
        clickListner();
        hideKeyboard();
        if (savedInstanceState != null) {
            mEmail.setText("Email:" + savedInstanceState.getString("email"));
            mFirstNameVal.setText(savedInstanceState.getString("firstName"));
            mLastNameVal.setText(savedInstanceState.getString("lastName"));
            mAddressVal.setText(savedInstanceState.getString("address"));
            mCityVal.setText(savedInstanceState.getString("city"));
            mStateVal.setText(savedInstanceState.getString("state"));
            timeZoneVal.setText(savedInstanceState.getString("timezone"));
            mZipVal.setText(savedInstanceState.getString("zip"));
            mPhone1Val.setText(savedInstanceState.getString("phone1"));
            mPhone2Val.setText(savedInstanceState.getString("phone2"));
        } else {
            GetUserPersonalData();

        }
    }

    private void assignViews() {
        mImgProfile = (CircleImageView) findViewById(R.id.imgProfile);
        mEmail = (TextView) findViewById(R.id.email);
        mEmailVal = (TextView) findViewById(R.id.emailVal);
        mFirstName = (TextView) findViewById(R.id.firstName);
        mFirstNameVal = (EditText) findViewById(R.id.firstNameVal);
        mLastName = (TextView) findViewById(R.id.lastName);
        mLastNameVal = (EditText) findViewById(R.id.lastNameVal);
        mAddress = (TextView) findViewById(R.id.address);
        mAddressVal = (EditText) findViewById(R.id.addressVal);
        mCity = (TextView) findViewById(R.id.city);
        mCityVal = (EditText) findViewById(R.id.cityVal);
        mState = (TextView) findViewById(R.id.state);
        mStateVal = (TextView) findViewById(R.id.stateVal);
        mZip = (TextView) findViewById(R.id.zip);
        mZipVal = (EditText) findViewById(R.id.zipVal);
        mPhone1 = (TextView) findViewById(R.id.phone1);
        mPhone1Val = (EditText) findViewById(R.id.phone1Val);
        mPhone2 = (TextView) findViewById(R.id.phone2);
        mPhone2Val = (EditText) findViewById(R.id.phone2Val);
        mBtnUpdateProfile = (Button) findViewById(R.id.btnUpdateProfile);
        timeZone = (TextView) findViewById(R.id.timeZone);
        timeZoneVal = (TextView) findViewById(R.id.timeZoneVal);
        // initiate state object, parser, and arrays
        //stateObject = new ArrayList<>();
    }

    private void clickListner() {

        mStateVal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (countryID > 0) {
                    Intent intent = new Intent(EditProfileActivity.this, SelectStateActivity.class);
                    intent.putExtra("CountryId", String.valueOf(countryID));
                    intent.putExtra("StateName",mStateVal.getText().toString());
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(EditProfileActivity.this, SelectCountryActivity.class);
                    startActivity(intent);
                }
                //countryPicker.showDialog(getSupportFragmentManager());
            }
        });

        timeZoneVal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditProfileActivity.this, SelectTimeZoneActivity.class);
                intent.putExtra("TimeZoneID", timeZoneVal.getText().toString());
                startActivity(intent);
            }
        });

        mBtnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validation() == true) {
                    UpdateUserPersonalData();
                }
            }
        });

    }

    private void GetUserPersonalData() {
        try {

            showBusyProgress();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("UserId", sessionManager.getSessionUserId());
            jsonObject.put("AccessToken", sessionManager.getPrefsSessionAccessToken());

            GsonRequest<UserData> userOrganizationListRequest = new GsonRequest<>(Request.Method.POST, Constants.getUserPersonalData, jsonObject.toString(), UserData.class,
                    new Response.Listener<UserData>() {
                        @Override
                        public void onResponse(@NonNull UserData response) {
                            hideBusyProgress();
                            if (response.getError() != null) {
                                String error = response.getError().getErrorMessage();
                                showToast(error);
                            } else {

                                if (response.getSuccess() == 1) {
                                    /*sessionManager.updateSessionUsername(userName);
                                    sessionManager.updateSessionPassword(password);*/

                                    String sourceString = response.getUser().getEmail() == null || response.getUser().getEmail().isEmpty() ? "Email: " + "<b>" + "-" + "<b>" : "Email: " + "<b>" + response.getUser().getEmail().toString() + "<b>";
                                    //String sourceString = "<b>" + id + "</b> " + name;
                                    mEmail.setText(Html.fromHtml(sourceString));
                                    //mEmail.setText(response.getUser().getEmail() == null || response.getUser().getEmail().isEmpty() ? "Email:-" : "Email:"+response.getUser().getEmail().toString());
                                    //mEmailVal.setText(response.getUser().getEmail() == null || response.getUser().getEmail().isEmpty() ? "" : response.getUser().getEmail().toString());
                                    mFirstNameVal.setText(response.getUser().getFirstName() == null || response.getUser().getFirstName().isEmpty() ? "" : response.getUser().getFirstName().toString());
                                    mLastNameVal.setText(response.getUser().getLastName() == null || response.getUser().getLastName().isEmpty() ? "" : response.getUser().getLastName().toString());
                                    mAddressVal.setText(response.getUser().getAddress() == null || response.getUser().getAddress().isEmpty() ? "" : response.getUser().getAddress().toString());
                                    mCityVal.setText(response.getUser().getCity() == null || response.getUser().getCity().isEmpty() ? "" : response.getUser().getCity().toString());
                                    mStateVal.setText(response.getUser().getState() == null || response.getUser().getState().isEmpty() ? "" : response.getUser().getState().toString());
                                    timeZoneVal.setText(response.getUser().getUserTimezone() == null || response.getUser().getUserTimezone().isEmpty() ? "" : response.getUser().getUserTimezone().toString());
                                    mZipVal.setText(response.getUser().getZip() == null || response.getUser().getZip().isEmpty() ? "" : response.getUser().getZip().toString());
                                    mPhone1Val.setText(response.getUser().getMobileNumber() == null || response.getUser().getMobileNumber().isEmpty() ? "" : response.getUser().getMobileNumber().toString());
                                    mPhone2Val.setText(response.getUser().getMobileNumber2() == null || response.getUser().getMobileNumber2().isEmpty() ? "" : response.getUser().getMobileNumber2().toString());
                                    countryID = Integer.parseInt(response.getUser().getCountryId() == null || response.getUser().getCountryId().isEmpty() ? "0" : response.getUser().getCountryId().toString());
                                    stateID = Integer.parseInt(response.getUser().getStateId() == null || response.getUser().getStateId().isEmpty() ? "0" : response.getUser().getStateId().toString());
                                    timeZoneID = Integer.parseInt(response.getUser().getTimezoneId() == null || response.getUser().getTimezoneId().isEmpty() ? "0" : response.getUser().getTimezoneId().toString());
                                    Constants.StateId = Integer.parseInt(response.getUser().getStateId() == null || response.getUser().getStateId().isEmpty() ? "0" : response.getUser().getStateId().toString());
                                    Constants.CountryId = Integer.parseInt(response.getUser().getCountryId() == null || response.getUser().getCountryId().isEmpty() ? "0" : response.getUser().getCountryId().toString());
                                    Constants.StateName = response.getUser().getState() == null || response.getUser().getState().isEmpty() ? "" : response.getUser().getState().toString();
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

    private void UpdateUserPersonalData() {
        try {

            showBusyProgress();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("UserId", sessionManager.getSessionUserId());
            jsonObject.put("AccessToken", sessionManager.getPrefsSessionAccessToken());

            jsonObject.put("FirstName", mFirstNameVal.getText().toString());
            sessionManager.updateSessionUserFirstName(mFirstNameVal.getText().toString());
            jsonObject.put("LastName", mLastNameVal.getText().toString());
            sessionManager.updateSessionUserLastName(mLastNameVal.getText().toString());
            jsonObject.put("MobileNumber", mPhone1Val.getText().toString());
            jsonObject.put("MobileNumber2", mPhone2Val.getText().toString());
            jsonObject.put("Address", mAddressVal.getText().toString());
            jsonObject.put("City", mCityVal.getText().toString());
            jsonObject.put("Zip", mZipVal.getText().toString());
            jsonObject.put("TimezoneId", timeZoneID);
            jsonObject.put("StateId", stateID);
            jsonObject.put("CountryId", countryID);


            /*{
                "AccessToken":"MjUyLTg1REEyUzMtQURTUzVELUVJNUI0QTIyMTI=",
                    "UserId": 2,
                    "FirstName": "Vikrant1",
                    "LastName": "Chougale1",
                    "MobileNumber": "9158521010",
                    "MobileNumber2":"222222222",
                    "Address":"Bay view",
                    "City":"California",
                    "StateId":"1",
                    "Zip":"55555",
                    "CountryId":"228"
            }*/


            GsonRequest<UserData> userOrganizationListRequest = new GsonRequest<>(Request.Method.POST, Constants.updateUserPersonalData, jsonObject.toString(), UserData.class,
                    new Response.Listener<UserData>() {
                        @Override
                        public void onResponse(@NonNull UserData response) {
                            hideBusyProgress();
                            if (response.getError() != null) {
                                String error = response.getError().getErrorMessage();
                                showToast(error);
                            } else {

                                if (response.getSuccess() == 1) {
                                    showToast(response.getMessage().toString());
                                   // finish();
                                   /* Intent intent = new Intent(EditProfileActivity.this, UserProfileActivity.class);
                                    startActivity(intent);*/

                                    //sessionManager.updateSessionUsername(userName);
                                    //sessionManager.updateSessionPassword(password);
                                    mEmailVal.setText(response.getUser().getEmail() == null || response.getUser().getEmail().isEmpty() ? "" : response.getUser().getEmail().toString());
                                    mFirstNameVal.setText(response.getUser().getFirstName() == null || response.getUser().getFirstName().isEmpty() ? "" : response.getUser().getFirstName().toString());
                                    mLastNameVal.setText(response.getUser().getLastName() == null || response.getUser().getLastName().isEmpty() ? "" : response.getUser().getLastName().toString());
                                    mAddressVal.setText(response.getUser().getAddress() == null || response.getUser().getAddress().isEmpty() ? "" : response.getUser().getAddress().toString());
                                    mCityVal.setText(response.getUser().getCity() == null || response.getUser().getCity().isEmpty() ? "" : response.getUser().getCity().toString());
                                    mStateVal.setText(response.getUser().getState() == null || response.getUser().getState().isEmpty() ? "" : response.getUser().getState().toString());
                                    mZipVal.setText(response.getUser().getZip() == null || response.getUser().getZip().isEmpty() ? "" : response.getUser().getZip().toString());
                                    mPhone1Val.setText(response.getUser().getMobileNumber() == null || response.getUser().getMobileNumber().isEmpty() ? "" : response.getUser().getMobileNumber().toString());
                                    mPhone2Val.setText(response.getUser().getMobileNumber2() == null || response.getUser().getMobileNumber2().isEmpty() ? "" : response.getUser().getMobileNumber2().toString());
                                    countryID = Integer.parseInt(response.getUser().getCountryId() == null || response.getUser().getCountryId().isEmpty() ? "0" : response.getUser().getCountryId().toString());
                                    stateID = Integer.parseInt(response.getUser().getStateId() == null || response.getUser().getStateId().isEmpty() ? "0" : response.getUser().getStateId().toString());

                                    setResult(Activity.RESULT_OK);
                                    finish();
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

    private boolean validation() {

        String firstName = mFirstNameVal.getText().toString().trim();
        String lastName = mLastNameVal.getText().toString().trim();

        if (firstName.isEmpty() || firstName == null) {
            showToast("Enter First Name");
            return false;
        } else if (lastName.isEmpty() || lastName == null) {
            showToast("Enter Last Name");
            return false;
        } else if (isValidMobile(mPhone1Val.getText().toString().trim()) != true) {
            mPhone1Val.setError("Enter Valid Phone Number");
            return false;
        } else if (isValidMobile(mPhone2Val.getText().toString().trim()) != true) {
            mPhone2Val.setError("Enter Valid Phone Number");
            return false;
        } else {
            return true;
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.update_profile_menu, menu);
        return true;

    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isValidMobile(String phone) {
        boolean check = false;
        if (!Pattern.matches("[a-zA-Z]+", phone)) {
            if (phone.length() < 6 || phone.length() > 13) {
                // if(phone.length() != 10) {
                check = false;
            } else {
                check = true;
            }
        } else {
            check = false;
        }
        return check;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    @Override
    protected void onResume() {
        if (Constants.CountryId > 0 && Constants.StateId > 0 && !Constants.StateName.isEmpty()) {
            countryID = Constants.CountryId;
            stateID = Constants.StateId;
            mStateVal.setText(Constants.StateName);

        }
        if (Constants.TimeZonId > 0) {
            timeZoneID = Constants.TimeZonId;
            timeZoneVal.setText(Constants.TimeZone);
        }
        super.onResume();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("email", mEmail.getText().toString());
        outState.putString("firstName", mFirstNameVal.getText().toString());
        outState.putString("lastName", mLastNameVal.getText().toString());
        outState.putString("address", mAddressVal.getText().toString());
        outState.putString("city", mCityVal.getText().toString());
        outState.putString("state", mStateVal.getText().toString());
        outState.putString("timezone", timeZoneVal.getText().toString());
        outState.putString("zip", mZipVal.getText().toString());
        outState.putString("phone1", mPhone1Val.getText().toString());
        outState.putString("phone2", mPhone2Val.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mEmail.setText("Email" + savedInstanceState.getString("email"));
        mFirstNameVal.setText(savedInstanceState.getString("firstName"));
        mLastNameVal.setText(savedInstanceState.getString("lastName"));
        mAddressVal.setText(savedInstanceState.getString("address"));
        mCityVal.setText(savedInstanceState.getString("city"));
        mStateVal.setText(savedInstanceState.getString("state"));
        timeZoneVal.setText(savedInstanceState.getString("timezone"));
        mZipVal.setText(savedInstanceState.getString("zip"));
        mPhone1Val.setText(savedInstanceState.getString("phone1"));
        mPhone2Val.setText(savedInstanceState.getString("phone2"));
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mFirstNameVal.getWindowToken(), 0);
    }
}
