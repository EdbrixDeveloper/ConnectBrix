package com.edbrix.connectbrix.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edbrix.connectbrix.Application;
import com.edbrix.connectbrix.R;
import com.edbrix.connectbrix.adapters.SelectStateAdapter;
import com.edbrix.connectbrix.adapters.SelectTimeZoneAdapter;
import com.edbrix.connectbrix.baseclass.BaseActivity;
import com.edbrix.connectbrix.data.StateList;
import com.edbrix.connectbrix.data.StateListData;
import com.edbrix.connectbrix.data.TimeZoneListData;
import com.edbrix.connectbrix.data.TimeZoneParentData;
import com.edbrix.connectbrix.utils.Constants;
import com.edbrix.connectbrix.utils.SessionManager;
import com.edbrix.connectbrix.volley.GsonRequest;
import com.edbrix.connectbrix.volley.SettingsMy;

import org.json.JSONObject;

public class SelectTimeZoneActivity extends BaseActivity {

    private static final String TAG = SelectTimeZoneActivity.class.getName();
    private LinearLayout mLinearSearch;
    private TextInputLayout mInputLayoutSearch;
    private EditText mInputSearch;
    private ListView mSelectTimeZoneList;

    SessionManager sessionManager;
    TimeZoneParentData timeZoneParentData;
    String timeZoneID = "";
    SelectTimeZoneAdapter selectTimeZoneAdapter;
    private SelectTimeZoneAdapter.OnTextViewActionListener onTextViewActionListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_time_zone);
        getSupportActionBar().setTitle("Select Time Zone");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessionManager = new SessionManager(this);
        timeZoneParentData = new TimeZoneParentData();
        assignViews();

        Intent intent = getIntent();
        timeZoneID = intent.getStringExtra("TimeZoneID");

        getTimeZones();


        mInputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //System.out.println("Text [" + s + "]");
                if (mInputSearch.isFocused()) {
                    selectTimeZoneAdapter.getFilter().filter(s.toString());
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        onTextViewActionListener = new SelectTimeZoneAdapter.OnTextViewActionListener() {
            @Override
            public void onTextViewClicked(TimeZoneListData datum, int position) {
                //showToast("State: " + datum.getTitle() + "  id: " + datum.getId());
                launchEvent(datum.getId(), datum.getTitle());
            }

        };
    }

    private void getTimeZones() {

        try {
            showBusyProgress();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("APIKEY", sessionManager.getPrefsOrganizationApiKey());
            jsonObject.put("SECRETKEY", sessionManager.getPrefsOrganizationSecretKey());

            GsonRequest<TimeZoneParentData> timeZoneListRequest = new GsonRequest<>(Request.Method.POST, Constants.getTimeZoneList, jsonObject.toString(), TimeZoneParentData.class,
                    new Response.Listener<TimeZoneParentData>() {
                        @Override
                        public void onResponse(@NonNull TimeZoneParentData response) {
                            hideBusyProgress();
                            if (response.getError() != null) {
                                String error = response.getError().getErrorMessage();
                                showToast(error);
                            } else {

                                if (response.getSuccess() == 1) {
                                    timeZoneParentData = response;

                                    if (timeZoneParentData.getTimezoneList() != null && timeZoneParentData.getTimezoneList().size() > 0) {
                                        //txtDataFound.setVisibility(View.GONE);
                                        mSelectTimeZoneList.setVisibility(View.VISIBLE);
                                        selectTimeZoneAdapter = new SelectTimeZoneAdapter(SelectTimeZoneActivity.this, timeZoneParentData.getTimezoneList(), onTextViewActionListener,timeZoneID);
                                        mSelectTimeZoneList.setAdapter(selectTimeZoneAdapter);

                                    } else {
                                        mSelectTimeZoneList.setVisibility(View.GONE);
                                    }

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
            timeZoneListRequest.setRetryPolicy(Application.getDefaultRetryPolice());
            timeZoneListRequest.setShouldCache(false);
            Application.getInstance().addToRequestQueue(timeZoneListRequest, "timeZoneListRequest");

        } catch (Exception e) {
            hideBusyProgress();
            Log.e(TAG, e.getMessage());
        }
    }

    private void assignViews() {
        mLinearSearch = (LinearLayout) findViewById(R.id.linearSearch);
        mInputLayoutSearch = (TextInputLayout) findViewById(R.id.input_layout_search);
        mInputSearch = (EditText) findViewById(R.id.input_search);
        mSelectTimeZoneList = (ListView) findViewById(R.id.selectTimeZoneList);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                //finish();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    private void launchEvent(String timezoneId, String timeZone) {

        Constants.TimeZonId = Integer.parseInt(timezoneId);
        Constants.TimeZone = timeZone;
        finish();
    }
}
