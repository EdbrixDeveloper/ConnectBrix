package com.edbrix.connectbrix.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edbrix.connectbrix.Application;
import com.edbrix.connectbrix.R;
import com.edbrix.connectbrix.adapters.SelectCountryAdapter;
import com.edbrix.connectbrix.baseclass.BaseActivity;
import com.edbrix.connectbrix.data.CountryList;
import com.edbrix.connectbrix.data.CountryListData;
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

public class SelectCountryActivity extends BaseActivity {

    private static final String TAG = SelectCountryActivity.class.getName();
    SessionManager sessionManager;

    private LinearLayout mLinearSearch;
    private EditText mInputSearch;
    private ListView mSelectCountryList;

    CountryListData countryListData;
    SelectCountryAdapter selectCountryAdapter;
    private SelectCountryAdapter.OnTextViewActionListener onTextViewActionListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_select_country);
        getSupportActionBar().setTitle("Select Country");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sessionManager = new SessionManager(this);
        countryListData = new CountryListData();
        assignViews();


        mInputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //System.out.println("Text [" + s + "]");
                if (mInputSearch.isFocused()) {
                    selectCountryAdapter.getFilter().filter(s.toString());
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

        /*mSelectCountryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // selected item
                *//*String selected = ((TextView) view.findViewById(R.id.your_textView_item_id)).getText().toString();

                Toast toast = Toast.makeText(getApplicationContext(), selected, Toast.LENGTH_SHORT);
                toast.show();*//*

                showToast("Cnrty:" + countryListData.getCountryList().get(position).getTitle() + "  id" + countryListData.getCountryList().get(position).getTitle());
            }
        });*/


        onTextViewActionListener = new SelectCountryAdapter.OnTextViewActionListener() {
            @Override
            public void onTextViewClicked(CountryList datum, int position) {
                //showToast("Cnrty:" + datum.getTitle() + "  id" + datum.getId());
                if (datum != null) {
                    Intent intent = new Intent(SelectCountryActivity.this, SelectStateActivity.class);
                    intent.putExtra("CountryId", datum.getId());
                    startActivity(intent);
                    finish();
                }
            }

        };

        if (savedInstanceState != null) {
            countryListData.setCountryList((ArrayList<CountryList>) savedInstanceState.getSerializable("countryList"));
            if (countryListData != null)
            {
                mSelectCountryList.setVisibility(View.VISIBLE);
                selectCountryAdapter = new SelectCountryAdapter(SelectCountryActivity.this, countryListData.getCountryList(), onTextViewActionListener);
                mSelectCountryList.setAdapter(selectCountryAdapter);
            }
            mInputSearch.setText(savedInstanceState.getString("searchCountry"));
        } else {
            getCountryList();
        }
    }

    private void assignViews() {
        mLinearSearch = (LinearLayout) findViewById(R.id.linearSearch);
        mInputSearch = (EditText) findViewById(R.id.input_search);
        mSelectCountryList = (ListView) findViewById(R.id.selectCountryList);
    }

    private void getCountryList() {
        try {

            showBusyProgress();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("APIKEY", sessionManager.getPrefsOrganizationApiKey());
            jsonObject.put("SECRETKEY", sessionManager.getPrefsOrganizationSecretKey());

            GsonRequest<CountryListData> countryListRequest = new GsonRequest<>(Request.Method.POST, Constants.getCountryList, jsonObject.toString(), CountryListData.class,
                    new Response.Listener<CountryListData>() {
                        @Override
                        public void onResponse(@NonNull CountryListData response) {
                            hideBusyProgress();
                            if (response.getError() != null) {
                                String error = response.getError().getErrorMessage();
                                showToast(error);
                            } else {

                                if (response.getSuccess() == 1) {
                                    countryListData = response;

                                    if (countryListData.getCountryList() != null && countryListData.getCountryList().size() > 0) {
                                        //txtDataFound.setVisibility(View.GONE);
                                        mSelectCountryList.setVisibility(View.VISIBLE);
                                        selectCountryAdapter = new SelectCountryAdapter(SelectCountryActivity.this, countryListData.getCountryList(), onTextViewActionListener);
                                        mSelectCountryList.setAdapter(selectCountryAdapter);

                                    } else {
                                        mSelectCountryList.setVisibility(View.GONE);
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
            countryListRequest.setRetryPolicy(Application.getDefaultRetryPolice());
            countryListRequest.setShouldCache(false);
            Application.getInstance().addToRequestQueue(countryListRequest, "countryListRequest");

        } catch (Exception e) {

            hideBusyProgress();
            Log.e(TAG, e.getMessage());
        }
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("countryList", countryListData.getCountryList());
        outState.putString("searchCountry",mInputSearch.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        countryListData.setCountryList((ArrayList<CountryList>) savedInstanceState.getSerializable("countryList"));
        if (countryListData != null)
        {
            mSelectCountryList.setVisibility(View.VISIBLE);
            selectCountryAdapter = new SelectCountryAdapter(SelectCountryActivity.this, countryListData.getCountryList(), onTextViewActionListener);
            mSelectCountryList.setAdapter(selectCountryAdapter);
        }
        mInputSearch.setText(savedInstanceState.getString("searchCountry"));
    }
}

