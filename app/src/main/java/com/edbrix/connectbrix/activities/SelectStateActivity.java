package com.edbrix.connectbrix.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
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
import com.edbrix.connectbrix.baseclass.BaseActivity;
import com.edbrix.connectbrix.data.StateList;
import com.edbrix.connectbrix.data.StateListData;
import com.edbrix.connectbrix.utils.Constants;
import com.edbrix.connectbrix.utils.SessionManager;
import com.edbrix.connectbrix.volley.GsonRequest;
import com.edbrix.connectbrix.volley.SettingsMy;

import org.json.JSONObject;

import java.util.ArrayList;

public class SelectStateActivity extends BaseActivity {

    private static final String TAG = SelectStateActivity.class.getName();
    SessionManager sessionManager;

    private LinearLayout mLinearSearch;
    private EditText mInputSearch;
    private ListView mSelectStateList;

    StateListData stateListData;
    SelectStateAdapter selectStateAdapter;
    private SelectStateAdapter.OnTextViewActionListener onTextViewActionListener;

    String CountryId = "";
    String stateName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_select_state);
        getSupportActionBar().setTitle("Select State");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sessionManager = new SessionManager(this);
        stateListData = new StateListData();
        assignViews();

        Intent intent = getIntent();
        CountryId = intent.getStringExtra("CountryId");
        stateName = intent.getStringExtra("StateName");

        mInputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //System.out.println("Text [" + s + "]");
                if (mInputSearch.isFocused()) {
                    selectStateAdapter.getFilter().filter(s.toString());
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


        onTextViewActionListener = new SelectStateAdapter.OnTextViewActionListener() {
            @Override
            public void onTextViewClicked(StateList datum, int position) {
                //showToast("State: " + datum.getTitle() + "  id: " + datum.getId());
                launchEvent(datum.getId(), datum.getTitle());
            }

        };

        if (savedInstanceState != null) {
            stateListData.setStateList((ArrayList<StateList>) savedInstanceState.getSerializable("stateList"));
            if (stateListData != null) {
                mSelectStateList.setVisibility(View.VISIBLE);
                selectStateAdapter = new SelectStateAdapter(SelectStateActivity.this, stateListData.getStateList(), onTextViewActionListener, stateName);
                mSelectStateList.setAdapter(selectStateAdapter);
            }
            mInputSearch.setText(savedInstanceState.getString("searchState"));
        } else {
            getStateList(CountryId);
        }


    }

    private void assignViews() {
        mLinearSearch = (LinearLayout) findViewById(R.id.linearSearch);
        mInputSearch = (EditText) findViewById(R.id.input_search);
        mSelectStateList = (ListView) findViewById(R.id.selectStateList);
    }

    private void getStateList(final String CountryId) {
        try {

            showBusyProgress();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("APIKEY", sessionManager.getPrefsOrganizationApiKey());
            jsonObject.put("SECRETKEY", sessionManager.getPrefsOrganizationSecretKey());
            jsonObject.put("CountryId", CountryId);

            GsonRequest<StateListData> stateListRequest = new GsonRequest<>(Request.Method.POST, Constants.getStateList, jsonObject.toString(), StateListData.class,
                    new Response.Listener<StateListData>() {
                        @Override
                        public void onResponse(@NonNull StateListData response) {
                            hideBusyProgress();
                            if (response.getError() != null) {
                                String error = response.getError().getErrorMessage();
                                showToast(error);
                            } else {

                                if (response.getSuccess() == 1) {
                                    stateListData = response;

                                    if (stateListData.getStateList() != null && stateListData.getStateList().size() > 0) {
                                        //txtDataFound.setVisibility(View.GONE);
                                        mSelectStateList.setVisibility(View.VISIBLE);
                                        selectStateAdapter = new SelectStateAdapter(SelectStateActivity.this, stateListData.getStateList(), onTextViewActionListener,stateName);
                                        mSelectStateList.setAdapter(selectStateAdapter);

                                    } else {
                                        mSelectStateList.setVisibility(View.GONE);
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
            stateListRequest.setRetryPolicy(Application.getDefaultRetryPolice());
            stateListRequest.setShouldCache(false);
            Application.getInstance().addToRequestQueue(stateListRequest, "countryListRequest");

        } catch (Exception e) {

            hideBusyProgress();
            Log.e(TAG, e.getMessage());
        }
    }

    //final String eventName3 = "com.edbrix.connectbrix.Event";

    private void launchEvent(String stateID, String stateName) {
        /*Intent eventIntent = new Intent(eventName3);
        eventIntent.putExtra("countryID", CountryId);
        eventIntent.putExtra("stateID", stateID);
        eventIntent.putExtra("stateName", stateName);
        this.sendBroadcast(eventIntent);*/

        Constants.StateId = Integer.parseInt(stateID);
        Constants.CountryId = Integer.parseInt(CountryId);
        Constants.StateName = stateName;
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.select_state_menu, menu);
        MenuItem shareItem = menu.findItem(R.id.menuCountry);
        shareItem.setTitle("Change Country");
        //menu.findItem(R.id.menuCountry).setTitle(Html.fromHtml("<font color='#fffff'>Country</font>"));

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                //finish();
                return true;
            case R.id.menuCountry:
                Intent intent = new Intent(this, SelectCountryActivity.class);
                intent.putExtra("CountryId",CountryId);
                startActivity(intent);
                finish();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("stateList", stateListData.getStateList());
        outState.putString("searchState",mInputSearch.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        stateListData.setStateList((ArrayList<StateList>) savedInstanceState.getSerializable("stateList"));
        if (stateListData != null) {
            mSelectStateList.setVisibility(View.VISIBLE);
            selectStateAdapter = new SelectStateAdapter(SelectStateActivity.this, stateListData.getStateList(), onTextViewActionListener, stateName);
            mSelectStateList.setAdapter(selectStateAdapter);
        }
        mInputSearch.setText(savedInstanceState.getString("searchState"));
    }
}


