package com.edbrix.connectbrix.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
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
import com.edbrix.connectbrix.data.UserData;
import com.edbrix.connectbrix.data.UserOrganizationListParentData;
import com.edbrix.connectbrix.utils.Constants;
import com.edbrix.connectbrix.utils.SessionManager;
import com.edbrix.connectbrix.volley.GsonRequest;
import com.edbrix.connectbrix.volley.SettingsMy;
import com.vikktorn.picker.City;
import com.vikktorn.picker.CityPicker;
import com.vikktorn.picker.Country;
import com.vikktorn.picker.CountryPicker;
import com.vikktorn.picker.OnCountryPickerListener;
import com.vikktorn.picker.OnStatePickerListener;
import com.vikktorn.picker.State;
import com.vikktorn.picker.StatePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.edbrix.connectbrix.utils.Constants.APP_KEY__;
import static com.edbrix.connectbrix.utils.Constants.APP_SECRET__;

public class EditProfileActivity extends BaseActivity implements OnStatePickerListener, OnCountryPickerListener {

    private static final String TAG = EditProfileActivity.class.getName();
    private CircleImageView mImgProfile;
    private TextView mEmail;
    private EditText mEmailVal;
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
    private Button mBtnUpdateProfile;

    SessionManager sessionManager;
    public static int countryID, stateID;
    private CountryPicker countryPicker;
    private StatePicker statePicker;
    // arrays of state object
    public static List<State> stateObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        getSupportActionBar().setTitle("Update Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        assignViews();
        sessionManager = new SessionManager(this);
        GetUserPersonalData();
        // get state from assets JSON
        try {
            getStateJson();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // initialize country picker
        countryPicker = new CountryPicker.Builder().with(this).listener(this).build();

        clickListner();
    }

    private void assignViews() {
        mImgProfile = (CircleImageView) findViewById(R.id.imgProfile);
        mEmail = (TextView) findViewById(R.id.email);
        mEmailVal = (EditText) findViewById(R.id.emailVal);
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
        // initiate state object, parser, and arrays
        stateObject = new ArrayList<>();
    }

    private void clickListner() {

        mStateVal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countryPicker.showDialog(getSupportFragmentManager());
            }
        });

        mBtnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validation() == true) {
                    //UpdateUserPersonalData();
                }
            }
        });

    }

    @Override
    public void onSelectCountry(Country country) {
        // get country name and country ID
        //countryName.setText(country.getName());
        countryID = country.getCountryId();
        statePicker.equalStateObject.clear();
        //cityPicker.equalCityObject.clear();

        //set state name text view and state pick button invisible
        /*pickStateButton.setVisibility(View.VISIBLE);
        stateNameTextView.setVisibility(View.VISIBLE);
        stateNameTextView.setText("Region");
        cityName.setText("City");*/
        // set text on main view
        /*countryCode.setText("Country code: " + country.getCode());
        countryPhoneCode.setText("Country dial code: " + country.getDialCode());
        countryCurrency.setText("Country currency: " + country.getCurrency());
        flagImage.setBackgroundResource(country.getFlag());*/


        // GET STATES OF SELECTED COUNTRY
        for (int i = 0; i < stateObject.size(); i++) {
            // init state picker
            statePicker = new StatePicker.Builder().with(this).listener(this).build();
            State stateData = new State();
            if (stateObject.get(i).getCountryId() == countryID) {

                stateData.setStateId(stateObject.get(i).getStateId());
                stateData.setStateName(stateObject.get(i).getStateName());
                stateData.setCountryId(stateObject.get(i).getCountryId());
                stateData.setFlag(country.getFlag());
                statePicker.equalStateObject.add(stateData);
            }
        }

        statePicker.showDialog(getSupportFragmentManager());///////
    }

    // ON SELECTED STATE ADD CITY TO PICKER
    @Override
    public void onSelectState(State state) {
        mStateVal.setText(state.getStateName());
        //stateNameTextView.setText(state.getStateName());
        stateID = state.getStateId();

    }

    // GET STATE FROM ASSETS JSON
    public void getStateJson() throws JSONException {
        String json = null;
        try {
            InputStream inputStream = getAssets().open("states.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, "UTF-8");

        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject = new JSONObject(json);
        JSONArray events = jsonObject.getJSONArray("states");
        for (int j = 0; j < events.length(); j++) {
            JSONObject cit = events.getJSONObject(j);
            State stateData = new State();

            stateData.setStateId(Integer.parseInt(cit.getString("id")));
            stateData.setStateName(cit.getString("name"));
            stateData.setCountryId(Integer.parseInt(cit.getString("country_id")));
            stateObject.add(stateData);
        }
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
            jsonObject.put("LastName", mLastNameVal.getText().toString());
            jsonObject.put("MobileNumber", mPhone1Val.getText().toString());
            jsonObject.put("MobileNumber2", mPhone2Val.getText().toString());
            jsonObject.put("Address", mAddressVal.getText().toString());
            jsonObject.put("City", mCityVal.getText().toString());
            jsonObject.put("Zip", mZipVal.getText().toString());
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
                                    Intent intent = new Intent(EditProfileActivity.this, UserProfileActivity.class);
                                    startActivity(intent);
                                    /*sessionManager.updateSessionUsername(userName);
                                    sessionManager.updateSessionPassword(password);
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
                                    stateID = Integer.parseInt(response.getUser().getStateId() == null || response.getUser().getStateId().isEmpty() ? "0" : response.getUser().getStateId().toString());*/
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
        /*if (NewPassword.isEmpty() || NewPassword == null) {
            showToast("Enter New Password");
            return false;
        } else if (ConfirmPassword.isEmpty() || ConfirmPassword == null) {
            showToast("Enter Confirm Password");
            return false;
        }*/
        return true;
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
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
